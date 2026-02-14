package com.carpet.rof.annotation;


import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.lang.annotation.Annotation;

@SupportedAnnotationTypes("com.carpet.rof.annotation.ROFLogger")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class ROFLoggerProcessor extends ROFProcessor
{


    @Override
    public Class<? extends Annotation> annotation()
    {
        return ROFLogger.class;
    }

    @Override
    public String className()
    {
        return "LoggerList";
    }

    @Override
    protected boolean validateStaticMethod(TypeElement type)
    {
        return true;
    }
}