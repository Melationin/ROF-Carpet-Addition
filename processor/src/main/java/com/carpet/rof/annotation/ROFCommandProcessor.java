package com.carpet.rof.annotation;



import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;

@SupportedAnnotationTypes("com.carpet.rof.annotation.ROFCommand")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class ROFCommandProcessor extends ROFProcessor {


    @Override
    public Class<? extends Annotation> annotation() {
        return ROFCommand.class;
    }

    @Override
    public String className() {
        return "CommandList";
    }

    @Override
    protected boolean validateStaticMethod(TypeElement type) {



        boolean found = false;

        for (Element e : type.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                ExecutableElement m = (ExecutableElement) e;

                if (m.getSimpleName().contentEquals("register")
                        && m.getParameters().size() == 1
                        && m.getModifiers().contains(Modifier.STATIC)
                        && m.getModifiers().contains(Modifier.PUBLIC)) {
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Classes annotated with @ROFCommand must declare: "
                            + "public static void register()",
                    type
            );
        }
        return found;
    }
}