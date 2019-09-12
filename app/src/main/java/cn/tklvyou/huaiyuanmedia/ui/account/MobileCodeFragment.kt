package cn.tklvyou.huaiyuanmedia.ui.account

import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo

import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.MyApplication
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseFragment
import cn.tklvyou.huaiyuanmedia.ui.main.MainActivity
import cn.tklvyou.huaiyuanmedia.widget.TimeCount
import com.blankj.utilcode.util.KeyboardUtils.hideSoftInput
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_mobile_code_login.*

class MobileCodeFragment : BaseFragment<AccountPresenter>(), AccountContract.View, View.OnClickListener {

    override fun initPresenter(): AccountPresenter {
        return AccountPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_mobile_code_login
    }

    private var jump = false
    override fun initView() {

        jump = mBundle.getBoolean("jump", false)
        etAccount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    val account = p0.toString().trim()
                    if (account.length == 11 && account.substring(0, 1) == "1") {
                        ivRight.visibility = View.VISIBLE
                    } else {
                        ivRight.visibility = View.INVISIBLE
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        btnLogin.setOnClickListener(this)
        btnForget.setOnClickListener(this)
        btnRegister.setOnClickListener(this)
        btnGetCaptcha.setOnClickListener(this)

        etCaptcha.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login()
            }
            return@setOnEditorActionListener true
        }
    }


    override fun lazyData() {
    }

    private var timeCount: TimeCount? = null
    override fun getCaptchaSuccess() {
        timeCount = TimeCount(60000, 1000, object : TimeCount.ITimeCountListener {
            override fun onTick(millisUntilFinished: Long) {
                btnGetCaptcha.setTextColor(Color.parseColor("#999999"))
                btnGetCaptcha.isClickable = false
                btnGetCaptcha.text = "${millisUntilFinished / 1000}秒"
            }

            override fun onFinish() {
                btnGetCaptcha.setTextColor(resources.getColor(R.color.colorAccent))
                btnGetCaptcha.isClickable = true
                btnGetCaptcha.text = "发送验证码"
            }

        })
        timeCount!!.start()
    }


    override fun loginSuccess() {
        if (jump) {
            startActivity(Intent(context, MainActivity::class.java))
        }
        mActivity.finish()
    }

    override fun loginError() {
        ToastUtils.showShort("登录失败，请重试")
    }


    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.btnLogin -> {
                login()
            }

            R.id.btnForget -> {
                startActivity(Intent(context, ForgetPasswordActivity::class.java))
            }

            R.id.btnGetCaptcha -> {
                val mobile = etAccount.text.toString().trim()

                if (ivRight.visibility != View.VISIBLE) {
                    ToastUtils.showShort("请输入正确的手机号")
                    return
                }

                mPresenter.getCaptcha(mobile, "mobilelogin")

            }

            R.id.btnRegister -> {
                startActivity(Intent(context, RegisterActivity::class.java))
            }

        }

    }

    private fun login() {
        hideSoftInput(etCaptcha)

        val account = etAccount.text.toString().trim()
        val code = etCaptcha.text.toString().trim()
        if (ivRight.visibility != View.VISIBLE) {
            ToastUtils.showShort("请输入正确的手机号")
            return
        }

        if (code.isEmpty()) {
            ToastUtils.showShort("请输入验证码")
            return
        }

        mPresenter.codeLogin(account, code)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timeCount?.cancel()
        timeCount = null
    }

}
