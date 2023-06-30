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

package raw.compiler.rql2.truffle.builtin

import raw.compiler.base.source.{AnythingType, Type}
import raw.compiler.common.source.Exp
import raw.compiler.rql2.source.FunType
import raw.compiler.rql2._
import raw.compiler.rql2.builtin.FunctionInvokeAfterEntry
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, TypeDoc}
import raw.runtime.truffle.ExpressionNode

class TruffleFunctionInvokeAfterEntry extends FunctionInvokeAfterEntry with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = super.toTruffle(t, args)
}
