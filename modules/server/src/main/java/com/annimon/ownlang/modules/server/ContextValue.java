package com.annimon.ownlang.modules.server;

import com.annimon.ownlang.lib.*;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.function.Consumer;

class ContextValue extends MapValue {

    private final Context ctx;

    public ContextValue(@NotNull Context ctx) {
        super(32);
        this.ctx = ctx;
        init();
    }

    private void init() {
        set("attribute", this::attribute);
        set("basicAuthCredentials", this::basicAuthCredentials);
        set("body", Converters.voidToString(ctx::body));
        set("bodyAsBytes", this::bodyAsBytes);
        set("characterEncoding", Converters.voidToString(ctx::characterEncoding));
        set("cookie", this::cookie);
        set("contentLength", Converters.voidToInt(ctx::contentLength));
        set("contentType", this::contentType);
        set("contextPath", Converters.voidToString(ctx::contextPath));
        set("endpointHandlerPath", Converters.voidToString(ctx::endpointHandlerPath));
        set("formParam", Converters.stringToString(ctx::formParam));
        set("fullUrl", Converters.voidToString(ctx::fullUrl));
        set("handlerType", Converters.voidToString(() -> ctx.handlerType().name()));
        set("header", this::header);
        set("host", Converters.voidToString(ctx::host));
        set("html", stringToContext(ctx::html));
        set("ip", Converters.voidToString(ctx::ip));
        set("json", objectToContext(ctx::json));
        set("jsonStream", objectToContext(ctx::jsonStream));
        set("matchedPath", Converters.voidToString(ctx::matchedPath));
        set("path", Converters.voidToString(ctx::path));
        set("port", Converters.voidToInt(ctx::port));
        set("protocol", Converters.voidToString(ctx::protocol));
        set("queryString", Converters.voidToString(ctx::queryString));
        set("redirect", this::redirect);
        set("removeCookie", this::removeCookie);
        set("render", this::render);
        set("result", this::result);
        set("statusCode", Converters.voidToInt(ctx::statusCode));
        set("scheme", Converters.voidToString(ctx::scheme));
        set("url", Converters.voidToString(ctx::url));
        set("userAgent", Converters.voidToString(ctx::userAgent));
    }

    private Value attribute(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        String key = args[0].asString();
        if (args.length == 1) {
            return ctx.attribute(key);
        } else {
            ctx.attribute(key, args[1]);
        }
        return this;
    }

    private Value basicAuthCredentials(Value[] args) {
        Arguments.check(0, args.length);
        final var cred = ctx.basicAuthCredentials();
        return ArrayValue.of(new String[] {
                cred.getUsername(),
                cred.getPassword()
        });
    }

    private Value bodyAsBytes(Value[] args) {
        Arguments.check(0, args.length);
        return ArrayValue.of(ctx.bodyAsBytes());
    }

    private Value cookie(Value[] args) {
        Arguments.checkRange(1, 3, args.length);
        if (args.length == 1) {
            return new StringValue(ctx.cookie(args[0].asString()));
        }
        int maxAge = args.length == 3 ? args[2].asInt() : -1;
        ctx.cookie(args[0].asString(), args[1].asString(), maxAge);
        return this;
    }

    private Value contentType(Value[] args) {
        Arguments.checkOrOr(0, 1, args.length);
        if (args.length == 0) {
            return new StringValue(ctx.contentType());
        } else {
            ctx.contentType(args[0].asString());
            return this;
        }
    }

    private Value header(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        String name = args[0].asString();
        if (args.length == 1) {
            return new StringValue(ctx.header(name));
        } else {
            ctx.header(name, args[1].asString());
            return this;
        }
    }

    private Value render(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        String filePath = args[0].asString();
        if (args.length == 1) {
            ctx.render(filePath);
        } else {
            MapValue map = ValueUtils.consumeMap(args[1], 1);
            Map<String, Object> data = map.convertMap(Value::asString, Value::asJavaObject);
            ctx.render(filePath, data);
        }
        return this;
    }

    private Value redirect(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        HttpStatus status = args.length == 1 ? HttpStatus.FOUND : HttpStatus.forStatus(args[1].asInt());
        ctx.redirect(args[0].asString(), status);
        return this;
    }

    private Value removeCookie(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        String name = args[0].asString();
        String path = args.length == 2 ? args[1].asString() : "/";
        ctx.removeCookie(name, path);
        return this;
    }

    private Value result(Value[] args) {
        Arguments.checkOrOr(0, 1, args.length);
        if (args.length == 0) {
            return new StringValue(ctx.result());
        } else {
            final var arg = args[0];
            if (arg.type() == Types.ARRAY) {
                ctx.result(ValueUtils.toByteArray((ArrayValue) arg));
            } else {
                ctx.result(arg.asString());
            }
            return this;
        }
    }

    private Value stringToContext(Consumer<String> consumer) {
        return new FunctionValue(args -> {
            Arguments.check(1, args.length);
            consumer.accept(args[0].asString());
            return this;
        });
    }

    private Value objectToContext(Consumer<Object> consumer) {
        return new FunctionValue(args -> {
            Arguments.check(1, args.length);
            consumer.accept(args[0].asJavaObject());
            return this;
        });
    }
}
