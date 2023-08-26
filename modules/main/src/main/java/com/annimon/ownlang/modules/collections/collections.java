package com.annimon.ownlang.modules.collections;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import com.annimon.ownlang.modules.Module;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Supplier;

public class collections implements Module {

    public static void initConstants() {
    }

    @Override
    public void init() {
        initConstants();
        Functions.set("hashMap", mapFunction(HashMap::new));
        Functions.set("linkedHashMap", mapFunction(LinkedHashMap::new));
        Functions.set("concurrentHashMap", mapFunction(ConcurrentHashMap::new));
        Functions.set("treeMap", sortedMapFunction(TreeMap::new, TreeMap::new));
        Functions.set("concurrentSkipListMap", sortedMapFunction(ConcurrentSkipListMap::new, ConcurrentSkipListMap::new));
    }

    private Function mapFunction(final Supplier<Map<Value, Value>> mapSupplier) {
        return (args) -> {
            Arguments.checkOrOr(0, 1, args.length);
            final Map<Value, Value> map = mapSupplier.get();
            if (args.length == 1) {
                if (args[0].type() == Types.MAP) {
                    map.putAll(((MapValue) args[0]).getMap());
                } else {
                    throw new TypeException("Map expected in first argument");
                }
            }
            return new MapValue(map);
        };
    }

    private Function sortedMapFunction(final Supplier<SortedMap<Value, Value>> mapSupplier,
                                       final java.util.function.Function<
                                               Comparator<? super Value>,
                                               SortedMap<Value, Value>> comparatorToMapFunction) {
        return (args) -> {
            Arguments.checkRange(0, 2, args.length);
            final SortedMap<Value, Value> map;
            switch (args.length) {
                case 0: // treeMap()
                    map = mapSupplier.get();
                    break;
                case 1: // treeMap(map) || treeMap(comparator)
                    if (args[0].type() == Types.MAP) {
                        map = mapSupplier.get();
                        map.putAll(((MapValue) args[0]).getMap());
                    } else if (args[0].type() == Types.FUNCTION) {
                        final Function comparator = ValueUtils.consumeFunction(args[0], 0);
                        map = comparatorToMapFunction.apply((o1, o2) -> comparator.execute(o1, o2).asInt());
                    } else {
                        throw new TypeException("Map or comparator function expected in first argument");
                    }
                    break;
                case 2: // treeMap(map, comparator)
                    if (args[0].type() != Types.MAP) {
                        throw new TypeException("Map expected in first argument");
                    }
                    final Function comparator = ValueUtils.consumeFunction(args[1], 1);
                    map = comparatorToMapFunction.apply((o1, o2) -> comparator.execute(o1, o2).asInt());
                    map.putAll(((MapValue) args[0]).getMap());
                    break;
                default:
                    throw new IllegalStateException();
            }
            return new MapValue(map);
        };
    }
}
