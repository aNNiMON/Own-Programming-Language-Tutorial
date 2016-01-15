package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.Variables;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class MatchExpression implements Expression {
    
    public final Expression expression;
    public final List<Pattern> patterns;

    public MatchExpression(Expression expression, List<Pattern> patterns) {
        this.expression = expression;
        this.patterns = patterns;
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
                    Variables.set(pattern.variable, value);
                    if (optMatches(p)) {
                        final Value result = evalResult(p.result);;
                        Variables.remove(pattern.variable);
                        return result;
                    }
                    Variables.remove(pattern.variable);
                }
            }
        }
        throw new RuntimeException("No pattern were matched");
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
}
