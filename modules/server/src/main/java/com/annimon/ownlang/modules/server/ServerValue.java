package com.annimon.ownlang.modules.server;

import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.lib.*;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;
import java.util.Arrays;

class ServerValue extends MapValue {

    private final Javalin server;

    public ServerValue(Javalin server) {
        super(12);
        this.server = server;
        init();
    }

    private void init() {
        set("get", httpMethod(server::get));
        set("post", httpMethod(server::post));
        set("put", httpMethod(server::put));
        set("patch", httpMethod(server::patch));
        set("head", httpMethod(server::head));
        set("delete", httpMethod(server::delete));
        set("options", httpMethod(server::options));
        set("error", this::error);
        set("exception", this::exception);
        set("start", this::start);
        set("stop", this::stop);
    }

    private Value error(Value[] args) {
        Arguments.checkOrOr(2, 3, args.length);
        final String contentType;
        if (args.length == 2) {
            contentType = "*";
        } else {
            contentType = args[2].asString();
        }
        int status = args[0].asInt();
        final Handler handler = toHandler(ValueUtils.consumeFunction(args[1], 1));
        server.error(status, contentType, handler);
        return this;
    }

    @SuppressWarnings("unchecked")
    private Value exception(Value[] args) {
        Arguments.check(2, args.length);
        try {
            String className = args[0].asString();
            final Class<?> clazz = Class.forName(className);
            final Function handler = ValueUtils.consumeFunction(args[1], 1);
            server.exception((Class<? extends Exception>) clazz, (exc, ctx) -> {
                Value exceptionType = new StringValue(exc.getClass().getName());
                handler.execute(exceptionType, new ContextValue(ctx));
            });
        } catch (ClassNotFoundException e) {
            throw new OwnLangRuntimeException(e);
        }
        return this;
    }

    private Value start(Value[] args) {
        Arguments.checkRange(0, 2, args.length);
        switch (args.length) {
            case 0 -> server.start();
            case 1 -> server.start(args[0].asInt());
            case 2 -> server.start(args[1].asString(), args[0].asInt());
        }
        return this;
    }

    private Value stop(Value[] args) {
        Arguments.check(0, args.length);
        server.stop();
        return this;
    }

    private FunctionValue httpMethod(HttpMethodHandler httpHandler) {
        return new FunctionValue(args -> {
            Arguments.checkAtLeast(2, args.length);
            final String path = args[0].asString();
            final Handler handler = toHandler(ValueUtils.consumeFunction(args[1], 1));
            final Role[] roles;
            if (args.length == 2) {
                roles = new Role[0];
            } else {
                roles = Arrays.stream(args)
                        .skip(2)
                        .map(Role::new)
                        .toArray(Role[]::new);
            }
            httpHandler.handle(path, handler, roles);
            return this;
        });
    }

    private Handler toHandler(Function function) {
        return ctx -> function.execute(new ContextValue(ctx));
    }

    private interface HttpMethodHandler {
        void handle(String path, Handler handler, RouteRole[] roles);
    }

    private record Role(Value value) implements RouteRole { }
}
