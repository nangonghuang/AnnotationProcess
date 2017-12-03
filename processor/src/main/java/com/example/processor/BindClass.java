package com.example.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * 用于表示生成的类，比如A，B类都有注解，会生成两个此对象
 * ClassName  ： javaPoet里面表示类类型的类，不要用class.getName了。。继承自TypeName，用于方法参数，
 *              比如我们在Java里面用String.class，在这里用TypeName.get(String.class)转成 TypeName
 */

public class BindClass {
    private final ClassName UTILS = ClassName.get("com.example.mybutterknife", "MyButterknife");
    private final ClassName VIEW = ClassName.get("android.view", "View");

    private TypeName targetTypeName;
    private ClassName bindingClassName;
    private boolean isFinal;
    /**
     * 用于保存一个类里面使用的注解的条目
     */
    private List<ViewBinding> fields;

    public BindClass(TypeName targetTypeName, ClassName bindingClassName,boolean isFinal) {
        this.targetTypeName = targetTypeName;
        this.bindingClassName = bindingClassName;
        this.isFinal = isFinal;
        fields = new ArrayList<>();
    }

    void addAnnotationField(ViewBinding viewBinding) {
        fields.add(viewBinding);
    }

    /**
     * 生成文件内容，此时还没有指定文件的位置
     * @return
     */
    JavaFile preJavaFile() {
        return JavaFile.builder(bindingClassName.packageName(), createTypeSpec())
                .addFileComment("Generated code from My Butter Knife. Do not modify!!!")
                .build();
    }

    /**
     * 创建类的描述信息
     * @return
     */
    private TypeSpec createTypeSpec() {
        TypeSpec.Builder result = TypeSpec.classBuilder(bindingClassName.simpleName())
                .addModifiers(PUBLIC);
        if (isFinal) {
            result.addModifiers(FINAL);
        }
        result.addMethod(createConstructor(targetTypeName));
        return result.build();
    }


    private MethodSpec createConstructor(TypeName targetType) {
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC);

        constructor.addParameter(targetType, "target", FINAL);
        constructor.addParameter(VIEW, "source");
        for (ViewBinding bindings : fields) {
            addViewBinding(constructor, bindings);
        }

        return constructor.build();
    }

    private void addViewBinding(MethodSpec.Builder result, ViewBinding binding) {
        // Optimize the common case where there's a single binding directly to a field.
        //FieldViewBinding fieldBinding = bindings.getFieldBinding();
        CodeBlock.Builder builder = CodeBlock.builder()
                .add("target.$L = ", binding.variableName);

        boolean requiresCast = requiresCast(binding.fieldType);
        if (!requiresCast) {
            builder.add("source.findViewById($L)", binding.id);
        } else {
            builder.add("$T.findViewByCast", UTILS);
            //builder.add(fieldBinding.isRequired() ? "RequiredView" : "OptionalView");
            //if (requiresCast) {
            //    builder.add("AsType");
            //}
            builder.add("(source, $L", binding.id);
            builder.add(", $T.class", binding.fieldType);
            builder.add(")");
        }
        result.addStatement("$L", builder.build());

    }


    private static boolean requiresCast(TypeName type) {
        return !"android.view.View".equals(type.toString());
    }

}
