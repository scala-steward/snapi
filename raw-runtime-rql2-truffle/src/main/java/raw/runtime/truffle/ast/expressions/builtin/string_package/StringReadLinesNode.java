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

package raw.runtime.truffle.ast.expressions.builtin.string_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.iterable.sources.ReadLinesCollection;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.runtime.truffle.utils.TruffleInputStream;

@NodeInfo(shortName = "String.ReadLines")
@NodeChild("location")
@NodeChild("encoding")
public abstract class StringReadLinesNode extends ExpressionNode {
    @Specialization
    protected Object doExecute(LocationObject locationObject, String encoding) {
        try {
            RuntimeContext context = RawContext.get(this).getRuntimeContext();
            TruffleInputStream stream = new TruffleInputStream(locationObject, context);
            TruffleCharInputStream charStream = new TruffleCharInputStream(stream, encoding);
            return new ReadLinesCollection(charStream, context);
        } catch (Exception ex) {
            throw new RawTruffleRuntimeException(ex.getMessage(), this);
        }
    }
}
