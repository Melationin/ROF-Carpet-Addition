package com.carpet.rof.annotation;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;


@SupportedAnnotationTypes("com.carpet.rof.annotation.lifecycle")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class ScopeProcessor extends ROFProcessor
{


    @Override
    public Class<? extends Annotation> annotation() {
        return Scope.class;
    }

    @Override
    public String className() {
        return "SettingList";
    }

    @Override
    protected boolean validateStaticMethod(TypeElement type) {
        return true;
    }
}
