package cn.tklvyou.huaiyuanmedia.ui.home.juzheng_details

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.common.ModuleUtils
import cn.tklvyou.huaiyuanmedia.helper.GlideManager
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.HaveSecondModuleNewsModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.model.SectionNewsMultipleItem
import cn.tklvyou.huaiyuanmedia.ui.adapter.SectionMultipleItemAdapter
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.video_player.VodActivity
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_ju_zheng_details.*

class JuZhengDetailsActivity : BaseHttpRecyclerActivity<ListPresenter, SectionNewsMultipleItem<Any>, BaseViewHolder, SectionMultipleItemAdapter>(), ListContract.View {


    override fun initPresenter(): ListPresenter {
        return ListPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_ju_zheng_details
    }


    private lateinit var model: HaveSecondModuleNewsModel.ModuleSecondBean
    override fun initView(savedInstanceState: Bundle?) {
        model = Gson().fromJson<HaveSecondModuleNewsModel.ModuleSecondBean>(intent.getStringExtra("model"), HaveSecondModuleNewsModel.ModuleSecondBean::class.java)
//        setTitle(model.pname)
        setTitle("", R.mipmap.home_title_logo)
        setNavigationImage()
        setNavigationOnClickListener { finish() }

        initSmartRefreshLayout(mSmartRefreshLayout)
        initRecyclerView(mRecyclerView)

        mRecyclerView.addItemDecoration(RecycleViewDivider(this, LinearLayout.VERTICAL))

        mSmartRefreshLayout.autoRefresh()
    }

    override fun onRetry() {
        super.onRetry()
        mSmartRefreshLayout.autoRefresh()
    }

    override fun getListAsync(page: Int) {
        mPresenter.getNewList(model.pname, model.nickname, page)
    }

    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {
        if (model != null) {
            val newList = ArrayList<SectionNewsMultipleItem<Any>>()

            model.data.forEach {
                newList.add(SectionNewsMultipleItem(this.model.pname, it))
            }
            onLoadSucceed(p, newList)
        } else {
            onLoadFailed(p, null)
        }
    }


    override fun setList(list: MutableList<SectionNewsMultipleItem<Any>>?) {
        setList(object : AdapterCallBack<SectionMultipleItemAdapter> {

            override fun createAdapter(): SectionMultipleItemAdapter {
                val adapter = SectionMultipleItemAdapter(list)
                val headerView = View.inflate(this@JuZhengDetailsActivity, R.layout.header_ju_zheng_view, null)
                headerView.findViewById<TextView>(R.id.tvNickName).text = model.nickname
                headerView.findViewById<TextView>(R.id.tvDetails).text = model.detail
                GlideManager.loadCircleImg(model.avatar, headerView.findViewById(R.id.ivIcon))
                val width = ScreenUtils.getAppScreenWidth()
                val height = ConvertUtils.dp2px(235f)
                Glide.with(this@JuZhengDetailsActivity)
                        .asBitmap()
                        .load(model.image)
                        .override(width,height)
                        .into(object : SimpleTarget<Bitmap>(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL) {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                val drawable = BitmapDrawable(resource)
                                val container = headerView.findViewById<LinearLayout>(R.id.llContainer)
                                container.background = drawable
                            }
                        })
                adapter.addHeaderView(headerView)
                return adapter
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)

            }
        })
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        val bean = (adapter as SectionMultipleItemAdapter).data[position].dataBean as NewsBean
        val id = bean.id

        val type = ModuleUtils.getTypeByNewsBean(bean)

        if (bean.url.isNotEmpty()) {
            startDetailsActivity(this, bean.url)
        } else {
            startNewsDetailActivity(this, type, id, position)
        }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)

        if (adapter !is SectionMultipleItemAdapter) {
            return
        }

        when (view!!.id) {
            R.id.sparkButton, R.id.tvGoodNum -> {
                val bean = adapter.data[position].dataBean as NewsBean
                if (bean.like_status == 1) {
                    mPresenter.cancelLikeNews(bean.id, position)
                } else {
                    mPresenter.addLikeNews(bean.id, position)
                }
            }

            //V视频 直播 播放按钮
            R.id.ivStartPlayer -> {
                val bean = adapter.data[position].dataBean as NewsBean
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(this, bean.url)
                } else {
                    //打开新的Activity
                    val intent = Intent(this, VodActivity::class.java)
                    intent.putExtra("videoPath", bean.video)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val position = data.getIntExtra("position", 0)
            val seeNum = data.getIntExtra("seeNum", 0)
            val zanNum = data.getIntExtra("zanNum", 0)
            val commenNum = data.getIntExtra("commentNum", 0)
            val like_status = data.getIntExtra("like_status", 0)

            val bean = adapter!!.data[position].dataBean as NewsBean
            bean.comment_num = commenNum
            bean.like_num = zanNum
            bean.visit_num = seeNum
            bean.like_status = like_status
            adapter.notifyItemChanged(position + 1)

        }
    }

    private fun startDetailsActivity(context: Context, url: String) {
        val intent = Intent(context, ServiceWebviewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("other", true)
        intent.putExtra("share_title", "")
        startActivity(intent)
    }

    private fun startNewsDetailActivity(context: Context, type: String, id: Int, position: Int) {
        val intent = Intent(context, NewsDetailActivity::class.java)
        intent.putExtra(NewsDetailActivity.INTENT_ID, id)
        intent.putExtra(NewsDetailActivity.INTENT_TYPE, type)
        intent.putExtra(NewsDetailActivity.POSITION, position)
        startActivityForResult(intent, 0)
    }

    override fun updateLikeStatus(isLike: Boolean, position: Int) {
        if (isLike) {
            (adapter.data[position].dataBean as NewsBean).like_status = 1
            (adapter.data[position].dataBean as NewsBean).like_num = (adapter.data[position].dataBean as NewsBean).like_num + 1
        } else {
            (adapter.data[position].dataBean as NewsBean).like_status = 0
            (adapter.data[position].dataBean as NewsBean).like_num = (adapter.data[position].dataBean as NewsBean).like_num - 1
        }

        adapter.notifyItemChangedAnimal(position + 1)
    }

}
