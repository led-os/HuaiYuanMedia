package cn.tklvyou.huaiyuanmedia.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import cn.tklvyou.huaiyuanmedia.BuildConfig
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.MyApplication
import cn.tklvyou.huaiyuanmedia.base.activity.BaseBottomTabActivity
import cn.tklvyou.huaiyuanmedia.base.activity.BaseX5WebViewActivity
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseX5WebViewFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.BackHandledInterface
import cn.tklvyou.huaiyuanmedia.common.Contacts
import cn.tklvyou.huaiyuanmedia.model.SystemConfigModel
import cn.tklvyou.huaiyuanmedia.ui.home.HomeFragment
import cn.tklvyou.huaiyuanmedia.ui.account.LoginActivity
import cn.tklvyou.huaiyuanmedia.ui.camera.CameraFragment
import cn.tklvyou.huaiyuanmedia.ui.mine.MineFragment
import cn.tklvyou.huaiyuanmedia.ui.service.ServiceFragment
import cn.tklvyou.huaiyuanmedia.ui.work.WorkFragment
import cn.tklvyou.huaiyuanmedia.utils.UpdateAppHttpUtil
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.trello.rxlifecycle3.components.support.RxFragment
import kotlinx.android.synthetic.main.activity_main.*
import com.google.gson.Gson
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app.UpdateCallback
import org.json.JSONException
import org.json.JSONObject


class MainActivity : BaseBottomTabActivity<MainPresenter>(), MainContract.View, BackHandledInterface {

    private var mBackHandedFragment: BaseX5WebViewFragment<*>? = null

    override fun setSelectedFragment(selectedFragment: BaseX5WebViewFragment<*>) {
        this.mBackHandedFragment = selectedFragment
    }

    override fun setSystemConfig(model: SystemConfigModel) {
        SPUtils.getInstance().put("search", model.default_search)
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

    private var isLogin = false
    private var mFragments: MutableList<RxFragment>? = null

    private var homeFragment: HomeFragment? = null
    private var workFragment: WorkFragment? = null
    private var cameraFragment: CameraFragment? = null
    private var serviceFragment: ServiceFragment? = null
    private var mineFragment: MineFragment? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(Bundle())
    }

    override fun initView(savedInstanceState: Bundle?) {
        hideTitleBar()

        isLogin = SPUtils.getInstance().getBoolean("login")
        if (!isLogin) {
            startActivity(Intent(this, LoginActivity::class.java))
            return
        } else {
//            getVersionInfo()

            if (MyApplication.showSplash) {
                startActivity(Intent(this, SplashActivity::class.java))
            }

            mFragments = ArrayList()

            homeFragment = HomeFragment()
            cameraFragment = CameraFragment()
            workFragment = WorkFragment()
            serviceFragment = ServiceFragment()
            mineFragment = MineFragment()

            mFragments!!.add(homeFragment!!)
            mFragments!!.add(cameraFragment!!)
            mFragments!!.add(workFragment!!)
            mFragments!!.add(serviceFragment!!)
            mFragments!!.add(mineFragment!!)


            bottomNavigationView.enableAnimation(false)
            bottomNavigationView.enableShiftingMode(false)
            bottomNavigationView.enableItemShiftingMode(false)

            bottomNavigationView.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_home -> {
                        selectFragment(0)
                    }
                    R.id.navigation_camera -> selectFragment(1)
                    R.id.navigation_service -> selectFragment(2)
                    R.id.navigation_audio_visual -> selectFragment(3)
                    R.id.navigation_mine -> selectFragment(4)
                }
                return@setOnNavigationItemSelectedListener true
            }

            selectFragment(0)

            mPresenter.getSystemConfig()
        }
    }

    private fun getVersionInfo() {

        val params = HashMap<String, String>()

        UpdateAppManager.Builder()
                //必须设置，当前Activity
                .setActivity(this)
                //必须设置实现httpManager接口的对象
                .setHttpManager(UpdateAppHttpUtil())
                //必须设置，更新地址
                .setUpdateUrl(Contacts.PRO_BASE_URL + "api/index/sysconfig")
                //以下设置，都是可选
                //设置请求方式，默认get
                .setPost(true)
                //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
                .setParams(params)
                //设置点击升级后，消失对话框，默认点击升级后，对话框显示下载进度
                //.hideDialogOnDownloading()
                //设置头部，不设置显示默认的图片，设置图片后自动识别主色调，然后为按钮，进度条设置颜色
                .setTopPic(R.mipmap.top_3)
                //为按钮，进度条设置颜色，默认从顶部图片自动识别。
                .setThemeColor(resources.getColor(R.color.colorAccent))
                //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
                .setTargetPath(Environment.getExternalStorageDirectory().absolutePath)
                //设置appKey，默认从AndroidManifest.xml获取，如果，使用自定义参数，则此项无效
                //.setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")
                //不显示通知栏进度条
                //.dismissNotificationProgress()
                //是否忽略版本
                //.showIgnoreVersion()

                .build()
                //检测是否有新版本
                .checkNewApp(object : UpdateCallback() {
                    /**
                     * 解析json,自定义协议
                     *
                     * @param json 服务器返回的json
                     * @return UpdateAppBean
                     */
                    override fun parseJson(json: String): UpdateAppBean {

                        val updateAppBean = UpdateAppBean()

                        try {
                            val `object` = JSONObject(json)
                            val code = `object`.getInt("code")
                            if (code == 1) {
                                val model = Gson().fromJson(`object`.getString("data"), SystemConfigModel::class.java)

                                val localVersionCode = Integer.parseInt(BuildConfig.VERSION_NAME.replace(".", ""))
                                val serviceVersionCode = Integer.parseInt(model.android_version.replace(".", ""))

                                updateAppBean
                                        //（必须）是否更新Yes,No
                                        .setUpdate(if (localVersionCode < serviceVersionCode) "Yes" else "No")
                                        //（必须）新版本号，
                                        .setNewVersion(model.android_version)
                                        //（必须）下载地址
                                        .setApkFileUrl(model.android_download)
                                        //（必须）更新内容
                                        .setUpdateLog(model.android_info)
                                        .isConstraint = model.android_update == 1
                                //设置md5，可以不设置
                                //.setNewMd5(jsonObject.optString("new_md51"));

                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        return updateAppBean
                    }

                    /**
                     * 网络请求之前
                     */
                    public override fun onBefore() {

                    }

                    /**
                     * 网路请求之后
                     */
                    public override fun onAfter() {

                    }

                })
    }


    override fun homeDoubleClick(position: Int) {
        super.homeDoubleClick(position)
        if (position == 0) {
            homeFragment?.reload()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        homeFragment = null
        cameraFragment = null
        workFragment = null
        serviceFragment = null
        mineFragment = null
        if (mFragments != null) {
            mFragments!!.clear()
            mFragments = null
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mBackHandedFragment == null || !mBackHandedFragment!!.onBackPressed()){
                if(supportFragmentManager.backStackEntryCount == 0){
                    appExit() //退出
                }else{
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
