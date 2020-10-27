package cn.tklvyou.huaiyuanmedia.ui.mine

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.common.Contacts
import cn.tklvyou.huaiyuanmedia.helper.GlideManager
import cn.tklvyou.huaiyuanmedia.model.MineRvModel
import cn.tklvyou.huaiyuanmedia.model.User
import cn.tklvyou.huaiyuanmedia.ui.account.LoginActivity
import cn.tklvyou.huaiyuanmedia.ui.account.data.PersonalDataActivity
import cn.tklvyou.huaiyuanmedia.ui.adapter.MineRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.mine.browse.RecentBrowseActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.collection.MyCollectActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.comment.MyCommentActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.concern.MyConcernActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.exchange.MyExchangeRecordActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.message.MyMessageActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.my_article.MyArticleActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.my_dianzan.MyDianZanActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.point.MyPointDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.reward.RewardRecordActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.wenzhen.MyWenZhenActivity
import cn.tklvyou.huaiyuanmedia.ui.setting.AboutUsActivity
import cn.tklvyou.huaiyuanmedia.ui.setting.SettingActivity
import cn.tklvyou.huaiyuanmedia.utils.GridDividerItemDecoration
import cn.tklvyou.huaiyuanmedia.utils.InterfaceUtils
import cn.tklvyou.huaiyuanmedia.utils.JSON
import cn.tklvyou.huaiyuanmedia.utils.YBitmapUtils
import cn.tklvyou.huaiyuanmedia.widget.SharePopupWindow
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ResourceUtils
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
import kotlinx.android.synthetic.main.fragment_mine.*

class MineFragment : BaseRecyclerFragment<MinePresenter, MineRvModel, BaseViewHolder, MineRvAdapter>(), MineContract.View, View.OnClickListener {

    override fun initPresenter(): MinePresenter {
        return MinePresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_mine
    }

    override fun getLoadingView(): View {
        return mineRecyclerView
    }

    private var url = Contacts.SHARE_DOWNLOAD_URL
    private var shareTitle = "榴乡怀远"
    private var shareHandler: WbShareHandler? = null
    private var wxapi: IWXAPI? = null

    override fun initView() {
        mineTitleBar.setBackgroundResource(android.R.color.transparent)
        mineTitleBar.setPositiveListener {
            startActivity(Intent(context, SettingActivity::class.java))
        }
        ivAvatar.setOnClickListener(this)
        tvMobile.setOnClickListener(this)
        tvNickName.setOnClickListener(this)
        llMyPointDetail.setOnClickListener(this)
        initRecyclerView(mineRecyclerView)
        mineRecyclerView.layoutManager = GridLayoutManager(context, 4)
        mineRecyclerView.addItemDecoration(GridDividerItemDecoration(40, Color.WHITE))

        val json = ResourceUtils.readAssets2String("minelist.json")
        val data = JSON.parseArray(json, MineRvModel::class.java)
        onLoadSucceed(1, data)

        if (SPUtils.getInstance().getString("token", "").isNotEmpty()) {
            mPresenter.getUser()
        }
    }

    override fun lazyData() {
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && !isFirstResume) {
            if (SPUtils.getInstance().getString("token", "").isNotEmpty()) {
                mPresenter.getUser()
            }
        }
    }

    override fun onUserVisible() {
        super.onUserVisible()
        if (SPUtils.getInstance().getString("token", "").isNotEmpty()) {
            mPresenter.getUser()
        }
    }

    override fun onClick(v: View?) {
        if (v == null) {
            return
        }
        when (v.id) {
            R.id.ivAvatar -> {
                skipPersonalData()
            }
            R.id.tvMobile -> {
                skipPersonalData()
            }
            R.id.tvNickName -> {
                skipPersonalData()
            }
            R.id.llMyPointDetail -> {
                if (SPUtils.getInstance().getString("token", "").isNotEmpty()) {
                    startActivity(Intent(context, MyPointDetailActivity::class.java))
                } else {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(context, LoginActivity::class.java))
                }
            }
            else -> {
            }
        }
    }

    override fun setUser(user: User.UserinfoBean) {

        val avatar = user.avatar
        if (!avatar.isNullOrEmpty() && !avatar.contains("base64")) {
            GlideManager.loadCircleImg(avatar, ivAvatar, R.mipmap.default_avatar)
        }

        tvNickName.text = user.nickname
        tvMobile.text = user.mobile
        tvPoint.text = user.score

        adapter.setBadgeNumber(user.unread)

    }


    override fun setList(list: MutableList<MineRvModel>?) {
        setList(object : AdapterCallBack<MineRvAdapter> {

            override fun createAdapter(): MineRvAdapter {
                return MineRvAdapter(R.layout.item_mine_rv_view, list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }

    override fun getListAsync(page: Int) {
    }

    private fun skipPersonalData() {
        if (SPUtils.getInstance().getString("token", "").isNotEmpty()) {
            startActivity(Intent(context, PersonalDataActivity::class.java))
        } else {
            startActivity(Intent(context, LoginActivity::class.java))
        }
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        when (position) {
            //我的收藏
            0 -> {
                if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(context, LoginActivity::class.java))
                    return
                }
                startActivity(Intent(context, MyCollectActivity::class.java))
            }
            //我的评论
            1 -> {
                if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(context, LoginActivity::class.java))
                    return
                }
                startActivity(Intent(context, MyCommentActivity::class.java))
            }
            //我的点赞
            2 -> {
                if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(context, LoginActivity::class.java))
                    return
                }
                startActivity(Intent(context, MyDianZanActivity::class.java))
            }
            //我的关注
            3 -> {
                if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(context, LoginActivity::class.java))
                    return
                }
                startActivity(Intent(context, MyConcernActivity::class.java))
            }
            //我的消息
            4 -> {
                if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(context, LoginActivity::class.java))
                    return
                }
                startActivity(Intent(context, MyMessageActivity::class.java))
            }
            //我的帖子
            5 -> {
                if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(context, LoginActivity::class.java))
                    return
                }
                startActivity(Intent(context, MyArticleActivity::class.java))
            }
            //最近浏览
            6 -> {
                if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(context, LoginActivity::class.java))
                    return
                }
                startActivity(Intent(context, RecentBrowseActivity::class.java))
            }
            //爆料记录
            7 -> {
                if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(context, LoginActivity::class.java))
                    return
                }
                startActivity(Intent(context, MyWenZhenActivity::class.java))
            }
            //兑换记录
            8 -> {
                if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(context, LoginActivity::class.java))
                    return
                }
                startActivity(Intent(context, MyExchangeRecordActivity::class.java))
            }
            //关于我们
            10 -> {
                startActivity(Intent(context, AboutUsActivity::class.java))
            }
            //软件分享
            11 ->{
                doShare()
            }
            //中奖纪录
            9->{
                if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                    ToastUtils.showShort("请登录后操作")
                    startActivity(Intent(context, LoginActivity::class.java))
                    return
                }
                startActivity(Intent(context, RewardRecordActivity::class.java))
            }
            else -> {
            }
        }

    }


    private fun doShare() {
        val sharePopupWindow = SharePopupWindow(mActivity)
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
        sharePopupWindow.showAtScreenBottom(mineRecyclerView)
    }

    /**
     * 判断是否安装微信
     */
    private fun isWeiXinAppInstall(): Boolean {
        if (wxapi == null)
            wxapi = WXAPIFactory.createWXAPI(mActivity, Contacts.WX_APPID)
        if (wxapi!!.isWXAppInstalled) {
            return true
        } else {
            ToastUtils.showShort("您未安装微信客户端")
            return false
        }
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
        webpage.webpageUrl = url
        val msg = WXMediaMessage(webpage)
        msg.title = shareTitle
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


    private var mTencent: Tencent? = null
    private fun shareToQQ() {
        mTencent = Tencent.createInstance(Contacts.QQ_APPID, mActivity.application)

        if (!mTencent!!.isQQInstalled(mActivity)) {
            ToastUtils.showShort("您未安装QQ客户端")
            return
        }

        val params = Bundle()
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitle)
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url) //必填 	这条分享消息被好友点击后的跳转URL。
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "榴乡怀远")
        mTencent!!.shareToQQ(mActivity, params, object : IUiListener {
            override fun onComplete(p0: Any?) {
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
        webpage.webpageUrl = url
        val msg = WXMediaMessage(webpage)
        msg.title = shareTitle
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
            mediaObject.title = shareTitle
            mediaObject.description = "榴乡怀远"
            val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.default_avatar)
            mediaObject.setThumbImage(YBitmapUtils.changeColor(bitmap))
            mediaObject.actionUrl = url
            mediaObject.defaultText = "Webpage"
            weiboMessage.mediaObject = mediaObject

            shareHandler!!.shareMessage(weiboMessage, false)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (mTencent != null) {
            Tencent.onActivityResultData(requestCode, resultCode, data, null)
        }
    }

    private var onClickResult = object : InterfaceUtils.OnClickResult {
        override fun onResult(msg: String?) {
            ToastUtils.showShort("分享成功")
            InterfaceUtils.getInstance().remove(this)
        }

    }

}