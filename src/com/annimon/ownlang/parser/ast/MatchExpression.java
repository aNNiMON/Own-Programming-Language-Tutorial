package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.PatternMatchingException;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.Variables;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class MatchExpression implements Expression, Statement {
    
    public final Expression expression;
    public final List<Pattern> patterns;

    public MatchExpression(Expression expression, List<Pattern> patterns) {
        this.expression = expression;
        this.patterns = patterns;
    }
    
    @Override
    public void execute() {
        eval();
    }
    
    @Override
    public Value eval() {
        final Value value = expression.eval();
        for (Pattern p : patterns) {
            if (p instanceof ConstantPattern) {
                final ConstantPattern pattern = (ConstantPattern) p;
                if (match(value, pattern.constant) && optMatches(p)) {
                    return evalResult(p.result);
                }
            }
            if (p instanceof VariablePattern) {
                final VariablePattern pattern = (VariablePattern) p;
                if (pattern.variable.equals("_")) return evalResult(p.result);
                
                if (Variables.isExists(pattern.variable)) {
                    if (match(value, Variables.get(pattern.variable)) && optMatches(p)) {
                        return evalResult(p.result);
                    }
                } else {
                    Variables.define(pattern.variable, value);
                    if (optMatches(p)) {
                        final Value result = evalResult(p.result);;
                        Variables.remove(pattern.variable);
                        return result;
                    }
                    Variables.remove(pattern.variable);
                }
            }
            if ((value.type() == Types.ARRAY) && (p instanceof ListPattern)) {
                final ListPattern pattern = (ListPattern) p;
                if (matchListPattern((ArrayValue) value, pattern)) {
                    // Clean up variables if matched
                    final Value result = evalResult(p.result);
                    for (String var : pattern.parts) {
                        Variables.remove(var);
                    }
                    return result;
                }
            }
        }
        throw new PatternMatchingException("No pattern were matched");
    }
    
    private boolean matchListPattern(ArrayValue array, ListPattern p) {
        final List<String> parts = p.parts;
        final int partsSize = parts.size();
        final int arraySize = array.size();
        switch (partsSize) {
            case 0: // match [] { case []: ... }
                if ((arraySize == 0) && optMatches(p)) {
                    return true;
                }
                return false;

            case 1: // match arr { case [x]: x = arr ... }
                final String variable = parts.get(0);
                Variables.define(variable, array);
                if (optMatches(p)) {
                    return true;
                }
                Variables.remove(variable);
                return false;
                
            default: { // match arr { case [...]: .. }
                if (partsSize == arraySize) {
                    // match [0, 1, 2] { case [a::b::c]: a=0, b=1, c=2 ... }
                    return matchListPatternEqualsSize(p, parts, partsSize, array);
                } else if (partsSize < arraySize) {
                    // match [1, 2, 3] { case [head :: tail]: ... }
                    return matchListPatternWithTail(p, parts, partsSize, array, arraySize);
                }
                return false;
            }
        }
    } 

    private boolean matchListPatternEqualsSize(ListPattern p, List<String> parts, int partsSize, ArrayValue array) {
        // Set variables
        for (int i = 0; i < partsSize; i++) {
            Variables.define(parts.get(i), array.get(i));
        }
        if (optMatches(p)) {
            // Clean up will be provided after evaluate result
            return true;
        }
        // Clean up variables if no match
        for (String var : parts) {
            Variables.remove(var);
        }
        return false;
    }
    
    private boolean matchListPatternWithTail(ListPattern p, List<String> parts, int partsSize, ArrayValue array, int arraySize) {
        // Set element variables
        final int lastPart = partsSize - 1;
        for (int i = 0; i < lastPart; i++) {
            Variables.define(parts.get(i), array.get(i));
        }
        // Set tail variable
        final ArrayValue tail = new ArrayValue(arraySize - partsSize + 1);
        for (int i = lastPart; i < arraySize; i++) {
            tail.set(i - lastPart, array.get(i));
        }
        Variables.define(parts.get(lastPart), tail);
        // Check optional condition
        if (optMatches(p)) {
            // Clean up will be provided after evaluate result
            return true;
        }
        // Clean up variables
        for (String var : parts) {
            Variables.remove(var);
        }
        return false;
    }
    
    private boolean match(Value value, Value constant) {
        if (value.type() != constant.type()) return false;
        return value.equals(constant);
    }
    
    private boolean optMatches(Pattern pattern) {
        if (pattern.optCondition == null) return true;
        return pattern.optCondition.eval() != NumberValue.ZERO;
    }
    
    private Value evalResult(Statement s) {
        try {
            s.execute();
        } catch (ReturnStatement ret) {
            return ret.getResult();
        }
        return NumberValue.ZERO;
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <R, T> R accept(ResultVisitor<R, T> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("match ").append(expression).append(" {");
        for (Pattern p : patterns) {
            sb.append("\n  case ").append(p);
        }
        sb.append("\n}");
        return sb.toString();
    }
    
    public static abstract class Pattern {
        public Statement result;
        public Expression optCondition;
    }
    
    public static class ConstantPattern extends Pattern {
        public Value constant;
        
        public ConstantPattern(Value pattern) {
            this.constant = pattern;
        }

        @Override
        public String toString() {
            return constant + ": " + result;
        }
    }
    
    public static class VariablePattern extends Pattern {
        public String variable;
        
        public VariablePattern(String pattern) {
            this.variable = pattern;
        }

        @Override
        public String toString() {
            return variable + ": " + result;
        }
    }
    
    public static class ListPattern extends Pattern {
        public List<String> parts;
        
        public ListPattern() {
            this(new ArrayList<String>());
        }
        
        public ListPattern(List<String> parts) {
            this.parts = parts;
        }

        public void add(String part) {
            parts.add(part);
        }

        @Override
        public String toString() {
            return parts + ": " + result;
        }
    }
}
