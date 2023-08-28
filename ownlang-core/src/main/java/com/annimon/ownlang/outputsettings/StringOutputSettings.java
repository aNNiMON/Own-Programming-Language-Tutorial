package com.annimon.ownlang.outputsettings;

import java.io.File;

public non-sealed class StringOutputSettings implements OutputSettings {

    private final StringBuffer out, err;

    public StringOutputSettings() {
        this(new StringBuffer());
    }

    public StringOutputSettings(StringBuffer out) {
        this(out, out);
    }

    public StringOutputSettings(StringBuffer out, StringBuffer err) {
        this.out = out;
        this.err = err;
    }

    @Override
    public String newline() {
        return System.lineSeparator();
    }

    @Override
    public void print(String value) {
        out.append(value);
    }

    @Override
    public void print(Object value) {
        out.append(value.toString());
    }

    @Override
    public void println() {
        out.append(newline());
    }

    @Override
    public void println(String value) {
        out.append(value).append(newline());
    }

    @Override
    public void println(Object value) {
        println(value.toString());
    }

    @Override
    public String getText() {
        return out.toString();
    }

    @Override
    public void error(Throwable throwable) {
        error(throwable.toString());
    }

    @Override
    public void error(CharSequence value) {
        err.append(value).append(newline());
    }

    @Override
    public File fileInstance(String path) {
        return new File(path);
    }
}
