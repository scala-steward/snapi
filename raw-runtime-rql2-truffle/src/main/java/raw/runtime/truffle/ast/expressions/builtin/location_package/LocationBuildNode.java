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

package raw.runtime.truffle.ast.expressions.builtin.location_package;


import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2IntType;
import raw.compiler.rql2.source.Rql2ListType;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.list.ListLibrary;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.sources.*;
import scala.Tuple2;
import scala.collection.immutable.Map;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.VectorBuilder;

import java.time.Duration;

// todo: A.Z ask about cache strategy

@NodeInfo(shortName = "Location.Build")
public class LocationBuildNode extends ExpressionNode {

    private String[] keys;
    @Child
    private ExpressionNode url;

    @Children
    private ExpressionNode[] values;

    private final Rql2TypeWithProperties[] types;

    private final CacheStrategy cacheStrategy;

    public LocationBuildNode(ExpressionNode url,
                             String[] keys,
                             ExpressionNode[] values,
                             Rql2TypeWithProperties[] types,
                             CacheStrategy cacheStrategy) {
        assert values.length == keys.length;
        assert values.length == types.length;
        this.url = url;
        this.keys = keys;
        this.values = values;
        this.types = types;
        this.cacheStrategy = cacheStrategy;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Map<LocationSettingKey, LocationSettingValue> map = new HashMap<>();
        String url = (String) this.url.executeGeneric(frame);
        for (int i = 0; i < this.keys.length; i++) {
            Object value = this.values[i].executeGeneric(frame);
            map = map.$plus(Tuple2.apply(new LocationSettingKey(keys[i]), buildLocationSettingValue(value, types[i])));
        }
        return new LocationObject(url, map, this.cacheStrategy);
    }

    private LocationSettingValue buildLocationSettingValue(Object value, Rql2TypeWithProperties type) {
        if (TypeGuards.isIntKind(type)) {
            return new LocationIntSetting((Integer) value);
        } else if (TypeGuards.isStringKind(type)) {
            return new LocationStringSetting((String) value);
        } else if (TypeGuards.isByteKind(type)) {
            byte[] bytes = (byte[]) value;
            VectorBuilder<Object> vec = new VectorBuilder<>();
            for (byte aByte : bytes) {
                vec = vec.$plus$eq(aByte);
            }
            return new raw.sources.LocationBinarySetting(vec.result());
        } else if (TypeGuards.isBooleanKind(type)) {
            return new LocationBooleanSetting((Boolean) value);
        } else if (TypeGuards.isIntervalKind(type)) {
            return new LocationDurationSetting(Duration.ofMillis(((IntervalObject) value).toMillis()));
        } else if (TypeGuards.isListKind(type) && ((Rql2ListType) type).innerType() instanceof Rql2IntType) {
            ListLibrary lists = ListLibrary.getFactory().create(value);
            int[] ints = (int[]) lists.getInnerList(value);
            return new raw.sources.LocationIntArraySetting(ints);
        } else if (TypeGuards.isListKind(type)) {
            ListLibrary listLibs = ListLibrary.getFactory().createDispatched(2);
            Object lists = listLibs.getInnerList(value);
            VectorBuilder<Tuple2<String, String>> vec = new VectorBuilder<>();
            int size = (int) listLibs.size(lists);
            for (int i = 0; i < size; i++) {
                Object list = listLibs.get(lists, i);
                Object key = listLibs.get(list, 0);
                Object val = listLibs.get(list, 1);
                vec = vec.$plus$eq(Tuple2.apply((String) key, (String) val));
            }
            return new raw.sources.LocationKVSetting(vec.result());
        } else {
            throw new RawTruffleRuntimeException("Unsupported type for LocationSettingValue", this);
        }

    }

}
