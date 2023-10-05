package com.annimon.ownlang.lib;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.parser.ast.Argument;
import com.annimon.ownlang.parser.ast.Arguments;
import com.annimon.ownlang.parser.ast.ReturnStatement;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.util.Range;
import com.annimon.ownlang.util.SourceLocation;

/**
 *
 * @author aNNiMON
 */
public class UserDefinedFunction implements Function, SourceLocation {
    
    public final Arguments arguments;
    public final Statement body;
    private final Range range;

    public UserDefinedFunction(Arguments arguments, Statement body, Range range) {
        this.arguments = arguments;
        this.body = body;
        this.range = range;
    }

    @Override
    public Range getRange() {
        return range;
    }

    @Override
    public int getArgsCount() {
        return arguments.size();
    }

    public String getArgsName(int index) {
        if (index < 0 || index >= getArgsCount()) return "";
        return arguments.get(index).name();
    }

    @Override
    public Value execute(Value[] values) {
        final int size = values.length;
        final int requiredArgsCount = arguments.getRequiredArgumentsCount();
        if (size < requiredArgsCount) {
            String error = String.format(
                    "Arguments count mismatch. Required %d, got %d", requiredArgsCount, size);
            throw new ArgumentsMismatchException(error, arguments.getRange());
        }
        final int totalArgsCount = getArgsCount();
        if (size > totalArgsCount) {
            String error = String.format(
                    "Arguments count mismatch. Total %d, got %d", totalArgsCount, size);
            throw new ArgumentsMismatchException(error, arguments.getRange());
        }

        try {
            ScopeHandler.push();
            for (int i = 0; i < size; i++) {
                ScopeHandler.defineVariableInCurrentScope(getArgsName(i), values[i]);
            }
            // Optional args if exists
            for (int i = size; i < totalArgsCount; i++) {
                final Argument arg = arguments.get(i);
                ScopeHandler.defineVariableInCurrentScope(arg.name(), arg.valueExpr().eval());
            }
            body.execute();
            return NumberValue.ZERO;
        } catch (ReturnStatement rt) {
            return rt.getResult();
        } finally {
            ScopeHandler.pop();
        }
    }

    @Override
    public String toString() {
        if (body instanceof ReturnStatement returnStmt) {
            return String.format("def%s = %s", arguments, returnStmt.expression);
        }
        return String.format("def%s %s", arguments, body);
    }
}
