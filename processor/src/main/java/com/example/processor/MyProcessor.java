package com.example.processor;

import com.example.annotation.AnnotationFIELD;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by alan on 2017/12/2.
 */
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    static final String CLASS_NAME = "GeneratedClass";
    final String PACKAGENAME = this.getClass().getPackage().getName();

    /**
     * AbstractProcessor的函数,需要重写
     *
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        printMessageInGradleConsole(" *** start : ***");
        Iterator<? extends TypeElement> iterator = set.iterator();
        while (iterator.hasNext()) {
            TypeElement next = iterator.next();
            printMessageInGradleConsole(next.getSimpleName());
        }

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
            builder.append(objectType).append(" says hello! ");


            logMsg(element);
        }

        builder.append("\";\n") // end return
                .append("\t}\n") // close method
                .append("}\n"); // close class


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

        printMessageInGradleConsole(" === end : ===");
        return true;
    }


    private void logMsg(Element element) {
        //ElementType.FIELD注解可以直接强转VariableElement
        VariableElement variableElement = (VariableElement) element;
        //ElementType.TYPE注解可以直接强转TypeElement，否则需要getEnclosingElement
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        //这里是util包下的Element
        Elements elementUtils = processingEnv.getElementUtils();
        PackageElement packageElement = elementUtils.getPackageOf(classElement);

        //注解用在哪个类
        String className = classElement.getQualifiedName().toString();
        //注解用在哪个包
        String packageName = packageElement.getQualifiedName().toString();
        //注解用在哪个类成员名（全路径名,包含参数信息)
        TypeMirror typeMirror = variableElement.asType();
        String type = typeMirror.toString();
        //注解的值
        int value = element.getAnnotation(AnnotationFIELD.class).value();
//        //取得方法参数列表
//        List<? extends VariableElement> methodParameters = executableElement.getParameters();
//        //参数类型列表
//        List<String> types = new ArrayList<>();
//        for (VariableElement var : methodParameters) {
//            TypeMirror methodParameterType = var.asType();
//            if (methodParameterType instanceof TypeVariable) {
//                TypeVariable typeVariable = (TypeVariable) methodParameterType;
//                methodParameterType = typeVariable.getUpperBound();
//
//            }
//            //参数名
//            String parameterName = var.getSimpleName().toString();
//            //参数类型
//            String parameteKind = methodParameterType.toString();
//            types.add(methodParameterType.toString());
//        }

        printMessageInGradleConsole("className : " + className);
        printMessageInGradleConsole("packageName : " + packageName);
        printMessageInGradleConsole("type : " + type);
        printMessageInGradleConsole("value : " + value);
    }

    /**
     * AbstractProcessor的函数,需要重写
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(AnnotationFIELD.class.getCanonicalName());

        return annotations;
    }

    /**
     * AbstractProcessor的函数,需要重写
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    void printMessageInGradleConsole(String str) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, str);
    }

    void printMessageInGradleConsole(CharSequence charSequence) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, charSequence);
    }
}
