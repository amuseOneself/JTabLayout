package com.liang.jtablayout

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.liang.jtablayout.R.id.scrollView
import kotlinx.android.synthetic.main.activity_operation.*
import java.util.ArrayList

class OperationActivity : AppCompatActivity() {
    private val views = ArrayList<String>()
    private var adapter: ViewPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operation)
        adapter = ViewPagerAdapter(this, views)
        ViewPager.adapter = adapter
        jTabLayout.setupWithViewPager(ViewPager)
        var inx = 0
        button.setOnClickListener {
            val str = editText.text.toString()
            if (str.isEmpty()) {
                views.add("Tab:$inx")
                adapter?.notifyDataSetChanged();
//                scrollView.addTab(scrollView.newTab().setTitle("Tab:$i").setTitleColor(Color.MAGENTA, Color.GREEN)
//                        .setIcon(android.R.drawable.ic_media_pause, android.R.drawable.ic_media_play))
                inx++

            } else {
                inx = Integer.parseInt(str)
                views.add("Tab:$inx")
                adapter?.notifyDataSetChanged();
//                scrollView.addTab(scrollView.newTab().setTitle("添加:$i"), Integer.parseInt(str))
            }
        }
    }
}
