package com.example.alan.annotationprocess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.annotation.AnnotationFIELD;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @AnnotationFIELD(R.id.text)
    private TextView textView;

    @AnnotationFIELD(2323)
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
