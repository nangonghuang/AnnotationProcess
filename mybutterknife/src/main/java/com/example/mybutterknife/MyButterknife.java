package com.example.mybutterknife;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.view.View;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by alan on 2017/12/3.
 */

public class MyButterknife {

    public static void bind(Activity target) {
        View view = target.getWindow().getDecorView();
        Class<? extends Activity> clazz = target.getClass();
        String generateClassName = clazz.getName() + "_ViewBinding";
        try {
            Class.forName(generateClassName).getConstructor(clazz, View.class).newInstance(target, view);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static <T> T findViewByCast(View source, @IdRes int id, Class<T> cls) {
        View view = source.findViewById(id);
        return castView(view, id, cls);
    }

    private static <T> T castView(View view, @IdRes int id, Class<T> cls) {
        try {
            return cls.cast(view);
        } catch (ClassCastException e) {
            //提示使用者类型转换异常
            throw new IllegalStateException(view.getClass().getName() + "不能强转成" + cls.getName());
        }
    }
}