package com.annimon.ownlang.parser.optimization;

import com.annimon.ownlang.parser.ast.Node;

public interface Optimizable {

    Node optimize(Node node);

    int optimizationsCount();

    String summaryInfo();
}
