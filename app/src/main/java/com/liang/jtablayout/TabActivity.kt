package com.liang.jtablayout

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.liang.jtab.JTabLayout

class TabActivity : AppCompatActivity() {

    private var tabLayout1: JTabLayout? = null
    private var tabLayout2: JTabLayout? = null
    private var tabLayout3: JTabLayout? = null
    private var tabLayout4: JTabLayout? = null
    private var tabLayout5: JTabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        tabLayout1 = findViewById(R.id.tabLayout1)
        tabLayout2 = findViewById(R.id.tabLayout2)
        tabLayout3 = findViewById(R.id.tabLayout3)
        tabLayout4 = findViewById(R.id.tabLayout4)
        tabLayout5 = findViewById(R.id.tabLayout5)

        initTab1()
        initTab2()
        initTab3()
        initTab4()
        initTab5()

    }

    private fun initTab1() {
        tabLayout1?.addTab(tabLayout1?.newTab()?.setIcon(R.mipmap.icon_qipaishi_normal, R.mipmap.icon_qipaishi_press)?.setTitle("娱乐"))
        tabLayout1?.addTab(tabLayout1?.newTab()?.setIcon(R.mipmap.icon_zhanji_normal, R.mipmap.icon_zhanji_press)?.setTitle("排名"))
        tabLayout1?.addTab(tabLayout1?.newTab()?.setIcon(R.mipmap.icon_xiaoxi_normal, R.mipmap.icon_xiaoxi_press)?.setTitle("消息"))
        tabLayout1?.addTab(tabLayout1?.newTab()?.setIcon(R.mipmap.icon_wode_normal, R.mipmap.icon_wode_press)?.setTitle("我的"))
        tabLayout1?.setBadgeColor(0, Color.YELLOW)
        tabLayout1?.setBadgeTextColor(0, Color.RED)
        tabLayout1?.setTabMsg(0, "火热")
        tabLayout1?.setTabMsg(2, 5)
        tabLayout1?.setTabMsgDot(3)
    }

    private fun initTab2() {
        tabLayout2?.addTab(tabLayout2?.newTab()?.setIcon(R.mipmap.tab_icon_hall_normal, R.mipmap.tab_icon_hall_press)?.setTitle("娱乐"))
        tabLayout2?.addTab(tabLayout2?.newTab()?.setIcon(R.mipmap.tab_icon_record_normal, R.mipmap.tab_icon_record_press)?.setTitle("排名"))
        tabLayout2?.addTab(tabLayout2?.newTab()?.setIcon(R.mipmap.tab_icon_chat_normal, R.mipmap.tab_icon_chat_press)?.setTitle("消息"))
        tabLayout2?.addTab(tabLayout2?.newTab()?.setIcon(R.mipmap.tab_icon_user_normal, R.mipmap.tab_icon_user_press)?.setTitle("我的"))

        tabLayout2?.setBadgeColor(0, Color.YELLOW)
        tabLayout2?.setBadgeTextColor(0, Color.RED)
        tabLayout2?.setBadgeStroke(0, 3, Color.RED)
        tabLayout2?.setTabMsg(0, "火热")
        tabLayout2?.setTabMsg(1, "新")
        tabLayout2?.setTabMsg(2, 952)
        tabLayout2?.setTabMsgDot(3)
    }

    private fun initTab3() {
        tabLayout3?.addTab(tabLayout3?.newTab()?.setIcon(R.mipmap.icon_qipaishi_normal, R.mipmap.icon_qipaishi_press)?.setTitle("娱乐"))
        tabLayout3?.addTab(tabLayout3?.newTab()?.setIcon(R.mipmap.icon_zhanji_normal, R.mipmap.icon_zhanji_press)?.setTitle("排名"))
        tabLayout3?.addTab(tabLayout3?.newTab()?.setIcon(R.mipmap.icon_xiaoxi_normal, R.mipmap.icon_xiaoxi_press)?.setTitle("消息"))
        tabLayout3?.addTab(tabLayout3?.newTab()?.setIcon(R.mipmap.icon_wode_normal, R.mipmap.icon_wode_press)?.setTitle("我的"))
    }

    private fun initTab4() {
        tabLayout4?.addTab(tabLayout4?.newTab()?.setTitle("娱乐"))
        tabLayout4?.addTab(tabLayout4?.newTab()?.setTitle("排名"))
        tabLayout4?.addTab(tabLayout4?.newTab()?.setTitle("消息"))
        tabLayout4?.addTab(tabLayout4?.newTab()?.setTitle("我的"))
        tabLayout4?.setTabPadding(20, 0, 20, 0)
    }

    private fun initTab5() {
        tabLayout5?.addTab(tabLayout5?.newTab()?.setTitle("娱乐"))
        tabLayout5?.addTab(tabLayout5?.newTab()?.setTitle("排名"))
        tabLayout5?.addTab(tabLayout5?.newTab()?.setTitle("消息"))
        tabLayout5?.addTab(tabLayout5?.newTab()?.setTitle("我的"))
    }
}
