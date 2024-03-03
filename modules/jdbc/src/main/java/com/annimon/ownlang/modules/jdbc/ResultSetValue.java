package com.annimon.ownlang.modules.jdbc;

import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.lib.Types;
import java.math.BigDecimal;
import java.sql.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import static com.annimon.ownlang.lib.ValueUtils.getNumber;
import static com.annimon.ownlang.modules.jdbc.JdbcConverters.*;

class ResultSetValue extends MapValue {

    private final ResultSet rs;

    public ResultSetValue(ResultSet rs) {
        super(70);
        this.rs = rs;
        init();
    }

    private void init() {
        set("findColumn", new FunctionValue(this::findColumn));

        set("afterLast", voidFunction(rs::afterLast));
        set("beforeFirst", voidFunction(rs::beforeFirst));
        set("cancelRowUpdates", voidFunction(rs::cancelRowUpdates));
        set("clearWarnings", voidFunction(rs::clearWarnings));
        set("close", voidFunction(rs::close));
        set("deleteRow", voidFunction(rs::deleteRow));
        set("insertRow", voidFunction(rs::insertRow));
        set("moveToCurrentRow", voidFunction(rs::moveToCurrentRow));
        set("moveToInsertRow", voidFunction(rs::moveToInsertRow));
        set("refreshRow", voidFunction(rs::refreshRow));
        set("updateRow", voidFunction(rs::updateRow));

        set("absolute", voidIntFunction(rs::absolute));
        set("relative", voidIntFunction(rs::relative));
        set("setFetchDirection", voidIntFunction(rs::setFetchDirection));
        set("setFetchSize", voidIntFunction(rs::setFetchSize));

        set("first", booleanFunction(rs::first));
        set("isAfterLast", booleanFunction(rs::isAfterLast));
        set("isBeforeFirst", booleanFunction(rs::isBeforeFirst));
        set("isClosed", booleanFunction(rs::isClosed));
        set("isFirst", booleanFunction(rs::isFirst));
        set("isLast", booleanFunction(rs::isLast));
        set("last", booleanFunction(rs::last));
        set("next", booleanFunction(rs::next));
        set("previous", booleanFunction(rs::previous));
        set("rowDeleted", booleanFunction(rs::rowDeleted));
        set("rowInserted", booleanFunction(rs::rowInserted));
        set("rowUpdated", booleanFunction(rs::rowUpdated));
        set("wasNull", booleanFunction(rs::wasNull));

        set("getConcurrency", intFunction(rs::getConcurrency));
        set("getFetchDirection", intFunction(rs::getFetchDirection));
        set("getFetchSize", intFunction(rs::getFetchSize));
        set("getHoldability", intFunction(rs::getHoldability));
        set("getRow", intFunction(rs::getRow));
        set("getType", intFunction(rs::getType));

        set("getCursorName", stringFunction(rs::getCursorName));
        set("getStatement", objectFunction(rs::getStatement, StatementValue::new));

        // Results
        set("getArray", getObjectResult(rs::getArray, rs::getArray, JdbcConverters::arrayToResultSetValue));
        set("getBigDecimal", getObjectResult(rs::getBigDecimal, rs::getBigDecimal, bd -> new StringValue(bd.toString())));
        set("getBoolean", getBooleanResult(rs::getBoolean, rs::getBoolean));
        set("getByte", getNumberResult(rs::getByte, rs::getByte));
        set("getBytes", getObjectResult(rs::getBytes, rs::getBytes, ArrayValue::of));
        set("getDate", getObjectResult(rs::getDate, rs::getDate, date -> NumberValue.of(date.getTime())));
        set("getDouble", getNumberResult(rs::getDouble, rs::getDouble));
        set("getFloat", getNumberResult(rs::getFloat, rs::getFloat));
        set("getInt", getNumberResult(rs::getInt, rs::getInt));
        set("getLong", getNumberResult(rs::getLong, rs::getLong));
        set("getNString", getStringResult(rs::getNString, rs::getNString));
        set("getRowId", getObjectResult(rs::getRowId, rs::getRowId, rowid -> ArrayValue.of(rowid.getBytes())));
        set("getShort", getNumberResult(rs::getShort, rs::getShort));
        set("getString", getStringResult(rs::getString, rs::getString));
        set("getTime", getObjectResult(rs::getTime, rs::getTime, time -> NumberValue.of(time.getTime())));
        set("getTimestamp", getObjectResult(rs::getTimestamp, rs::getTimestamp, timestamp -> NumberValue.of(timestamp.getTime())));
        set("getURL", getObjectResult(rs::getURL, rs::getURL, url -> new StringValue(url.toExternalForm())));

        // Update
        set("updateNull", new FunctionValue(this::updateNull));
        set("updateBigDecimal", updateData(rs::updateBigDecimal, rs::updateBigDecimal, args -> new BigDecimal(args[1].asString())));
        set("updateBoolean", updateData(rs::updateBoolean, rs::updateBoolean, args -> args[1].asInt() != 0));
        set("updateByte", updateData(rs::updateByte, rs::updateByte, args -> getNumber(args[1]).byteValue()));
        set("updateBytes", updateData(rs::updateBytes, rs::updateBytes, args -> valueToByteArray(args[1])));
        set("updateDate", updateData(rs::updateDate, rs::updateDate, args -> new Date(getNumber(args[1]).longValue())));
        set("updateDouble", updateData(rs::updateDouble, rs::updateDouble, args -> getNumber(args[1]).doubleValue()));
        set("updateFloat", updateData(rs::updateFloat, rs::updateFloat, args -> getNumber(args[1]).floatValue()));
        set("updateInt", updateData(rs::updateInt, rs::updateInt, args -> getNumber(args[1]).intValue()));
        set("updateLong", updateData(rs::updateLong, rs::updateLong, args -> getNumber(args[1]).longValue()));
        set("updateNString", updateData(rs::updateNString, rs::updateNString, args -> args[1].asString()));
        set("updateShort", updateData(rs::updateShort, rs::updateShort, args -> getNumber(args[1]).shortValue()));
        set("updateString", updateData(rs::updateString, rs::updateString, args -> args[1].asString()));
        set("updateTime", updateData(rs::updateTime, rs::updateTime, args -> new Time(getNumber(args[1]).longValue())));
        set("updateTimestamp", updateData(rs::updateTimestamp, rs::updateTimestamp, args -> new Timestamp(getNumber(args[1]).longValue())));
    }

    private Value findColumn(Value[] args) {
        try {
            return NumberValue.of(rs.findColumn(args[0].asString()));
        } catch (SQLException sqlex) {
            throw new OwnLangRuntimeException(sqlex);
        }
    }

    private Value updateNull(Value[] args) {
        Arguments.check(1, args.length);
        try {
            if (args[0].type() == Types.NUMBER) {
                rs.updateNull(args[0].asInt());
            }
            rs.updateNull(args[0].asString());
            return NumberValue.ONE;
        } catch (SQLException sqlex) {
            throw new OwnLangRuntimeException(sqlex);
        }
    }

    static <T> Value getObjectResult(ObjectColumnResultInt<T> numberResult,
                                     ObjectColumnResultString<T> stringResult,
                                     Function<T, Value> converter) {
        return new FunctionValue(args -> {
            Arguments.check(1, args.length);
            try {
                if (args[0].type() == Types.NUMBER) {
                    return converter.apply(numberResult.get(args[0].asInt()));
                }
                return converter.apply(stringResult.get(args[0].asString()));
            } catch (SQLException sqlex) {
                throw new OwnLangRuntimeException(sqlex);
            }
        });
    }

    static <T> Value getObjectResult(ObjectColumnResultInt<T> numberResult,
                                     ObjectColumnResultString<T> stringResult,
                                     BiFunction<T, Value[], Value> converter) {
        return new FunctionValue(args -> {
            Arguments.checkAtLeast(1, args.length);
            try {
                if (args[0].type() == Types.NUMBER) {
                    return converter.apply(numberResult.get(args[0].asInt()), args);
                }
                return converter.apply(stringResult.get(args[0].asString()), args);
            } catch (SQLException sqlex) {
                throw new OwnLangRuntimeException(sqlex);
            }
        });
    }

    interface ObjectColumnResultInt<T> {
        T get(int columnIndex) throws SQLException;
    }

    interface ObjectColumnResultString<T> {
        T get(String columnName) throws SQLException;
    }
}
