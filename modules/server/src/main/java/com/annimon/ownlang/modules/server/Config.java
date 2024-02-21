package com.annimon.ownlang.modules.server;

import com.annimon.ownlang.lib.*;
import io.javalin.config.JavalinConfig;
import io.javalin.config.Key;
import io.javalin.http.staticfiles.Location;
import java.util.Map;
import java.util.function.Consumer;

/*
 * Sample config:
 * {
 *   "webjars": true,
 *   "classpathDirs": ["dir1", "dir2"],
 *   "externalDirs": ["dir1", "dir2"],
 *
 *   "asyncTimeout": 6_000,
 *   "defaultContentType": "text/plain",
 *   "etags": true,
 *   "maxRequestSize": 1_000_000,
 *
 *   "defaultHost": "localhost",
 *   "defaultPort": 8000,
 *
 *   "caseInsensitiveRoutes": true,
 *   "ignoreTrailingSlashes": true,
 *   "multipleSlashesAsSingle": true,
 *   "contextPath": "/",
 *
 *   "basicAuth": ["user", "password"],
 *   "dev": true,
 *   "showBanner": false,
 *   "sslRedirects": true,
 *   "virtualThreads": true,
 *   "appData": {
 *     "key1": "value1",
 *     "key2": "value2"
 *   }
 * }
 */

class Config {
    private final Map<String, Value> map;

    public Config(Map<String, Value> map) {
        this.map = map;
    }

    public void setup(JavalinConfig config) {
        // staticFiles
        ifTrue("webjars", config.staticFiles::enableWebjars);
        ifArray("classpathDirs", directories -> {
            for (Value directory : directories) {
                config.staticFiles.add(directory.asString(), Location.CLASSPATH);
            }
        });
        ifArray("externalDirs", directories -> {
            for (Value directory : directories) {
                config.staticFiles.add(directory.asString(), Location.EXTERNAL);
            }
        });

        // http
        ifNumber("asyncTimeout", value -> config.http.asyncTimeout = value.asLong());
        ifString("defaultContentType", value -> config.http.defaultContentType = value);
        ifBoolean("etags", flag -> config.http.generateEtags = flag);
        ifNumber("maxRequestSize", value -> config.http.maxRequestSize = value.asLong());

        // jetty
        ifString("defaultHost", value -> config.jetty.defaultHost = value);
        ifNumber("defaultPort", value -> config.jetty.defaultPort = value.asInt());

        // routing
        ifBoolean("caseInsensitiveRoutes", flag -> config.router.caseInsensitiveRoutes = flag);
        ifBoolean("ignoreTrailingSlashes", flag -> config.router.ignoreTrailingSlashes = flag);
        ifBoolean("multipleSlashesAsSingle", flag -> config.router.treatMultipleSlashesAsSingleSlash = flag);
        ifString("contextPath", path -> config.router.contextPath = path);

        // other
        ifArray("basicAuth", arr -> config.bundledPlugins.enableBasicAuth(arr.get(0).asString(), arr.get(1).asString()));
        ifBoolean("showBanner", flag -> config.showJavalinBanner = flag);
        ifTrue("dev", config.bundledPlugins::enableDevLogging);
        ifTrue("sslRedirects", config.bundledPlugins::enableSslRedirects);
        ifBoolean("virtualThreads", flag -> config.useVirtualThreads = flag);
        ifMap("appData", appData -> appData.getMapStringKeys()
                .forEach((key, value) -> config.appData(new Key<>(key), value)));
    }

    private void ifTrue(String key, Runnable action) {
        if (map.containsKey(key) && map.get(key).asInt() != 0) {
            action.run();
        }
    }

    private void ifBoolean(String key, Consumer<Boolean> consumer) {
        if (!map.containsKey(key)) return;
        consumer.accept(map.get(key).asInt() != 0);
    }

    private void ifNumber(String key, Consumer<NumberValue> consumer) {
        if (!map.containsKey(key)) return;
        final Value value = map.get(key);
        if (value.type() == Types.NUMBER) {
            consumer.accept((NumberValue) value);
        }
    }

    private void ifString(String key, Consumer<String> consumer) {
        if (!map.containsKey(key)) return;
        consumer.accept(map.get(key).asString());
    }

    private void ifArray(String key, Consumer<ArrayValue> consumer) {
        if (!map.containsKey(key)) return;
        final Value value = map.get(key);
        if (value.type() == Types.ARRAY) {
            consumer.accept((ArrayValue) value);
        }
    }

    private void ifMap(String key, Consumer<MapValue> consumer) {
        if (!map.containsKey(key)) return;
        final Value value = map.get(key);
        if (value.type() == Types.MAP) {
            consumer.accept((MapValue) value);
        }
    }
}
