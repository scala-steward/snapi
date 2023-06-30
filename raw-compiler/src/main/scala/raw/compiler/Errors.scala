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

package raw.compiler

import raw.api.RawException

/**
 * Used for errors that are found during semantic analysis.
 * @param message The error message.
 * @param positions The positions where the error occurred.
 */
final case class ErrorMessage(message: String, positions: List[ErrorRange])
final case class ErrorRange(begin: ErrorPosition, end: ErrorPosition)
final case class ErrorPosition(line: Int, column: Int)

/**
 * Used for exceptions that are thrown by the compiler itself.
 * Must abort compilation.
 * Should NOT BE USED for:
 * - semantic analysis errors or other normal errors since there is no tracking of positions.
 * - errors during execution.
 * Should BE USED for:
 * - errors that are not found during type checking but which prevent the compiler from proceeding, e.g.
 *   missing implementations or the like.
 * Parsing may throw this exception if they encounter an error that they cannot recover from.
 *
 * The message can be safely shared with the user.
 */
sealed class CompilerException(message: String, cause: Throwable = null) extends RawException(message, cause)

final class CompilerParserException(message: String, val position: ErrorPosition) extends CompilerException(message) {
  def this(message: String, pos: org.bitbucket.inkytonik.kiama.util.Position) =
    this(message, ErrorPosition(pos.line, pos.column))
}

/**
 * Exception thrown for errors that happened during the execution of a program.
 *
 * The message can be safely shared with the user.
 */
final class CompilerExecutionException(val reason: String, val cause: Throwable = null)
    extends RawException(reason, cause)
