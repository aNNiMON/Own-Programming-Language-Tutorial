package com.annimon.ownlang.utils;

import com.annimon.ownlang.parser.Lexer;
import com.annimon.ownlang.parser.Parser;
import com.annimon.ownlang.parser.SourceLoader;
import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.optimization.ConstantFolding;
import com.annimon.ownlang.parser.optimization.ConstantPropagation;
import com.annimon.ownlang.parser.optimization.DeadCodeElimination;
import com.annimon.ownlang.parser.optimization.ExpressionSimplification;
import com.annimon.ownlang.parser.optimization.InstructionCombining;
import com.annimon.ownlang.parser.optimization.Optimizable;
import com.annimon.ownlang.parser.visitors.PrintVisitor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public final class OptimizationDumper {

    private static final Optimizable[] OPTIMIZATIONS = new Optimizable[] {
        new ConstantFolding(),
        new ConstantPropagation(),
        new DeadCodeElimination(),
        new ExpressionSimplification(),
        new InstructionCombining()
    };
    private static final File WORK_DIR = new File("optimizations");

    public static void main(String[] args) throws Exception {
        WORK_DIR.mkdir();
        final String input = (args.length >= 1) ? args[0] : "program.own";
        final Map<String, String> optimizationSteps = getOptimizationSteps(input);
        writeStepsToFile(optimizationSteps);
        writeSummary(optimizationSteps);
    }

    private static Map<String, String> getOptimizationSteps(String input) throws IOException {
        final Map<String, String> result = new LinkedHashMap<>();
        Node node = Parser.parse(Lexer.tokenize(SourceLoader.readSource(input)));
        int optimizationPasses = 1;
        int lastBatchModificationCount;
        int batchModificationCount = 0;
        result.put("Source", nodeToString(node));
        do {
            lastBatchModificationCount = batchModificationCount;
            batchModificationCount = 0;
            for (Optimizable optimization : OPTIMIZATIONS) {
                final String lastSource = nodeToString(node);
                node = optimization.optimize(node);
                final String currentSource = nodeToString(node);
                if (!lastSource.equals(currentSource)) {
                    final String optName = String.format("%s, %d pass",
                            optimization.getClass().getSimpleName(),
                            optimizationPasses);
                    result.put(optName, nodeToString(node));
                }
                batchModificationCount += optimization.optimizationsCount();
            }
            optimizationPasses++;
        } while (lastBatchModificationCount != batchModificationCount);
        return result;
    }

    private static String nodeToString(Node n) {
//        return n.toString();
        return n.accept(new PrintVisitor(), new StringBuilder()).toString();
    }

    private static void writeStepsToFile(Map<String, String> optimizationSteps) throws IOException {
        Arrays.stream(WORK_DIR.listFiles((d, name) -> name.endsWith(".txt")))
                .forEach(File::delete);

        int counter = 1;
        for (Map.Entry<String, String> entry : optimizationSteps.entrySet()) {
            final String filename = String.format("file_%d.txt", counter++);
            final File file = new File(WORK_DIR, filename);
            writeContent(file, writer -> {
                writer.append(entry.getKey());
                writer.append("\n\n");
                writer.append(entry.getValue());
            });
        }
    }

    private static void writeSummary(final Map<String, String> optimizationSteps) throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append("[pr]");
        for (Map.Entry<String, String> entry : optimizationSteps.entrySet()) {
            sb.append(entry.getKey());
            sb.append("\n[code own]");
            sb.append(entry.getValue());
            sb.append("[/code][sl]\n");
        }
        sb.append("[/pr]");
        writeContent(new File(WORK_DIR, "summary.txt"),
                writer -> writer.write(sb.toString()));
    }

    private static void writeContent(File file, ThrowableConsumer<Writer> consumer) throws IOException {
        try (OutputStream out = new FileOutputStream(file);
             OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8")) {
            consumer.accept(writer);
        }
    }

    interface ThrowableConsumer<T> {
        void accept(T t) throws IOException;
    }
}
