package com.liang.jtablayout

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.liang.jtab.indicator.JIndicator
import kotlinx.android.synthetic.main.activity_tab.*

@RequiresApi(Build.VERSION_CODES.M)
class TabActivity : AppCompatActivity() {

    private val titles4 = arrayOf("首页", "影视歌曲", "民生", "手机电脑数码", "其他")
    private val titles5 = arrayOf("首页", "新闻", "影视歌曲", "民生", "手机电脑数码", "娱乐", "排名", "消息", "我的", "其他")

    private var adapter4: ViewPagerAdapter? = null
    private var adapter5: ViewPagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        initTab1()
        initTab2()
        initTab3()
        initTab4()
        initTab5()
        initTab6()
        initViewPager()

    }

    private fun initTab1() {
//        tabLayout1.addTab(TabMenu(this).setTitle("娱乐").setBackgroundRes(R.drawable.tab_bgl))
//        tabLayout1.addTab(TabMenu(this).setTitle("游戏").setBackgroundRes(R.drawable.tab_bgc))
//        tabLayout1.addTab(TabMenu(this).setTitle("排名").setBackgroundRes(R.drawable.tab_bgc))
//        tabLayout1.addTab(TabMenu(this).setTitle("最新").setBackgroundRes(R.drawable.tab_bgr))
        tabLayout1.setBadgeTextSize(10)
        tabLayout1.showBadgeMsg(0, 8)
        tabLayout1.showBadgeMsg(2)
        tabLayout1.showBadgeMsg(3, "新")
    }

    private fun initTab2() {
        tabLayout2.addTab(tabLayout2.newTab().setIcon(R.mipmap.tab_icon_hall_normal, R.mipmap.tab_icon_hall_press).setTitle("娱乐"))
        tabLayout2.addTab(tabLayout2.newTab().setIcon(R.mipmap.tab_icon_record_normal, R.mipmap.tab_icon_record_press).setTitle("排名"))
        tabLayout2.addTab(tabLayout2.newTab().setIcon(R.mipmap.tab_icon_chat_normal, R.mipmap.tab_icon_chat_press).setTitle("消息"))
        tabLayout2.addTab(tabLayout2.newTab().setIcon(R.mipmap.tab_icon_user_normal, R.mipmap.tab_icon_user_press).setTitle("我的"))

        tabLayout2.setBadgeColor(0, Color.YELLOW)
        tabLayout2.setBadgeColor(1, Color.MAGENTA)
        tabLayout2.setBadgeTextColor(0, Color.RED)
//        tabLayout2.setBadgeStroke(0, 3, Color.RED)
        tabLayout2.showBadgeMsg(0, "HOT !")
        tabLayout2.showBadgeMsg(1, "新")
        tabLayout2.showBadgeMsg(2, 952)
        tabLayout2.showBadgeMsg(3)
    }

    private fun initTab3() {
        tabLayout3.addTab(tabLayout3.newTab().setIcon(R.mipmap.tab_icon_hall_normal, R.mipmap.tab_icon_hall_press).setTitle("娱乐"))
        tabLayout3.addTab(tabLayout3.newTab().setIcon(R.mipmap.tab_icon_record_normal, R.mipmap.tab_icon_record_press).setTitle("排名"))
        tabLayout3.addTab(tabLayout3.newTab().setIcon(R.mipmap.tab_icon_chat_normal, R.mipmap.tab_icon_chat_press).setTitle("消息"))
        tabLayout3.addTab(tabLayout3.newTab().setIcon(R.mipmap.tab_icon_user_normal, R.mipmap.tab_icon_user_press).setTitle("我的"))
    }


    private fun initTab4() {
        for (title: String in titles4) {
            tabLayout4.addTab(tabLayout4.newTab().setTitle(title))
        }

        tabLayout4.getTabAt(2)?.setTitleColor(Color.GRAY, Color.RED)

        tabLayout4.getTabAt(3)?.setTitleColor(Color.GRAY, Color.GREEN)

        tabLayout4.getTabAt(1)?.setTitleColor(Color.GRAY, Color.MAGENTA)

        tabLayout4.setTabTextSize(15)

        val indicator = JIndicator()
        indicator.setColor(getColor(android.R.color.holo_orange_dark))
        indicator.height = 10
        tabLayout4.setIndicator(indicator)

        tabLayout4.showBadgeMsg(8)

    }


    private fun initTab5() {

        val indicator = JIndicator()
        indicator.setColor(getColor(android.R.color.holo_orange_dark))
        indicator.height = 10
        indicator.setRadius(5)
        indicator.isTransitionScroll = true
        tabLayout5.setIndicator(indicator)
        tabLayout5.setTabPadding(50, 0, 50, 0)

    }

    private fun initTab6() {
        tabLayout6.addTab(tabLayout6.newTab().setIcon(R.mipmap.icon_qipaishi_normal, R.mipmap.icon_qipaishi_press).setTitle("娱乐"))
        tabLayout6.addTab(tabLayout6.newTab().setIcon(R.mipmap.icon_zhanji_normal, R.mipmap.icon_zhanji_press).setTitle("排名"))
        tabLayout6.addTab(tabLayout6.newTab().setIcon(R.mipmap.icon_xiaoxi_normal, R.mipmap.icon_xiaoxi_press).setTitle("消息"))
        tabLayout6.addTab(tabLayout6.newTab().setIcon(R.mipmap.icon_wode_normal, R.mipmap.icon_wode_press).setTitle("我的"))
        tabLayout6.setBadgeColor(0, Color.YELLOW)
        tabLayout6.setBadgeTextColor(0, Color.RED)
        tabLayout6.showBadgeMsg(0, "NEW")
        tabLayout6.showBadgeMsg(2, 5)
        tabLayout6.showBadgeMsg(3)
        tabLayout6.setBadgeStroke(0, 3, Color.RED)
    }

    private fun initViewPager() {
        adapter4 = ViewPagerAdapter(this, titles4.toMutableList())
        adapter5 = ViewPagerAdapter(this, titles5.toMutableList())
        viewPager4.adapter = adapter4;
        viewPager5.adapter = adapter5;

        tabLayout4.setupWithViewPager(viewPager4, false)
        tabLayout5.setupWithViewPager(viewPager5)
    }


}
