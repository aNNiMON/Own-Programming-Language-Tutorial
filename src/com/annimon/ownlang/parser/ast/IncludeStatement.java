package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.parser.Lexer;
import com.annimon.ownlang.parser.Parser;
import com.annimon.ownlang.parser.SourceLoader;
import com.annimon.ownlang.parser.Token;
import com.annimon.ownlang.parser.visitors.FunctionAdder;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class IncludeStatement implements Statement {

    public final Expression expression;
    
    public IncludeStatement(Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public void execute() {
        try {
            final String input = SourceLoader.readSource(expression.eval().asString());
            final List<Token> tokens = new Lexer(input).tokenize();
            final Parser parser = new Parser(tokens);
            final Statement program = parser.parse();
            if (!parser.getParseErrors().hasErrors()) {
                program.accept(new FunctionAdder());
                program.execute();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "include " + expression;
    }
}
