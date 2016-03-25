package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

import java.util.Map;

public final class functional_map implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.checkOrOr(2, 3, args.length);
        
        final Value container = args[0];
        if (container.type() == Types.ARRAY) {
            if (args[1].type() != Types.FUNCTION) {
                throw new TypeException("Function expected in second arg");
            }
            final Function mapper = ((FunctionValue) args[1]).getValue();
            return mapArray((ArrayValue) container, mapper);
        }
        
        if (container.type() == Types.MAP) {
            if (args[1].type() != Types.FUNCTION) {
                throw new TypeException("Function expected in second arg");
            }
            if (args[2].type() != Types.FUNCTION) {
                throw new TypeException("Function expected in third arg");
            }
            final Function keyMapper = ((FunctionValue) args[1]).getValue();
            final Function valueMapper = ((FunctionValue) args[2]).getValue();
            return mapMap((MapValue) container, keyMapper, valueMapper);
        }

        throw new TypeException("Invalid first argument. Array or map expected");
    }
    
    private Value mapArray(ArrayValue array, Function mapper) {
        final int size = array.size();
        final ArrayValue result = new ArrayValue(size);
        for (int i = 0; i < size; i++) {
            result.set(i, mapper.execute(array.get(i)));
        }
        return result;
    }
    
    private Value mapMap(MapValue map, Function keyMapper, Function valueMapper) {
        final MapValue result = new MapValue(map.size());
        for (Map.Entry<Value, Value> element : map) {
            final Value newKey = keyMapper.execute(element.getKey());
            final Value newValue = valueMapper.execute(element.getValue());
            result.set(newKey, newValue);
        }
        return result;
    }
}