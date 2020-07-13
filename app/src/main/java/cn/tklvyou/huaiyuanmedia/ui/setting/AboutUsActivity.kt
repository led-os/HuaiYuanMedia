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
import com.google.gson.Gson
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app.UpdateCallback
import com.vector.update_app_kotlin.updateApp
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


    override fun setSystemConfig(model: SystemConfigModel) {
        GlideManager.loadImg(model.qrcode, ivCode)

        val localVersionCode = Integer.parseInt(BuildConfig.VERSION_NAME.replace(".", ""))
        val serviceVersionCode = Integer.parseInt(model.android_version.replace(".", ""))

        if (localVersionCode < serviceVersionCode) {
            tvVersionStatus.text = "有新版本可用"
            btnCheckVersion.setOnClickListener {
                updateApp(init = {
                    topPic = R.mipmap.top_3
                    themeColor = resources.getColor(R.color.colorAccent)
                }).update(localVersionCode < serviceVersionCode, model.android_version,
                        model.android_download, model.android_info,
                        model.android_update == 1)
            }
        } else {
            tvVersionStatus.text = "已是最新版本"
        }
    }

}


