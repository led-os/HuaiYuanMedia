package cn.tklvyou.huaiyuanmedia.ui.camera.zhuan_pan

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.api.BaseResult
import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.model.RewardModel
import cn.tklvyou.huaiyuanmedia.ui.mine.reward.RewardConstant.EXTRA_RECORD_ID
import cn.tklvyou.huaiyuanmedia.utils.CommonUtil
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.lljjcoder.Interface.OnCityItemClickListener
import com.lljjcoder.bean.CityBean
import com.lljjcoder.bean.DistrictBean
import com.lljjcoder.bean.ProvinceBean
import com.lljjcoder.citywheel.CityConfig
import com.lljjcoder.style.citypickerview.CityPickerView
import kotlinx.android.synthetic.main.activity_fill_address.*


/**
 *@description :
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2020年10月16日11:24
 * @Email: 971613168@qq.com
 */
@SuppressLint("CheckResult")
class FillAddressActivity : BaseActivity<NullPresenter>() {
    //申明对象
    private val mPicker = CityPickerView()
    private var recordId = 0L
    private var finalAddress = ""
    private val separateStr = "〥"
    override fun initView(savedInstanceState: Bundle?) {
        recordId = intent.getLongExtra(EXTRA_RECORD_ID, -1L)
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        setTitle("填写收货地址")
        getAwardInfo(recordId)

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
        val address = etArea.text.toString() +separateStr+ etAddress.text.toString()
        if (StringUtils.isEmpty(name)) {
            ToastUtils.showShort("请填写收货人")
            return
        }
        if (StringUtils.isEmpty(mobile)) {
            ToastUtils.showShort("请填写收货人联系方式")
            return
        }
        if(!CommonUtil.isMobileNumber(mobile)){
            ToastUtils.showShort("请填写正确的联系方式")
            return
        }
        if (StringUtils.isEmpty(address)) {
            ToastUtils.showShort("请填写收货人地址")
            return
        }
        if (StringUtils.isEmpty(etArea.text.toString())) {
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
                        }, 1500)
                    } else {
                        showFailed("提交失败")
                    }
                }) { throwable: Throwable ->
                    throwable.printStackTrace()
                    showFailed("提交失败")
                }
    }


    private fun setEmptyData() {
            //预先加载仿iOS滚轮实现的全部数据
            mPicker.init(this)
            //添加默认的配置，不需要自己定义，当然也可以自定义相关熟悉，详细属性请看demo
            val cityConfig = CityConfig.Builder().setLineHeigh(1).province("安徽省")
                    .city("合肥市").confirTextColor("#585858").build()
            mPicker.setConfig(cityConfig)
            //监听选择点击事件及返回结果
            mPicker.setOnCityItemClickListener(object : OnCityItemClickListener() {
                override fun onSelected(province: ProvinceBean?, city: CityBean?, district: DistrictBean?) {
                    finalAddress = ""
                    if (province != null) {
                        finalAddress = province.name
                    }
                    if (city != null) {
                        finalAddress += city.name
                    }
                    if (district != null) {
                        finalAddress += district.name
                    }
                    etArea.setText(finalAddress)
                }

                override fun onCancel() {
                }
            })

            //显示
            ivAddressSelect.setOnClickListener {
                mPicker.showCityPicker()
            }
            tvCommit.setOnClickListener {
                doCommit()
            }

    }


    private fun getAwardInfo(recordId: Long) {
        showLoading()
        RetrofitHelper.getInstance().server
                .getAwardInfo(recordId)
                .compose(RxSchedulers.applySchedulers())
                .compose<BaseResult<RewardModel?>>(bindToLife())
                .subscribe({ result: BaseResult<RewardModel?> ->
                    if (result.code == 1 && result.data != null) {
                        showSuccess("")
                        fillData(result.data!!)
                    } else {
                        showFailed("")
                    }
                }) { throwable: Throwable ->
                    throwable.printStackTrace()
                    showFailed("")
                }
    }


    private fun fillData(data: RewardModel) {
        tvGoodsName.text = data.award
      if(StringUtils.isEmpty(data.name)){
          setEmptyData()
      }else{
          tvGoodsName.text = data.award
          etReceiverName.setText(data.name)
          etReceiverName.isEnabled = false
          etReceiverMobile.setText(data.mobile)
          etReceiverMobile.isEnabled = false
          etArea.setText(data.area)
          etArea.isEnabled = false
          etAddress.setText(data.address)
          etAddress.isEnabled = false
          llGoods.visibility = View.VISIBLE
          tvCommit.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_454545))
          tvCommit.isEnabled = false
      }

    }
}