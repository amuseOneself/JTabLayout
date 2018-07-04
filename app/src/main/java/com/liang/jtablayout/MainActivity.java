package com.liang.jtablayout;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.liang.jtab.JTabLayout;
import com.liang.jtab.SlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout layout;
    private JTabLayout scrollView;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private List<View> views = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.JTabLayout);
        viewPager = findViewById(R.id.ViewPager);
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
                views.add(getLayoutInflater().inflate(R.layout.flash_one, null));
                adapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                scrollView.addTab(getLayoutInflater().inflate(R.layout.flash_one, null));
                if (views.size() > 0) {
                    views.remove(views.size() - 1);
                    adapter.notifyDataSetChanged();
                }

            }
        });
    }
}
