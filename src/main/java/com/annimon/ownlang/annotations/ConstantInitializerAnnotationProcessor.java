package com.annimon.ownlang.annotations;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes("com.annimon.ownlang.annotations.ConstantInitializer")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ConstantInitializerAnnotationProcessor extends AbstractProcessor {

    // https://github.com/corgrath/abandoned-Requires-Static-Method-Annotation
    private static final String METHOD_NAME = "initConstants";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ConstantInitializer.class);
        for (Element element : elements) {
            final Optional<? extends Element> result = element.getEnclosedElements().stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD)
                    .filter(m -> m.getSimpleName().contentEquals(METHOD_NAME))
                    .filter(m -> m.getModifiers().containsAll(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC)))
                    .filter(m -> m.asType().accept(visitor, null))
                    .findFirst();
            if (result.isPresent()) {
                showError(element);
                return true;
            }
        }
        return true;
    }

    private void showError(Element element) {
        final String message = String.format("Class %s requires a method"
                + " `public static void %s() {}`", element.getSimpleName(), METHOD_NAME);
        processingEnv.getMessager().printMessage(Kind.ERROR, message, element);
    }

    private final SimpleTypeVisitor6<Boolean, Void> visitor = new SimpleTypeVisitor6<Boolean, Void>() {
        @Override
        public Boolean visitExecutable(ExecutableType t, Void v) {
            if (t.getReturnType().getKind() != TypeKind.VOID) return false;
            if (!t.getParameterTypes().isEmpty()) return false;
            return true;
        }
    };
}
