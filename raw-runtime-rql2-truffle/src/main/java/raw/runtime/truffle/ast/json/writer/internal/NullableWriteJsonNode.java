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

package raw.runtime.truffle.ast.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.runtime.exceptions.json.JsonWriterRawTruffleException;
import raw.runtime.truffle.runtime.option.OptionLibrary;

import java.io.IOException;

@NodeInfo(shortName = "NullableWriteJson")
public class NullableWriteJsonNode extends StatementNode {

    @Child
    private DirectCallNode childDirectCall;

    @Child
    private OptionLibrary options = OptionLibrary.getFactory().createDispatched(1);

    public NullableWriteJsonNode(ProgramStatementNode childProgramStatementNode) {
        this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        Object option = args[0];
        JsonGenerator gen = (JsonGenerator) args[1];
        if(options.isDefined(option)) {
            childDirectCall.call(options.get(option), gen);
        } else {
            writeNull(gen);
        }
    }

    @TruffleBoundary
    private void writeNull(JsonGenerator gen) {
        try {
            gen.writeNull();
        } catch (IOException e) {
            throw new JsonWriterRawTruffleException(e.getMessage(), this);
        }
    }
}
