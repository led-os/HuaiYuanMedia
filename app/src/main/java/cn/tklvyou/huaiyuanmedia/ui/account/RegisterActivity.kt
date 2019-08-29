package cn.tklvyou.huaiyuanmedia.ui.account

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.ui.main.MainActivity
import cn.tklvyou.huaiyuanmedia.widget.TimeCount
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity<AccountRegisterPresenter>(), AccountContract.RegisterView, View.OnClickListener {

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_register
    }

    override fun initPresenter(): AccountRegisterPresenter {
        return AccountRegisterPresenter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        hideTitleBar()

        etMobile.addTextChangedListener(object : TextWatcher {
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


        cbShow1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        cbShow2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                etConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                etConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }


        btnRegister.setOnClickListener(this)
        btnBack.setOnClickListener(this)
        btnProtocol.setOnClickListener(this)
        btnGetCaptcha.setOnClickListener(this)

    }


    override fun getCaptchaSuccess() {
        val timeCount = TimeCount(60000, 1000, object : TimeCount.ITimeCountListener {
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
        timeCount.start()
    }


    override fun registerSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onClick(p0: View?) {
        if (p0 == null) {
            return
        }
        when (p0.id) {
            R.id.btnProtocol -> {
                startActivity(Intent(this,AgreementActivity::class.java))
            }

            R.id.btnBack -> {
                finish()
            }

            R.id.btnGetCaptcha -> {
                val mobile = etMobile.text.toString().trim()

                if (ivRight.visibility != View.VISIBLE) {
                    ToastUtils.showShort("请输入正确的手机号")
                    return
                }

                mPresenter.getCaptcha(mobile, "register")
            }

            R.id.btnRegister -> {
                val mobile = etMobile.text.toString().trim()
                val captcha = etCaptcha.text.toString().trim()
                val password = etPassword.text.toString().trim()
                val confirmPassword = etConfirmPassword.text.toString().trim()


                if (ivRight.visibility != View.VISIBLE) {
                    ToastUtils.showShort("请输入正确的手机号")
                    return
                }

                if (captcha.isEmpty()) {
                    ToastUtils.showShort("请输入验证码")
                    return
                }

                if (password.isEmpty()) {
                    ToastUtils.showShort("请输入密码")
                    return
                }

                if (confirmPassword.isEmpty()) {
                    ToastUtils.showShort("请输入确认密码")
                    return
                }

                if (password != confirmPassword) {
                    ToastUtils.showShort("两次输入密码不一致")
                    return
                }

                if (!cbReadMe.isChecked) {
                    ToastUtils.showShort("请勾选同意注册协议")
                    return
                }

                mPresenter.register(mobile, password, captcha)

            }


        }

    }


}