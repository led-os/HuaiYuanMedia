package cn.tklvyou.huaiyuanmedia.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import cn.tklvyou.huaiyuanmedia.BuildConfig
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseBottomTabActivity
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseX5WebViewFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.BackHandledInterface
import cn.tklvyou.huaiyuanmedia.model.LifeInfo
import cn.tklvyou.huaiyuanmedia.model.SystemConfigModel
import cn.tklvyou.huaiyuanmedia.ui.audio.AudioFragment
import cn.tklvyou.huaiyuanmedia.ui.camera.CameraFragment
import cn.tklvyou.huaiyuanmedia.ui.home.HomeFragment
import cn.tklvyou.huaiyuanmedia.ui.mine.MineFragment
import cn.tklvyou.huaiyuanmedia.ui.work.WorkFragment
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.trello.rxlifecycle3.components.support.RxFragment
import com.vector.update_app_kotlin.updateApp
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseBottomTabActivity<MainPresenter>(), MainContract.View, BackHandledInterface {

    private var mBackHandedFragment: BaseX5WebViewFragment<*>? = null

    override fun setSelectedFragment(selectedFragment: BaseX5WebViewFragment<*>) {
        this.mBackHandedFragment = selectedFragment
    }

    override fun getFragments(): MutableList<RxFragment> {
        return mFragments!!
    }

    override fun getFragmentContainerResId(): Int {
        return R.id.mainContainer
    }


    override fun initPresenter(): MainPresenter {
        return MainPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_main
    }

    private var mFragments: MutableList<RxFragment>? = null

    private var homeFragment: HomeFragment? = null
    private var workFragment: WorkFragment? = null
    private var cameraFragment: CameraFragment? = null
    private var audioFragment: AudioFragment? = null
    private var mineFragment: MineFragment? = null


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(Bundle())
    }

    override fun initView(savedInstanceState: Bundle?) {
        hideTitleBar()
//        AndroidBug5497Workaround.assistActivity(this)

        mFragments = ArrayList()

        homeFragment = HomeFragment()
        cameraFragment = CameraFragment()
        workFragment = WorkFragment()
        audioFragment = AudioFragment()
        mineFragment = MineFragment()

        mFragments!!.add(homeFragment!!)
        mFragments!!.add(audioFragment!!)
        mFragments!!.add(cameraFragment!!)
        mFragments!!.add(workFragment!!)
        mFragments!!.add(mineFragment!!)


        bottomNavigationView.enableAnimation(false)
        bottomNavigationView.enableShiftingMode(false)
        bottomNavigationView.enableItemShiftingMode(false)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    selectFragment(0)
                    homeFragment?.clickTabCallback(0)
                }
                R.id.navigation_audio_visual -> {
                    selectFragment(1)
                    homeFragment?.clickTabCallback(1)
                }
                R.id.navigation_camera -> {
                    selectFragment(2)
                    homeFragment?.clickTabCallback(2)
                }
                R.id.navigation_service -> {
                    selectFragment(3)
                    homeFragment?.clickTabCallback(3)
                }

                R.id.navigation_mine -> {
                    selectFragment(4)
                    homeFragment?.clickTabCallback(4)
                }
            }

            mPresenter.getLifeInfo()

            return@setOnNavigationItemSelectedListener true
        }

        selectFragment(0)
        mPresenter.getSystemConfig()
    }


    override fun homeDoubleClick(position: Int) {
        super.homeDoubleClick(position)
        if (position == 0) {
            homeFragment?.reload()
        }

    }

    private var isNeed = false
    override fun onResume() {
        super.onResume()
        if (isNeed) {
            mPresenter.getLifeInfo()
        }
    }

    override fun setSystemConfig(model: SystemConfigModel?, life_info: LifeInfo?) {
        isNeed = true
        if (model != null) {
            SPUtils.getInstance().put("search", model.default_search)

            val localVersionCode = Integer.parseInt(BuildConfig.VERSION_NAME.replace(".", ""))
            val serviceVersionCode = Integer.parseInt(model.android_version.replace(".", ""))

            updateApp(init = {
                topPic = R.mipmap.top_3
                themeColor = resources.getColor(R.color.colorAccent)
            }).update(localVersionCode < serviceVersionCode, model.android_version,
                    model.android_download, model.android_info,
                    model.android_update == 1)

        }

        if (life_info != null && life_info.interaction != null && life_info.count > 0) {
            initBadgeView(life_info.count, life_info.interaction)
        } else {
            initBadgeView(0, null)
        }

    }

    private fun initBadgeView(number: Int, message: String?) {
        val badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.navigation_camera)
        badgeDrawable.backgroundColor = Color.RED
        badgeDrawable.badgeTextColor = Color.WHITE
//        badgeDrawable.maxCharacterCount = 99
//        badgeDrawable.number = number
        badgeDrawable.isVisible = number != 0

        cameraFragment?.flushHeaderView(message)
    }


    override fun onDestroy() {
        super.onDestroy()
        homeFragment = null
        cameraFragment = null
        workFragment = null
        audioFragment = null
        mineFragment = null
        if (mFragments != null) {
            mFragments!!.clear()
            mFragments = null
        }

    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bottomNavigationView.currentItem == 1) {
                if (!audioFragment!!.onBackPressed()) {
                    appExit() //退出
                }
            } else if (mBackHandedFragment == null || !mBackHandedFragment!!.onBackPressed()) {
                if (supportFragmentManager.backStackEntryCount == 0) {
                    appExit() //退出
                } else {
                    supportFragmentManager.popBackStack() //fragment 出栈
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private var isExit: Boolean = false

    @SuppressLint("HandlerLeak")
    private val exitHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            isExit = false
        }
    }

    private fun appExit() {
        if (!isExit) {
            isExit = true
            ToastUtils.showShort("再按一次退出程序")
            exitHandler.sendEmptyMessageDelayed(0, 2000)
        } else {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
            System.exit(0)
        }
    }

}
