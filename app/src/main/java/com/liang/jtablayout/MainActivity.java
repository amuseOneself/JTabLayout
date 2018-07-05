package com.liang.jtablayout;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.liang.jtab.JTabLayout;
import com.liang.jtab.SlidingTabStrip;
import com.liang.jtab.TabView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout layout;
    private JTabLayout scrollView;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private List<View> views = new ArrayList<>();

    private EditText editText1;
    private EditText editText2;

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.JTabLayout);
        viewPager = findViewById(R.id.ViewPager);
        editText1 = findViewById(R.id.button3);
        editText2 = findViewById(R.id.button4);
        linearLayout = findViewById(R.id.LinearLayout);
        adapter = new ViewPagerAdapter(views);
        viewPager.setAdapter(adapter);
        layout = scrollView.getTabStrip();

//        scrollView.addView(layout, new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        scrollView.setViewPager(viewPager);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                scrollView.addTab(getLayoutInflater().inflate(R.layout.flash_one, null));
//                views.add(getLayoutInflater().inflate(R.layout.flash_one, null));
//                adapter.notifyDataSetChanged();

                String str = editText1.getText().toString();
                if (str.isEmpty()) {
                    scrollView.addTab(new TabView(MainActivity.this).setTitle("123456").setTitleColor(Color.GRAY, Color.BLUE));
                } else {
                    scrollView.addTab(new TabView(MainActivity.this).setTitle("添加"), Integer.parseInt(str));
                }


            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                scrollView.addTab(getLayoutInflater().inflate(R.layout.flash_one, null));
//                if (views.size() > 0) {
//                    views.remove(views.size() - 1);
//                    adapter.notifyDataSetChanged();
//                }

                String str = editText2.getText().toString();
                if (str.isEmpty()) {
                    scrollView.removeAllTabs();
                } else {
                    scrollView.removeTabAt(Integer.parseInt(str));
                }
            }
        });
    }
}
