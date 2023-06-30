/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.compiler.rql2

import org.bitbucket.inkytonik.kiama.relation.EnsureTree
import org.bitbucket.inkytonik.kiama.util.{Position, Positions}
import raw.compiler._
import raw.compiler.base.source.{BaseNode, Type}
import raw.compiler.base.{CompilerContext, TreeDeclDescription, TreeParamDescription}
import raw.compiler.common.PhaseDescriptor
import raw.compiler.common.source._
import raw.runtime.JvmEntrypoint
import raw.compiler.rql2.builtin.{EnvironmentPackageBuilder, LibraryPackageBuilder}
import raw.compiler.rql2.lsp.{CompilerLspService, LspSyntaxAnalyzer}
import raw.compiler.rql2.source._
import raw.runtime._
import raw.compiler.base.errors.{BaseError, UnexpectedType, UnknownDecl}
import raw.compiler.rql2.errors.{
  ErrorsPrettyPrinter,
  ExpectedTypeButGotExpression,
  ItemsNotComparable,
  KeyNotComparable,
  MandatoryArgumentAfterOptionalArgument,
  MandatoryArgumentsMissing,
  NamedParameterAfterOptionalParameter,
  NoOptionalArgumentsExpected,
  OutputTypeRequiredForRecursiveFunction,
  PackageNotFound,
  RepeatedFieldNames,
  RepeatedOptionalArguments,
  UnexpectedArguments,
  UnexpectedOptionalArgument
}

import scala.collection.mutable

abstract class Compiler(implicit compilerContext: CompilerContext) extends raw.compiler.common.Compiler {

  override protected def phases: Seq[PhaseDescriptor] = Seq(
    PhaseDescriptor(
      "SugarExtensionDesugarer",
      classOf[SugarExtensionDesugarer].asInstanceOf[Class[raw.compiler.base.PipelinedPhase[SourceProgram]]]
    ),
    PhaseDescriptor(
      "(Sugar)SugarExtensionDesugarer",
      classOf[SugarExtensionDesugarer].asInstanceOf[Class[raw.compiler.base.PipelinedPhase[SourceProgram]]]
    ),
    PhaseDescriptor(
      "ListProjDesugarer",
      classOf[ListProjDesugarer].asInstanceOf[Class[raw.compiler.base.PipelinedPhase[SourceProgram]]]
    ),
    PhaseDescriptor(
      "Propagation",
      classOf[Propagation].asInstanceOf[Class[raw.compiler.base.PipelinedPhase[SourceProgram]]]
    ),
    PhaseDescriptor(
      "ImplicitCasts",
      classOf[ImplicitCasts].asInstanceOf[Class[raw.compiler.base.PipelinedPhase[SourceProgram]]]
    )
  )

  override def prettyPrint(node: BaseNode): String = {
    SourcePrettyPrinter.format(node)
  }

  override def parseType(tipe: String): Option[Type] = {
    val positions = new Positions()
    val parser = new FrontendSyntaxAnalyzer(positions)
    parser.parseType(tipe).toOption
  }

  override def getTreeFromSource(
      source: String
  )(implicit programContext: raw.compiler.base.ProgramContext): TreeWithPositions = {
    // This is the entrypoint for user code, so use the frontend parser.
    new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext.asInstanceOf[ProgramContext])
  }

  override def getTree(
      program: SourceProgram
  )(implicit programContext: raw.compiler.base.ProgramContext): Tree = {
    new Tree(program)(programContext.asInstanceOf[ProgramContext])
  }

  override def prune(program: SourceProgram, tipe: Type)(
      implicit programContext: raw.compiler.base.ProgramContext
  ): Option[SourceProgram] = {
    None
  }

  override def template(
      program: SourceProgram
  )(implicit programContext: base.ProgramContext): (String, SourceProgram) = {
    throw new AssertionError("operation not supported")
  }

  override def getProgramContext(
      programSettings: ProgramSettings,
      runtimeContext: RuntimeContext
  ): ProgramContext = {
    new ProgramContext(programSettings, runtimeContext)
  }

  override def parseAndValidate(source: String, maybeDecl: Option[String])(
      implicit programContext: base.ProgramContext
  ): Either[List[ErrorMessage], SourceProgram] = {
    buildInputTree(source).right.flatMap { tree =>
      val Rql2Program(methods, me) = tree.root

      if (maybeDecl.isEmpty) {
        if (programContext.runtimeContext.maybeArguments.nonEmpty) {
          throw new CompilerException("arguments found")
        }

        if (me.isEmpty) {
          throw new CompilerException("no expression found")
        }

        val e = me.get
        Right(Rql2Program(methods, Some(e)))
      } else {
        val decl = maybeDecl.get
        if (!tree.description.expDecls.contains(decl)) {
          throw new CompilerException("declaration not found")
        }
        if (programContext.runtimeContext.maybeArguments.isEmpty) {
          throw new CompilerException("arguments missing")
        }

        // Compile the library separately.
        compile(tree.root).right.map {
          case JvmEntrypoint(className) =>
            // Build the function type from the package declaration.
            // As a side-effect, obtain the list of mandatory arguments, since that will be necessary to correctly
            // build the function call, as described below.
            val mandatoryIdns = mutable.HashSet[String]()
            val List(TreeDeclDescription(Some(params), outType, _)) = tree.description.expDecls(decl)
            val ms = mutable.ArrayBuffer[Type]()
            val os = mutable.ArrayBuffer[FunOptTypeParam]()
            params.foreach {
              case TreeParamDescription(idn, tipe, required) =>
                if (required) {
                  mandatoryIdns.add(idn)
                  ms.append(tipe)
                } else {
                  os.append(FunOptTypeParam(idn, tipe))
                }
            }
            val funType = FunType(ms.to, os.to, outType, Set.empty)

            val args = programContext.runtimeContext.maybeArguments.get

            // Declaration was passed and arguments were passed.
            // Build code for arguments.
            val codeArgs = args.map {
              case (k, v) =>
                val e = v match {
                  case _: ParamNull => NullConst()
                  case _: ParamByte => EnvironmentPackageBuilder.Parameter(Rql2ByteType(), StringConst(k))
                  case _: ParamShort => EnvironmentPackageBuilder.Parameter(Rql2ShortType(), StringConst(k))
                  case _: ParamInt => EnvironmentPackageBuilder.Parameter(Rql2IntType(), StringConst(k))
                  case _: ParamLong => EnvironmentPackageBuilder.Parameter(Rql2LongType(), StringConst(k))
                  case _: ParamFloat => EnvironmentPackageBuilder.Parameter(Rql2FloatType(), StringConst(k))
                  case _: ParamDouble => EnvironmentPackageBuilder.Parameter(Rql2DoubleType(), StringConst(k))
                  case _: ParamDecimal => EnvironmentPackageBuilder.Parameter(Rql2DecimalType(), StringConst(k))
                  case _: ParamBool => EnvironmentPackageBuilder.Parameter(Rql2BoolType(), StringConst(k))
                  case _: ParamString => EnvironmentPackageBuilder.Parameter(Rql2StringType(), StringConst(k))
                  case _: ParamDate => EnvironmentPackageBuilder.Parameter(Rql2DateType(), StringConst(k))
                  case _: ParamTime => EnvironmentPackageBuilder.Parameter(Rql2TimeType(), StringConst(k))
                  case _: ParamTimestamp => EnvironmentPackageBuilder.Parameter(Rql2TimestampType(), StringConst(k))
                  case _: ParamInterval => EnvironmentPackageBuilder.Parameter(Rql2IntervalType(), StringConst(k))
                }
                // Build the argument node.
                // If argument is mandatory, then according to RQL2's language definition, the argument is not named.
                // However, if the argument os optional, the name is added.
                FunAppArg(e, if (mandatoryIdns.contains(k)) None else Some(k))
            }

            val i = IdnDef()
            Rql2Program(
              raw.compiler.rql2.source
                .Let(
                  Vector(
                    raw.compiler.rql2.source
                      .LetBind(LibraryPackageBuilder.Use(StringConst(className), StringConst(decl), funType), i, None)
                  ),
                  FunApp(IdnExp(i), codeArgs.toVector)
                )
            )
        }
      }
    }
  }

  private def formatErrors(errors: Seq[BaseError], positions: Positions): List[ErrorMessage] = {
    errors.map { err =>
      val ranges = positions.getStart(err.node) match {
        case Some(begin) =>
          val Some(end) = positions.getFinish(err.node)
          List(ErrorRange(ErrorPosition(begin.line, begin.column), ErrorPosition(end.line, end.column)))
        case _ => List.empty
      }
      ErrorMessage(ErrorsPrettyPrinter.format(err), ranges)
    }.toList
  }

  private def parseError(error: String, position: Position): ErrorLSPResponse = {
    val range = ErrorRange(ErrorPosition(position.line, position.column), ErrorPosition(position.line, position.column))
    ErrorLSPResponse(List(ErrorMessage(error, List(range))))
  }

  override def lsp(request: LSPRequest)(
      implicit programContext: base.ProgramContext
  ): LSPResponse = {
    request match {
      case FormatCodeLSPRequest(code, _) =>
        val pretty = new SourceCommentsPrettyPrinter
        pretty.prettyCode(code) match {
          case Left((error, position)) =>
            val msg = "could not parse source: " + error
            parseError(msg, position)
          case Right(code) => FormatCodeLSPResponse(code, List.empty)
        }
      case AiValidateLSPRequest(code, _) =>
        // Will analyze the code and return only unknown declarations errors.
        val positions = new Positions()
        val parser = new FrontendSyntaxAnalyzer(positions)
        parser.parse(code) match {
          case Right(program) =>
            val sourceProgram = program.asInstanceOf[SourceProgram]
            val kiamaTree = new org.bitbucket.inkytonik.kiama.relation.Tree[SourceNode, SourceProgram](
              sourceProgram
            )
            val analyzer = new SemanticAnalyzer(kiamaTree)(programContext.asInstanceOf[ProgramContext])

            // Selecting only a subset of the errors
            val selection = analyzer.errors.filter {
              // For the case of a function that does not exist in a package
              case UnexpectedType(_, PackageType(_), ExpectedProjType(_), _, _) => true
              case _: UnknownDecl => true
              case _: OutputTypeRequiredForRecursiveFunction => true
              case _: UnexpectedOptionalArgument => true
              case _: NoOptionalArgumentsExpected => true
              case _: KeyNotComparable => true
              case _: ItemsNotComparable => true
              case _: MandatoryArgumentAfterOptionalArgument => true
              case _: RepeatedFieldNames => true
              case _: UnexpectedArguments => true
              case _: MandatoryArgumentsMissing => true
              case _: RepeatedOptionalArguments => true
              case _: PackageNotFound => true
              case _: NamedParameterAfterOptionalParameter => true
              case _: ExpectedTypeButGotExpression => true
              case _ => false
            }

            ErrorLSPResponse(formatErrors(selection, positions))
          case Left((err, pos)) => parseError(err, pos)
        }
      case _ =>
        // Parse tree with dedicated parser, which is more lose and tries to obtain an AST even with broken code.
        val positions = new Positions()
        val parser = new LspSyntaxAnalyzer(positions)
        parser.parse(request.code) match {
          case Right(program) =>
            // Manually instantiate an analyzer to create a "flexible tree" that copes with broken code.
            val sourceProgram = program.asInstanceOf[SourceProgram]
            val kiamaTree = new org.bitbucket.inkytonik.kiama.relation.Tree[SourceNode, SourceProgram](
              sourceProgram,
              shape = EnsureTree // The LSP parser can create "cloned nodes" so this protects it.
            )
            // Do not perform any validation on errors as we fully expect the tree to be "broken" in most cases.
            val analyzer = new SemanticAnalyzer(kiamaTree)(programContext.asInstanceOf[ProgramContext])
            // Handle the LSP request.
            val lspService = new CompilerLspService(
              analyzer,
              positions,
              prettyPrint
            )(programContext.asInstanceOf[ProgramContext])
            request match {
              case dotAutoCompleteLSPRequest: DotAutoCompleteLSPRequest =>
                lspService.dotAutoComplete(dotAutoCompleteLSPRequest)
              case wordAutoCompleteLSPRequest: WordAutoCompleteLSPRequest =>
                lspService.wordAutoComplete(wordAutoCompleteLSPRequest)
              case hoverLSPRequest: HoverLSPRequest => lspService.hover(hoverLSPRequest)
              case definitionLSPRequest: DefinitionLSPRequest => lspService.definition(definitionLSPRequest)
              case renameLSPRequest: RenameLSPRequest => lspService.rename(renameLSPRequest)
              case _: ValidateLSPRequest =>
                val response = lspService.validate
                if (response.errors.isEmpty) {
                  // The "flexible" tree did not find any semantic errors.
                  // So now we should parse with the "strict" parser/analyzer to get a proper tree and check for errors
                  // in that one.
                  buildInputTree(request.code) match {
                    case Right(_) => ErrorLSPResponse(List.empty)
                    case Left(errors) => ErrorLSPResponse(errors)
                  }
                } else {
                  // The "flexible" tree found some semantic errors, so report only those.
                  response
                }
            }
          case Left((err, pos)) =>
            // Could not produce an AST, therefore report parse error.
            // TODO (msb): Do we want to log this, to see if we can produce an AST in the future???
            //             Peak into 'err' and 'pos' to know more.
            parseError(err, pos)
        }
    }
  }

  override def supportsCaching: Boolean = false

}
