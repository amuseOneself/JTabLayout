package com.liang.jtablayout;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tab2Activity extends AppCompatActivity {

    JTabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter adapter;

    private String[] titles = {"首页", "新闻", "影视歌曲", "民生", "手机电脑数码", "娱乐", "排名", "消息", "我的", "其他"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab2);
        tabLayout = findViewById(R.id.jTabLayout);
        viewPager = findViewById(R.id.viewPager);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.tab_icon_hall_normal, R.mipmap.tab_icon_hall_press).setText("娱乐"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.tab_icon_record_normal, R.mipmap.tab_icon_record_press).setText("排名"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.tab_icon_chat_normal, R.mipmap.tab_icon_chat_press).setText("消息"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.tab_icon_user_normal, R.mipmap.tab_icon_user_press).setText("我的"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.tab_icon_hall_normal, R.mipmap.tab_icon_hall_press).setText("娱乐"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.tab_icon_record_normal, R.mipmap.tab_icon_record_press).setText("排名"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.tab_icon_chat_normal, R.mipmap.tab_icon_chat_press).setText("消息"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.tab_icon_user_normal, R.mipmap.tab_icon_user_press).setText("我的"));

        adapter = new ViewPagerAdapter(this, Arrays.asList(titles));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
