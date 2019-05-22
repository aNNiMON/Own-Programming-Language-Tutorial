package com.annimon.ownlang.modules.okhttp;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class HttpClientValue extends MapValue {

    private final OkHttpClient client;

    public HttpClientValue(OkHttpClient client) {
        super(10);
        this.client = client;
        init();
    }

    public OkHttpClient getClient() {
        return client;
    }

    private void init() {
        set("connectTimeoutMillis", Converters.voidToInt(client::connectTimeoutMillis));
        set("followRedirects", Converters.voidToBoolean(client::followRedirects));
        set("followSslRedirects", Converters.voidToBoolean(client::followSslRedirects));
        set("newCall", args -> {
            Arguments.check(1, args.length);
            final Request request =  Values.getRequest(args[0], " at first argument");
            return new CallValue(client.newCall(request));
        });
        set("newWebSocket", this::newWebSocket);
        set("pingIntervalMillis", Converters.voidToInt(client::pingIntervalMillis));
        set("readTimeoutMillis", Converters.voidToInt(client::readTimeoutMillis));
        set("retryOnConnectionFailure", Converters.voidToBoolean(client::retryOnConnectionFailure));
        set("writeTimeoutMillis", Converters.voidToInt(client::writeTimeoutMillis));
    }

    private static final StringValue onOpen = new StringValue("onOpen");
    private static final StringValue onTextMessage = new StringValue("onTextMessage");
    private static final StringValue onBytesMessage = new StringValue("onBytesMessage");
    private static final StringValue onClosing = new StringValue("onClosing");
    private static final StringValue onClosed = new StringValue("onClosed");
    private static final StringValue onFailure = new StringValue("onFailure");

    private Value newWebSocket(Value[] args) {
        Arguments.check(2, args.length);
        final Request request =  Values.getRequest(args[0], " at first argument");
        if (args[1].type() != Types.MAP) {
            throw new TypeException("Map expected at second argument");
        }
        final MapValue callbacks = (MapValue) args[1];
        final WebSocket ws = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                final Value func = callbacks.get(onOpen);
                if (func != null) {
                    ValueUtils.consumeFunction(func, " at onOpen").execute(
                            new WebSocketValue(webSocket),
                            new ResponseValue(response));
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                final Value func = callbacks.get(onTextMessage);
                if (func != null) {
                    ValueUtils.consumeFunction(func, "at onTextMessage").execute(
                            new WebSocketValue(webSocket),
                            new StringValue(text));
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                final Value func = callbacks.get(onBytesMessage);
                if (func != null) {
                    ValueUtils.consumeFunction(func, "at onBytesMessage").execute(
                            new WebSocketValue(webSocket),
                            ArrayValue.of(bytes.toByteArray()));
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                final Value func = callbacks.get(onClosing);
                if (func != null) {
                    ValueUtils.consumeFunction(func, "at onClosing").execute(
                            new WebSocketValue(webSocket),
                            NumberValue.of(code),
                            new StringValue(reason));
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                final Value func = callbacks.get(onClosed);
                if (func != null) {
                    ValueUtils.consumeFunction(func, "at onClosed").execute(
                            new WebSocketValue(webSocket),
                            NumberValue.of(code),
                            new StringValue(reason));
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                final Value func = callbacks.get(onFailure);
                if (func != null) {
                    ValueUtils.consumeFunction(func, "at onFailure").execute(
                            new WebSocketValue(webSocket),
                            new StringValue(t.getMessage()),
                            new ResponseValue(response));
                }
            }
        });
        return new CallValue(client.newCall(request));
    }
}
