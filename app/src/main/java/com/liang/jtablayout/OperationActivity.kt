package com.liang.jtablayout

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.util.Log
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.Toast
import com.liang.jtab.JTabLayout
import com.liang.jtab.indicator.JIndicator
import com.liang.jtab.listener.OnTabSelectedListener
import com.liang.jtab.view.TabView
import kotlinx.android.synthetic.main.activity_operation.*
import java.util.*

class OperationActivity : AppCompatActivity() {
    private val views = ArrayList<String>()
    private var adapter: ViewPagerAdapter? = null
    private var withViewPager: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operation)

        val indicator = JIndicator()
        indicator.isTransitionScroll = true
        jTabLayout.setIndicator(indicator)
        adapter = ViewPagerAdapter(this, views)
        ViewPager.adapter = adapter
        RadioGroup.check(R.id.RadioButton)

        tabLayout()

        initBadge()

    }

    private fun initBadge() {
        button5.setOnClickListener {
            val position = editText3.text.toString()
            val msg = editText4.text.toString()
            if (position.isEmpty()) {
                return@setOnClickListener
            }
            if (msg.isEmpty()) {
                jTabLayout.showBadgeMsg(Integer.parseInt(position))
            } else {
                jTabLayout.showBadgeMsg(Integer.parseInt(position), msg)
            }

        }

        button6.setOnClickListener {
            val position = editText3.text.toString()
            if (position.isEmpty()) {
                return@setOnClickListener
            }
            jTabLayout.hideBadgeMsg(Integer.parseInt(position))
        }
    }

    private fun tabLayout() {
        CheckBox.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            withViewPager = b;
            jTabLayout.removeAllTabs()
            views.clear()
            adapter?.notifyDataSetChanged()
            textView.text = ""
            jTabLayout.setupWithViewPager(if (b) ViewPager else null)
        }

        var inx = 0
        RadioGroup.setOnCheckedChangeListener { _: RadioGroup, i: Int ->
            when (i) {
                R.id.RadioButton -> jTabLayout.setMode(JTabLayout.MODE_FIXED)
                R.id.RadioButton1 -> jTabLayout.setMode(JTabLayout.MODE_SCROLLABLE)
            }
        }
        button.setOnClickListener {
            val position = editText.text.toString()
            val title = editText1.text.toString()

            if (position.isEmpty()) {
                if (withViewPager) {
                    views.add(title)
                    adapter?.notifyDataSetChanged();
                } else {
                    val tabView = jTabLayout.newTab().setTitle(title);
                    jTabLayout.addTab(tabView)
                }
                inx++
            } else {
                inx = Integer.parseInt(position)
                if (inx > views.size) {
                    return@setOnClickListener
                }
                if (withViewPager) {
                    views.add(inx, title)
                    adapter?.notifyDataSetChanged();
                } else {
                    jTabLayout.addTab(jTabLayout.newTab().setTitle(title), inx)
                }
            }
        }

        button2.setOnClickListener {
            val position = editText2.text.toString()
            if (position.isEmpty()) {
                if (withViewPager) {
                    views.clear()
                    adapter?.notifyDataSetChanged()
                } else {
                    jTabLayout.removeAllTabs()
                }
                inx = 0
            } else {
                if (views.size > Integer.parseInt(position)) {
                    inx = if (withViewPager) {
                        views.removeAt(Integer.parseInt(position))
                        adapter?.notifyDataSetChanged()
                        views.size
                    } else {
                        jTabLayout.removeTabAt(Integer.parseInt(position))
                        jTabLayout.tabCount
                    }
                }
            }

            if (inx == 0) {
                textView.text = ""
            }
        }


        jTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(position: Int) {
                textView.text = "onTabSelected position: $position"
            }

            override fun onTabReselected(position: Int) {
                textView.text = "onTabReselected position: $position"
            }
        })
    }
}
