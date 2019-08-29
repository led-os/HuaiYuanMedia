package cn.tklvyou.huaiyuanmedia.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.Button
import cn.tklvyou.huaiyuanmedia.R
import com.blankj.utilcode.util.ConvertUtils

/**
 * 投票进度条
 */
class VoteLoadButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : Button(context, attrs, defStyleAttr) {

    /**
     * 当前状态
     */
    private var currentState = 0
    /**
     * 当前投票进度
     * 百分比
     */
    private var currentPercent = 0

    private var currentCount = 0

    private var mPaint: Paint? = null

    init {
        currentState = STATE_NO_DOWNLOAD
        setBackgroundResource(R.drawable.bg_download_button_download)

        mPaint = Paint()
        //设置画笔的样式  fill为填充  stroke为空心
        mPaint!!.style = Paint.Style.FILL
        //设置画笔的颜色
        mPaint!!.color = Color.BLACK
        //设置填充的宽度
        mPaint!!.strokeWidth = 1f
        //设置是否抗锯齿
        mPaint!!.isAntiAlias = true
        //设置对齐方式
        mPaint!!.textAlign = Paint.Align.CENTER
        //设置字体的大小   需要将sp单位转为px
        mPaint!!.textSize = ConvertUtils.sp2px(14f).toFloat()
    }

    /**
     * 设置当前状态
     */
    fun setState(state: Int) {
        this.currentState = state
        postInvalidate()
    }

    /**
     * 设置投票进度
     */
    fun setDownLoadProgress(percent: Int, count: Int) {
        this.currentPercent = percent
        this.currentCount = count
        postInvalidate()
    }

    private var solidDrawable: Drawable? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (currentState) {
            STATE_NO_DOWNLOAD -> {
                currentPercent = 0
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.bg_download_button_download)
            }
            STATE_DOWNLOADING -> {
                gravity = Gravity.CENTER_VERTICAL
                setPadding(ConvertUtils.dp2px(13f), 0, 0, 0)

                val backDrawable = context.resources.getDrawable(R.drawable.bg_download_button_download_select)

                // 动态填充的Drawable
                solidDrawable = resources.getDrawable(R.drawable.bg_download_button_clip)
                // 原background是显示的边框, 合并后显示
                background = LayerDrawable(arrayOf(backDrawable,solidDrawable))

                // 利用ClipDrawable的裁剪效果实现进度展示
                solidDrawable?.level = currentPercent * 100

                canvas.drawText("$currentCount 人", width - 80f, height.toFloat() / 2 + 14, mPaint)

            }

            STATE_USED -> {
                gravity = Gravity.CENTER_VERTICAL
                setPadding(ConvertUtils.dp2px(13f), 0, 0, 0)

                val backDrawable = context.resources.getDrawable(R.drawable.bg_download_button_download_normal)

                // 动态填充的Drawable
                solidDrawable = resources.getDrawable(R.drawable.bg_download_button_clip_gray)
                // 原background是显示的边框, 合并后显示
                background = LayerDrawable(arrayOf(backDrawable,solidDrawable))

                // 利用ClipDrawable的裁剪效果实现进度展示
                solidDrawable?.level = currentPercent * 100

                canvas.drawText("$currentCount 人", width - 80f, height.toFloat() / 2 + 14, mPaint)

            }
        }
    }


    companion object {
        // 未投票
        val STATE_NO_DOWNLOAD = 0
        // 已投票-选中
        val STATE_DOWNLOADING = 1
        // 已投票-未选中
        val STATE_USED = 2
    }
}