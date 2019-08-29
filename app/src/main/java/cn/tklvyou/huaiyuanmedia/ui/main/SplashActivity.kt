package cn.tklvyou.huaiyuanmedia.ui.main

import android.os.Bundle
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.MyApplication
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import java.util.*

class SplashActivity : BaseActivity<NullPresenter>() {

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_splash
    }

    override fun initView(savedInstanceState: Bundle?) {
        hideTitleBar()
        MyApplication.showSplash = false
        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                finish()
            }
        }
        timer.schedule(timerTask, 3000)
    }


}
