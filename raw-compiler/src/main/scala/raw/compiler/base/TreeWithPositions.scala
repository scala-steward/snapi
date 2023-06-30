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

package raw.compiler.base

import org.bitbucket.inkytonik.kiama.util.Positions
import raw.compiler.CompilerParserException
import raw.compiler.base.source._
import raw.compiler._
import raw.utils.RawUtils

abstract class TreeWithPositions[N <: BaseNode: Manifest, P <: N: Manifest, E <: N: Manifest](
    val originalSource: String,
    ensureTree: Boolean
)(implicit programContext: ProgramContext)
    extends BaseTree[N, P, E](ensureTree) {

  @throws[CompilerParserException]
  protected def doParse(): P

  val positions: Positions = new Positions()

  override lazy val originalRoot: P = {
    doParse()
  }

  override lazy val errors: List[ErrorMessage] = {
    analyzer.errors.map { err =>
      getRange(err.node) match {
        case Some(range) => ErrorMessage(format(err), List(range))
        case _ => ErrorMessage(format(err), List.empty)
      }
    }.to
  }

  private def getRange(n: BaseNode): Option[ErrorRange] = {
    // Our positions model, unlike Kiama, requires both a beginning and an end position.
    positions.getStart(n) match {
      case Some(begin) =>
        val Some(end) = positions.getFinish(n)
        Some(ErrorRange(ErrorPosition(begin.line, begin.column), ErrorPosition(end.line, end.column)))
      case _ => None
    }
  }

  override protected def isTreeValid: Boolean = {
    val isValid = super.isTreeValid
    logTree(isValid)
    isValid
  }

  private def logTree(isValid: Boolean): Unit = {
    if (isValid) {
      // Tree is valid

      logger.trace(s"""Tree:
        |$pretty""".stripMargin)
      logger.trace(s"""Tree types:
        |$prettyTypes""".stripMargin)
    } else {
      // Tree is not valid

      val msg = s"""Tree has semantic errors!
        |(The full tree is printed first, followed by the errors):
        |====
        |Tree
        |====
        |$pretty
        |
        |======
        |Errors
        |======
        |$prettyErrors""".stripMargin
      if (messageTooBig(msg)) {
        val p = RawUtils.saveToTemporaryFileNoDeleteOnExit(msg, "deepcheck-", ".log")
        logger.debug(s"""Tree has semantic errors!
          |Details in ${p.toAbsolutePath.toString}""".stripMargin)
      } else {
        logger.debug(s"""Tree has semantic errors!
          |$prettyErrors""".stripMargin)
      }
    }

  }

  protected def prettyErrors: String = {
    var output = ""
    for (err <- analyzer.errors) {
      output += s"Error: ${format(err)}\n"

      (positions.getStart(err.node), positions.getFinish(err.node)) match {
        case (Some(start), Some(finish)) => for ((line, _lineno) <- originalSource.split("\n").zipWithIndex) {
            val lineno = _lineno + 1
            output += line + "\n"

            var startCol = -1
            var endCol = -1

            if (start.line == lineno && finish.line == lineno) {
              startCol = start.column
              endCol = finish.column
            } else if (start.line == lineno) {
              startCol = start.column
              endCol = line.length
            } else if (finish.line == lineno) {
              startCol = 0
              endCol = finish.column
            } else if (lineno > start.line && lineno < finish.line) {
              startCol = 0
              endCol = line.length
            }

            if (startCol != -1) {
              assert(endCol != -1)
              for (i <- 0 until line.length) {
                output +=
                  (if (i >= (startCol - 1) && i <= (endCol - 1)) {
                     "^"
                   } else {
                     " "
                   })
              }
              output += "\n"
            }
          }
        case _ => logger.warn("** NO start/finish position available for this error! **")
      }

    }
    output
  }

  protected def prettyTypes: String = {
    val collectLogs = collectNodes[E, Seq, String] {
      case e: E =>
        val t = analyzer.tipe(e)
        var output = s"Type: ${format(t)}\n"

        for ((line, _lineno) <- originalSource.split("\n").zipWithIndex) {
          val lineno = _lineno + 1
          output += line + "\n"

          var startCol = -1
          var endCol = -1

          if (positions.getStart(e).get.line == lineno && positions.getFinish(e).get.line == lineno) {
            startCol = positions.getStart(e).get.column
            endCol = positions.getFinish(e).get.column
          } else if (positions.getStart(e).get.line == lineno) {
            startCol = positions.getStart(e).get.column
            endCol = line.length
          } else if (positions.getFinish(e).get.line == lineno) {
            startCol = 0
            endCol = positions.getFinish(e).get.column
          } else if (lineno > positions.getStart(e).get.line && lineno < positions.getFinish(e).get.line) {
            startCol = 0
            endCol = line.length
          }

          if (startCol != -1) {
            assert(endCol != -1)
            for (i <- 0 until line.length) {
              output +=
                (if (i >= (startCol - 1) && i <= (endCol - 1)) {
                   "^"
                 } else {
                   " "
                 })
            }
            output += "\n"
          }
        }
        output
    }

    collectLogs(root).mkString("\n")
  }

  override def normalize: P = throw new AssertionError("normalize not supported for TreeWithPositions")

}
