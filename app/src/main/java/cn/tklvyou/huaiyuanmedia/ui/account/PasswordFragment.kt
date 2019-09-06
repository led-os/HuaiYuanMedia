package cn.tklvyou.huaiyuanmedia.ui.account

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo

import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.MyApplication
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseFragment
import cn.tklvyou.huaiyuanmedia.ui.main.MainActivity
import com.blankj.utilcode.util.KeyboardUtils.hideSoftInput
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_password.*

class PasswordFragment : BaseFragment<AccountPresenter>(), AccountContract.View, View.OnClickListener {

    override fun initPresenter(): AccountPresenter {
        return AccountPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_password
    }

    override fun initView() {
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

        etPassword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login()
            }
            return@setOnEditorActionListener true
        }
    }

    override fun lazyData() {
    }

    override fun loginSuccess() {
        MyApplication.showSplash = false
        mActivity.finish()
//        startActivity(Intent(context, MainActivity::class.java))
    }

    override fun loginError() {
        ToastUtils.showShort("登录失败，请重试")
    }

    override fun getCaptchaSuccess() {

    }


    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.btnLogin -> {
                login()
            }

            R.id.btnForget -> {
                startActivity(Intent(context, ForgetPasswordActivity::class.java))
            }

            R.id.btnRegister -> {
                startActivity(Intent(context, RegisterActivity::class.java))
            }

        }

    }

    private fun login() {
        hideSoftInput(etPassword)

        val account = etAccount.text.toString().trim()
        val password = etPassword.text.toString().trim()
        if (ivRight.visibility != View.VISIBLE) {
            ToastUtils.showShort("请输入正确的手机号")
            return
        }

        if (password.isEmpty()) {
            ToastUtils.showShort("请输入密码")
            return
        }

        mPresenter.login(account, password)
    }


}
