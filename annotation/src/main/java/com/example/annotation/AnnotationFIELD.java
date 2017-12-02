package com.example.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by alan on 2017/12/2.
 */
@Target(FIELD)
@Retention(CLASS)
public @interface AnnotationFIELD {

    int value() default 0;
}
