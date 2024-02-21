package com.annimon.ownlang.modules.socket;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * socket.io module.
 *
 * @author aNNiMON
 */
public final class socket implements Module {

    @Override
    public Map<String, Value> constants() {
        return Map.of(
                "EVENT_CONNECT", new StringValue(Socket.EVENT_CONNECT),
                "EVENT_CONNECT_ERROR", new StringValue(Socket.EVENT_CONNECT_ERROR),
                "EVENT_DISCONNECT", new StringValue(Socket.EVENT_DISCONNECT)
        );
    }

    @Override
    public Map<String, Function> functions() {
        return Map.of("newSocket", socket::newSocket);
    }

    private static Value newSocket(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);

        try {
            final String url = args[0].asString();
            if (args.length == 1) {
                return new SocketValue(IO.socket(url));
            }

            if (args[1].type() != Types.MAP) {
                throw new TypeException("Map expected in second argument");
            }
            IO.Options options = parseOptions((MapValue) args[1]);
            return new SocketValue(IO.socket(url, options));
        } catch (URISyntaxException ue) {
            return NumberValue.MINUS_ONE;
        }
    }

    private static class SocketValue extends MapValue {

        private final Socket socket;

        public SocketValue(Socket socket) {
            super(12);
            this.socket = socket;
            init();
        }

        private void init() {
            set("close", Converters.voidToVoid(socket::close));
            set("connect", Converters.voidToVoid(socket::connect));
            set("connected", Converters.voidToBoolean(socket::connected));
            set("disconnect", Converters.voidToVoid(socket::disconnect));
            set("emit", this::emit);
            set("hasListeners", this::hasListeners);
            set("id", Converters.voidToString(socket::id));
            set("off", this::off);
            set("on", this::on);
            set("once", this::once);
            set("open", Converters.voidToVoid(socket::open));
            set("send", this::send);
        }

        private Value hasListeners(Value[] args) {
            Arguments.check(1, args.length);
            return NumberValue.fromBoolean(
                    socket.hasListeners(args[0].asString())
            );
        }

        private Value emit(Value[] args) {
            Arguments.checkOrOr(2, 3, args.length);
            final String event = args[0].asString();
            final Value value = args[1];
            if (args.length == 3) {
                // TODO ack
            }
            socket.emit(event, ValueUtils.toObject(value));
            return this;
        }

        private Value off(Value[] args) {
            Arguments.checkOrOr(0, 1, args.length);
            if (args.length == 1) {
                socket.off(args[0].asString());
            } else {
                socket.off();
            }
            return this;
        }

        private Value on(Value[] args) {
            Arguments.check(2, args.length);
            final String event = args[0].asString();
            final Function listener = ((FunctionValue) args[1]).getValue();
            socket.on(event, sArgs -> {
                executeSocketListener(listener, sArgs);
            });
            return this;
        }

        private Value once(Value[] args) {
            Arguments.check(2, args.length);
            final String event = args[0].asString();
            final Function listener = ((FunctionValue) args[1]).getValue();
            socket.once(event, sArgs -> {
                executeSocketListener(listener, sArgs);
            });
            return this;
        }

        private Value send(Value[] args) {
            Arguments.check(1, args.length);
            socket.send(ValueUtils.toObject(args[0]));
            return this;
        }

        private void executeSocketListener(Function listener, Object[] sArgs) {
            if (sArgs == null) {
                listener.execute(new ArrayValue(0));
            } else {
                int size = sArgs.length;
                final Value[] fArgs = new Value[size];
                for (int i = 0; i < size; i++) {
                    fArgs[i] = ValueUtils.toValue(sArgs[i]);
                }
                listener.execute(new ArrayValue(fArgs));
            }
        }
    }

    private static IO.Options parseOptions(MapValue map) {
        final IO.Options result = new IO.Options();
        map.ifPresent("forceNew", v -> result.forceNew = v.asInt() != 0);
        map.ifPresent("multiplex", v -> result.multiplex = v.asInt() != 0);
        map.ifPresent("reconnection", v -> result.reconnection = v.asInt() != 0);
        map.ifPresent("rememberUpgrade", v -> result.rememberUpgrade = v.asInt() != 0);
        map.ifPresent("secure", v -> result.secure = v.asInt() != 0);
        map.ifPresent("timestampRequests", v -> result.timestampRequests = v.asInt() != 0);
        map.ifPresent("upgrade", v -> result.upgrade = v.asInt() != 0);

        map.ifPresent("policyPort", v -> result.policyPort = v.asInt());
        map.ifPresent("port", v -> result.port = v.asInt());
        map.ifPresent("reconnectionAttempts", v -> result.reconnectionAttempts = v.asInt());

        map.ifPresent("reconnectionDelay", v -> result.reconnectionDelay = getNumber(v).longValue());
        map.ifPresent("reconnectionDelayMax", v -> result.reconnectionDelayMax = getNumber(v).longValue());
        map.ifPresent("timeout", v -> result.timeout = getNumber(v).longValue());

        map.ifPresent("randomizationFactor", v -> result.randomizationFactor = v.asNumber());

        map.ifPresent("host", v -> result.host = v.asString());
        map.ifPresent("hostname", v -> result.hostname = v.asString());
        map.ifPresent("path", v -> result.path = v.asString());
        map.ifPresent("query", v -> result.query = v.asString());
        map.ifPresent("timestampParam", v -> result.timestampParam = v.asString());

        map.ifPresent("transports", v -> {
            if (v.type() != Types.ARRAY) return;

            final ArrayValue arr = (ArrayValue) v;
            final String[] values = new String[arr.size()];
            int index = 0;
            for (Value value : arr) {
                values[index++] = value.asString();
            }
            result.transports = values;
        });
        return result;
    }

    private static Number getNumber(Value value) {
        if (value.type() != Types.NUMBER) return value.asInt();
        return ((NumberValue) value).raw();
    }
}
