package com.annimon.ownlang.parser.linters;

import com.annimon.ownlang.parser.ast.IncludeStatement;
import com.annimon.ownlang.parser.ast.ValueExpression;
import com.annimon.ownlang.util.input.InputSourceDetector;

/**
 *
 * @author aNNiMON
 */
final class IncludeSourceValidator extends LintVisitor {
    private final InputSourceDetector detector;

    IncludeSourceValidator(LinterResults results) {
        super(results);
        detector = new InputSourceDetector();
    }

    @Override
    public void visit(IncludeStatement s) {
        super.visit(s);
        if (s.expression instanceof ValueExpression expr) {
            final String path = expr.eval().asString();
            if (!detector.isReadable(path)) {
                results.add(LinterResult.error(
                        "Include statement path \"%s\" is not readable".formatted(path),
                        s.getRange()));
            }
        } else {
            results.add(LinterResult.warning(
                    "Include statement path \"%s\" is not a constant string".formatted(s.expression),
                    s.getRange()));
        }
    }
}
