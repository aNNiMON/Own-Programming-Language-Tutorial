package com.annimon.ownlang.modules.server;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import java.util.Map;
import static java.util.Map.entry;

public final class server implements Module {

    @Override
    public Map<String, Value> constants() {
        return Map.of();
    }

    @Override
    public Map<String, Function> functions() {
        return Map.ofEntries(
                entry("newServer", this::newServer),
                entry("serve", this::serve)
        );
    }

    private Value newServer(Value[] args) {
        Arguments.checkOrOr(0, 1, args.length);
        if (args.length == 0) {
            return new ServerValue(Javalin.create());
        } else {
            final Map<String, Value> map = ValueUtils.consumeMap(args[0], 0).getMapStringKeys();
            final Config config = new Config(map);
            return new ServerValue(Javalin.create(config::setup));
        }
    }

    private Value serve(Value[] args) {
        Arguments.checkRange(0, 2, args.length);
        int port = args.length >= 1 ? args[0].asInt() : 8080;
        String dir = args.length >= 2 ? args[1].asString() : ".";
        return new ServerValue(Javalin.create(config -> {
            config.staticFiles.add(dir, Location.EXTERNAL);
            config.showJavalinBanner = false;
        }).start(port));
    }
}
