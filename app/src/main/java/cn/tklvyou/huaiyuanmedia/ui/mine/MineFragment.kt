package cn.tklvyou.huaiyuanmedia.ui.mine

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
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
import cn.tklvyou.huaiyuanmedia.ui.mine.wenzhen.MyWenZhenActivity
import cn.tklvyou.huaiyuanmedia.ui.setting.AboutUsActivity
import cn.tklvyou.huaiyuanmedia.ui.setting.SettingActivity
import cn.tklvyou.huaiyuanmedia.utils.GridDividerItemDecoration
import cn.tklvyou.huaiyuanmedia.utils.JSON
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson
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
        LogUtils.e(Gson().toJson(user))

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
                startActivity(Intent(context, MyCollectActivity::class.java))
            }
            //我的评论
            1 -> {
                startActivity(Intent(context, MyCommentActivity::class.java))
            }
            //我的点赞
            2 -> {
                startActivity(Intent(context, MyDianZanActivity::class.java))
            }
            //我的关注
            3 -> {
                startActivity(Intent(context, MyConcernActivity::class.java))
            }
            //我的消息
            4 -> {
                startActivity(Intent(context, MyMessageActivity::class.java))
            }
            //我的帖子
            5 -> {
                startActivity(Intent(context, MyArticleActivity::class.java))
            }
            //最近浏览
            6 -> {
                startActivity(Intent(context, RecentBrowseActivity::class.java))
            }
            //爆料记录
            7 -> {
                startActivity(Intent(context, MyWenZhenActivity::class.java))
            }
            //兑换记录
            8 -> {
                startActivity(Intent(context, MyExchangeRecordActivity::class.java))
            }
            //关于我们
            9 -> {
                startActivity(Intent(context, AboutUsActivity::class.java))
            }
            else -> {
            }
        }

    }

}