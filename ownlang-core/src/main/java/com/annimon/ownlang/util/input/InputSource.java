package com.annimon.ownlang.util.input;

import java.io.IOException;

public interface InputSource {
    String getPath();

    String load() throws IOException;

    default String getBasePath() {
        final String normalizedPath = getPath().replace("\\", "/");
        int i = normalizedPath.lastIndexOf("/");
        if (i == -1) return "";
        return normalizedPath.substring(0, i + 1);
    }
}
