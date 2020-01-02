package com.liang.tab3xdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.liang.tablayout3x.LinearLayoutManager
import com.liang.widget.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tabLayout.layoutManager = LinearLayoutManager(this,TabLayout.Vertical,TabLayout.ModeScrollable)
        repeat(10){
            tabLayout.addTab(tabLayout.newTab().setTitle("Test: $it"))
        }
    }
}
