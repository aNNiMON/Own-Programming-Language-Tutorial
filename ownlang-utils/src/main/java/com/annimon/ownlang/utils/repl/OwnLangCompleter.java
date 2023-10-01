package com.annimon.ownlang.utils.repl;

import com.annimon.ownlang.lib.ScopeHandler;
import com.annimon.ownlang.parser.Lexer;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jline.console.completer.StringsCompleter;

public final class OwnLangCompleter extends StringsCompleter {

    private final Set<String> staticCandidates;

    public OwnLangCompleter(String... candidates) {
        staticCandidates = new HashSet<>();
        Collections.addAll(staticCandidates, candidates);
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        updateCandidates();
        return super.complete(buffer, cursor, candidates);
    }

    private void updateCandidates() {
        getStrings().clear();
        getStrings().addAll(Lexer.getKeywords());
        getStrings().addAll(staticCandidates);
        getStrings().addAll(ScopeHandler.constants().keySet());
        getStrings().addAll(ScopeHandler.variables().keySet());
        getStrings().addAll(ScopeHandler.functions().keySet());
    }
}
