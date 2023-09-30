package com.annimon.ownlang.stages;

import com.annimon.ownlang.parser.Lexer;
import com.annimon.ownlang.parser.Token;
import java.util.List;

public class LexerStage implements Stage<String, List<Token>> {

    public static final String TAG_TOKENS = "tokens";

    @Override
    public List<Token> perform(StagesData stagesData, String input) {
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
        stagesData.put(TAG_TOKENS, tokens);
        return tokens;
    }
}
