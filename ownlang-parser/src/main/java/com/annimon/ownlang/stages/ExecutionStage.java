package com.annimon.ownlang.stages;

import com.annimon.ownlang.parser.ast.Node;

public class ExecutionStage implements Stage<Node, Node> {

    @Override
    public Node perform(StagesData stagesData, Node input) {
        input.eval();
        return input;
    }
}
