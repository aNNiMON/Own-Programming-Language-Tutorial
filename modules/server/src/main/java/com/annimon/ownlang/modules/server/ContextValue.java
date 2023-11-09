package com.annimon.ownlang.modules.server;

import com.annimon.ownlang.lib.*;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ContextValue extends MapValue {

    private final Context ctx;

    public ContextValue(@NotNull Context ctx) {
        super(10);
        this.ctx = ctx;
        init();
    }

    private void init() {
        set("body", Converters.voidToString(ctx::body));
        set("characterEncoding", Converters.voidToString(ctx::characterEncoding));
        set("contentType", Converters.voidToString(ctx::contentType));
        set("contextPath", Converters.voidToString(ctx::contextPath));
        set("fullUrl", Converters.voidToString(ctx::fullUrl));
        set("host", Converters.voidToString(ctx::host));
        set("ip", Converters.voidToString(ctx::ip));
        set("matchedPath", Converters.voidToString(ctx::matchedPath));
        set("path", Converters.voidToString(ctx::path));
        set("protocol", Converters.voidToString(ctx::protocol));
        set("queryString", Converters.voidToString(ctx::queryString));
        set("url", Converters.voidToString(ctx::url));
        set("userAgent", Converters.voidToString(ctx::userAgent));

        set("contentLength", Converters.voidToInt(ctx::contentLength));
        set("port", Converters.voidToInt(ctx::port));
        set("statusCode", Converters.voidToInt(ctx::statusCode));

        set("json", objectToContext(ctx::json));
        set("jsonStream", objectToContext(ctx::jsonStream));

        set("render", this::render);
        set("result", this::result);
        set("redirect", this::redirect);
    }

    private Value render(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        String filePath = args[0].asString();
        if (args.length == 1) {
            ctx.render(filePath);
        } else {
            MapValue map = (MapValue) args[1];
            Map<String, Object> data = new HashMap<>(map.size());
            map.getMap().forEach((k, v) -> data.put(k.asString(), v.asJavaObject()));
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

    private Value objectToContext(Consumer<Object> consumer) {
        return new FunctionValue(args -> {
            Arguments.check(1, args.length);
            consumer.accept(args[0].asJavaObject());
            return this;
        });
    }
}
