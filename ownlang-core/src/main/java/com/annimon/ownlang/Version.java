package com.annimon.ownlang;

public final class Version {
    public static final int VERSION_MAJOR = 2;
    public static final int VERSION_MINOR = 0;
    public static final int VERSION_PATCH = 0;

    public static final String VERSION = VERSION_MAJOR + "."
            + VERSION_MINOR + "." + VERSION_PATCH
            + "_" + Gen.BUILD_DATE;
}