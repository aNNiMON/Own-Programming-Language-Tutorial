package com.annimon.ownlang.parser;

public record ParseError(Exception exception, Pos pos) {

    @Override
    public String toString() {
        return "Error on line " + pos.row() + ": " + exception;
    }
}
