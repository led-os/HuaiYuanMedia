package cn.tklvyou.huaiyuanmedia.ui.camera.history_updates

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseHttpRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.common.Contacts
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.WxCircleAdapter
import cn.tklvyou.huaiyuanmedia.ui.camera.message.ArticleMessageActivity
import cn.tklvyou.huaiyuanmedia.ui.home.life_detail.LifeDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.utils.InterfaceUtils
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.utils.YBitmapUtils
import cn.tklvyou.huaiyuanmedia.widget.SharePopupWindow
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sina.weibo.sdk.api.WebpageObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.sina.weibo.sdk.share.WbShareHandler
import com.sina.weibo.sdk.utils.Utility
import com.tencent.connect.share.QQShare
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.android.synthetic.main.fragment_history_updates.*

/**
 * 最新动态  好友动态
 */
class HistoryUpdatesFragment : BaseHttpRecyclerFragment<HistoryUpdatesPresenter, NewsBean, BaseViewHolder, WxCircleAdapter>(), HistoryUpdatesContract.View {

    private var isMine = true
    private var fragmentIndex = 0
    private var wxapi: IWXAPI? = null
    private var mTencent: Tencent? = null
    var id: Int? = -1

    private var shareTitle: String? = ""
    override fun initPresenter(): HistoryUpdatesPresenter {
        return HistoryUpdatesPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_history_updates
    }

    override fun getLoadingView(): View {
        return mRecyclerView
    }

    private lateinit var headerView: View

    private var shareHandler: WbShareHandler? = null

    private var onClickResult = object : InterfaceUtils.OnClickResult {
        override fun onResult(msg: String?) {
            ToastUtils.showShort("分享成功")
            if (SPUtils.getInstance().getString("token", "").isNotEmpty()) {
                id?.let { mPresenter.getScoreByShare(it) }
            }
            InterfaceUtils.getInstance().remove(this)
        }

    }

    override fun initView() {
        isMine = mBundle.getBoolean("isMine", true)
        fragmentIndex = mBundle.getInt("fragmentIndex", 0)
        initSmartRefreshLayout(mSmartRefreshLayout)
        initRecyclerView(mRecyclerView)

        mSmartRefreshLayout.setEnableRefresh(false)

        mRecyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))

        headerView = View.inflate(mActivity, R.layout.header_camera_message, null)

        mPresenter.getNewList(isMine, 1)
    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getNewList(isMine, 1)
    }

    override fun lazyData() {
    }

    override fun getListAsync(page: Int) {
        mPresenter.getNewList(isMine, page)
    }

    private var message: String? = null
    public fun flushHeaderView(message: String?) {
        if (adapter != null) {
            if (message == null) {
                adapter.removeHeaderView(headerView)
            } else {
                val tvTip = headerView.findViewById<TextView>(R.id.tvTip)
                if (adapter.headerLayoutCount == 0) {
                    tvTip.text = message
                    tvTip.setOnClickListener {
                        startActivity(Intent(mActivity, ArticleMessageActivity::class.java))
                    }
                    adapter.addHeaderView(headerView)
                } else {
                    tvTip.text = message
                }
                mRecyclerView.scrollToPosition(0)
            }
        } else {
            this.message = message
        }
    }

    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {
        if (model != null) {
            onLoadSucceed(p, model.data)
        } else {
            onLoadFailed(p, null)
        }
    }

    override fun setList(list: MutableList<NewsBean>?) {
        setList(object : AdapterCallBack<WxCircleAdapter> {

            override fun createAdapter(): WxCircleAdapter {
                val adapter = WxCircleAdapter(R.layout.item_winxin_circle, list, fragmentIndex)
                if (!message.isNullOrEmpty()) {
                    val tvTip = headerView.findViewById<TextView>(R.id.tvTip)
                    tvTip.text = message
                    tvTip.setOnClickListener {
                        startActivity(Intent(mActivity, ArticleMessageActivity::class.java))
                    }

                    adapter.addHeaderView(headerView)
                    message = ""
                }
                return adapter
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })

    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        skipNewsDetail(position)
        /*val bean = (adapter as WxCircleAdapter).data[position]
        val id = bean.id
        val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
        startNewsDetailActivity(context!!, type, id, position)*/

    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)

        when (view!!.id) {
            R.id.deleteBtn -> {
                val dialog = CommonDialog(context)
                dialog.setTitle("温馨提示")
                dialog.setMessage("是否删除？")
                dialog.setYesOnclickListener("确认") {
                    val bean = (adapter as WxCircleAdapter).data[position]
                    mPresenter.deleteArticle(bean.id, position)
                    dialog.dismiss()
                }
                dialog.show()
            }
            R.id.contentTv -> {
                skipNewsDetail(position)
            }
            R.id.textPlus -> {
                skipNewsDetail(position)
            }
            R.id.contentText -> {
                skipNewsDetail(position)
            }
            R.id.tvAttention -> {
                val bean = (adapter as WxCircleAdapter).data[position] as NewsBean
                if (bean.attention_status == 1) {
                    mPresenter.cancelConcern(bean.user_id, 2, position)
                } else {
                    mPresenter.addConcern(bean.user_id, 2, position)
                }
            }
            R.id.ivShareNum, R.id.tvShareNum -> {
                doShare(position)
            }

            R.id.sparkButton, R.id.tvGoodNum -> {
                val bean = (adapter as WxCircleAdapter).data[position] as NewsBean
                if (bean.like_status == 1) {
                    mPresenter.cancelLikeNews(bean.id, position)
                } else {
                    mPresenter.addLikeNews(bean.id, position)
                }

            }
        }

    }

    override fun updateLikeStatus(isLike: Boolean, position: Int) {
        if (isLike) {
            adapter.data[position].like_status = 1
            adapter.data[position].like_num = adapter.data[position].like_num + 1
        } else {
            adapter.data[position].like_status = 0
            adapter.data[position].like_num = adapter.data[position].like_num - 1
        }

        if (adapter.headerLayoutCount == 0) {
            adapter.notifyItemChangedAnimal(position)
        } else {
            adapter.notifyItemChangedAnimal(position + 1)
        }

    }

    override fun addConcernSuccess(position: Int) {
        adapter.data[position].attention_status = 1
        if (adapter.headerLayoutCount == 0) {
            adapter.notifyItemChangedAnimal(position)
        } else {
            adapter.notifyItemChangedAnimal(position + 1)
        }


    }

    override fun cancelConcernSuccess(position: Int) {
        adapter.data[position].attention_status = 0
        if (adapter.headerLayoutCount == 0) {
            adapter.notifyItemChangedAnimal(position)
        } else {
            adapter.notifyItemChangedAnimal(position + 1)
        }
    }


    override fun deleteSuccess(position: Int) {
        adapter.remove(position)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val position = data.getIntExtra("position", 0)
            val seeNum = data.getIntExtra("seeNum", 0)
            val zanNum = data.getIntExtra("zanNum", 0)
            val commenNum = data.getIntExtra("commentNum", 0)
            val like_status = data.getIntExtra("like_status", 0)

            val bean = (adapter as WxCircleAdapter).data[position]
            bean.comment_num = commenNum
            bean.like_num = zanNum
            bean.visit_num = seeNum
            bean.like_status = like_status

            if (adapter.headerLayoutCount == 0) {
                adapter.notifyItemChangedAnimal(position)
            } else {
                adapter.notifyItemChangedAnimal(position + 1)
            }

        }
    }

    private fun startNewsDetailActivity(context: Context, type: String, id: Int, position: Int) {
        val intent = Intent(context, LifeDetailActivity::class.java)
        intent.putExtra(NewsDetailActivity.INTENT_ID, id)
        intent.putExtra(NewsDetailActivity.INTENT_TYPE, type)
        intent.putExtra(NewsDetailActivity.POSITION, position)
        startActivityForResult(intent, 0)
    }


    private fun skipNewsDetail(position: Int) {
        val bean = (adapter as WxCircleAdapter).data[position]
        val id = bean.id
        val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
        startNewsDetailActivity(context!!, type, id, position)
    }

    private fun doShare(position: Int) {
        if (position >= adapter.data.size) {
            return
        }
        val newsBean = adapter.data[position]
        shareTitle = newsBean.name
        id = newsBean.id
        val sharePopupWindow = SharePopupWindow(context)
        sharePopupWindow.setISharePopupWindowClickListener(object : SharePopupWindow.ISharePopupWindowClickListener {
            override fun onWxClick() {
                shareToWX()
                sharePopupWindow.dismiss()
            }

            override fun onWxFriendClick() {
                shareToWXFriend()
                sharePopupWindow.dismiss()
            }

            override fun onQQClick() {
                shareToQQ()
                sharePopupWindow.dismiss()
            }

            override fun onWBClick() {
                shareToWB()
                sharePopupWindow.dismiss()
            }

        })
        sharePopupWindow.showAtScreenBottom(mRecyclerView)
    }

    private fun shareToWXFriend() {
        if (!isWeiXinAppInstall()) {
            ToastUtils.showShort("您未安装微信")
            return
        }
        if (!isWXAppSupportAPI()) {
            ToastUtils.showShort("您的微信版本不支持分享功能")
            return
        }

        val webpage = WXWebpageObject()
        webpage.webpageUrl = Contacts.SHARE_BASE_URL + id + "?date=" + System.currentTimeMillis()
        val msg = WXMediaMessage(webpage)
        msg.title = handleLimitText(shareTitle)
        msg.description = "榴乡怀远"
        val bmp = BitmapFactory.decodeResource(resources, R.mipmap.img_logo)
        val thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true)
        bmp.recycle()
        msg.thumbData = ImageUtils.bitmap2Bytes(YBitmapUtils.changeColor(thumbBmp), Bitmap.CompressFormat.JPEG)
        val req = SendMessageToWX.Req()
        req.transaction = "webpage" + System.currentTimeMillis()
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneTimeline
        wxapi!!.sendReq(req)

        InterfaceUtils.getInstance().add(onClickResult)
    }


    /**
     * 判断是否安装微信
     */
    fun isWeiXinAppInstall(): Boolean {
        if (wxapi == null)
            wxapi = WXAPIFactory.createWXAPI(mActivity, Contacts.WX_APPID)
        if (wxapi!!.isWXAppInstalled) {
            return true
        } else {
            ToastUtils.showShort("您未安装微信客户端")
            return false
        }
    }

    /**
     * 是否支持分享到朋友圈
     */
    fun isWXAppSupportAPI(): Boolean {
        if (isWeiXinAppInstall()) {
            val wxSdkVersion = wxapi!!.wxAppSupportAPI
            return wxSdkVersion >= 0x21020001
        } else {
            return false
        }
    }

    private fun shareToWX() {
        if (!isWeiXinAppInstall()) {
            ToastUtils.showShort("您未安装微信")
            return
        }
        if (!isWXAppSupportAPI()) {
            ToastUtils.showShort("您的微信版本不支持分享功能")
            return
        }

        val webpage = WXWebpageObject()
        webpage.webpageUrl = Contacts.SHARE_BASE_URL + id + "?date=" + System.currentTimeMillis()
        val msg = WXMediaMessage(webpage)
        msg.title = handleLimitText(shareTitle)
//        msg.title = shareTitle
        msg.description = "榴乡怀远"
        val bmp = BitmapFactory.decodeResource(resources, R.mipmap.img_logo)
        val thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true)
        bmp.recycle()
        msg.thumbData = ImageUtils.bitmap2Bytes(YBitmapUtils.changeColor(thumbBmp), Bitmap.CompressFormat.JPEG)
        val req = SendMessageToWX.Req()
        req.transaction = "webpage" + System.currentTimeMillis()
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneSession
        wxapi!!.sendReq(req)

        InterfaceUtils.getInstance().add(onClickResult)

    }

    private fun shareToQQ() {
        mTencent = Tencent.createInstance(Contacts.QQ_APPID, mActivity.application)

        if (!mTencent!!.isQQInstalled(mActivity)) {
            ToastUtils.showShort("您未安装QQ客户端")
            return
        }

        val params = Bundle()
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
        params.putString(QQShare.SHARE_TO_QQ_TITLE, handleLimitText(shareTitle))
//        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "摘要") //可选，最长40个字
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Contacts.SHARE_BASE_URL + id) //必填 	这条分享消息被好友点击后的跳转URL。
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "榴乡怀远")
        mTencent!!.shareToQQ(mActivity, params, object : IUiListener {
            override fun onComplete(p0: Any?) {
                if (SPUtils.getInstance().getString("token", "").isNotEmpty()) {
                    id?.let { mPresenter.getScoreByShare(it) }
                }
                ToastUtils.showShort("分享成功")
            }

            override fun onCancel() {
                ToastUtils.showShort("取消分享")
            }

            override fun onError(p0: UiError?) {
                ToastUtils.showShort("分享失败：" + p0?.errorMessage)
            }

        })
    }

    private fun shareToWB() {
        val pinfo = mActivity.packageManager.getInstalledPackages(0)// 获取所有已安装程序的包信息
        var isInstall = false
        if (pinfo != null) {
            pinfo.forEach {

                val pn = it.packageName
                if (pn == "com.sina.weibo") {
                    isInstall = true
                }
            }
        }
        if (!isInstall) {
            ToastUtils.showShort("您未安装微博")
        } else {
            shareHandler = WbShareHandler(mActivity)
            shareHandler!!.registerApp()

            val weiboMessage = WeiboMultiMessage()

            val mediaObject = WebpageObject()
            mediaObject.identify = Utility.generateGUID()
            mediaObject.title = handleLimitText(shareTitle)
            mediaObject.description = "榴乡怀远"
            val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.default_avatar)
            mediaObject.setThumbImage(YBitmapUtils.changeColor(bitmap))
            mediaObject.actionUrl = Contacts.SHARE_BASE_URL + id
            mediaObject.defaultText = "Webpage"
            weiboMessage.mediaObject = mediaObject

            shareHandler!!.shareMessage(weiboMessage, false)
        }
    }

    private fun handleLimitText(title: String?): String {
        if (TextUtils.isEmpty(title)) {
            return "榴乡怀远"
        }
        if (title!!.length > 50) {
            return title.substring(0, 50)
        }
        return title
    }
}