package com.annimon.ownlang.modules.jdbc;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.Variables;
import com.annimon.ownlang.modules.Module;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author aNNiMON
 */
public final class jdbc implements Module {

    public static void initConstants() {
        Variables.define("TRANSACTION_NONE", NumberValue.of(Connection.TRANSACTION_NONE));
        Variables.define("TRANSACTION_READ_COMMITTED", NumberValue.of(Connection.TRANSACTION_READ_COMMITTED));
        Variables.define("TRANSACTION_READ_UNCOMMITTED", NumberValue.of(Connection.TRANSACTION_READ_UNCOMMITTED));
        Variables.define("TRANSACTION_REPEATABLE_READ", NumberValue.of(Connection.TRANSACTION_REPEATABLE_READ));
        Variables.define("TRANSACTION_SERIALIZABLE", NumberValue.of(Connection.TRANSACTION_SERIALIZABLE));

        Variables.define("CLOSE_ALL_RESULTS", NumberValue.of(Statement.CLOSE_ALL_RESULTS));
        Variables.define("CLOSE_CURRENT_RESULT", NumberValue.of(Statement.CLOSE_CURRENT_RESULT));
        Variables.define("EXECUTE_FAILED", NumberValue.of(Statement.EXECUTE_FAILED));
        Variables.define("KEEP_CURRENT_RESULT", NumberValue.of(Statement.KEEP_CURRENT_RESULT));
        Variables.define("NO_GENERATED_KEYS", NumberValue.of(Statement.NO_GENERATED_KEYS));
        Variables.define("RETURN_GENERATED_KEYS", NumberValue.of(Statement.RETURN_GENERATED_KEYS));
        Variables.define("SUCCESS_NO_INFO", NumberValue.of(Statement.SUCCESS_NO_INFO));

        Variables.define("CLOSE_CURSORS_AT_COMMIT", NumberValue.of(ResultSet.CLOSE_CURSORS_AT_COMMIT));
        Variables.define("CONCUR_READ_ONLY", NumberValue.of(ResultSet.CONCUR_READ_ONLY));
        Variables.define("CONCUR_UPDATABLE", NumberValue.of(ResultSet.CONCUR_UPDATABLE));
        Variables.define("FETCH_FORWARD", NumberValue.of(ResultSet.FETCH_FORWARD));
        Variables.define("FETCH_REVERSE", NumberValue.of(ResultSet.FETCH_REVERSE));
        Variables.define("FETCH_UNKNOWN", NumberValue.of(ResultSet.FETCH_UNKNOWN));
        Variables.define("HOLD_CURSORS_OVER_COMMIT", NumberValue.of(ResultSet.HOLD_CURSORS_OVER_COMMIT));
        Variables.define("TYPE_FORWARD_ONLY", NumberValue.of(ResultSet.TYPE_FORWARD_ONLY));
        Variables.define("TYPE_SCROLL_INSENSITIVE", NumberValue.of(ResultSet.TYPE_SCROLL_INSENSITIVE));
        Variables.define("TYPE_SCROLL_SENSITIVE", NumberValue.of(ResultSet.TYPE_SCROLL_SENSITIVE));
    }

    @Override
    public void init() {
        initConstants();
        Functions.set("getConnection", getConnectionFunction());
        Functions.set("sqlite", getConnectionFunction("jdbc:sqlite:"));
        Functions.set("mysql", getConnectionFunction("jdbc:", () -> Class.forName("com.mysql.jdbc.Driver")));
    }

    private static com.annimon.ownlang.lib.Function getConnectionFunction() {
        return getConnectionFunction("", null);
    }

    private static com.annimon.ownlang.lib.Function getConnectionFunction(String connectionPrefix) {
        return getConnectionFunction(connectionPrefix, null);
    }

    private static com.annimon.ownlang.lib.Function getConnectionFunction(String connectionPrefix, ThrowableRunnable preAction) {
        return (args) -> {
            try {
                if (preAction != null) {
                    preAction.run();
                }
                switch (args.length) {
                    case 1:
                        return new ConnectionValue(getConnection(connectionPrefix + args[0].asString()));
                    case 2:
                        Class.forName(args[1].asString());
                        return new ConnectionValue(getConnection(connectionPrefix + args[0].asString()));
                    case 3: {
                        final String url = connectionPrefix + args[0].asString();
                        return new ConnectionValue(getConnection(url, args[1].asString(), args[2].asString()));
                    }
                    case 4: {
                        Class.forName(args[3].asString());
                        String url = connectionPrefix + args[0].asString();
                        return new ConnectionValue(getConnection(url, args[1].asString(), args[2].asString()));
                    }
                    default:
                        throw new ArgumentsMismatchException("Wrong number of arguments");
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    private static Connection getConnection(String url) throws SQLException {
        return DriverManager.getConnection(url);
    }

    private static Connection getConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    //<editor-fold defaultstate="collapsed" desc="Values">
    private static class ConnectionValue extends MapValue {

        private final Connection connection;

        public ConnectionValue(Connection connection) {
            super(20);
            this.connection = connection;
            init();
        }

        private void init() {
            set("createStatement", new FunctionValue(this::createStatement));
            set("prepareStatement", new FunctionValue(this::prepareStatement));
            set("close", new FunctionValue(this::close));

            set("clearWarnings", voidFunction(connection::clearWarnings));
            set("close", voidFunction(connection::close));
            set("commit", voidFunction(connection::commit));
            set("rollback", voidFunction(connection::rollback));

            set("setHoldability", voidIntFunction(connection::setHoldability));
            set("setTransactionIsolation", voidIntFunction(connection::setTransactionIsolation));

            set("getAutoCommit", booleanFunction(connection::getAutoCommit));
            set("isClosed", booleanFunction(connection::isClosed));
            set("isReadOnly", booleanFunction(connection::isReadOnly));

            set("getHoldability", intFunction(connection::getHoldability));
            set("getNetworkTimeout", intFunction(connection::getNetworkTimeout));
            set("getTransactionIsolation", intFunction(connection::getTransactionIsolation));
            set("getUpdateCount", intFunction(connection::getHoldability));

            set("getCatalog", stringFunction(connection::getCatalog));
            set("getSchema", stringFunction(connection::getSchema));
        }

        private Value createStatement(Value... args) {
            try {
                switch (args.length) {
                    case 0:
                        return new StatementValue(connection.createStatement());
                    case 2:
                        return new StatementValue(connection.createStatement(args[0].asInt(), args[1].asInt()));
                    case 3:
                        return new StatementValue(connection.createStatement(args[0].asInt(), args[1].asInt(), args[2].asInt()));
                    default:
                        throw new ArgumentsMismatchException("Wrong number of arguments");
                }
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        }

        private Value prepareStatement(Value... args) {
            Arguments.checkRange(1, 4, args.length);
            try {
                final String sql = args[0].asString();
                switch (args.length) {
                    case 1:
                        return new StatementValue(connection.prepareStatement(sql));
                    case 2:
                        final PreparedStatement ps = columnData(args[1],
                                (int autogeneratedKeys) -> connection.prepareStatement(sql, autogeneratedKeys),
                                (int[] columnIndices) -> connection.prepareStatement(sql, columnIndices),
                                (String[] columnNames) -> connection.prepareStatement(sql, columnNames));
                        return new StatementValue(ps);
                    case 3:
                        return new StatementValue(connection.prepareStatement(sql, args[1].asInt(), args[2].asInt()));
                    case 4:
                        return new StatementValue(connection.prepareStatement(sql, args[1].asInt(), args[2].asInt(), args[3].asInt()));
                    default:
                        throw new ArgumentsMismatchException("Wrong number of arguments");
                }
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        }

        private Value close(Value... args) {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException sqlex) {
                // skip
            }
            return NumberValue.ZERO;
        }
    }

    private static class StatementValue extends MapValue {

        private final Statement statement;
        private final PreparedStatement ps;

        public StatementValue(Statement statement) {
            super(55);
            this.statement = statement;
            if (statement instanceof PreparedStatement) {
                ps = (PreparedStatement) statement;
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

            set("setCursorName", updateData(statement::setCursorName, (args) -> args[0].asString()));
            set("setEscapeProcessing", updateData(statement::setEscapeProcessing, (args) -> args[0].asInt() != 0));
            set("setLargeMaxRows", updateData(statement::setLargeMaxRows, (args) -> getNumber(args[0]).longValue()));
            set("setPoolable", updateData(statement::setPoolable, (args) -> args[0].asInt() != 0));

            set("getResultSet", objectFunction(statement::getResultSet, ResultSetValue::new));
            set("getGeneratedKeys", objectFunction(statement::getGeneratedKeys, ResultSetValue::new));
            set("executeBatch", objectFunction(statement::executeBatch, jdbc::intArrayToValue));
            set("executeLargeBatch", objectFunction(statement::executeLargeBatch, jdbc::longArrayToValue));

            if (ps != null) {
                set("clearParameters", voidFunction(ps::clearParameters));

                set("setBigDecimal", updateData(ps::setBigDecimal, (args) -> new BigDecimal(args[1].asString())));
                set("setBoolean", updateData(ps::setBoolean, (args) -> args[1].asInt() != 0));
                set("setByte", updateData(ps::setByte, (args) -> getNumber(args[1]).byteValue()));
                set("setBytes", updateData(ps::setBytes, (args) -> valueToByteArray(args[1])));
                set("setDate", updateData(ps::setDate, (args) -> new Date(getNumber(args[1]).longValue())));
                set("setDouble", updateData(ps::setDouble, (args) -> getNumber(args[1]).doubleValue()));
                set("setFloat", updateData(ps::setFloat, (args) -> getNumber(args[1]).floatValue()));
                set("setInt", updateData(ps::setInt, (args) -> args[1].asInt()));
                set("setLong", updateData(ps::setLong, (args) -> getNumber(args[1]).longValue()));
                set("setNString", updateData(ps::setNString, (args) -> args[1].asString()));
                set("setNull", updateData(ps::setNull, (args) -> args[1].asInt()));
                set("setShort", updateData(ps::setShort, (args) -> getNumber(args[1]).shortValue()));
                set("setString", updateData(ps::setString, (args) -> args[1].asString()));
                set("setTime", updateData(ps::setTime, (args) -> new Time(getNumber(args[1]).longValue())));
                set("setTimestamp", updateData(ps::setTimestamp, (args) -> new Timestamp(getNumber(args[1]).longValue())));
                set("setURL", updateData(ps::setURL, (args) -> {
                    try {
                        return new URL(args[1].asString());
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                }));
            }
        }

        private Value addBatch(Value... args) {
            if (ps != null) Arguments.checkOrOr(0, 1, args.length);
            else Arguments.check(1, args.length);
            try {
                if (args.length == 0 && ps != null) ps.addBatch();
                else statement.addBatch(args[0].asString());
                return NumberValue.ONE;
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        }

        private Value execute(Value... args) {
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
                throw new RuntimeException(sqlex);
            }
        }

        private Value executeQuery(Value... args) {
            if (ps != null) Arguments.checkOrOr(0, 1, args.length);
            else Arguments.check(1, args.length);
            try {
                if (args.length == 0 && ps != null) return new ResultSetValue(ps.executeQuery());
                return new ResultSetValue(statement.executeQuery(args[0].asString()));
            } catch (SQLException sqlex) {
                return NumberValue.ZERO;
            }
        }

        private Value executeUpdate(Value... args) {
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
                throw new RuntimeException(sqlex);
            }
        }

        private Value executeLargeUpdate(Value... args) {
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
                throw new RuntimeException(sqlex);
            }
        }
    }

    private static class ResultSetValue extends MapValue {

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
            set("getArray", getObjectResult(rs::getArray, rs::getArray, jdbc::arrayToResultSetValue));
            set("getBigDecimal", getObjectResult(rs::getBigDecimal, rs::getBigDecimal, (bd) -> new StringValue(bd.toString())));
            set("getBoolean", getBooleanResult(rs::getBoolean, rs::getBoolean));
            set("getByte", getNumberResult(rs::getByte, rs::getByte));
            set("getBytes", getObjectResult(rs::getBytes, rs::getBytes, (bytes) -> ArrayValue.of(bytes)));
            set("getDate", getObjectResult(rs::getDate, rs::getDate, (date) -> NumberValue.of(date.getTime())));
            set("getDouble", getNumberResult(rs::getDouble, rs::getDouble));
            set("getFloat", getNumberResult(rs::getFloat, rs::getFloat));
            set("getInt", getNumberResult(rs::getInt, rs::getInt));
            set("getLong", getNumberResult(rs::getLong, rs::getLong));
            set("getNString", getStringResult(rs::getNString, rs::getNString));
            set("getRowId", getObjectResult(rs::getRowId, rs::getRowId, (rowid) -> ArrayValue.of(rowid.getBytes())));
            set("getShort", getNumberResult(rs::getShort, rs::getShort));
            set("getString", getStringResult(rs::getString, rs::getString));
            set("getTime", getObjectResult(rs::getTime, rs::getTime, (time) -> NumberValue.of(time.getTime())));
            set("getTimestamp", getObjectResult(rs::getTimestamp, rs::getTimestamp, (timestamp) -> NumberValue.of(timestamp.getTime())));
            set("getURL", getObjectResult(rs::getURL, rs::getURL, (url) -> new StringValue(url.toExternalForm())));

            // Update
            set("updateNull", new FunctionValue(this::updateNull));
            set("updateBigDecimal", updateData(rs::updateBigDecimal, rs::updateBigDecimal, (args) -> new BigDecimal(args[1].asString())));
            set("updateBoolean", updateData(rs::updateBoolean, rs::updateBoolean, (args) -> args[1].asInt() != 0));
            set("updateByte", updateData(rs::updateByte, rs::updateByte, (args) -> getNumber(args[1]).byteValue()));
            set("updateBytes", updateData(rs::updateBytes, rs::updateBytes, (args) -> valueToByteArray(args[1])));
            set("updateDate", updateData(rs::updateDate, rs::updateDate, (args) -> new Date(getNumber(args[1]).longValue())));
            set("updateDouble", updateData(rs::updateDouble, rs::updateDouble, (args) -> getNumber(args[1]).doubleValue()));
            set("updateFloat", updateData(rs::updateFloat, rs::updateFloat, (args) -> getNumber(args[1]).floatValue()));
            set("updateInt", updateData(rs::updateInt, rs::updateInt, (args) -> getNumber(args[1]).intValue()));
            set("updateLong", updateData(rs::updateLong, rs::updateLong, (args) -> getNumber(args[1]).longValue()));
            set("updateNString", updateData(rs::updateNString, rs::updateNString, (args) -> args[1].asString()));
            set("updateShort", updateData(rs::updateShort, rs::updateShort, (args) -> getNumber(args[1]).shortValue()));
            set("updateString", updateData(rs::updateString, rs::updateString, (args) -> args[1].asString()));
            set("updateTime", updateData(rs::updateTime, rs::updateTime, (args) -> new Time(getNumber(args[1]).longValue())));
            set("updateTimestamp", updateData(rs::updateTimestamp, rs::updateTimestamp, (args) -> new Timestamp(getNumber(args[1]).longValue())));
        }

        private Value findColumn(Value... args) {
            try {
                return NumberValue.of(rs.findColumn(args[0].asString()));
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        }

        private Value updateNull(Value... args) {
            Arguments.check(1, args.length);
            try {
                if (args[0].type() == Types.NUMBER) {
                    rs.updateNull(args[0].asInt());
                }
                rs.updateNull(args[0].asString());
                return NumberValue.ONE;
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        }
    }
//</editor-fold>

    private static Number getNumber(Value value) {
        if (value.type() == Types.NUMBER) return ((NumberValue) value).raw();
        return value.asInt();
    }

    private static <T> T columnData(Value value, AutogeneratedKeys<T> autogeneratedKeysFunction,
            ColumnIndices<T> columnIndicesFunction, ColumnNames<T> columnNamesFunction) {
        try {
            if (value.type() != Types.ARRAY) {
                return autogeneratedKeysFunction.apply(value.asInt());
            }

            final ArrayValue array = (ArrayValue) value;
            final int size = array.size();
            final boolean isIntArray = (size > 0) && (array.get(0).type() == Types.NUMBER);
            int index = 0;

            if (isIntArray) {
                final int[] columnIndices = new int[size];
                for (Value v : array) {
                    columnIndices[index++] = v.asInt();
                }
                return columnIndicesFunction.apply(columnIndices);
            }

            final String[] columnNames = new String[size];
            for (Value v : array) {
                columnNames[index++] = v.asString();
            }
            return columnNamesFunction.apply(columnNames);

        } catch (SQLException sqlex) {
            throw new RuntimeException(sqlex);
        }
    }

    private interface AutogeneratedKeys<T> {

        T apply(int autogeneratedKeys) throws SQLException;
    }

    private interface ColumnIndices<T> {

        T apply(int[] columnIndices) throws SQLException;
    }

    private interface ColumnNames<T> {

        T apply(String[] columnNames) throws SQLException;
    }

    private static FunctionValue voidFunction(VoidResult result) {
        return new FunctionValue((args) -> {
            try {
                result.execute();
                return NumberValue.ONE;
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }

    private static FunctionValue voidIntFunction(VoidResultInt result) {
        return new FunctionValue((args) -> {
            Arguments.check(1, args.length);
            try {
                result.execute(args[0].asInt());
                return NumberValue.ONE;
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }

    private static FunctionValue booleanFunction(BooleanResult result) {
        return new FunctionValue((args) -> {
            try {
                return NumberValue.fromBoolean(result.get());
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }

    private static Value intFunction(IntResult numberResult) {
        return new FunctionValue((args) -> {
            try {
                return NumberValue.of(numberResult.get());
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }

    private static Value stringFunction(StringResult stringResult) {
        return new FunctionValue((args) -> {
            try {
                return new StringValue(stringResult.get());
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }

    private static <T> Value objectFunction(ObjectResult<T> objectResult, Function<T, Value> converter) {
        return new FunctionValue((args) -> {
            try {
                return converter.apply(objectResult.get());
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }


    private static Value getBooleanResult(BooleanColumnResultInt numberResult, BooleanColumnResultString stringResult) {
        return new FunctionValue((args) -> {
            Arguments.check(1, args.length);
            try {
                if (args[0].type() == Types.NUMBER) {
                    return NumberValue.fromBoolean(numberResult.get(args[0].asInt()));
                }
                return NumberValue.fromBoolean(stringResult.get(args[0].asString()));
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }

    private static Value getNumberResult(NumberColumnResultInt numberResult, NumberColumnResultIntString stringResult) {
        return new FunctionValue((args) -> {
            Arguments.check(1, args.length);
            try {
                if (args[0].type() == Types.NUMBER) {
                    return NumberValue.of(numberResult.get(args[0].asInt()));
                }
                return NumberValue.of(stringResult.get(args[0].asString()));
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }

    private static Value getStringResult(StringColumnResultInt numberResult, StringColumnResultIntString stringResult) {
        return new FunctionValue((args) -> {
            Arguments.check(1, args.length);
            try {
                if (args[0].type() == Types.NUMBER) {
                    return new StringValue(numberResult.get(args[0].asInt()));
                }
                return new StringValue(stringResult.get(args[0].asString()));
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }

    private static <T> Value getObjectResult(ObjectColumnResultInt<T> numberResult, ObjectColumnResultString<T> stringResult,
            Function<T, Value> converter) {
        return new FunctionValue((args) -> {
            Arguments.check(1, args.length);
            try {
                if (args[0].type() == Types.NUMBER) {
                    return converter.apply(numberResult.get(args[0].asInt()));
                }
                return converter.apply(stringResult.get(args[0].asString()));
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }

    private static <T> Value getObjectResult(ObjectColumnResultInt<T> numberResult, ObjectColumnResultString<T> stringResult,
            BiFunction<T, Value[], Value> converter) {
        return new FunctionValue((args) -> {
            Arguments.checkAtLeast(1, args.length);
            try {
                if (args[0].type() == Types.NUMBER) {
                    return converter.apply(numberResult.get(args[0].asInt()), args);
                }
                return converter.apply(stringResult.get(args[0].asString()), args);
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }

    private static <T> Value updateData(VoidResultObject<T> result, Function<Value[], T> converter) {
        return new FunctionValue((args) -> {
            Arguments.checkAtLeast(2, args.length);
            try {
                result.execute(converter.apply(args));
                return NumberValue.ONE;
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }

    private static <T> Value updateData(VoidColumnResultIntObject<T> numberResult, Function<Value[], T> converter) {
        return new FunctionValue((args) -> {
            Arguments.checkAtLeast(2, args.length);
            try {
                numberResult.execute(args[0].asInt(), converter.apply(args));
                return NumberValue.ONE;
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }

    private static <T> Value updateData(VoidColumnResultIntObject<T> numberResult, VoidColumnResultStringObject<T> stringResult,
            Function<Value[], T> converter) {
        return new FunctionValue((args) -> {
            Arguments.checkAtLeast(2, args.length);
            try {
                if (args[0].type() == Types.NUMBER) {
                    numberResult.execute(args[0].asInt(), converter.apply(args));
                } else {
                    stringResult.execute(args[0].asString(), converter.apply(args));
                }
                return NumberValue.ONE;
            } catch (SQLException sqlex) {
                throw new RuntimeException(sqlex);
            }
        });
    }

    private static Value arrayToResultSetValue(Array array, Value[] args) {
        try {
            final ResultSet result;
            switch (args.length) {
                case 1:
                    // column
                    result = array.getResultSet();
                    break;
                case 3:
                    // column, index, count
                    long index = (args[1].type() == Types.NUMBER) ? ((NumberValue) args[1]).asLong() : args[1].asInt();
                    result = array.getResultSet(index, args[2].asInt());
                    break;
                default:
                    throw new ArgumentsMismatchException("Wrong number of arguments");
            }
            return new ResultSetValue(result);
        } catch (SQLException sqlex) {
            throw new RuntimeException(sqlex);
        }
    }

    private static byte[] valueToByteArray(Value value) {
        if (value.type() != Types.ARRAY) {
            return new byte[0];
        }
        final ArrayValue array = (ArrayValue) value;
        final int size = array.size();
        final byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = getNumber(array.get(i)).byteValue();
        }
        return result;
    }

    private static Value intArrayToValue(int[] array) {
        final ArrayValue result = new ArrayValue(array.length);
        for (int i = 0; i < array.length; i++) {
            result.set(i, NumberValue.of(array[i]));
        }
        return result;
    }

    private static Value longArrayToValue(long[] array) {
        final ArrayValue result = new ArrayValue(array.length);
        for (int i = 0; i < array.length; i++) {
            result.set(i, NumberValue.of(array[i]));
        }
        return result;
    }

    private interface ThrowableRunnable {
        void run() throws Exception;
    }

    private interface VoidResult {
        void execute() throws SQLException;
    }

    private interface VoidResultInt {
        void execute(int index) throws SQLException;
    }

    private interface VoidResultObject<T> {
        void execute(T data) throws SQLException;
    }

    private interface BooleanResult {
        boolean get() throws SQLException;
    }

    private interface IntResult {
        int get() throws SQLException;
    }

    private interface StringResult {
        String get() throws SQLException;
    }

    private interface ObjectResult<T> {
        T get() throws SQLException;
    }

    private interface BooleanColumnResultInt {
        boolean get(int columnIndex) throws SQLException;
    }

    private interface BooleanColumnResultString {
        boolean get(String columnName) throws SQLException;
    }

    private interface NumberColumnResultInt {
        Number get(int columnIndex) throws SQLException;
    }

    private interface NumberColumnResultIntString {
        Number get(String columnName) throws SQLException;
    }

    private interface StringColumnResultInt {
        String get(int columnIndex) throws SQLException;
    }

    private interface StringColumnResultIntString {
        String get(String columnName) throws SQLException;
    }

    private interface ObjectColumnResultInt<T> {
        T get(int columnIndex) throws SQLException;
    }

    private interface ObjectColumnResultString<T> {
        T get(String columnName) throws SQLException;
    }

    private interface VoidColumnResultIntObject<T> {
        void execute(int columnIndex, T value) throws SQLException;
    }

    private interface VoidColumnResultStringObject<T> {
        void execute(String columnName, T value) throws SQLException;
    }
}
