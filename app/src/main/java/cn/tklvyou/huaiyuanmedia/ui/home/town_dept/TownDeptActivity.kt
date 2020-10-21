package cn.tklvyou.huaiyuanmedia.ui.home.town_dept

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.common.ModuleUtils
import cn.tklvyou.huaiyuanmedia.model.*
import cn.tklvyou.huaiyuanmedia.ui.adapter.SectionMultipleItemAdapter
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import cn.tklvyou.huaiyuanmedia.ui.home.BannerDetailsActivity
import cn.tklvyou.huaiyuanmedia.ui.home.ChannelConstant
import cn.tklvyou.huaiyuanmedia.ui.home.ChannelConstant.CHANNEL_TYPE_TOWN_SECOND
import cn.tklvyou.huaiyuanmedia.ui.home.ChannelConstant.EXTRA_HOME_CHANNEL
import cn.tklvyou.huaiyuanmedia.ui.home.juzheng_details.ListContract
import cn.tklvyou.huaiyuanmedia.ui.home.juzheng_details.ListPresenter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.video_player.VodActivity
import cn.tklvyou.huaiyuanmedia.utils.BannerGlideImageLoader
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.tencent.liteav.demo.player.activity.SuperPlayerActivity
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.activity_ju_zheng_details.*

/**
 *@description :乡镇部门页面
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2020年10月20日17:22
 * @Email: 971613168@qq.com
 */
class TownDeptActivity : BaseHttpRecyclerActivity<ListPresenter, SectionNewsMultipleItem<Any>, BaseViewHolder, SectionMultipleItemAdapter>(), ListContract.View {
    private lateinit var townModel: TownDataModel
    private lateinit var param: String
    private lateinit var bannerModelList: MutableList<BannerModel>
    private val mTag = "TownDeptActivity"
    private var newList :ArrayList<SectionNewsMultipleItem<Any>>? =null
    private var mPage : Int = 1
    override fun initPresenter(): ListPresenter {
        return ListPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_ju_zheng_details
    }


    override fun initView(savedInstanceState: Bundle?) {
//        setTitle(model.pname)
        townModel = intent.getSerializableExtra(ChannelConstant.EXTRA_TOWN_DATA) as TownDataModel
        param = intent.getStringExtra(EXTRA_HOME_CHANNEL)
        setTitle(townModel.module_second, R.mipmap.home_title_logo)
        setNavigationImage()
        setNavigationOnClickListener { finish() }

        initSmartRefreshLayout(mSmartRefreshLayout)
        initRecyclerView(mRecyclerView)

        mRecyclerView.addItemDecoration(RecycleViewDivider(this, LinearLayout.VERTICAL))
        loadData()
    }

    private fun loadData(){
        mPresenter.getSecondBanner(param,townModel.module_second)
    }

    override fun onRetry() {
        super.onRetry()
        loadData()
    }

    override fun setBanner(bannerModelList: MutableList<BannerModel>?) {
        if (bannerModelList != null) {
            this.bannerModelList = bannerModelList
            mPresenter.getNewList(param, townModel.module_second, 1)
        }else{
            onLoadFailed(1, null)
        }
    }

    override fun getListAsync(page: Int) {
        mPresenter.getNewList(param, townModel.module_second, page)
    }

    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {
        if (model != null) {
             newList = ArrayList<SectionNewsMultipleItem<Any>>()
            model.data.forEach {
                newList!!.add(SectionNewsMultipleItem(CHANNEL_TYPE_TOWN_SECOND, it))
            }
          onLoadSucceed(p,newList)
        } else {
            onLoadFailed(p, null)
        }
    }


    override fun setList(list: MutableList<SectionNewsMultipleItem<Any>>?) {
        setList(object : AdapterCallBack<SectionMultipleItemAdapter> {

            override fun createAdapter(): SectionMultipleItemAdapter {
                val adapter = SectionMultipleItemAdapter(list)
                val bannerView = View.inflate(mContext, R.layout.item_normal_banner, null)
                initBannerView(bannerView, bannerModelList)
                adapter.addHeaderView(bannerView)
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

    private fun initBannerView(view: View, bannerModelList: MutableList<BannerModel>) {

        val images = ArrayList<String>()
        val titles = ArrayList<String>()

        bannerModelList.forEach {
            titles.add(it.name)
            images.add(it.image)
        }

        val banner = view.findViewById<Banner>(R.id.banner)
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
        //设置图片加载器
        banner.setImageLoader(BannerGlideImageLoader())
        //设置图片集合
        banner.setImages(images)
        //设置banner动画效果
//        banner.setBannerAnimation(Transformer.DepthPage)
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(titles)
        //设置自动轮播，默认为true
        banner.isAutoPlay(true)
        //设置轮播时间
        banner.setDelayTime(3000)
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.RIGHT)
        banner.setOnBannerListener(object : OnBannerListener {
            override fun OnBannerClick(position: Int) {
                if (bannerModelList[position].article_id != 0) {
                    //跳转至新闻详情
                    bannerSkipToNewsDetail(bannerModelList[position])
                } else if (bannerModelList[position].url.trim().isNotEmpty()) {
                    //跳转至外链
                    startWebDetailsActivity(mContext!!, bannerModelList[position].url)
                } else if (bannerModelList[position].content.trim().isNotEmpty()) {
                    val intent = Intent(mContext, BannerDetailsActivity::class.java)
                    intent.putExtra("title", bannerModelList[position].name)
                    intent.putExtra("content", bannerModelList[position].content)
                    startActivity(intent)
                } else {
                    val intent = Intent(mContext, SuperPlayerActivity::class.java)
                    startActivity(intent)

                }

            }

        })
        //banner设置方法全部调用完毕时最后调用
        banner.start()
    }

    /**
     * 根据banner内容跳转指定新闻页面
     */
    private fun bannerSkipToNewsDetail(banner: BannerModel) {
        val bean = banner.article_info
        if (bean.url.isNotEmpty()) {
            startWebDetailsActivity(mContext!!, bean.url)
        } else {
            val type = ModuleUtils.getTypeByNewsBean(bean)
            startNewsDetailActivity(mContext!!, type, bean.id,-1)
        }
    }

    private fun startWebDetailsActivity(context: Context, url: String, title: String = "") {
        val intent = Intent(context, ServiceWebviewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("other", true)
        intent.putExtra("share_title", title)
        startActivity(intent)
    }




}