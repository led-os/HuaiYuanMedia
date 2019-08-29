package cn.tklvyou.huaiyuanmedia.ui.setting.edit_phone

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.common.CommonConstant.TIME_INTERVAL
import cn.tklvyou.huaiyuanmedia.common.CommonConstant.TIME_ONE_SECOND
import cn.tklvyou.huaiyuanmedia.utils.CommonUtil
import cn.tklvyou.huaiyuanmedia.widget.TimeCount
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_phone_edit.*

class EditPhoneActivity : BaseActivity<EditPhonePresenter>(), EditPhoneContract.EditView, View.OnClickListener {

    private var timeCount: TimeCount? = null

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSendVCode -> handleSend(getTextValue(etPhone))
            R.id.tvConfirmBind -> handleCommit(getTextValue(etPhone))
            else -> {
            }
        }
    }


    override fun initPresenter(): EditPhonePresenter {
        return EditPhonePresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_phone_edit
    }


    override fun initView(savedInstanceState: Bundle?) {
        setTitle("更换绑定")
        setNavigationImage()
        setNavigationOnClickListener { finish() }

        tvSendVCode.setOnClickListener(this)
        tvConfirmBind.setOnClickListener(this)
    }

    override fun getCaptchaSuccess() {
        handleVCodeSendSuccess()
    }

    override fun editSuccess(mobile: String) {
        ToastUtils.showShort("更换成功")
        val data = Intent()
        data.putExtra("mobile",mobile)
        setResult(Activity.RESULT_OK,data)
        finish()
    }

    override fun editFailed() {

    }

    private fun handleVCodeSendSuccess() {
        setClickEnable(false)
        timeCount = TimeCount(TIME_INTERVAL, TIME_ONE_SECOND, object : TimeCount.ITimeCountListener {
            override fun onTick(millisUntilFinished: Long) {
                showCountDownTiming(millisUntilFinished)
            }

            override fun onFinish() {
                resetCountDownTime()
            }
        })
        timeCount!!.start()
    }


    private fun setClickEnable(enable: Boolean) {
        tvSendVCode.isEnabled = enable
    }

    private fun resetCountDownTime() {
        setClickEnable(true)
        setTextValue("发送验证码")
    }

    private fun setTextValue(value: String) {
        tvSendVCode.text = value
    }

    private fun parseSecond(millisUntilFinished: Long): String {
        return (millisUntilFinished / 1000).toString() + "秒"
    }

    private fun showCountDownTiming(mMillisUntilFinished: Long) {
        setTextValue(parseSecond(mMillisUntilFinished))
    }


    override fun onDestroy() {
        timeCount?.cancel()
        timeCount = null
        super.onDestroy()
    }

    private fun handleSend(phone: String) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort("未获取到用户手机号")
            return
        }
        if (!CommonUtil.isMobileNumber(phone)) {
            ToastUtils.showShort("请输入正确的手机号")
            return
        }
        mPresenter.getCaptcha(phone, "changemobile")
    }

    private fun getTextValue(editText: EditText): String {
        return editText.text.toString()
    }


    private fun handleCommit(mobile: String) {
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showShort("未获取到手机号")
            return
        }
        if (!CommonUtil.isMobileNumber(mobile)) {
            ToastUtils.showShort("请输入正确的手机号")
            return
        }
        if (TextUtils.isEmpty(getTextValue(etVCode))) {
            ToastUtils.showShort("请输入验证码")
            return
        }
        mPresenter.edit(mobile, getTextValue(etVCode))
    }
}
