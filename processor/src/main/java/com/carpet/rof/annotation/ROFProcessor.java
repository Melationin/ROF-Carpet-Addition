package com.carpet.rof.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public abstract class ROFProcessor extends AbstractProcessor {

    private boolean generated = false;


    public abstract  Class<? extends java.lang.annotation.Annotation> annotation();
    public abstract  String className();
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        if (generated || roundEnv.processingOver()) {
            return false;
        }

        Set<? extends Element> elements =
                roundEnv.getElementsAnnotatedWith(annotation());

        if (elements.isEmpty()) return false;

        try {
            generateRegistry(elements);
            generated = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private void generateRegistry(Set<? extends Element> elements)
            throws IOException {

        String pkg = "com.carpet.rof.generated";
        String cls = className();

        JavaFileObject file = processingEnv
                .getFiler()
                .createSourceFile(pkg + "." + cls);

        try (Writer w = file.openWriter()) {
            w.write("package " + pkg + ";\n\n");
            w.write("import java.util.List;\n\n");
            w.write("public final class " + cls + " {\n");
            w.write("  public static final List<String> CLASS_NAMES = List.of(\n");

            int i = 0;
            for (Element e : elements) {
                TypeElement t = (TypeElement) e;
                if(validateStaticMethod(t)) {
                    w.write("    \"" + t.getQualifiedName() + "\"");
                    if (elements.size() > ++i) w.write(",\n");
                }
            }

            w.write("  );\n");
            w.write("}\n");
        }
    }

    protected abstract boolean validateStaticMethod(TypeElement type);
}
