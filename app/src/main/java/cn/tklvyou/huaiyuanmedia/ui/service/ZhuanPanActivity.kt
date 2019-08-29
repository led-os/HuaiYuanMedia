package cn.tklvyou.huaiyuanmedia.ui.service

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.widget.ImageView
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.model.LotteryModel
import cn.tklvyou.huaiyuanmedia.model.LotteryResultModel
import cn.tklvyou.huaiyuanmedia.utils.JSON
import cn.tklvyou.huaiyuanmedia.widget.dailog.ConfirmDialog
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import com.cretin.www.wheelsruflibrary.listener.RotateListener
import com.cretin.www.wheelsruflibrary.view.WheelSurfView
import kotlinx.android.synthetic.main.activity_zhuan_pan.*


class ZhuanPanActivity : BaseActivity<ZhuanPanPresenter>(), ZhuanPanContract.View {

    override fun initPresenter(): ZhuanPanPresenter {
        return ZhuanPanPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_zhuan_pan
    }

    private lateinit var idList: MutableList<Int>
    private lateinit var scoreStr: String

    private var num = 0

    override fun initView(savedInstanceState: Bundle?) {
        hideTitleBar()

        btnBack.setOnClickListener {
            finish()
        }

        mPresenter.getLotteryModel()
    }

    override fun setLotteryModel(model: LotteryModel) {
        num = model.num
        tvNum.text = "剩余转盘次数：" + model.num

        //颜色
        val colors = arrayOf<Int>(Color.parseColor("#FFC6B1"),
                Color.parseColor("#FCB195"),
                Color.parseColor("#FFC6B1"),
                Color.parseColor("#FCB195"),
                Color.parseColor("#FFC6B1"),
                Color.parseColor("#FCB195"))

        idList = model.data.map { it.id }.toMutableList()


        //文字
        val des = model.data.map { it.name }.toTypedArray()

        LogUtils.e(JSON.toJSONString(idList), JSON.toJSONString(des))

//        //图标
//        var mListBitmap: List<Bitmap> = ArrayList()
//        for (i in colors.indices) {
//            mListBitmap.add(BitmapFactory.decodeResource(getResources(), R.mipmap.iphone))
//        }
//        //主动旋转一下图片
//        mListBitmap = WheelSurfView.rotateBitmaps(mListBitmap)

        //获取第三个视图
        val build = WheelSurfView.Builder()
                .setmColors(colors)
                .setmHuanImgRes(R.mipmap.zhuan_pan_yuan_bg)
                .setmGoImgRes(R.mipmap.zhuan_pan_go)
                .setmDeses(des)
                .setmTypeModel(1)
                .setmTypeNum(6)
                .build()
        wheelSurfView.setConfig(build)

        //添加滚动监听
        wheelSurfView.setRotateListener(object : RotateListener {
            override fun rotateEnd(position: Int, des: String) {
                wheelSurfView.goBtn.isEnabled = true
                val dialog = ConfirmDialog(this@ZhuanPanActivity)
                dialog.setTitle("新增石榴籽")

//                dialog.setStyleMessage(SpanUtils().append("转盘送积分活动获得石榴籽")
//                        .append(scoreStr).setForegroundColor(resources.getColor(R.color.colorAccent))
//                        .create())
                dialog.setStyleMessage(SpanUtils().appendLine("恭喜您").setHorizontalAlign(Layout.Alignment.ALIGN_CENTER).setFontSize(17,true).setForegroundColor(Color.parseColor("#FF3C44"))
                        .append("获得$scoreStr").setForegroundColor(Color.parseColor("#FF3C44"))
                        .create())

                dialog.setYesOnclickListener("知道了") {
                    dialog.dismiss()
                }

                dialog.show()
            }

            override fun rotating(valueAnimator: ValueAnimator) {

            }

            override fun rotateBefore(goImg: ImageView) {
                if (num > 0) {
                    wheelSurfView.goBtn.isEnabled = false
                    mPresenter.startLottery()
                } else {
                    ToastUtils.showShort("转盘次数不足")
                }


//                val builder = AlertDialog.Builder(this@ZhuanPanActivity)
//                builder.setTitle("温馨提示")
//                builder.setMessage("确定要消耗一次抽奖机会？")
//                builder.setPositiveButton("确定") { dialog, which ->
//                    if (num > 0) {
//                        wheelSurfView.goBtn.isEnabled = false
//                        mPresenter.startLottery()
//                    } else {
//                        ToastUtils.showShort("转盘次数不足")
//                    }
//                }
//                builder.setNegativeButton("取消") { dialog, which -> }
//                builder.show()

            }
        })
    }

    override fun setLotteryResult(model: LotteryResultModel?) {
        if (model != null) {
            num--
            tvNum.text = "剩余转盘次数：$num"
            scoreStr = model.name
            wheelSurfView.startRotate(idList.size - idList.indexOf(model.id) + 1)
        } else {
            wheelSurfView.goBtn.isEnabled = true
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        wheelSurfView.setRotateListener(null)

    }


}
