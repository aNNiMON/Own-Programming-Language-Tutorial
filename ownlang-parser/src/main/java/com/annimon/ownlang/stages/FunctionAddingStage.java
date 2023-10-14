package com.annimon.ownlang.stages;

import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.visitors.FunctionAdder;

public class FunctionAddingStage implements Stage<Node, Node> {

    @Override
    public Node perform(StagesData stagesData, Node input) {
        input.accept(new FunctionAdder());
        return input;
    }
}
