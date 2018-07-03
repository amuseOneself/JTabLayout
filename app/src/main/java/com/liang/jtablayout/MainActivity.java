package com.liang.jtablayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.liang.jtab.JTabLayout;
import com.liang.jtab.SlidingTabStrip;

public class MainActivity extends AppCompatActivity {

    private LinearLayout layout;
    private JTabLayout scrollView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.JTabLayout);
        layout = scrollView.getTabStrip();
//        scrollView.addView(layout, new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.addTab(getLayoutInflater().inflate(R.layout.flash_one, null));
            }
        });
    }
}
