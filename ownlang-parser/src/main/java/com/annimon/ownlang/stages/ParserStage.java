package com.annimon.ownlang.stages;

import com.annimon.ownlang.exceptions.OwnLangParserException;
import com.annimon.ownlang.parser.Parser;
import com.annimon.ownlang.parser.Token;
import com.annimon.ownlang.parser.ast.Statement;
import java.util.List;

public class ParserStage implements Stage<List<Token>, Statement> {

    public static final String TAG_PROGRAM = "program";
    public static final String TAG_HAS_PARSE_ERRORS = "hasParseErrors";

    @Override
    public Statement perform(StagesData stagesData, List<Token> input) {
        final Parser parser = new Parser(input);
        final Statement program = parser.parse();
        final var parseErrors = parser.getParseErrors();
        stagesData.put(TAG_PROGRAM, program);
        stagesData.put(TAG_HAS_PARSE_ERRORS, parseErrors.hasErrors());
        if (parseErrors.hasErrors()) {
            throw new OwnLangParserException(parseErrors);
        }
        return program;
    }
}
