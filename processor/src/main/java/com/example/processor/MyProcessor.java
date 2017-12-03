package com.example.processor;

import com.example.annotation.AnnotationFIELD;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by alan on 2017/12/2.
 */
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    static final String CLASS_NAME = "GeneratedClass";
    final String PACKAGENAME = this.getClass().getPackage().getName();

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        StringBuilder builder = new StringBuilder()
                .append("package ")
                .append(PACKAGENAME)
                .append(";\n\n")
                .append("public class " + CLASS_NAME + " {\n\n") // open class
                .append("\tpublic String getMessage() {\n") // open method
                .append("\t\treturn \"");

        // for each javax.lang.model.element.Element annotated with the CustomAnnotation
        for (Element element : roundEnvironment.getElementsAnnotatedWith(AnnotationFIELD.class)) {
            String objectType = element.getSimpleName().toString();
            printMessageInGradleConsole("====objectType :==== " + objectType);
            builder.append(objectType).append(" says hello! ");
        }

        builder.append("\";\n") // end return
                .append("\t}\n") // close method
                .append("}\n"); // close class

        printMessageInGradleConsole("Package : " + PACKAGENAME);

        try { // write the file
            JavaFileObject source = processingEnv.getFiler().createSourceFile(PACKAGENAME + "." + CLASS_NAME);
            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(AnnotationFIELD.class.getCanonicalName());

        return annotations;
    }

    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    void printMessageInGradleConsole(String str){
        processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING,str);
    }
}
