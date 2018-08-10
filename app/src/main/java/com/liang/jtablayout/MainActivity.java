package com.liang.jtablayout;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.liang.jtab.indicator.JIndicator;
import com.liang.jtab.JTabLayout;
import com.liang.jtab.listener.OnTabSelectedListener;

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

    int i;

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

        scrollView.setupWithViewPager(viewPager, false);
        i = 0;
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                scrollView.addTab(getLayoutInflater().inflate(R.layout.flash_one, null));
                String str = editText1.getText().toString();
                if (str.isEmpty()) {
                    views.add(getLayoutInflater().inflate(R.layout.flash_one, null));
                    adapter.notifyDataSetChanged();
                    scrollView.addTab(scrollView.newTab().setTitle("Tab:" + i).setTitleColor(Color.MAGENTA, Color.GREEN)
                            .setIcon(android.R.drawable.ic_media_pause,android.R.drawable.ic_media_play));
                    i++;

                } else {
                    i = Integer.parseInt(str);
                    views.add(Integer.parseInt(str), getLayoutInflater().inflate(R.layout.flash_one, null));
                    adapter.notifyDataSetChanged();
                    scrollView.addTab(scrollView.newTab().setTitle("添加:" + i), Integer.parseInt(str));
                }
            }
        });

        JIndicator indicator = new JIndicator();
        indicator.setTransitionScroll(true);
        scrollView.setIndicator(indicator);

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                scrollView.addTab(getLayoutInflater().inflate(R.layout.flash_one, null));

                String str = editText2.getText().toString();
                if (str.isEmpty()) {
                    views.clear();
                    adapter.notifyDataSetChanged();
                    scrollView.removeAllTabs();
                    i = 0;
                } else {
                    scrollView.removeTabAt(Integer.parseInt(str));
                    if (views.size() > 0) {
                        views.remove(Integer.parseInt(str));
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        scrollView.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                Log.e("OnTabSelectedListener", "onTabSelected: ..." + position);
            }

            @Override
            public void onTabUnselected(int position) {
                Log.e("OnTabSelectedListener", "onTabUnselected: ..." + position);
            }

            @Override
            public void onTabReselected(int position) {
                Log.e("OnTabSelectedListener", "onTabReselected: ..." + position);
            }
        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
