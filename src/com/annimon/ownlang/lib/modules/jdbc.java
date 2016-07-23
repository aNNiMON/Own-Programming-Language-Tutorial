package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.annotations.ConstantInitializer;
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
@ConstantInitializer
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
            set(new StringValue("createStatement"), new FunctionValue(this::createStatement));
            set(new StringValue("prepareStatement"), new FunctionValue(this::prepareStatement));
            set(new StringValue("close"), new FunctionValue(this::close));

            set(new StringValue("clearWarnings"), voidFunction(connection::clearWarnings));
            set(new StringValue("close"), voidFunction(connection::close));
            set(new StringValue("commit"), voidFunction(connection::commit));
            set(new StringValue("rollback"), voidFunction(connection::rollback));

            set(new StringValue("setHoldability"), voidIntFunction(connection::setHoldability));
            set(new StringValue("setTransactionIsolation"), voidIntFunction(connection::setTransactionIsolation));

            set(new StringValue("getAutoCommit"), booleanFunction(connection::getAutoCommit));
            set(new StringValue("isClosed"), booleanFunction(connection::isClosed));
            set(new StringValue("isReadOnly"), booleanFunction(connection::isReadOnly));

            set(new StringValue("getHoldability"), intFunction(connection::getHoldability));
            set(new StringValue("getNetworkTimeout"), intFunction(connection::getNetworkTimeout));
            set(new StringValue("getTransactionIsolation"), intFunction(connection::getTransactionIsolation));
            set(new StringValue("getUpdateCount"), intFunction(connection::getHoldability));

            set(new StringValue("getCatalog"), stringFunction(connection::getCatalog));
            set(new StringValue("getSchema"), stringFunction(connection::getSchema));
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
            set(new StringValue("addBatch"), new FunctionValue(this::addBatch));
            set(new StringValue("execute"), new FunctionValue(this::execute));
            set(new StringValue("executeQuery"), new FunctionValue(this::executeQuery));
            set(new StringValue("executeUpdate"), new FunctionValue(this::executeUpdate));
            set(new StringValue("executeLargeUpdate"), new FunctionValue(this::executeLargeUpdate));

            set(new StringValue("cancel"), voidFunction(statement::cancel));
            set(new StringValue("clearBatch"), voidFunction(statement::clearBatch));
            set(new StringValue("clearWarnings"), voidFunction(statement::clearWarnings));
            set(new StringValue("close"), voidFunction(statement::close));
            set(new StringValue("closeOnCompletion"), voidFunction(statement::closeOnCompletion));

            set(new StringValue("setFetchDirection"), voidIntFunction(statement::setFetchDirection));
            set(new StringValue("setFetchSize"), voidIntFunction(statement::setFetchSize));
            set(new StringValue("setMaxFieldSize"), voidIntFunction(statement::setMaxFieldSize));
            set(new StringValue("setMaxRows"), voidIntFunction(statement::setMaxRows));
            set(new StringValue("setQueryTimeout"), voidIntFunction(statement::setQueryTimeout));

            set(new StringValue("getMoreResults"), booleanFunction(statement::getMoreResults));
            set(new StringValue("isCloseOnCompletion"), booleanFunction(statement::isCloseOnCompletion));
            set(new StringValue("isClosed"), booleanFunction(statement::isClosed));
            set(new StringValue("isPoolable"), booleanFunction(statement::isPoolable));

            set(new StringValue("getFetchDirection"), intFunction(statement::getFetchDirection));
            set(new StringValue("getFetchSize"), intFunction(statement::getFetchSize));
            set(new StringValue("getMaxFieldSize"), intFunction(statement::getMaxFieldSize));
            set(new StringValue("getMaxRows"), intFunction(statement::getMaxRows));
            set(new StringValue("getQueryTimeout"), intFunction(statement::getQueryTimeout));
            set(new StringValue("getResultSetConcurrency"), intFunction(statement::getResultSetConcurrency));
            set(new StringValue("getResultSetHoldability"), intFunction(statement::getResultSetHoldability));
            set(new StringValue("getResultSetType"), intFunction(statement::getResultSetType));
            set(new StringValue("getUpdateCount"), intFunction(statement::getUpdateCount));

            set(new StringValue("setCursorName"), updateData(statement::setCursorName, (args) -> args[0].asString()));
            set(new StringValue("setEscapeProcessing"), updateData(statement::setEscapeProcessing, (args) -> args[0].asInt() != 0));
            set(new StringValue("setLargeMaxRows"), updateData(statement::setLargeMaxRows, (args) -> getNumber(args[0]).longValue()));
            set(new StringValue("setPoolable"), updateData(statement::setPoolable, (args) -> args[0].asInt() != 0));

            set(new StringValue("getResultSet"), objectFunction(statement::getResultSet, ResultSetValue::new));
            set(new StringValue("getGeneratedKeys"), objectFunction(statement::getGeneratedKeys, ResultSetValue::new));
            set(new StringValue("executeBatch"), objectFunction(statement::executeBatch, jdbc::intArrayToValue));
            set(new StringValue("executeLargeBatch"), objectFunction(statement::executeLargeBatch, jdbc::longArrayToValue));

            if (ps != null) {
                set(new StringValue("clearParameters"), voidFunction(ps::clearParameters));

                set(new StringValue("setBigDecimal"), updateData(ps::setBigDecimal, (args) -> new BigDecimal(args[1].asString())));
                set(new StringValue("setBoolean"), updateData(ps::setBoolean, (args) -> args[1].asInt() != 0));
                set(new StringValue("setByte"), updateData(ps::setByte, (args) -> getNumber(args[1]).byteValue()));
                set(new StringValue("setBytes"), updateData(ps::setBytes, (args) -> valueToByteArray(args[1])));
                set(new StringValue("setDate"), updateData(ps::setDate, (args) -> new Date(getNumber(args[1]).longValue())));
                set(new StringValue("setDouble"), updateData(ps::setDouble, (args) -> getNumber(args[1]).doubleValue()));
                set(new StringValue("setFloat"), updateData(ps::setFloat, (args) -> getNumber(args[1]).floatValue()));
                set(new StringValue("setInt"), updateData(ps::setInt, (args) -> args[1].asInt()));
                set(new StringValue("setLong"), updateData(ps::setLong, (args) -> getNumber(args[1]).longValue()));
                set(new StringValue("setNString"), updateData(ps::setNString, (args) -> args[1].asString()));
                set(new StringValue("setNull"), updateData(ps::setNull, (args) -> args[1].asInt()));
                set(new StringValue("setShort"), updateData(ps::setShort, (args) -> getNumber(args[1]).shortValue()));
                set(new StringValue("setString"), updateData(ps::setString, (args) -> args[1].asString()));
                set(new StringValue("setTime"), updateData(ps::setTime, (args) -> new Time(getNumber(args[1]).longValue())));
                set(new StringValue("setTimestamp"), updateData(ps::setTimestamp, (args) -> new Timestamp(getNumber(args[1]).longValue())));
                set(new StringValue("setURL"), updateData(ps::setURL, (args) -> {
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
            set(new StringValue("findColumn"), new FunctionValue(this::findColumn));

            set(new StringValue("afterLast"), voidFunction(rs::afterLast));
            set(new StringValue("beforeFirst"), voidFunction(rs::beforeFirst));
            set(new StringValue("cancelRowUpdates"), voidFunction(rs::cancelRowUpdates));
            set(new StringValue("clearWarnings"), voidFunction(rs::clearWarnings));
            set(new StringValue("close"), voidFunction(rs::close));
            set(new StringValue("deleteRow"), voidFunction(rs::deleteRow));
            set(new StringValue("insertRow"), voidFunction(rs::insertRow));
            set(new StringValue("moveToCurrentRow"), voidFunction(rs::moveToCurrentRow));
            set(new StringValue("moveToInsertRow"), voidFunction(rs::moveToInsertRow));
            set(new StringValue("refreshRow"), voidFunction(rs::refreshRow));
            set(new StringValue("updateRow"), voidFunction(rs::updateRow));

            set(new StringValue("absolute"), voidIntFunction(rs::absolute));
            set(new StringValue("relative"), voidIntFunction(rs::relative));
            set(new StringValue("setFetchDirection"), voidIntFunction(rs::setFetchDirection));
            set(new StringValue("setFetchSize"), voidIntFunction(rs::setFetchSize));

            set(new StringValue("first"), booleanFunction(rs::first));
            set(new StringValue("isAfterLast"), booleanFunction(rs::isAfterLast));
            set(new StringValue("isBeforeFirst"), booleanFunction(rs::isBeforeFirst));
            set(new StringValue("isClosed"), booleanFunction(rs::isClosed));
            set(new StringValue("isFirst"), booleanFunction(rs::isFirst));
            set(new StringValue("isLast"), booleanFunction(rs::isLast));
            set(new StringValue("last"), booleanFunction(rs::last));
            set(new StringValue("next"), booleanFunction(rs::next));
            set(new StringValue("previous"), booleanFunction(rs::previous));
            set(new StringValue("rowDeleted"), booleanFunction(rs::rowDeleted));
            set(new StringValue("rowInserted"), booleanFunction(rs::rowInserted));
            set(new StringValue("rowUpdated"), booleanFunction(rs::rowUpdated));
            set(new StringValue("wasNull"), booleanFunction(rs::wasNull));

            set(new StringValue("getConcurrency"), intFunction(rs::getConcurrency));
            set(new StringValue("getFetchDirection"), intFunction(rs::getFetchDirection));
            set(new StringValue("getFetchSize"), intFunction(rs::getFetchSize));
            set(new StringValue("getHoldability"), intFunction(rs::getHoldability));
            set(new StringValue("getRow"), intFunction(rs::getRow));
            set(new StringValue("getType"), intFunction(rs::getType));

            set(new StringValue("getCursorName"), stringFunction(rs::getCursorName));
            set(new StringValue("getStatement"), objectFunction(rs::getStatement, StatementValue::new));

            // Results
            set(new StringValue("getArray"), getObjectResult(rs::getArray, rs::getArray, jdbc::arrayToResultSetValue));
            set(new StringValue("getBigDecimal"), getObjectResult(rs::getBigDecimal, rs::getBigDecimal, (bd) -> new StringValue(bd.toString())));
            set(new StringValue("getBoolean"), getBooleanResult(rs::getBoolean, rs::getBoolean));
            set(new StringValue("getByte"), getNumberResult(rs::getByte, rs::getByte));
            set(new StringValue("getBytes"), getObjectResult(rs::getBytes, rs::getBytes, (bytes) -> ArrayValue.of(bytes)));
            set(new StringValue("getDate"), getObjectResult(rs::getDate, rs::getDate, (date) -> NumberValue.of(date.getTime())));
            set(new StringValue("getDouble"), getNumberResult(rs::getDouble, rs::getDouble));
            set(new StringValue("getFloat"), getNumberResult(rs::getFloat, rs::getFloat));
            set(new StringValue("getInt"), getNumberResult(rs::getInt, rs::getInt));
            set(new StringValue("getLong"), getNumberResult(rs::getLong, rs::getLong));
            set(new StringValue("getNString"), getStringResult(rs::getNString, rs::getNString));
            set(new StringValue("getRowId"), getObjectResult(rs::getRowId, rs::getRowId, (rowid) -> ArrayValue.of(rowid.getBytes())));
            set(new StringValue("getShort"), getNumberResult(rs::getShort, rs::getShort));
            set(new StringValue("getString"), getStringResult(rs::getString, rs::getString));
            set(new StringValue("getTime"), getObjectResult(rs::getTime, rs::getTime, (time) -> NumberValue.of(time.getTime())));
            set(new StringValue("getTimestamp"), getObjectResult(rs::getTimestamp, rs::getTimestamp, (timestamp) -> NumberValue.of(timestamp.getTime())));
            set(new StringValue("getURL"), getObjectResult(rs::getURL, rs::getURL, (url) -> new StringValue(url.toExternalForm())));

            // Update
            set(new StringValue("updateNull"), new FunctionValue(this::updateNull));
            set(new StringValue("updateBigDecimal"), updateData(rs::updateBigDecimal, rs::updateBigDecimal, (args) -> new BigDecimal(args[1].asString())));
            set(new StringValue("updateBoolean"), updateData(rs::updateBoolean, rs::updateBoolean, (args) -> args[1].asInt() != 0));
            set(new StringValue("updateByte"), updateData(rs::updateByte, rs::updateByte, (args) -> getNumber(args[1]).byteValue()));
            set(new StringValue("updateBytes"), updateData(rs::updateBytes, rs::updateBytes, (args) -> valueToByteArray(args[1])));
            set(new StringValue("updateDate"), updateData(rs::updateDate, rs::updateDate, (args) -> new Date(getNumber(args[1]).longValue())));
            set(new StringValue("updateDouble"), updateData(rs::updateDouble, rs::updateDouble, (args) -> getNumber(args[1]).doubleValue()));
            set(new StringValue("updateFloat"), updateData(rs::updateFloat, rs::updateFloat, (args) -> getNumber(args[1]).floatValue()));
            set(new StringValue("updateInt"), updateData(rs::updateInt, rs::updateInt, (args) -> getNumber(args[1]).intValue()));
            set(new StringValue("updateLong"), updateData(rs::updateLong, rs::updateLong, (args) -> getNumber(args[1]).longValue()));
            set(new StringValue("updateNString"), updateData(rs::updateNString, rs::updateNString, (args) -> args[1].asString()));
            set(new StringValue("updateShort"), updateData(rs::updateShort, rs::updateShort, (args) -> getNumber(args[1]).shortValue()));
            set(new StringValue("updateString"), updateData(rs::updateString, rs::updateString, (args) -> args[1].asString()));
            set(new StringValue("updateTime"), updateData(rs::updateTime, rs::updateTime, (args) -> new Time(getNumber(args[1]).longValue())));
            set(new StringValue("updateTimestamp"), updateData(rs::updateTimestamp, rs::updateTimestamp, (args) -> new Timestamp(getNumber(args[1]).longValue())));
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
