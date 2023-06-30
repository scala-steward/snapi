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

package raw.runtime.truffle.runtime.iterable.sources;


import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.generator.collection.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.IntRangeComputeNext;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.LongRangeComputeNext;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.TimestampRangeComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

@ExportLibrary(IterableLibrary.class)
public class RangeCollection {

    final Object computeNext;

    public RangeCollection(int start, int end, int step) {
        computeNext = new IntRangeComputeNext(start, end, step);
    }

    public RangeCollection(long start, long end, long step) {
        computeNext = new LongRangeComputeNext(start, end, step);
    }

    public RangeCollection(TimestampObject start, TimestampObject end, IntervalObject step) {
        computeNext = new TimestampRangeComputeNext(start, end, step);
    }
    @ExportMessage
    boolean isIterable() {
        return true;
    }

    @ExportMessage
    Object getGenerator() {
        return new CollectionAbstractGenerator(computeNext);
    }
}
