package com.example.mybutterknife;

import android.app.Activity;

/**
 * Created by alan on 2017/12/3.
 */

public class MyButterknife {

    public static void bind(Activity target) {
        Class<? extends Activity> clazz = target.getClass();
        try {
            clazz.getClassLoader().loadClass(clazz.getName()+"_ViewBinding");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        }
    }
}