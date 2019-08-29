package cn.tklvyou.huaiyuanmedia.widget

import android.os.CountDownTimer

/**
 * 自定义计时器
 */
class TimeCount(millisInFuture: Long, countDownInterval: Long, private val listener: ITimeCountListener) : CountDownTimer(millisInFuture, countDownInterval) {

    override fun onTick(millisUntilFinished: Long) {
        listener.onTick(millisUntilFinished)
    }

    override fun onFinish() {
        listener.onFinish()
    }

    interface ITimeCountListener{
        fun onTick(millisUntilFinished: Long)
        fun onFinish()
    }

}
