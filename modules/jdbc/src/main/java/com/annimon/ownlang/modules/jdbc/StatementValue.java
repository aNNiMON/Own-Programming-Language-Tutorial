package com.annimon.ownlang.modules.jdbc;

import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.lib.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import static com.annimon.ownlang.lib.ValueUtils.getNumber;
import static com.annimon.ownlang.modules.jdbc.JdbcConverters.*;

class StatementValue extends MapValue {

    private final Statement statement;
    private final PreparedStatement ps;

    public StatementValue(Statement statement) {
        super(55);
        this.statement = statement;
        if (statement instanceof PreparedStatement pss) {
            ps = pss;
        } else {
            ps = null;
        }
        init();
    }

    private void init() {
        set("addBatch", new FunctionValue(this::addBatch));
        set("execute", new FunctionValue(this::execute));
        set("executeQuery", new FunctionValue(this::executeQuery));
        set("executeUpdate", new FunctionValue(this::executeUpdate));
        set("executeLargeUpdate", new FunctionValue(this::executeLargeUpdate));

        set("cancel", voidFunction(statement::cancel));
        set("clearBatch", voidFunction(statement::clearBatch));
        set("clearWarnings", voidFunction(statement::clearWarnings));
        set("close", voidFunction(statement::close));
        set("closeOnCompletion", voidFunction(statement::closeOnCompletion));

        set("setFetchDirection", voidIntFunction(statement::setFetchDirection));
        set("setFetchSize", voidIntFunction(statement::setFetchSize));
        set("setMaxFieldSize", voidIntFunction(statement::setMaxFieldSize));
        set("setMaxRows", voidIntFunction(statement::setMaxRows));
        set("setQueryTimeout", voidIntFunction(statement::setQueryTimeout));

        set("getMoreResults", booleanFunction(statement::getMoreResults));
        set("isCloseOnCompletion", booleanFunction(statement::isCloseOnCompletion));
        set("isClosed", booleanFunction(statement::isClosed));
        set("isPoolable", booleanFunction(statement::isPoolable));

        set("getFetchDirection", intFunction(statement::getFetchDirection));
        set("getFetchSize", intFunction(statement::getFetchSize));
        set("getMaxFieldSize", intFunction(statement::getMaxFieldSize));
        set("getMaxRows", intFunction(statement::getMaxRows));
        set("getQueryTimeout", intFunction(statement::getQueryTimeout));
        set("getResultSetConcurrency", intFunction(statement::getResultSetConcurrency));
        set("getResultSetHoldability", intFunction(statement::getResultSetHoldability));
        set("getResultSetType", intFunction(statement::getResultSetType));
        set("getUpdateCount", intFunction(statement::getUpdateCount));

        set("setCursorName", updateData(statement::setCursorName, args -> args[0].asString()));
        set("setEscapeProcessing", updateData(statement::setEscapeProcessing, args -> args[0].asInt() != 0));
        set("setLargeMaxRows", updateData(statement::setLargeMaxRows, args -> getNumber(args[0]).longValue()));
        set("setPoolable", updateData(statement::setPoolable, args -> args[0].asInt() != 0));

        set("getResultSet", objectFunction(statement::getResultSet, ResultSetValue::new));
        set("getGeneratedKeys", objectFunction(statement::getGeneratedKeys, ResultSetValue::new));
        set("executeBatch", objectFunction(statement::executeBatch, JdbcConverters::intArrayToValue));
        set("executeLargeBatch", objectFunction(statement::executeLargeBatch, JdbcConverters::longArrayToValue));

        if (ps != null) {
            set("clearParameters", voidFunction(ps::clearParameters));

            set("setBigDecimal", updateData(ps::setBigDecimal, args -> new BigDecimal(args[1].asString())));
            set("setBoolean", updateData(ps::setBoolean, args -> args[1].asInt() != 0));
            set("setByte", updateData(ps::setByte, args -> getNumber(args[1]).byteValue()));
            set("setBytes", updateData(ps::setBytes, args -> valueToByteArray(args[1])));
            set("setDate", updateData(ps::setDate, args -> new Date(getNumber(args[1]).longValue())));
            set("setDouble", updateData(ps::setDouble, args -> getNumber(args[1]).doubleValue()));
            set("setFloat", updateData(ps::setFloat, args -> getNumber(args[1]).floatValue()));
            set("setInt", updateData(ps::setInt, args -> args[1].asInt()));
            set("setLong", updateData(ps::setLong, args -> getNumber(args[1]).longValue()));
            set("setNString", updateData(ps::setNString, args -> args[1].asString()));
            set("setNull", updateData(ps::setNull, args -> args[1].asInt()));
            set("setShort", updateData(ps::setShort, args -> getNumber(args[1]).shortValue()));
            set("setString", updateData(ps::setString, args -> args[1].asString()));
            set("setTime", updateData(ps::setTime, args -> new Time(getNumber(args[1]).longValue())));
            set("setTimestamp", updateData(ps::setTimestamp, args -> new Timestamp(getNumber(args[1]).longValue())));
            set("setURL", updateData(ps::setURL, args -> {
                try {
                    return new URL(args[1].asString());
                } catch (IOException ioe) {
                    throw new OwnLangRuntimeException(ioe);
                }
            }));
        }
    }

    private Value addBatch(Value[] args) {
        if (ps != null) Arguments.checkOrOr(0, 1, args.length);
        else Arguments.check(1, args.length);
        try {
            if (args.length == 0 && ps != null) ps.addBatch();
            else statement.addBatch(args[0].asString());
            return NumberValue.ONE;
        } catch (SQLException sqlex) {
            throw new OwnLangRuntimeException(sqlex);
        }
    }

    private Value execute(Value[] args) {
        if (ps != null) Arguments.checkRange(0, 2, args.length);
        else Arguments.checkOrOr(1, 2, args.length);
        try {
            if (args.length == 0 && ps != null) return NumberValue.fromBoolean(ps.execute());
            final String sql = args[0].asString();
            if (args.length == 1) return NumberValue.fromBoolean(statement.execute(sql));

            boolean result = columnData(args[1],
                    (int autogeneratedKeys) -> statement.execute(sql, autogeneratedKeys),
                    (int[] columnIndices) -> statement.execute(sql, columnIndices),
                    (String[] columnNames) -> statement.execute(sql, columnNames));
            return NumberValue.fromBoolean(result);
        } catch (SQLException sqlex) {
            throw new OwnLangRuntimeException(sqlex);
        }
    }

    private Value executeQuery(Value[] args) {
        if (ps != null) Arguments.checkOrOr(0, 1, args.length);
        else Arguments.check(1, args.length);
        try {
            if (args.length == 0 && ps != null) return new ResultSetValue(ps.executeQuery());
            return new ResultSetValue(statement.executeQuery(args[0].asString()));
        } catch (SQLException sqlex) {
            return NumberValue.ZERO;
        }
    }

    private Value executeUpdate(Value[] args) {
        if (ps != null) Arguments.checkRange(0, 2, args.length);
        else Arguments.checkOrOr(1, 2, args.length);
        try {
            if (args.length == 0 && ps != null) return NumberValue.of(ps.executeUpdate());
            final String sql = args[0].asString();
            if (args.length == 1) return NumberValue.of(statement.executeUpdate(sql));

            int rowCount = columnData(args[1],
                    (int autogeneratedKeys) -> statement.executeUpdate(sql, autogeneratedKeys),
                    (int[] columnIndices) -> statement.executeUpdate(sql, columnIndices),
                    (String[] columnNames) -> statement.executeUpdate(sql, columnNames));
            return NumberValue.of(rowCount);
        } catch (SQLException sqlex) {
            throw new OwnLangRuntimeException(sqlex);
        }
    }

    private Value executeLargeUpdate(Value[] args) {
        if (ps != null) Arguments.checkRange(0, 2, args.length);
        else Arguments.checkOrOr(1, 2, args.length);
        try {
            if (args.length == 0 && ps != null) return NumberValue.of(ps.executeLargeUpdate());
            final String sql = args[0].asString();
            if (args.length == 1) return NumberValue.of(statement.executeLargeUpdate(sql));

            long rowCount = columnData(args[1],
                    (int autogeneratedKeys) -> statement.executeLargeUpdate(sql, autogeneratedKeys),
                    (int[] columnIndices) -> statement.executeLargeUpdate(sql, columnIndices),
                    (String[] columnNames) -> statement.executeLargeUpdate(sql, columnNames));
            return NumberValue.of(rowCount);
        } catch (SQLException sqlex) {
            throw new OwnLangRuntimeException(sqlex);
        }
    }
}
