package com.carpet.rof.annotation;


import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

@SupportedAnnotationTypes("com.carpet.rof.annotation.ROFRule")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class ROFRuleProcessor extends ROFProcessor {


    @Override
    public Class<? extends Annotation> annotation() {
        return ROFRule.class;
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