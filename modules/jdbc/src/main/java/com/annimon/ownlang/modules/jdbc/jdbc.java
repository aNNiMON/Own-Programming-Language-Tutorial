package com.annimon.ownlang.modules.jdbc;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.modules.Module;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class jdbc implements Module {

    @Override
    public Map<String, Value> constants() {
        final var result = new LinkedHashMap<String, Value>(25);
        result.put("TRANSACTION_NONE", NumberValue.of(Connection.TRANSACTION_NONE));
        result.put("TRANSACTION_READ_COMMITTED", NumberValue.of(Connection.TRANSACTION_READ_COMMITTED));
        result.put("TRANSACTION_READ_UNCOMMITTED", NumberValue.of(Connection.TRANSACTION_READ_UNCOMMITTED));
        result.put("TRANSACTION_REPEATABLE_READ", NumberValue.of(Connection.TRANSACTION_REPEATABLE_READ));
        result.put("TRANSACTION_SERIALIZABLE", NumberValue.of(Connection.TRANSACTION_SERIALIZABLE));

        result.put("CLOSE_ALL_RESULTS", NumberValue.of(Statement.CLOSE_ALL_RESULTS));
        result.put("CLOSE_CURRENT_RESULT", NumberValue.of(Statement.CLOSE_CURRENT_RESULT));
        result.put("EXECUTE_FAILED", NumberValue.of(Statement.EXECUTE_FAILED));
        result.put("KEEP_CURRENT_RESULT", NumberValue.of(Statement.KEEP_CURRENT_RESULT));
        result.put("NO_GENERATED_KEYS", NumberValue.of(Statement.NO_GENERATED_KEYS));
        result.put("RETURN_GENERATED_KEYS", NumberValue.of(Statement.RETURN_GENERATED_KEYS));
        result.put("SUCCESS_NO_INFO", NumberValue.of(Statement.SUCCESS_NO_INFO));

        result.put("CLOSE_CURSORS_AT_COMMIT", NumberValue.of(ResultSet.CLOSE_CURSORS_AT_COMMIT));
        result.put("CONCUR_READ_ONLY", NumberValue.of(ResultSet.CONCUR_READ_ONLY));
        result.put("CONCUR_UPDATABLE", NumberValue.of(ResultSet.CONCUR_UPDATABLE));
        result.put("FETCH_FORWARD", NumberValue.of(ResultSet.FETCH_FORWARD));
        result.put("FETCH_REVERSE", NumberValue.of(ResultSet.FETCH_REVERSE));
        result.put("FETCH_UNKNOWN", NumberValue.of(ResultSet.FETCH_UNKNOWN));
        result.put("HOLD_CURSORS_OVER_COMMIT", NumberValue.of(ResultSet.HOLD_CURSORS_OVER_COMMIT));
        result.put("TYPE_FORWARD_ONLY", NumberValue.of(ResultSet.TYPE_FORWARD_ONLY));
        result.put("TYPE_SCROLL_INSENSITIVE", NumberValue.of(ResultSet.TYPE_SCROLL_INSENSITIVE));
        result.put("TYPE_SCROLL_SENSITIVE", NumberValue.of(ResultSet.TYPE_SCROLL_SENSITIVE));
        return result;
    }

    @Override
    public Map<String, com.annimon.ownlang.lib.Function> functions() {
        return Map.of(
                "getConnection", getConnectionFunction(),
                "sqlite", getConnectionFunction("jdbc:sqlite:"),
                "mysql", getConnectionFunction("jdbc:")
        );
    }

    private static com.annimon.ownlang.lib.Function getConnectionFunction() {
        return getConnectionFunction("", null);
    }

    private static com.annimon.ownlang.lib.Function getConnectionFunction(String connectionPrefix) {
        return getConnectionFunction(connectionPrefix, null);
    }

    private static com.annimon.ownlang.lib.Function getConnectionFunction(String connectionPrefix, ThrowableRunnable preAction) {
        return args -> {
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
                throw new OwnLangRuntimeException(ex);
            }
        };
    }

    private static Connection getConnection(String url) throws SQLException {
        return DriverManager.getConnection(url);
    }

    private static Connection getConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    interface ThrowableRunnable {
        void run() throws Exception;
    }
}
