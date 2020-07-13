package cn.tklvyou.huaiyuanmedia.ui.audio

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.helper.GlideManager
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.model.TelModel
import cn.tklvyou.huaiyuanmedia.ui.account.LoginActivity
import cn.tklvyou.huaiyuanmedia.ui.adapter.AudioListRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.adapter.TVNewsDetailsRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.comment.CommentListActivity
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.widget.dailog.InputDialog
import com.blankj.utilcode.util.*
import com.chad.library.adapter.base.BaseViewHolder
import com.pili.pldroid.player.widget.PLVideoView
import kotlinx.android.synthetic.main.fragment_audio.*

class AudioFragment : BaseRecyclerFragment<AudioPresenter, TelModel.ListBean, BaseViewHolder, TVNewsDetailsRvAdapter>(), AudioContract.View {

    override fun initPresenter(): AudioPresenter {
        return AudioPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_audio
    }


    override fun getLoadingView(): View {
        return loading
    }

    private var isLike = false
    private var type = ""
    private var like_num = 0


    private val UP_DATE_CODE = 555
    private val FADE_OUT = 1
    private var mShowing = true
    private var sDefaultTimeout = 3000

    private var isFull = false

    @SuppressLint("HandlerLeak")
    private var mHandler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                FADE_OUT -> {
                    if (mShowing) {
                        mMediaActions.visibility = View.GONE
                        mMediaFullScreen.visibility = View.GONE
                        mShowing = false
                    }
                }

                UP_DATE_CODE -> {
                    sendEmptyMessageDelayed(UP_DATE_CODE, 500)
                }
            }

        }
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        isUserVisibility = !hidden

        if (isFirstResume) {
            return
        }

        if (hidden) {
            mVideoView.surfaceView.visibility = View.INVISIBLE
            if (mVideoView.isPlaying) {
                mVideoView.pause()
                mMediaActions.setImageResource(R.mipmap.ic_start_play)
            }

            if (KeyboardUtils.isSoftInputVisible(mActivity)) {
                KeyboardUtils.hideSoftInput(mActivity)
            }
        } else {
            mVideoView.surfaceView.visibility = View.VISIBLE
            mMediaActions.performClick()
        }
    }

    override fun initView() {
        serviceTitleBar.setBackgroundResource(R.drawable.shape_gradient_common_titlebar)

        initRecyclerView(mRecyclerView)
        mRecyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL))

        setFullScreen()
        llVideo.setOnClickListener {
            if (!mShowing) {
                if (type == "广播") {
                    mMediaFullScreen.visibility = View.GONE
                } else {
                    mMediaFullScreen.visibility = View.VISIBLE
                }
                mMediaActions.visibility = View.VISIBLE
                mShowing = true
            }

            if (sDefaultTimeout != 0) {
                mHandler.removeMessages(FADE_OUT)
                mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), sDefaultTimeout.toLong())
            }
        }

        mPresenter.getNewList("视听", "", 1, true)
    }


    override fun lazyData() {

    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getNewList("视听", "", 1, true)
    }


    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {

        if (model != null) {
            var id = model.data[0].id

            type = if (model.data[0].type == "tv") "电视" else "广播"

            if (type == "广播") {
                mMediaFullScreen.visibility = View.INVISIBLE
            }

            listRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
            val adapter = AudioListRvAdapter(R.layout.item_audio_view, model.data)
            adapter.setOnItemClickListener { position ->
                id = model.data[position].id
                type = if (model.data[position].type == "tv") "电视" else "广播"

                if (type == "广播") {
                    mMediaFullScreen.visibility = View.INVISIBLE
                } else {
                    mMediaFullScreen.visibility = View.VISIBLE
                }

                editTextBodyLl.visibility = View.GONE

                when (type) {
                    "电视" -> {
                        llFMLayout.visibility = View.GONE
                        llTVLayout.visibility = View.VISIBLE
                        optionLayout.visibility = View.GONE
                    }

                    "广播" -> {
                        llFMLayout.visibility = View.VISIBLE
                        llTVLayout.visibility = View.GONE
                        optionLayout.visibility = View.VISIBLE
                    }
                }
                mPresenter.getDetailsById(id)
            }


            listRecyclerView.adapter = adapter

            when (type) {
                "电视" -> {
                    llFMLayout.visibility = View.GONE
                    llTVLayout.visibility = View.VISIBLE
                    optionLayout.visibility = View.GONE
                }

                "广播" -> {
                    llFMLayout.visibility = View.VISIBLE
                    llTVLayout.visibility = View.GONE
                    optionLayout.visibility = View.VISIBLE
                }
            }


            commentLayout.setOnClickListener {
                if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(context, LoginActivity::class.java))
                    return@setOnClickListener
                }
                showInputDialog(id)
//                updateEditTextBodyVisible(View.VISIBLE)
            }

            //发表评论
            sendIv.setOnClickListener(View.OnClickListener {
                //发布评论
                val content = circleEt.text.toString().trim { it <= ' ' }
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showShort("评论内容不能为空...")
                    return@OnClickListener
                }

                mPresenter.addComment(id, content)
            })

            dianzanLayout.setOnClickListener {
                if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(context, LoginActivity::class.java))
                    return@setOnClickListener
                }

                if (isLike) {
                    mPresenter.cancelLikeNews(id)
                } else {
                    mPresenter.addLikeNews(id)
                }
            }

            mPresenter.getDetailsById(id)
        }
    }

    private var isFirst = true
    private var isUserVisibility = true
    override fun setDetails(item: NewsBean) {
        ivCover.visibility = View.VISIBLE

        if (mVideoView.isPlaying) {
            mVideoView.stopPlayback()
            mMediaActions.setImageResource(R.mipmap.ic_start_play)
        }

        val imageUrl: String = if (item.images == null || item.images.size == 0) {
            item.image
        } else {
            item.images[0]
        }

        GlideManager.loadImg(imageUrl, ivCover)
//      mVideoView.setCoverView(ivCover)

        mDYLoading.start()
        mVideoView.setBufferingIndicator(mDYLoading)
        mVideoView.displayAspectRatio = PLVideoView.ASPECT_RATIO_PAVED_PARENT
        mVideoView.setVideoPath(item.video)
        mMediaActions.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

                if (mVideoView.isPlaying) {
                    mMediaActions.setImageResource(R.mipmap.ic_start_play)
                    mVideoView.pause()
                } else {
                    mMediaActions.setImageResource(R.drawable.exo_icon_pause)
                    mVideoView.start()

                    mHandler.removeMessages(FADE_OUT)
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), sDefaultTimeout.toLong())

                    if (type == "电视") {
                        ivCover.visibility = View.INVISIBLE
                    } else {
                        ivCover.visibility = View.VISIBLE
                    }
                }
            }
        })

        mVideoView.setOnErrorListener { p0 ->
            true
        }

        mVideoView.setOnCompletionListener {
            mMediaActions.setImageResource(R.drawable.ic_video_play)
        }


        when (type) {
            "电视" -> {
                if (item.tel_list != null && item.tel_list.size == 3) {
                    llTVLayout.visibility = View.VISIBLE
                    rbYesterday.text = SpanUtils().appendLine("昨天").append(item.tel_list[0].date).create()
                    rbToday.text = SpanUtils().appendLine("今天").append(item.tel_list[1].date).create()
                    rbTomorrow.text = SpanUtils().appendLine("明天").append(item.tel_list[2].date).create()

                    rbToday.isChecked = true
                    onLoadSucceed(1, item.tel_list[1].list)


                    rgTime.setOnCheckedChangeListener { group, checkedId ->
                        when (checkedId) {
                            R.id.rbYesterday -> {
                                onLoadSucceed(1, item.tel_list[0].list)
                            }

                            R.id.rbToday -> {
                                onLoadSucceed(1, item.tel_list[1].list)
                            }

                            R.id.rbTomorrow -> {
                                onLoadSucceed(1, item.tel_list[2].list)
                            }
                        }

                    }

                } else {
                    llTVLayout.visibility = View.GONE
                }


            }

            "广播" -> {
                ivCover.visibility = View.VISIBLE
                this.like_num = item.like_num

                val drawables = tvGoodStatus.compoundDrawables
                isLike = item.like_status == 1

                if (item.like_status == 1) {
                    tvGoodStatus.text = "已赞"
                    tvGoodStatus.setTextColor(resources.getColor(R.color.colorAccent))
                    val redGoodDrawable = resources.getDrawable(R.mipmap.icon_good_select)
                    redGoodDrawable.bounds = drawables[0].getBounds()
                    tvGoodStatus.setCompoundDrawables(redGoodDrawable, drawables[1], drawables[2], drawables[3])
                } else {
                    tvGoodStatus.text = "赞"
                    tvGoodStatus.setTextColor(resources.getColor(R.color.default_gray_text_color))
                    val grayGoodDrawable = resources.getDrawable(R.mipmap.icon_details_dianzan)
                    grayGoodDrawable.bounds = drawables[0].getBounds()
                    tvGoodStatus.setCompoundDrawables(grayGoodDrawable, drawables[1], drawables[2], drawables[3])
                }


                tvCommentNum.text = "评论  ${item.comment_num}"
                tvCommentNum.setOnClickListener {
                    val intent = Intent(context, CommentListActivity::class.java)
                    intent.putExtra("id", item.id)
                    startActivity(intent)
                }

                tvGoodNum.text = "赞  ${item.like_num}"


                commentContainer.removeAllViews()
                //评论列表
                item.comment.forEach {
                    val commentView = View.inflate(context, R.layout.item_news_comment_view, null)
                    val ivAvatar = commentView.findViewById<ImageView>(R.id.ivAvatar)
                    val tvNickname = commentView.findViewById<TextView>(R.id.tvNickName)
                    val tvTime = commentView.findViewById<TextView>(R.id.tvTime)
                    val tvContent = commentView.findViewById<TextView>(R.id.tvContent)

                    if (it.avatar.trim().isNotEmpty()) {
                        GlideManager.loadRoundImg(it.avatar, ivAvatar, 5f)
                    }

                    tvNickname.text = it.nickname
                    tvTime.text = "" + it.createtime
                    tvContent.text = it.detail

                    commentContainer.addView(commentView)
                }

            }
        }

        if (isUserVisibility) {
            mMediaActions.performClick()
        }

    }

    override fun updateLikeStatus(isLike: Boolean) {
        this.isLike = isLike

        val drawables = tvGoodStatus.compoundDrawables

        if (isLike) {
            tvGoodStatus.text = "已赞"
            tvGoodStatus.setTextColor(resources.getColor(R.color.colorAccent))
            val redGoodDrawable = resources.getDrawable(R.mipmap.icon_good_select)
            redGoodDrawable.bounds = drawables[0].getBounds()
            tvGoodStatus.setCompoundDrawables(redGoodDrawable, drawables[1], drawables[2], drawables[3])

            like_num++
            tvGoodNum.text = "赞  $like_num"
        } else {
            tvGoodStatus.text = "赞"
            tvGoodStatus.setTextColor(resources.getColor(R.color.default_gray_text_color))
            val grayGoodDrawable = resources.getDrawable(R.mipmap.icon_details_dianzan)
            grayGoodDrawable.bounds = drawables[0].getBounds()
            tvGoodStatus.setCompoundDrawables(grayGoodDrawable, drawables[1], drawables[2], drawables[3])

            like_num--
            tvGoodNum.text = "赞  $like_num"
        }
    }

    override fun addCommentSuccess(id: Int) {
//        updateEditTextBodyVisible(View.GONE)
        mPresenter.getDetailsById(id)
    }

    override fun getListAsync(page: Int) {
        mPresenter.getNewList("视听", "", page, false)
    }

    override fun setList(list: MutableList<TelModel.ListBean>) {
        setList(object : AdapterCallBack<TVNewsDetailsRvAdapter> {

            override fun createAdapter(): TVNewsDetailsRvAdapter {
                return TVNewsDetailsRvAdapter(R.layout.item_tv_news_details_list_view, list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })

        adapter.notifyDataSetChanged()
    }

    private fun setFullScreen() {
        //此时是半屏,设置全屏播放
        mMediaFullScreen.setOnClickListener {
            startFullscreen(mActivity, mVideoView.javaClass)
            isFull = true
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (mVideoView.isPlaying) {
            if (ivCover.visibility == View.VISIBLE) {
                ivCover.visibility = View.GONE
            }
            mMediaActions.setImageResource(R.drawable.exo_icon_pause)
        } else {
            mMediaActions.setImageResource(R.mipmap.ic_start_play)
        }

    }

    private var viewConta: FrameLayout? = null

    private fun startFullscreen(context: Activity, _class: Class<PLVideoView>) {
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val vp = context.window.decorView.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)

        try {
            val constructor = _class.getConstructor(Context::class.java)
            val jzvd = constructor.newInstance(context)
            val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            viewConta = View.inflate(context, R.layout.item_video_cover_img, null) as FrameLayout
            viewConta!!.addView(jzvd, 0, lp)

            val mediaActions = viewConta!!.findViewById<ImageView>(R.id.mMediaActions)
            val mediaFullScreen = viewConta!!.findViewById<ImageView>(R.id.mMediaFullScreen)

            mediaActions.setOnClickListener {
                if (mVideoView.isPlaying) {
                    mediaActions.setImageResource(R.mipmap.ic_start_play)
                    mVideoView.pause()
                } else {
                    mediaActions.setImageResource(R.drawable.exo_icon_pause)
                    mVideoView.start()
                }
            }

            mediaFullScreen.setOnClickListener {
                context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                vp.removeView(viewConta)
                isFull = false
            }

            vp.addView(viewConta, lp)
            mVideoView.start()
            mediaActions.setImageResource(R.drawable.exo_icon_pause)

        } catch (e: java.lang.InstantiationException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onBackPressed(): Boolean {
        LogUtils.e(isFull)
        return if (isFull) {
            mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            val vp = mActivity.window.decorView.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
            vp.removeView(viewConta)
            isFull = false
            true
        } else {
            false
        }
    }



    private fun showInputDialog(id: Int) {
        val dialog = InputDialog(mActivity, "输入评论", true)
        dialog.setConfirmListerner {
            val content = dialog.content.trim { it <= ' ' }
            if (TextUtils.isEmpty(content)) {
                ToastUtils.showShort("评论内容不能为空...")
                return@setConfirmListerner
            }
            mPresenter.addComment(id, content)
            ToastUtils.showShort(content)
            dialog.dismiss()
        }
        dialog.setCancelListerner { dialog.dismiss() }
        dialog.show()
    }


    private fun updateEditTextBodyVisible(visibility: Int) {
        editTextBodyLl.visibility = visibility

        if (View.VISIBLE == visibility) {
            optionLayout.visibility = View.GONE

            circleEt.requestFocus()
            //弹出键盘
            mActivity?.showSoftInput(circleEt)

        } else if (View.GONE == visibility) {
            optionLayout.visibility = View.VISIBLE
            //隐藏键盘
            mActivity?.hideSoftInput(circleEt.windowToken)
        }
    }


}