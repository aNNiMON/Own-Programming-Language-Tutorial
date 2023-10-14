package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.OwnLangParserException;
import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.parser.error.ParseErrorsFormatterStage;
import com.annimon.ownlang.stages.*;
import com.annimon.ownlang.util.input.InputSourceFile;
import com.annimon.ownlang.util.input.SourceLoaderStage;

/**
 *
 * @author aNNiMON
 */
public final class IncludeStatement extends InterruptableNode implements Statement {

    public final Expression expression;
    
    public IncludeStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute() {
        super.interruptionCheck();

        final var stagesData = new StagesDataMap();
        try {
            final String path = expression.eval().asString();
            new SourceLoaderStage()
                    .then(new LexerStage())
                    .then(new ParserStage())
                    .then(new FunctionAddingStage())
                    .then(new ExecutionStage())
                    .perform(stagesData, new InputSourceFile(path));
        } catch (OwnLangParserException ex) {
            final var error = new ParseErrorsFormatterStage()
                    .perform(stagesData, ex.getParseErrors());
            throw new OwnLangRuntimeException(error, ex);
        }
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
        return "include " + expression;
    }
}
