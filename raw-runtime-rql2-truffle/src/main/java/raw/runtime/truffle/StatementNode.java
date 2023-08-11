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

package raw.runtime.truffle;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;
import raw.runtime.truffle.StatementNodeWrapper;

@GenerateWrapper
public abstract class StatementNode extends Node implements InstrumentableNode {

    private boolean hasStatementTag;
    private boolean hasRootTag;

    public abstract void executeVoid(VirtualFrame frame);

    @Override
    public final SourceSection getSourceSection() {
        return getRootNode().getSourceSection();
    }

    @Override
    public boolean isInstrumentable() {
        return true;
    }

    @Override
    public InstrumentableNode.WrapperNode createWrapper(ProbeNode probe) {
        // ASTNodeWrapper is generated by @GenerateWrapper
        return new StatementNodeWrapper(this, probe);
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        if (tag == StandardTags.StatementTag.class) {
            return hasStatementTag;
        } else if (tag == StandardTags.RootTag.class || tag == StandardTags.RootBodyTag.class) {
            return hasRootTag;
        }
        return false;
    }

    public final void addStatementTag() {
        hasStatementTag = true;
    }

    public final void addRootTag() {
        hasRootTag = true;
    }

}
