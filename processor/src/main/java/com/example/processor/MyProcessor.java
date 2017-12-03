package com.example.processor;

import com.example.annotation.AnnotationFIELD;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.swing.text.View;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by alan on 2017/12/2.
 */
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    static final String NAME_SUFFIX = "_ViewBinding";
    String packageName;
    private String className;
    private String classFullName;

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
//        Map<Element, ViewBinding> map = new LinkedHashMap<>();
//        List<ViewBinding> list = new ArrayList<>();
//
//        for (Element element : roundEnvironment.getElementsAnnotatedWith(AnnotationFIELD.class)) {
//            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
//            Elements elementUtils = processingEnv.getElementUtils();
//            PackageElement packageElement = elementUtils.getPackageOf(typeElement);
//
//            className = typeElement.getSimpleName() + NAME_SUFFIX;
//            classFullName = typeElement.getQualifiedName().toString();
//            packageName = packageElement.getQualifiedName().toString();
//
//            TypeMirror typeMirror = element.asType();
//            String type = typeMirror.toString();
//            //注解的值
//            int value = element.getAnnotation(AnnotationFIELD.class).value();
//
//            ViewBinding viewBinding = new ViewBinding(type, element.getSimpleName().toString(), value);
//            list.add(viewBinding);
//
//            printMessageInGradleConsole("viewBinding : " + viewBinding);
//        }
//
//        try {
//            printMessageInGradleConsole("classFullName : " + classFullName);
//            MethodSpec.Builder constructorbuilder = MethodSpec.constructorBuilder()
//                    .addModifiers(Modifier.PUBLIC)
//                    .addParameter(this.getClass(), "activity");
//            constructorbuilder.addStatement("$T decor = $L", View.class.getName(), "activity.getWindow().getDecorView()");
//            for (ViewBinding viewBinding : list) {
//                constructorbuilder.addStatement("$S $S = $S.findViewById($S)", viewBinding.fieldType, viewBinding.variableName, "decor", "1212314");
//            }
//            constructorbuilder.addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!");
//
//            TypeSpec helloWorld = TypeSpec.classBuilder(className)
//                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                    .addMethod(constructorbuilder.build())
//                    .build();
//
//            JavaFile javaFile = JavaFile.builder(packageName, helloWorld)
//                    .build();
//
//            TypeName.get()
//            javaFile.writeTo(new File("d:", className));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        parseRoundEnvironment(roundEnvironment);

        printMessageInGradleConsole(" === end : ===");
        return true;
    }

    private void parseRoundEnvironment(RoundEnvironment roundEnv) {
        Map<TypeElement, BindClass> map = new LinkedHashMap<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(AnnotationFIELD.class)) {
            TypeElement classElement = (TypeElement) element.getEnclosingElement(); //表示注解所在的类

            int annotationValue = element.getAnnotation(AnnotationFIELD.class).value();

            BindClass bindClass = map.get(classElement);
            if (bindClass == null) {
                bindClass = getBindClass(classElement);
                map.put(classElement, bindClass);
            }
            ViewBinding viewBinding = getBindItemInfo(element, annotationValue);
            bindClass.addAnnotationField(viewBinding);
        }


        for (Map.Entry<TypeElement, BindClass> entry : map.entrySet()) {
            try {
                entry.getValue().preJavaFile().writeTo(processingEnv.getFiler());//一个一个类去生成
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private ViewBinding getBindItemInfo(Element element, int annotationValue) {
        String name = element.getSimpleName().toString();
        TypeName type = TypeName.get(element.asType());
        return new ViewBinding(type, name, annotationValue);
    }

    private BindClass getBindClass(TypeElement classElement) {
        TypeName targetType = TypeName.get(classElement.asType());//表示注解所在的类
        if (targetType instanceof ParameterizedTypeName) {  //如果注解有泛型参数
            targetType = ((ParameterizedTypeName) targetType).rawType;
        }
        //比如com.example.alan.annotationprocess.MainActivity，就拿到
        //com.example.alan.annotationprocess了
        String packageName = classElement.getQualifiedName().toString();
        packageName = packageName.substring(0, packageName.lastIndexOf("."));
        String className = classElement.getSimpleName().toString();
        ClassName bindingClassName = ClassName.get(packageName, className + "_ViewBinding");

        boolean isFinal = classElement.getModifiers().contains(Modifier.FINAL);

        return new BindClass(targetType, bindingClassName, isFinal);
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
        //注解修饰的变量的名字
        Name simpleName = element.getSimpleName();
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

        printMessageInGradleConsole("simpleName : " + simpleName);
        printMessageInGradleConsole("className : " + className);
        printMessageInGradleConsole("packageName : " + packageName);
        printMessageInGradleConsole("type : " + type);
        printMessageInGradleConsole("value : " + value);
    }

    /**
     * AbstractProcessor的函数,需要重写，就不用加注解了
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
     * AbstractProcessor的函数,需要重写，就不用加注解了
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
