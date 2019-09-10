package cn.tklvyou.huaiyuanmedia.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.common.SpConstant.PREF_KEY_TOKEN
import cn.tklvyou.huaiyuanmedia.helper.AccountHelper
import cn.tklvyou.huaiyuanmedia.ui.account.LoginActivity
import cn.tklvyou.huaiyuanmedia.ui.setting.edit_pass.EditPasswordActivity
import cn.tklvyou.huaiyuanmedia.utils.DataCleanManager
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_setting.*


/**
 *@description :
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2019年07月30日17:07
 * @Email: 971613168@qq.com
 */
class SettingActivity : BaseActivity<SettingPresenter>(), View.OnClickListener, SettingContract.LogoutView {

    override fun initPresenter(): SettingPresenter {
        return SettingPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_setting
    }


    override fun initView(savedInstanceState: Bundle?) {
        setTitle("系统设置")
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        ivSkipEditPass.setOnClickListener(this)
        tvLogOut.setOnClickListener(this)
        clearCache.setOnClickListener(this)

        try {
            val cacheSize = DataCleanManager.getTotalCacheSize(this)
            tvCache.text = cacheSize
        } catch (e: Exception) {

        }

        if (SPUtils.getInstance().getString("token", "").isEmpty()) {
            tvLogOut.visibility = View.INVISIBLE
        }


    }


    override fun onClick(v: View?) {
        if (v == null) {
            return
        }
        when (v.id) {
            R.id.ivSkipEditPass -> {
                if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(this, LoginActivity::class.java))
                    return
                }
                startActivity(Intent(this, EditPasswordActivity::class.java))
            }

            R.id.clearCache -> {
                val dialog = CommonDialog(this)
                dialog.setMessage("确定要清除缓存吗？")
                dialog.setYesOnclickListener("确认") {
                    DataCleanManager.cleanInternalCache(this@SettingActivity)
                    DataCleanManager.cleanExternalCache(this@SettingActivity)
                    try {
                        val cacheSize = DataCleanManager.getTotalCacheSize(this@SettingActivity)
                        tvCache.text = cacheSize
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    dialog.dismiss()
                }
                dialog.show()
            }

            R.id.tvLogOut -> {
                val dialog = CommonDialog(this)
                dialog.setMessage("确定要退出该账号吗？")
                dialog.setYesOnclickListener("确认") {
                    mPresenter.logout()
                }
                dialog.show()
            }
        }

    }

    override fun logoutSuccess() {
        handleLogout()
    }


    private fun handleLogout() {
        ActivityUtils.finishOtherActivities(this::class.java)
        SPUtils.getInstance().put(PREF_KEY_TOKEN, "")
        SPUtils.getInstance().put("login", false)
        SPUtils.getInstance().put("groupId", 0)
        val intent= Intent(this, LoginActivity::class.java)
        intent.putExtra("jump",true)
        startActivity(intent)
        finish()
    }

}