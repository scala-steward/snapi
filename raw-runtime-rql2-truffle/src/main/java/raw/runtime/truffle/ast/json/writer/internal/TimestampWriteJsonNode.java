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
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.runtime.exceptions.json.JsonWriterRawTruffleException;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@NodeInfo(shortName = "TimestampWriteJson")
public class TimestampWriteJsonNode extends StatementNode {

    public void executeVoid(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        this.doWrite((TimestampObject) args[0], (JsonGenerator) args[1]);
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @CompilerDirectives.TruffleBoundary
    private void doWrite(TimestampObject value, JsonGenerator gen) {
        try {
            gen.writeString(formatter.format(value.getTimestamp()));
        } catch (IOException e) {
            throw new JsonWriterRawTruffleException(e.getMessage(), this);
        }
    }
}
