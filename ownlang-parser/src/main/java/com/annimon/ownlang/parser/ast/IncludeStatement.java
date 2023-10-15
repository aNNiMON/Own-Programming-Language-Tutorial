package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.OwnLangParserException;
import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.parser.error.ParseErrorsFormatterStage;
import com.annimon.ownlang.stages.*;
import com.annimon.ownlang.util.Range;
import com.annimon.ownlang.util.SourceLocation;
import com.annimon.ownlang.util.input.InputSourceFile;
import com.annimon.ownlang.util.input.SourceLoaderStage;

/**
 *
 * @author aNNiMON
 */
public final class IncludeStatement extends InterruptableNode implements Statement, SourceLocation {

    public final Node expression;
    private Range range;
    
    public IncludeStatement(Node expression) {
        this.expression = expression;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    @Override
    public Range getRange() {
        return range;
    }

    @Override
    public Value eval() {
        super.interruptionCheck();

        final var stagesData = new StagesDataMap();
        try {
            final String path = expression.eval().asString();
            new SourceLoaderStage()
                    .then(new LexerStage())
                    .then(new ParserStage())
                    // TODO LinterStage based on main context
                    .then(new FunctionAddingStage())
                    .then(new ExecutionStage())
                    .perform(stagesData, new InputSourceFile(path));
        } catch (OwnLangParserException ex) {
            final var error = new ParseErrorsFormatterStage()
                    .perform(stagesData, ex.getParseErrors());
            throw new OwnLangRuntimeException(error, ex);
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
        return "include " + expression;
    }
}
