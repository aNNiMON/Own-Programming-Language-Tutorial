package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.annotations.ConstantInitializer;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;

/**
 * socket.io module.
 *
 * @author aNNiMON
 */
@ConstantInitializer
public final class socket implements Module {

    public static void initConstants() {
        Variables.define("EVENT_CONNECT", new StringValue(Socket.EVENT_CONNECT));
        Variables.define("EVENT_CONNECTING", new StringValue(Socket.EVENT_CONNECTING));
        Variables.define("EVENT_CONNECT_ERROR", new StringValue(Socket.EVENT_CONNECT_ERROR));
        Variables.define("EVENT_CONNECT_TIMEOUT", new StringValue(Socket.EVENT_CONNECT_TIMEOUT));
        Variables.define("EVENT_DISCONNECT", new StringValue(Socket.EVENT_DISCONNECT));
        Variables.define("EVENT_ERROR", new StringValue(Socket.EVENT_ERROR));
        Variables.define("EVENT_MESSAGE", new StringValue(Socket.EVENT_MESSAGE));
        Variables.define("EVENT_PING", new StringValue(Socket.EVENT_PING));
        Variables.define("EVENT_PONG", new StringValue(Socket.EVENT_PONG));
        Variables.define("EVENT_RECONNECT", new StringValue(Socket.EVENT_RECONNECT));
        Variables.define("EVENT_RECONNECTING", new StringValue(Socket.EVENT_RECONNECTING));
        Variables.define("EVENT_RECONNECT_ATTEMPT", new StringValue(Socket.EVENT_RECONNECT_ATTEMPT));
        Variables.define("EVENT_RECONNECT_ERROR", new StringValue(Socket.EVENT_RECONNECT_ERROR));
        Variables.define("EVENT_RECONNECT_FAILED", new StringValue(Socket.EVENT_RECONNECT_FAILED));
    }

    @Override
    public void init() {
        initConstants();
        Functions.set("newSocket", socket::newSocket);
    }

    private static Value newSocket(Value... args) {
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
            super(11);
            this.socket = socket;
            init();
        }

        private void init() {
            set("close", this::close);
            set("connect", this::connect);
            set("connected", this::connected);
            set("disconnect", this::disconnect);
            set("emit", this::emit);
            set("hasListeners", this::hasListeners);
            set("id", this::id);
            set("off", this::off);
            set("on", this::on);
            set("once", this::once);
            set("open", this::open);
            set("send", this::send);
        }

        private Value close(Value... args) {
            socket.close();
            return this;
        }

        private Value connect(Value... args) {
            socket.connect();
            return this;
        }

        private Value connected(Value... args) {
            return NumberValue.fromBoolean(socket.connected());
        }

        private Value disconnect(Value... args) {
            socket.disconnect();
            return this;
        }

        private Value hasListeners(Value... args) {
            Arguments.check(1, args.length);
            return NumberValue.fromBoolean(
                    socket.hasListeners(args[0].asString())
            );
        }

        private Value emit(Value... args) {
            Arguments.checkOrOr(2, 3, args.length);
            final String event = args[0].asString();
            final Value value = args[1];
            if (args.length == 3) {
                // TODO ack
            }
            socket.emit(event, ValueUtils.toObject(value));
            return this;
        }

        private Value id(Value... args) {
            return new StringValue(socket.id());
        }

        private Value off(Value... args) {
            Arguments.checkOrOr(0, 1, args.length);
            if (args.length == 1) {
                socket.off(args[0].asString());
            } else {
                socket.off();
            }
            return this;
        }

        private Value on(Value... args) {
            Arguments.check(2, args.length);
            final String event = args[0].asString();
            final Function listener = ((FunctionValue) args[1]).getValue();
            socket.on(event, sArgs -> {
                executeSocketListener(listener, sArgs);
            });
            return this;
        }

        private Value once(Value... args) {
            Arguments.check(2, args.length);
            final String event = args[0].asString();
            final Function listener = ((FunctionValue) args[1]).getValue();
            socket.once(event, sArgs -> {
                executeSocketListener(listener, sArgs);
            });
            return this;
        }

        private Value open(Value... args) {
            socket.open();
            return this;
        }

        private Value send(Value... args) {
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
