package cn.tklvyou.huaiyuanmedia.ui.camera.zhuan_pan

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.api.BaseResult
import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_fill_address.*

/**
 *@description :
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2020年10月16日11:24
 * @Email: 971613168@qq.com
 */
class FillAddressActivity : BaseActivity<NullPresenter>() {
    private var recordId = 0
    override fun initView(savedInstanceState: Bundle?) {
        recordId = intent.getIntExtra("recordId", -1)
        setTitle("填写收货地址")
        LogUtils.dTag("填写收货地址", "recordId=" + recordId)
        tvCommit.setOnClickListener {
            doCommit()
        }
    }

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_fill_address
    }

    @SuppressLint("CheckResult")
    private fun doCommit() {
        val name = etReceiverName.text.toString()
        val mobile = etReceiverMobile.text.toString()
        val address = etAddress.text.toString()
        if (StringUtils.isEmpty(name)) {
            ToastUtils.showShort("请填写收货人")
            return
        }
        if (StringUtils.isEmpty(mobile)) {
            ToastUtils.showShort("请填写收货人联系方式")
            return
        }
        if (StringUtils.isEmpty(address)) {
            ToastUtils.showShort("请填写收货人地址")
            return
        }
        showLoading()
        RetrofitHelper.getInstance().server
                .updateInfo(recordId, mobile, name, address)
                .compose(RxSchedulers.applySchedulers())
                .compose<BaseResult<Any?>>(bindToLife())
                .subscribe({ result: BaseResult<Any?> ->
                    if (result.code == 1) {
                        showSuccess("提交成功")
                        Handler().postDelayed(Runnable {
                            finish()
                        }, 800)
                    } else {
                        showFailed("提交失败")
                    }
                }) { throwable: Throwable ->
                    throwable.printStackTrace()
                    showFailed("提交失败")
                }
    }
}