package cn.tklvyou.huaiyuanmedia.ui.home.new_list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseHttpRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.helper.GlideManager
import cn.tklvyou.huaiyuanmedia.model.*
import cn.tklvyou.huaiyuanmedia.ui.account.LoginActivity
import cn.tklvyou.huaiyuanmedia.ui.adapter.JuzhengHeaderViewholder
import cn.tklvyou.huaiyuanmedia.ui.adapter.NewsMultipleItemQuickAdapter
import cn.tklvyou.huaiyuanmedia.ui.adapter.SuixiHeaderRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.AudioController
import cn.tklvyou.huaiyuanmedia.ui.home.BannerDetailsActivity
import cn.tklvyou.huaiyuanmedia.ui.home.all_juzheng.AllJuZhengActivity
import cn.tklvyou.huaiyuanmedia.ui.home.all_tv.AllTvActivity
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.home.publish_wenzheng.PublishWenzhengActivity
import cn.tklvyou.huaiyuanmedia.ui.home.search_list.SearchListActivity
import cn.tklvyou.huaiyuanmedia.ui.home.tv_news_detail.TVNewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import cn.tklvyou.huaiyuanmedia.ui.video_player.VodActivity
import cn.tklvyou.huaiyuanmedia.utils.BannerGlideImageLoader
import cn.tklvyou.huaiyuanmedia.utils.GridDividerItemDecoration
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.widget.page_recycler.PageRecyclerView
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.fragment_news_list.*
import java.io.Serializable
import kotlin.collections.ArrayList

/**
 * @description: 展示每个频道新闻列表的fragment
 */

class NewsListFragment : BaseHttpRecyclerFragment<NewListPresenter, NewsMultipleItem<Any>, BaseViewHolder, NewsMultipleItemQuickAdapter>(), NewListContract.View {


    override fun deleteSuccess(position: Int) {
    }

    override fun initPresenter(): NewListPresenter {
        return NewListPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_news_list
    }

    private var type = -1
    private var param = ""
    private var isRefresh = false
    private var audioController: AudioController? = null

    override fun initView() {
        initSmartRefreshLayout(refreshLayout)
        initRecyclerView(recyclerView)

        type = mBundle.getInt("type", -1)
        param = mBundle.getString("param", "")
        val firstPage = mBundle.getBoolean("is_first", false)

        if (!firstPage) {
            refreshLayout.autoRefreshAnimationOnly()
        }

        when (type) {

            NewsMultipleItem.DANG_JIAN, NewsMultipleItem.ZHUAN_LAN, NewsMultipleItem.GONG_GAO, NewsMultipleItem.TUI_JIAN,
            NewsMultipleItem.NEWS, NewsMultipleItem.SHI_XUN, NewsMultipleItem.WEN_ZHENG, NewsMultipleItem.JU_ZHENG, NewsMultipleItem.ZHUAN_TI -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))
            }

            NewsMultipleItem.VIDEO, NewsMultipleItem.TV, NewsMultipleItem.ZHI_BO -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 20, resources.getColor(R.color.common_bg)))
            }


            NewsMultipleItem.WECHAT_MOMENTS -> {
            }

            NewsMultipleItem.READING -> {
                recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                recyclerView.addItemDecoration(GridDividerItemDecoration(30, resources.getColor(R.color.common_bg), true))
            }

            NewsMultipleItem.LISTEN -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 30, resources.getColor(R.color.common_bg), true))
            }

            else -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))
            }


        }

    }

    override fun getLoadingView(): View {
        return recyclerView
    }


    private var showLoading = false

    override fun onRetry() {
        super.onRetry()
        showLoading = true
        lazyData()
    }

    override fun lazyData() {
        when (type) {
            NewsMultipleItem.VIDEO, NewsMultipleItem.JU_ZHENG, NewsMultipleItem.ZHUAN_TI, NewsMultipleItem.TUI_JIAN -> {
                mPresenter.getBanner(param)
            }

            NewsMultipleItem.TV -> {
                mPresenter.getHaveSecondModuleNews(1, param, showLoading)
            }

            NewsMultipleItem.NEWS -> {
                val isShow = mBundle.getBoolean("banner", true)
                if (isShow) {
                    mPresenter.getBanner(param)
                } else {
                    mPresenter.getNewList(param, null, 1, showLoading)
                }
            }

            NewsMultipleItem.WEN_ZHENG -> {
                floatButton.visibility = View.VISIBLE
                floatButton.setOnClickListener {
                    if (SPUtils.getInstance().getString("token", "").isNotEmpty()) {
                        startActivity(Intent(context, PublishWenzhengActivity::class.java))
                        isRefresh = true
                    } else {
                        ToastUtils.showShort("请登录后操作")
                        startActivity(Intent(context, LoginActivity::class.java))
                    }

                }
                mPresenter.getNewList(param, null, 1, showLoading)
            }


            NewsMultipleItem.WECHAT_MOMENTS, NewsMultipleItem.READING, NewsMultipleItem.LISTEN, NewsMultipleItem.DANG_JIAN,
            NewsMultipleItem.GONG_GAO, NewsMultipleItem.ZHI_BO, NewsMultipleItem.SHI_XUN -> {
                mPresenter.getNewList(param, null, 1, showLoading)
            }


            NewsMultipleItem.ZHUAN_LAN -> {
                mPresenter.getJuZhengHeader(param)
            }


            else -> {
                mPresenter.getNewList(param, null, 1, showLoading)
            }

        }

    }


    public fun refreshData() {
        recyclerView.scrollToPosition(0)
        when (type) {
            NewsMultipleItem.VIDEO, NewsMultipleItem.NEWS -> {
                if (::bannerModelList.isInitialized) {
                    refreshLayout.autoRefresh()
                } else {
                    showLoading = true
                    mPresenter.getBanner(param)
                }
            }

            NewsMultipleItem.JU_ZHENG, NewsMultipleItem.ZHUAN_LAN -> {
                if (::juzhengHeaderList.isInitialized) {
                    refreshLayout.autoRefresh()
                } else {
                    showLoading = true
                    mPresenter.getJuZhengHeader(param)
                }
            }

            else -> {
                refreshLayout.autoRefresh()
            }
        }
    }

    override fun onUserVisible() {
        super.onUserVisible()
        when (type) {
            NewsMultipleItem.WEN_ZHENG -> {
                if (isRefresh) {
                    isRefresh = false
                    refreshLayout.autoRefresh()
                }
            }

        }
    }


    override fun onUserInvisible() {
        super.onUserInvisible()
        if (type == NewsMultipleItem.LISTEN) {
            audioController?.onPause()
        }
    }

    private lateinit var juzhengHeaderList: MutableList<NewsBean>
    override fun setJuZhengHeader(beans: MutableList<NewsBean>?) {
        if (beans != null) {
            refreshLayout.setEnableRefresh(true)
            this.juzhengHeaderList = beans
            when (type) {
                NewsMultipleItem.JU_ZHENG -> {
                    mPresenter.getNewList(param, null, 1, showLoading)
                }

                NewsMultipleItem.ZHUAN_LAN -> {
                    mPresenter.getNewList(param, null, 1, showLoading)
                }
            }
        } else {
            refreshLayout.setEnableRefresh(false)
            onLoadFailed(1, null)
        }
    }

    private lateinit var verticalHeaderList: MutableList<NewsBean>
    override fun setVerticalHeader(beans: MutableList<NewsBean>?) {
        if (beans != null) {
            refreshLayout.setEnableRefresh(true)
            this.verticalHeaderList = beans
            when (type) {
                NewsMultipleItem.ZHUAN_TI -> {
                    mPresenter.getNewList(param, null, 1, showLoading)
                }
            }
        } else {
            refreshLayout.setEnableRefresh(false)
            onLoadFailed(1, null)
        }

    }


    private lateinit var bannerModelList: MutableList<BannerModel>
    override fun setBanner(bannerModelList: MutableList<BannerModel>?) {
        if (bannerModelList != null) {
            refreshLayout.setEnableRefresh(true)
            this.bannerModelList = bannerModelList
            when (type) {
                NewsMultipleItem.VIDEO -> {
                    mPresenter.getNewList(param, null, 1, showLoading)
                }

                NewsMultipleItem.NEWS -> {
                    mPresenter.getNewList(param, null, 1, showLoading)
                }

                NewsMultipleItem.TUI_JIAN -> {
                    mPresenter.getNewList(param, null, 1, showLoading)
                }

                NewsMultipleItem.JU_ZHENG -> {
                    mPresenter.getJuZhengHeader(param)
                }

                NewsMultipleItem.ZHUAN_TI -> {
                    mPresenter.getVerticalHeader(param)
                }


            }
        } else {
            refreshLayout.setEnableRefresh(false)
            onLoadFailed(1, null)
        }

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
                    startWebDetailsActivity(context!!, bannerModelList[position].url)
                } else if (bannerModelList[position].content.trim().isNotEmpty()) {
                    val intent = Intent(context, BannerDetailsActivity::class.java)
                    intent.putExtra("title", bannerModelList[position].name)
                    intent.putExtra("content", bannerModelList[position].content)
                    startActivity(intent)
                }

            }

        })
        //banner设置方法全部调用完毕时最后调用
        banner.start()
    }

    private var juzhengList: MutableList<String> = ArrayList<String>()

    override fun setList(list: MutableList<NewsMultipleItem<Any>>) {
        setList(object : AdapterCallBack<NewsMultipleItemQuickAdapter> {

            override fun createAdapter(): NewsMultipleItemQuickAdapter {
                var adapter: NewsMultipleItemQuickAdapter?
                if (type == NewsMultipleItem.TV) {
                    val contentList = list.filter { (it.dataBean as HaveSecondModuleNewsModel).module_second != "置顶频道" }
                            .toList()
                    adapter = NewsMultipleItemQuickAdapter(context, contentList)
                } else {
                    adapter = NewsMultipleItemQuickAdapter(context, list)
                }

                when (type) {
                    NewsMultipleItem.VIDEO -> {
                        val view = View.inflate(context, R.layout.item_normal_banner, null)
                        initBannerView(view, bannerModelList)
                        adapter.addHeaderView(view)
                    }

                    NewsMultipleItem.TV -> {
                        val headerModelList = list.filter { (it.dataBean as HaveSecondModuleNewsModel).module_second == "置顶频道" }
                                .toList()

                        val view = View.inflate(context, R.layout.item_suixi_tv_header, null)
                        val suixiHeaderRecyclerView = view.findViewById<RecyclerView>(R.id.suixiHeaderRecyclerView)
                        suixiHeaderRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
                        suixiHeaderRecyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.HORIZONTAL, 20, Color.WHITE))


                        var dataBeans: MutableList<HaveSecondModuleNewsModel.DataBean> = ArrayList()
                        if (headerModelList.isNotEmpty()) {
                            dataBeans = (headerModelList[0].dataBean as HaveSecondModuleNewsModel).data
                        }

                        val suixiHeaderRvAdapter = SuixiHeaderRvAdapter(R.layout.item_suixi_tv_header_child, dataBeans)
                        suixiHeaderRecyclerView.adapter = suixiHeaderRvAdapter
                        suixiHeaderRvAdapter.setOnItemClickListener { adapter, view, position ->
                            val bean = (adapter as SuixiHeaderRvAdapter).data[position]
                            val id = bean.id
                            val type = if (bean.type == "tv") "电视" else "广播"
                            TVNewsDetailActivity.startTVNewsDetailActivity(context!!, type, id)
                        }
                        adapter.addHeaderView(view)
                    }

                    NewsMultipleItem.NEWS -> {
                        val view = View.inflate(context, R.layout.item_search_and_banner_header, null)
                        val isShow = mBundle.getBoolean("banner", true)
                        if (isShow) {
                            initBannerView(view, bannerModelList)
                            initSearchView(view)
                            adapter.addHeaderView(view)
                        }

                    }

                    NewsMultipleItem.JU_ZHENG -> {

                        val bannerView = View.inflate(context, R.layout.item_normal_banner, null)
                        initBannerView(bannerView, bannerModelList)

                        val view = View.inflate(context, R.layout.item_juzheng_header_layout, null)
                        initJuzhengHeaderView(view, juzhengHeaderList)

                        adapter.addHeaderView(bannerView)
                        adapter.addHeaderView(view)
                    }

                    NewsMultipleItem.ZHUAN_LAN -> {
                        val view = View.inflate(context, R.layout.item_zhuanlan_header_layout, null)
                        initZhuanLanHeaderView(view, juzhengHeaderList)
                        adapter.addHeaderView(view)
                    }

                    NewsMultipleItem.LISTEN -> {
                        audioController = AudioController(context)
                        adapter.setAudioController(audioController)
                    }


                    NewsMultipleItem.ZHUAN_TI -> {
                        val bannerView = View.inflate(context, R.layout.item_normal_banner, null)
                        initBannerView(bannerView, bannerModelList)

                        val view = View.inflate(context, R.layout.item_viewfilper_header, null)
                        initVerticalHeaderView(view, verticalHeaderList)

                        adapter.addHeaderView(bannerView)
                        adapter.addHeaderView(view)
                    }

                    NewsMultipleItem.TUI_JIAN -> {
                        val view = View.inflate(context, R.layout.item_normal_banner, null)
                        initBannerView(view, bannerModelList)
                        adapter.addHeaderView(view)
                    }

                }
                return adapter
            }

            override fun refreshAdapter() {
                when (type) {
                    NewsMultipleItem.VIDEO, NewsMultipleItem.NEWS, NewsMultipleItem.SHI_XUN, NewsMultipleItem.WEN_ZHENG,
                    NewsMultipleItem.JU_ZHENG, NewsMultipleItem.WECHAT_MOMENTS, NewsMultipleItem.READING, NewsMultipleItem.DANG_JIAN,
                    NewsMultipleItem.ZHUAN_LAN, NewsMultipleItem.GONG_GAO, NewsMultipleItem.ZHI_BO, NewsMultipleItem.TUI_JIAN, NewsMultipleItem.ZHUAN_TI -> {
                        adapter.setNewData(list)
                    }

                    NewsMultipleItem.TV -> {
                        val contentList = list.filter { (it.dataBean as HaveSecondModuleNewsModel).module_second != "置顶频道" }
                                .toList()
                        adapter.setNewData(contentList)
                        adapter.loadMoreEnd()
                    }

                    NewsMultipleItem.LISTEN -> {
                        audioController!!.onPause()
                        adapter.setNewData(list)
                    }

                    else -> {
                        adapter.setNewData(list)
                    }
                }
            }
        })

    }

    private fun initVerticalHeaderView(view: View, verticalHeaderList: MutableList<NewsBean>) {
        val vf = view.findViewById<ViewFlipper>(R.id.mViewFlipper)
        verticalHeaderList.forEach { bean ->
            val id = bean.id

            val itemView = View.inflate(context, R.layout.item_view_filper_layout, null)
            val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
            tvTitle.text = bean.name
            itemView.setOnClickListener {
                when (bean.module) {
                    "V视频" -> {
                        val type = "视频"
                        if (bean.url.isNotEmpty()) {
                            startDetailsActivity(context!!, bean.url)
                        } else {
                            startNewsDetailActivity(context!!, type, id)
                        }
                    }
                    "濉溪TV" -> {
                        if (bean.module_second == "置顶频道") {
                            val type = if (bean.type == "tv") "电视" else "广播"
                            TVNewsDetailActivity.startTVNewsDetailActivity(context!!, type, id)
                        } else {
                            val type = "电视"
                            NewsDetailActivity.startNewsDetailActivity(context!!, type, id)
                        }
                    }
                    "新闻", "矩阵", "专栏", "党建", "专题" -> {
                        val type = "文章"
                        if (bean.url.isNotEmpty()) {
                            startDetailsActivity(context!!, bean.url)
                        } else {
                            startNewsDetailActivity(context!!, type, id)
                        }
                    }
                    "视讯" -> {
                        val type = "视讯"
                        if (bean.url.isNotEmpty()) {
                            startDetailsActivity(context!!, bean.url)
                        } else {
                            startNewsDetailActivity(context!!, type, id)
                        }
                    }
                    "问政" -> {
                        val type = "问政"
                        if (bean.url.isNotEmpty()) {
                            startDetailsActivity(context!!, bean.url)
                        } else {
                            startNewsDetailActivity(context!!, type, id)
                        }
                    }

                    "原创", "随手拍" -> {
                        val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
                        if (bean.url.isNotEmpty()) {
                            startDetailsActivity(context!!, bean.url)
                        } else {
                            startNewsDetailActivity(context!!, type, id)
                        }
                    }
                    "悦读" -> {
                        val type = "悦读"
                        if (bean.url.isNotEmpty()) {
                            startDetailsActivity(context!!, bean.url)
                        } else {
                            startNewsDetailActivity(context!!, type, id)
                        }
                    }
                    "悦听" -> {
                        val type = "悦听"
                        if (bean.url.isNotEmpty()) {
                            startDetailsActivity(context!!, bean.url)
                        } else {
                            startNewsDetailActivity(context!!, type, id)
                        }
                    }
                    "公告" -> {
                        val type = "公告"
                        if (bean.url.isNotEmpty()) {
                            startDetailsActivity(context!!, bean.url)
                        } else {
                            startNewsDetailActivity(context!!, type, id)
                        }
                    }

                }
            }
            vf.addView(itemView)
        }
    }

    private fun initSearchView(view: View) {
        val searchLayout = view.findViewById<LinearLayout>(R.id.searchLayout)
        val etSearch = view.findViewById<TextView>(R.id.etSearch)
        etSearch.hint = SPUtils.getInstance().getString("search", "")
        searchLayout.setOnClickListener {
            val intent = Intent(context, SearchListActivity::class.java)
            intent.putExtra("search", etSearch.hint.toString())
            startActivity(intent)
        }
    }

    private var juzhengSecondModule = ""
    private fun initJuzhengHeaderView(view: View, juzhengHeaderList: MutableList<NewsBean>) {
        val mRecyclerView = view.findViewById<PageRecyclerView>(R.id.customSwipeView)
        // 设置指示器
        mRecyclerView.setIndicator(view.findViewById(R.id.indicator))
        // 设置行数和列数
        mRecyclerView.setPageSize(1, 4)
        mRecyclerView.adapter = mRecyclerView.PageAdapter(juzhengHeaderList, object : PageRecyclerView.CallBack {
            //记录选中的RadioButton的位置
            private var mSelectedItem = -1

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                return JuzhengHeaderViewholder(LayoutInflater.from(context).inflate(R.layout.item_juzheng_header_child_layout, parent, false))
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
                val bean = juzhengHeaderList[position]
                GlideManager.loadRoundImg(bean.avatar, (holder as JuzhengHeaderViewholder).ivAvatar)
                holder.tvNickName.text = bean.nickname
                holder.radioButton.isChecked = position == mSelectedItem
            }

            override fun onItemClickListener(view: View?, position: Int) {
                if (position != mSelectedItem) {
                    if (juzhengHeaderList[position].url.isNullOrEmpty()) {
                        mSelectedItem = position
                        juzhengSecondModule = if (juzhengHeaderList[position].nickname == "全部") "" else juzhengHeaderList[position].nickname
                        mRecyclerView.adapter!!.notifyDataSetChanged()
                        showLoading()
                        mPresenter.getNewList(param, juzhengSecondModule, 1, false)
                    } else {
                        startDetailsActivity(context!!, juzhengHeaderList[position].url)
                    }
                } else {
                    //重复点击同一个，不执行任何操作
                }

            }

            override fun onItemLongClickListener(view: View?, position: Int) {
            }


        })

    }

    private fun initZhuanLanHeaderView(view: View, juzhengHeaderList: MutableList<NewsBean>) {
        val mRecyclerView = view.findViewById<PageRecyclerView>(R.id.customSwipeView)
        // 设置指示器
        mRecyclerView.setIndicator(view.findViewById(R.id.indicator))
        // 设置行数和列数
        mRecyclerView.setPageSize(2, 4)
        mRecyclerView.adapter = mRecyclerView.PageAdapter(juzhengHeaderList, object : PageRecyclerView.CallBack {
            //记录选中的RadioButton的位置
            private var mSelectedItem = -1

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                return JuzhengHeaderViewholder(LayoutInflater.from(context).inflate(R.layout.item_juzheng_header_child_layout, parent, false))
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
                val bean = juzhengHeaderList[position]
                GlideManager.loadRoundImg(bean.avatar, (holder as JuzhengHeaderViewholder).ivAvatar)
                holder.tvNickName.text = bean.nickname
                holder.radioButton.isChecked = position == mSelectedItem
            }

            override fun onItemClickListener(view: View?, position: Int) {
                if (position != mSelectedItem) {
                    if (juzhengHeaderList[position].url.isNullOrEmpty()) {
                        mSelectedItem = position
                        juzhengSecondModule = if (juzhengHeaderList[position].nickname == "全部") "" else juzhengHeaderList[position].nickname
                        mRecyclerView.adapter!!.notifyDataSetChanged()
                        showLoading()
                        mPresenter.getNewList(param, juzhengSecondModule, 1, false)
                    } else {
                        startDetailsActivity(context!!, juzhengHeaderList[position].url)
                    }
                } else {
                    //重复点击同一个，不执行任何操作
                }

            }

            override fun onItemLongClickListener(view: View?, position: Int) {
            }


        })

    }


    /**
     * 分页加载 自动调用的方法
     *
     *
     *
     */
    override fun getListAsync(page: Int) {
        when (type) {
            NewsMultipleItem.VIDEO -> {
                mPresenter.getNewList(param, null, page, false)
            }

            NewsMultipleItem.TV -> {
                mPresenter.getHaveSecondModuleNews(page, param, false)
            }

            NewsMultipleItem.NEWS -> {
                mPresenter.getNewList(param, null, page, false)
            }

            NewsMultipleItem.SHI_XUN -> {
                mPresenter.getNewList(param, null, page, false)
            }

            NewsMultipleItem.WEN_ZHENG -> {
                mPresenter.getNewList(param, null, page, false)
            }

            NewsMultipleItem.JU_ZHENG -> {
                mPresenter.getNewList(param, juzhengSecondModule, page, false)
            }

            NewsMultipleItem.ZHUAN_LAN -> {
                mPresenter.getNewList(param, juzhengSecondModule, page, false)
            }

            NewsMultipleItem.WECHAT_MOMENTS -> {
                mPresenter.getNewList(param, null, page, false)
            }

            NewsMultipleItem.READING -> {
                mPresenter.getNewList(param, null, page, false)
            }

            NewsMultipleItem.LISTEN -> {
                mPresenter.getNewList(param, null, page, false)
            }

            NewsMultipleItem.DANG_JIAN -> {
                mPresenter.getNewList(param, null, page, false)
            }

            NewsMultipleItem.GONG_GAO -> {
                mPresenter.getNewList(param, null, page, false)
            }

            NewsMultipleItem.ZHI_BO -> {
                mPresenter.getNewList(param, null, page, false)
            }

            NewsMultipleItem.TUI_JIAN -> {
                mPresenter.getNewList(param, null, page, false)
            }

            else -> {
                mPresenter.getNewList(param, null, page, false)
            }

        }
    }

    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {
        showLoading = false
        if (model != null) {
            val newList = ArrayList<NewsMultipleItem<Any>>()

            model.data.forEach {
                newList.add(NewsMultipleItem(param, it))
            }

            onLoadSucceed(p, newList)
        } else {
            onLoadFailed(p, null)
        }
    }

    override fun setHaveSecondModuleNews(p: Int, datas: MutableList<HaveSecondModuleNewsModel>?) {
        showLoading = false
        if (datas != null) {
            val newList = ArrayList<NewsMultipleItem<Any>>()

            datas.forEach {
                newList.add(NewsMultipleItem(param, it))
            }
            onLoadSucceed(p, newList)
        } else {
            onLoadFailed(p, null)
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        when (type) {
            NewsMultipleItem.VIDEO -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = "视频"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
            }

            NewsMultipleItem.NEWS -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = "文章"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
            }

            NewsMultipleItem.WECHAT_MOMENTS -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
            }

            NewsMultipleItem.SHI_XUN -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = "视讯"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
            }

            NewsMultipleItem.WEN_ZHENG -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = "问政"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
            }

            NewsMultipleItem.READING -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = "悦读"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
            }

            NewsMultipleItem.LISTEN -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = "悦听"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
                audioController?.onPause()
            }

            NewsMultipleItem.DANG_JIAN -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = "文章"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
            }

            NewsMultipleItem.GONG_GAO -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = "公告"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
            }

            NewsMultipleItem.JU_ZHENG -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = "文章"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
            }

            NewsMultipleItem.ZHUAN_LAN -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = "文章"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
            }

            NewsMultipleItem.ZHI_BO -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = "直播"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
            }

            NewsMultipleItem.ZHUAN_TI -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = "文章"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
            }

            NewsMultipleItem.TUI_JIAN -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                var type = ""

                when (bean.module) {
                    "原创" -> {
                        type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
                    }

                    "矩阵" -> {
                        type = "文章"
                    }

                    "专题" -> {
                        type = "文章"
                    }

                    "视讯" -> {
                        type = "视讯"
                    }

                    else -> {
                        type = "文章"
                    }
                }

                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }

            }

            else -> {
                val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                val id = bean.id
                val type = "文章"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    startNewsDetailActivity(context!!, type, id, position)
                }
            }

        }


    }

    private fun startNewsDetailActivity(context: Context, type: String, id: Int, position: Int = -1) {
        val intent = Intent(context, NewsDetailActivity::class.java)
        intent.putExtra(NewsDetailActivity.INTENT_ID, id)
        intent.putExtra(NewsDetailActivity.INTENT_TYPE, type)
        if (position == -1) {
            startActivity(intent)
        } else {
            intent.putExtra(NewsDetailActivity.POSITION, position)
            startActivityForResult(intent, 0)
        }

    }

    private fun startDetailsActivity(context: Context, url: String) {
        val intent = Intent(context, ServiceWebviewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("other", true)
        intent.putExtra("share_title", "")
        startActivity(intent)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        if (view != null) {
            when (view.id) {

                //V视频 直播 播放按钮
                R.id.ivStartPlayer -> {

                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(context!!, bean.url)
                    } else {
                        //打开新的Activity
                        val intent = Intent(context, VodActivity::class.java)
                        intent.putExtra("videoPath", bean.video)
                        startActivity(intent)
                    }

                }

                //濉溪TV 第一个 播放按钮
                R.id.ivSuiXiTVFirstStartPlayer -> {
                    val intent = Intent(context, VodActivity::class.java)
                    intent.putExtra("videoPath", ((adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as HaveSecondModuleNewsModel).data[0].video)
                    startActivity(intent)
                }

                //濉溪TV 第二个 播放按钮
                R.id.ivSuiXiTVSecondStartPlayer -> {
                    val intent = Intent(context, VodActivity::class.java)
                    intent.putExtra("videoPath", ((adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as HaveSecondModuleNewsModel).data[1].video)
                    startActivity(intent)
                }

                //濉溪TV 第一个视频布局
                R.id.llSuixiTvFirst -> {
                    val bean = ((adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as HaveSecondModuleNewsModel).data[0]
                    val id = bean.id
                    val type = "电视"
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(context!!, bean.url)
                    } else {
                        startNewsDetailActivity(context!!, type, id, position)
                    }
                }

                //濉溪TV 第二个视频布局
                R.id.llSuixiTvSecond -> {
                    val bean = ((adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as HaveSecondModuleNewsModel).data[1]
                    val id = bean.id
                    val type = "电视"
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(context!!, bean.url)
                    } else {
                        startNewsDetailActivity(context!!, type, id, position)
                    }
                }

                //濉溪Tv 全部
                R.id.tvSuixiTvAll -> {
                    val intent = Intent(context!!, AllTvActivity::class.java)
                    intent.putExtra("position", position)
                    startActivity(intent)
                }

                //矩阵 全部
                R.id.tvJuzhengModuleSecond -> {
                    val intent = Intent(context!!, AllJuZhengActivity::class.java)
                    intent.putExtra("position", position)
                    intent.putExtra("list", juzhengList as Serializable)
                    startActivity(intent)
                }

                R.id.play -> {
                    mPresenter.getDetailsById(((adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean).id)
                }

                R.id.sparkButton, R.id.tvGoodNum, R.id.sparkButton1, R.id.tvGoodNum1 -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    if (bean.like_status == 1) {
                        mPresenter.cancelLikeNews(bean.id, position)
                    } else {
                        mPresenter.addLikeNews(bean.id, position)
                    }

                }

                else -> {
                }
            }
        }
    }


    override fun updateLikeStatus(isLike: Boolean, position: Int) {
        if (isLike) {
            (adapter.data[position].dataBean as NewsBean).like_status = 1
            (adapter.data[position].dataBean as NewsBean).like_num = (adapter.data[position].dataBean as NewsBean).like_num + 1
        } else {
            (adapter.data[position].dataBean as NewsBean).like_status = 0
            (adapter.data[position].dataBean as NewsBean).like_num = (adapter.data[position].dataBean as NewsBean).like_num - 1
        }

        when (type) {
            NewsMultipleItem.JU_ZHENG, NewsMultipleItem.ZHUAN_TI, NewsMultipleItem.TUI_JIAN -> {
                adapter.notifyItemChangedAnimal(position + 1)
            }
            else -> {
                adapter.notifyItemChangedAnimal(position)
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

            when (type) {
                NewsMultipleItem.VIDEO -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position + 1)
                }


                NewsMultipleItem.NEWS -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position + 1)
                }

                NewsMultipleItem.WECHAT_MOMENTS -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position)
                }

                NewsMultipleItem.SHI_XUN -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position)
                }

                NewsMultipleItem.WEN_ZHENG -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position)
                }

                NewsMultipleItem.READING -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position)
                }

                NewsMultipleItem.LISTEN -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position)
                }

                NewsMultipleItem.DANG_JIAN -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position)
                }

                NewsMultipleItem.GONG_GAO -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position)
                }

                NewsMultipleItem.JU_ZHENG -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position + 1)
                }

                NewsMultipleItem.ZHUAN_LAN -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position + 1)
                }

                NewsMultipleItem.ZHI_BO -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position)
                }

                NewsMultipleItem.TUI_JIAN -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position + 1)
                }

                NewsMultipleItem.ZHUAN_TI -> {
                    val bean = (adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position + 1)
                }

            }

        }
    }


    /**
     * 根据banner内容跳转指定新闻页面
     */
    private fun bannerSkipToNewsDetail(banner: BannerModel) {
        val bean = banner.article_info
        if (bean.url.isNotEmpty()) {
            startWebDetailsActivity(context!!, bean.url)
        } else {
            when (bean.module) {

                "V视频" -> {
                    val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
                    startNewsDetailActivity(context!!, type, bean.id)
                }

                "濉溪TV" -> {
                    if (bean.module_second == "置顶频道") {
                        val type = if (bean.type == "tv") "电视" else "广播"
                        TVNewsDetailActivity.startTVNewsDetailActivity(context!!, type, bean.id)
                    } else {
                        val type = "电视"
                        NewsDetailActivity.startNewsDetailActivity(context!!, type, bean.id)
                    }
                }
                "新闻", "矩阵", "新闻网", "专栏", "党建", "专题" -> {
                    val type = "文章"
                    startNewsDetailActivity(context!!, type, bean.id)
                }
                "视讯" -> {
                    val type = "视讯"
                    startNewsDetailActivity(context!!, type, bean.id)
                }
                "问政" -> {
                    val type = "问政"
                    startNewsDetailActivity(context!!, type, bean.id)
                }

                "原创", "随手拍" -> {
                    val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
                    startNewsDetailActivity(context!!, type, bean.id)
                }
                "悦读" -> {
                    val type = "悦读"
                    startNewsDetailActivity(context!!, type, bean.id)
                }
                "悦听" -> {
                    val type = "悦听"
                    startNewsDetailActivity(context!!, type, bean.id)
                }
                "公告" -> {
                    val type = "公告"
                    startNewsDetailActivity(context!!, type, bean.id)
                }
                "直播" -> {
                    val type = "直播"
                    startNewsDetailActivity(context!!, type, bean.id)
                }

            }

        }


    }

    private fun startWebDetailsActivity(context: Context, url: String, title: String = "") {
        val intent = Intent(context, ServiceWebviewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("other", true)
        intent.putExtra("share_title", title)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioController?.release()
    }

}