package cn.tklvyou.huaiyuanmedia.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.WindowManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.base.activity.BaseNoTitleActivity
import cn.tklvyou.huaiyuanmedia.common.ModuleUtils
import cn.tklvyou.huaiyuanmedia.model.AdModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.utils.YBitmapUtils
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import kotlinx.android.synthetic.main.activity_splash.*
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import java.io.File


class SplashActivity : BaseNoTitleActivity<AdPresenter>(), MainContract.AdView {


    override fun initPresenter(): AdPresenter {
        return AdPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_splash
    }

    private var timeCount = 0
    private var continueCount = true
    private var initTimeCount = 0

    private var jump = true


    @SuppressLint("HandlerLeak")
    private var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            countNum()
            if (continueCount) {
                this.sendMessageDelayed(this.obtainMessage(-1), 1000)
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
//        hideTitleBar()

        BarUtils.setStatusBarVisibility(this,false)
        if(BarUtils.isSupportNavBar()){
            BarUtils.setNavBarVisibility(this,false)
        }

        if (isSchemeIntentData()) {
            layoutSkip.visibility = View.GONE
            return
        }

        initTimeCount = 6
        handler.sendMessageDelayed(handler.obtainMessage(-1), 1000)

        if (NetworkUtils.isConnected()) {
            mPresenter.getAdView()
        }

        layoutSkip.setOnClickListener {
            continueCount = false
            toNextActivity()
            finish()
        }

        initAdView()

    }

    private fun isSchemeIntentData(): Boolean {
        val uri = intent.data
        return if (uri != null) {
            //访问路径
            val path = uri.path
            //获取参数值
            val module = uri.getQueryParameter("json")
            val bean = Gson().fromJson<NewsBean>(module, NewsBean::class.java)
            LogUtils.e(path, ModuleUtils.getTypeByNewsBean(bean))
            toNewsDetailsActivity(bean)
            true
        } else {
            false
        }
    }


    private fun initAdView() {
        if (SPStaticUtils.getBoolean("ad1", false)) {
            layoutSkip.visibility = View.VISIBLE

            val url = SPStaticUtils.getString("ad1_url", "")
            val folder = cacheDir.absolutePath + File.separator + "download"
            val appDir = File(folder, "pictures")
            val fileName = "ad1.png"
            val destFile = File(appDir, fileName)

            ivAdvertising.setImageBitmap(ImageUtils.getBitmap(destFile))

            if (url.isNotEmpty()) {
                ivAdvertising.setOnClickListener {
                    jump = false
                    val intent = Intent(this, WebviewActivity::class.java)
                    intent.putExtra("url", url)
                    intent.putExtra("ad", true)
                    startActivity(intent)
                    handler.removeMessages(0)
                    finish()
                }
            }

            handler.postDelayed({
                if (SPStaticUtils.getBoolean("ad2", false)) {
                    val url1 = SPStaticUtils.getString("ad2_url", "")
                    val fileName1 = "ad2.png"
                    val destFile1 = File(appDir, fileName1)

                    ivAdvertising.setImageBitmap(ImageUtils.getBitmap(destFile1))

                    if (url1.isNotEmpty()) {
                        ivAdvertising.setOnClickListener {
                            jump = false
                            val intent = Intent(this, WebviewActivity::class.java)
                            intent.putExtra("url", url1)
                            intent.putExtra("ad", true)
                            startActivity(intent)
                            handler.removeMessages(0)
                            finish()
                        }
                    } else {
                        ivAdvertising.setOnClickListener {
                            jump = true
                        }
                    }

                }
            }, 4500)

        } else if (SPStaticUtils.getBoolean("ad2", false)) {
            layoutSkip.visibility = View.VISIBLE

            val url = SPStaticUtils.getString("ad2_url", "")
            val folder = cacheDir.absolutePath + File.separator + "download"
            val appDir = File(folder, "pictures")
            val fileName = "ad2.png"
            val destFile = File(appDir, fileName)

            ivAdvertising.setImageBitmap(ImageUtils.getBitmap(destFile))

            if (url.isNotEmpty()) {
                ivAdvertising.setOnClickListener {
                    jump = false
                    val intent = Intent(this, WebviewActivity::class.java)
                    intent.putExtra("url", url)
                    intent.putExtra("ad", true)
                    startActivity(intent)
                    handler.removeMessages(0)
                    finish()
                }
            }
        }
    }

    override fun setAdView(data: MutableList<AdModel>) {
        initTimeCount = 6
        val size = data.size
        if (size > 0) {
            if (data[0].image.isNotEmpty()) {

                Glide.with(this).downloadOnly().load(data[0].image).into(object : SimpleTarget<File>() {

                    override fun onResourceReady(resource: File, transition: Transition<in File>?) {

                        try {
                            //获取到下载得到的图片，进行本地保存
                            val folder = cacheDir.absolutePath + File.separator + "download"
                            val appDir = File(folder, "pictures")
                            if (!appDir.exists()) {
                                appDir.mkdirs()
                            }
                            val fileName = "ad1.png"
                            val destFile = File(appDir, fileName)

                            val status = YBitmapUtils.copy(resource, destFile)
                            if (status) {
                                if (!SPStaticUtils.getBoolean("ad1", false)) {
                                    ivAdvertising.setImageBitmap(ImageUtils.getBitmap(resource))
                                }
                                SPStaticUtils.put("ad1", true)
                                SPStaticUtils.put("ad1_url", data[0].url)
                            } else {
                                ToastUtils.showShort("下载失败")
                            }


                        } catch (e: Exception) {
                            ToastUtils.showShort("下载失败")
                        }


                    }

                })

                if (data[0].url.isNotEmpty()) {
                    ivAdvertising.setOnClickListener {
                        jump = false
                        val intent = Intent(this, WebviewActivity::class.java)
                        intent.putExtra("url", data[0].url)
                        intent.putExtra("ad", true)
                        startActivity(intent)
                        handler.removeMessages(0)
                        finish()
                    }
                }

            } else {
                SPStaticUtils.put("ad1", false)
                SPStaticUtils.put("ad1_url", "")
            }
        }

        if (size > 1) {

            if (data[1].image.isNotEmpty()) {
                handler.postDelayed({
                    runUiThread {
                        Glide.with(this).downloadOnly().load(data[1].image).into(object : SimpleTarget<File>() {

                            override fun onResourceReady(resource: File, transition: Transition<in File>?) {

                                try {
                                    //获取到下载得到的图片，进行本地保存
                                    val folder = cacheDir.absolutePath + File.separator + "download"
                                    val appDir = File(folder, "pictures")
                                    if (!appDir.exists()) {
                                        appDir.mkdirs()
                                    }
                                    val fileName = "ad2.png"
                                    val destFile = File(appDir, fileName)

                                    val status = YBitmapUtils.copy(resource, destFile)
                                    if (status) {
                                        if (!SPStaticUtils.getBoolean("ad2", false)) {
                                            ivAdvertising.setImageBitmap(ImageUtils.getBitmap(resource))
                                        }
                                        SPStaticUtils.put("ad2", true)
                                        SPStaticUtils.put("ad2_url", data[1].url)
                                    } else {
                                        ToastUtils.showShort("下载失败")
                                    }

                                } catch (e: Exception) {
                                    ToastUtils.showShort("下载失败")
                                }


                            }

                        })

                        if (data[1].url.isNotEmpty()) {
                            ivAdvertising.setOnClickListener {
                                jump = false
                                val intent = Intent(this, WebviewActivity::class.java)
                                intent.putExtra("url", data[1].url)
                                intent.putExtra("ad", true)
                                startActivity(intent)
                                handler.removeMessages(0)
                                finish()
                            }
                        } else {
                            ivAdvertising.setOnClickListener {
                                jump = true
                            }
                        }
                    }
                }, 4500)

            } else {
                SPStaticUtils.put("ad2", false)
                SPStaticUtils.put("ad2_url", "")
            }

        }


    }


    private fun countNum(): Int {//数秒
        timeCount++
        if (timeCount == 3) {//数秒，超过3秒后如果没有网络，则进入下一个页面
            if (!NetworkUtils.isConnected()) {
                continueCount = false
                toNextActivity()
                finish()
            }
            ivAdvertising.visibility = View.VISIBLE
            layoutSkip.visibility = View.VISIBLE
        }
        if (timeCount == initTimeCount) {
            continueCount = false
            toNextActivity()
            finish()
        }
        return timeCount
    }

    private fun toNextActivity() {
        if (jump) {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun toNewsDetailsActivity(bean: NewsBean) {
        val type = ModuleUtils.getTypeByNewsBean(bean)
        if (bean.url.isNotEmpty()) {
            val intent = Intent(this, ServiceWebviewActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("back", true)
            intent.putExtra("url", bean.url)
            intent.putExtra("other", true)
            intent.putExtra("share_title", "")
            startActivity(intent)
        } else {
            val intent = Intent(this, NewsDetailActivity::class.java)
            intent.putExtra("back", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            intent.putExtra(NewsDetailActivity.INTENT_ID, bean.id)
            intent.putExtra(NewsDetailActivity.INTENT_TYPE, type)
            startActivity(intent)
        }
        finish()
    }

}
