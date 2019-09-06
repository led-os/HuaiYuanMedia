package cn.tklvyou.huaiyuanmedia.ui.setting

import android.os.Bundle
import android.os.Environment
import cn.tklvyou.huaiyuanmedia.BuildConfig
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.MyApplication
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.common.Contacts
import cn.tklvyou.huaiyuanmedia.helper.GlideManager
import cn.tklvyou.huaiyuanmedia.model.SystemConfigModel
import cn.tklvyou.huaiyuanmedia.utils.CommonUtil
import cn.tklvyou.huaiyuanmedia.utils.UpdateAppHttpUtil
import com.google.gson.Gson
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app.UpdateCallback
import kotlinx.android.synthetic.main.activity_about_us.*
import org.json.JSONException
import org.json.JSONObject

/**
 *@description :关于
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2019年08月02日14:08
 * @Email: 971613168@qq.com
 */
class AboutUsActivity : BaseActivity<AboutUsPresenter>(), AboutUsContract.View {
    override fun setSystemConfig(model: SystemConfigModel) {
        GlideManager.loadImg(model.qrcode, ivCode)

        val localVersionCode = Integer.parseInt(BuildConfig.VERSION_NAME.replace(".", ""))
        val serviceVersionCode = Integer.parseInt(model.android_version.replace(".", ""))

        if (localVersionCode < serviceVersionCode) {
            tvVersionStatus.text = "有新版本可用"
            btnCheckVersion.setOnClickListener {
                getVersionInfo()
            }
        } else {
            tvVersionStatus.text = "已是最新版本"
        }
    }

    override fun initPresenter(): AboutUsPresenter {
        return AboutUsPresenter()
    }


    override fun getActivityLayoutID(): Int {
        return R.layout.activity_about_us
    }


    override fun initView(savedInstanceState: Bundle?) {
        setTitle("关于我们")
        setNavigationImage()
        setNavigationOnClickListener { finish() }


        tvAppName.text = CommonUtil.getAppName(MyApplication.getAppContext())
        val name = "V " + CommonUtil.getVersionName(MyApplication.getAppContext())
        tvAppVersion.text = name

        mPresenter.getSystemConfig()

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


}


