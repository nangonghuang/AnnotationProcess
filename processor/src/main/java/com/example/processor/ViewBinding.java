package com.example.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

/**
 * Created by alan on 2017/12/3.
 * 用来描述注解的每一个条目
 */

public class ViewBinding {
    /**
     * 注解所描述的类的类型
     */
    TypeName fieldType;
    /**
     * 注解所描述的变量名字
     */
    String variableName;
    /**
     * 注解的成员值
     */
    int id;

    public ViewBinding(TypeName fieldType, String variableName, int id) {
        this.fieldType = fieldType;
        this.variableName = variableName;
        this.id = id;
    }

    ClassName getRawType() {
        if (fieldType instanceof ParameterizedTypeName) {
            return ((ParameterizedTypeName) fieldType).rawType;
        }
        return (ClassName) fieldType;
    }

    @Override
    public String toString() {
        return "ViewBinding{" +
                "fieldType='" + fieldType + '\'' +
                ", variableName='" + variableName + '\'' +
                ", id=" + id +
                '}';
    }
}
