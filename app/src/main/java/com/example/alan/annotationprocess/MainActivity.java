package com.example.alan.annotationprocess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.annotation.AnnotationFIELD;
import com.example.mybutterknife.MyButterknife;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @AnnotationFIELD(R.id.text)
    TextView textView;

//    @AnnotationFIELD(2323)
//    private List<String> list;

    //生成类的时候应该要先查找下这个id是不是在R文件里面？？
    @AnnotationFIELD(2323)
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyButterknife.bind(this);
        textView.setText("name ");
    }
}
