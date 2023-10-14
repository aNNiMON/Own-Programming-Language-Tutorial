package com.annimon.ownlang.stages;

import com.annimon.ownlang.exceptions.OwnLangParserException;
import com.annimon.ownlang.parser.Parser;
import com.annimon.ownlang.parser.Token;
import com.annimon.ownlang.parser.ast.Node;
import java.util.List;

public class ParserStage implements Stage<List<Token>, Node> {

    public static final String TAG_PROGRAM = "program";
    public static final String TAG_HAS_PARSE_ERRORS = "hasParseErrors";

    @Override
    public Node perform(StagesData stagesData, List<Token> input) {
        final Parser parser = new Parser(input);
        final Node program = parser.parse();
        final var parseErrors = parser.getParseErrors();
        stagesData.put(TAG_PROGRAM, program);
        stagesData.put(TAG_HAS_PARSE_ERRORS, parseErrors.hasErrors());
        if (parseErrors.hasErrors()) {
            throw new OwnLangParserException(parseErrors);
        }
        return program;
    }
}
