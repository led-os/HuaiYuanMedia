package cn.tklvyou.huaiyuanmedia.ui.home.new_list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import cn.tklvyou.huaiyuanmedia.ui.service.ServiceWebviewActivity
import cn.tklvyou.huaiyuanmedia.ui.video_player.VodActivity
import cn.tklvyou.huaiyuanmedia.utils.BannerGlideImageLoader
import cn.tklvyou.huaiyuanmedia.utils.GridDividerItemDecoration
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.widget.page_recycler.PageRecyclerView
import com.blankj.utilcode.util.SPUtils
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
    private var isRefresh = false
    private var audioController: AudioController? = null

    override fun initView() {
        initSmartRefreshLayout(refreshLayout)
        initRecyclerView(recyclerView)

        type = mBundle.getInt("type", -1)
        if (type != NewsMultipleItem.VIDEO) {
            refreshLayout.autoRefreshAnimationOnly()
        }

        when (type) {
            NewsMultipleItem.VIDEO -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 20, resources.getColor(R.color.common_bg)))
            }

            NewsMultipleItem.TV -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 20, resources.getColor(R.color.common_bg)))
            }

            NewsMultipleItem.NEWS -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))
            }

            NewsMultipleItem.SHI_XUN -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))
            }

            NewsMultipleItem.WEN_ZHENG -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))
            }

            NewsMultipleItem.JU_ZHENG -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))
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

            NewsMultipleItem.DANG_JIAN -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))
            }

            NewsMultipleItem.ZHUAN_LAN -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))
            }

            NewsMultipleItem.GONG_GAO -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))
            }

            NewsMultipleItem.ZHI_BO -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 20, resources.getColor(R.color.common_bg)))
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
            NewsMultipleItem.VIDEO -> {
                mPresenter.getBanner("V视频")
            }

            NewsMultipleItem.TV -> {
                mPresenter.getHaveSecondModuleNews(1, "濉溪TV", showLoading)
            }

            NewsMultipleItem.NEWS -> {
                mPresenter.getBanner("新闻")
            }

            NewsMultipleItem.SHI_XUN -> {
                mPresenter.getNewList("视讯", null, 1, showLoading)
            }

            NewsMultipleItem.WEN_ZHENG -> {
                floatButton.visibility = View.VISIBLE
                floatButton.setOnClickListener {
                    startActivity(Intent(context, PublishWenzhengActivity::class.java))
                    isRefresh = true
                }
                mPresenter.getNewList("问政", null, 1, showLoading)
            }

            NewsMultipleItem.JU_ZHENG -> {
                mPresenter.getJuZhengHeader("矩阵")
            }

            NewsMultipleItem.WECHAT_MOMENTS -> {
                mPresenter.getNewList("原创", null, 1, showLoading)
            }

            NewsMultipleItem.READING -> {
                mPresenter.getNewList("悦读", null, 1, showLoading)
            }

            NewsMultipleItem.LISTEN -> {
                mPresenter.getNewList("悦听", null, 1, showLoading)
            }

            NewsMultipleItem.DANG_JIAN -> {
                mPresenter.getNewList("党建", null, 1, showLoading)
            }

            NewsMultipleItem.ZHUAN_LAN -> {
                mPresenter.getJuZhengHeader("专栏")
            }

            NewsMultipleItem.GONG_GAO -> {
                mPresenter.getNewList("公告", null, 1, showLoading)
            }

            NewsMultipleItem.ZHI_BO -> {
                mPresenter.getNewList("直播", null, 1, showLoading)
            }

        }

    }


    public fun refreshData() {
        recyclerView.scrollToPosition(0)
        refreshLayout.autoRefresh()
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

    private lateinit var juzhengHeaderList: MutableList<NewsBean>
    override fun setJuZhengHeader(beans: MutableList<NewsBean>?) {
        if (beans != null) {
            refreshLayout.setEnableRefresh(true)
            this.juzhengHeaderList = beans
            when (type) {
                NewsMultipleItem.JU_ZHENG -> {
                    mPresenter.getNewList("矩阵", null, 1, showLoading)
                }

                NewsMultipleItem.ZHUAN_LAN -> {
                    mPresenter.getNewList("专栏", null, 1, showLoading)
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
                    mPresenter.getNewList("V视频", null, 1, showLoading)
                }

                NewsMultipleItem.NEWS -> {
                    mPresenter.getNewList("新闻", null, 1, showLoading)
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
                if (bannerModelList[position].content.trim().isNotEmpty()) {
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
                if(type == NewsMultipleItem.TV){
                     val contentList = list.filter { (it.dataBean as HaveSecondModuleNewsModel).module_second != "置顶频道" }
                            .toList()
                    adapter = NewsMultipleItemQuickAdapter(context, contentList)
                }else{
                    adapter = NewsMultipleItemQuickAdapter(context, list)
                }
                when (type) {
                    NewsMultipleItem.VIDEO -> {
                        val view = View.inflate(context, R.layout.item_normal_banner, null)
                        initBannerView(view, bannerModelList)
                        adapter.addHeaderView(view)
                    }

                    NewsMultipleItem.TV ->{
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
                        initBannerView(view, bannerModelList)
                        initSearchView(view)
                        adapter.addHeaderView(view)
                    }

                    NewsMultipleItem.JU_ZHENG -> {
                        val view = View.inflate(context, R.layout.item_juzheng_header_layout, null)
                        initJuzhengHeaderView(view, juzhengHeaderList)
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

                }
                return adapter
            }

            override fun refreshAdapter() {
                when (type) {
                    NewsMultipleItem.VIDEO -> {
                        adapter.setNewData(list)
                    }

                    NewsMultipleItem.TV -> {
                        val contentList = list.filter { (it.dataBean as HaveSecondModuleNewsModel).module_second != "置顶频道" }
                                .toList()
                        adapter.setNewData(contentList)
                        adapter.loadMoreEnd()
                    }

                    NewsMultipleItem.NEWS -> {
                        adapter.setNewData(list)
                    }

                    NewsMultipleItem.SHI_XUN -> {
                        adapter.setNewData(list)
                    }

                    NewsMultipleItem.WEN_ZHENG -> {
                        adapter.setNewData(list)
                    }

                    NewsMultipleItem.JU_ZHENG -> {
                        adapter.setNewData(list)
                    }


                    NewsMultipleItem.WECHAT_MOMENTS -> {
                        adapter.setNewData(list)
                    }

                    NewsMultipleItem.READING -> {
                        adapter.setNewData(list)
                    }

                    NewsMultipleItem.LISTEN -> {
                        audioController!!.onPause()
                        adapter.setNewData(list)
                    }

                    NewsMultipleItem.DANG_JIAN -> {
                        adapter.setNewData(list)
                    }

                    NewsMultipleItem.ZHUAN_LAN -> {
                        adapter.setNewData(list)
                    }

                    NewsMultipleItem.GONG_GAO -> {
                        adapter.setNewData(list)
                    }

                    NewsMultipleItem.ZHI_BO -> {
                        adapter.setNewData(list)
                    }

                }

            }
        })

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

//        val btnClear = view.findViewById<ImageView>(R.id.btnClear)

//        btnClear.setOnClickListener {
//            etSearch.setText("")
//        }
//
//        etSearch.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                val intent = Intent(context, SearchListActivity::class.java)
//                if (etSearch.text.toString().isEmpty()) {
//                    intent.putExtra("search", etSearch.hint.toString())
//                } else {
//                    intent.putExtra("search", etSearch.text.toString())
//                }
//                startActivity(intent)
//            }
//            return@setOnEditorActionListener true
//        }
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
                        mPresenter.getNewList("矩阵", juzhengSecondModule, 1, false)
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
                        mPresenter.getNewList("专栏", juzhengSecondModule, 1, false)
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
     */
    override fun getListAsync(page: Int) {

        when (type) {
            NewsMultipleItem.VIDEO -> {
                mPresenter.getNewList("V视频", null, page, false)
            }

            NewsMultipleItem.TV -> {
                mPresenter.getHaveSecondModuleNews(page, "濉溪TV", false)
            }

            NewsMultipleItem.NEWS -> {
                mPresenter.getNewList("新闻", null, page, false)
            }

            NewsMultipleItem.SHI_XUN -> {
                mPresenter.getNewList("视讯", null, page, false)
            }

            NewsMultipleItem.WEN_ZHENG -> {
                mPresenter.getNewList("问政", null, page, false)
            }

            NewsMultipleItem.JU_ZHENG -> {
                mPresenter.getNewList("矩阵", juzhengSecondModule, page, false)
            }

            NewsMultipleItem.ZHUAN_LAN -> {
                mPresenter.getNewList("专栏", juzhengSecondModule, page, false)
            }

            NewsMultipleItem.WECHAT_MOMENTS -> {
                mPresenter.getNewList("原创", null, page, false)
            }

            NewsMultipleItem.READING -> {
                mPresenter.getNewList("悦读", null, page, false)
            }

            NewsMultipleItem.LISTEN -> {
                mPresenter.getNewList("悦听", null, page, false)
            }

            NewsMultipleItem.DANG_JIAN -> {
                mPresenter.getNewList("党建", null, page, false)
            }

            NewsMultipleItem.GONG_GAO -> {
                mPresenter.getNewList("公告", null, page, false)
            }

            NewsMultipleItem.ZHI_BO -> {
                mPresenter.getNewList("直播", null, page, false)
            }

        }
    }

    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {
        showLoading = false
        if (model != null) {
            val newList = ArrayList<NewsMultipleItem<Any>>()
            var modelStr = ""
            when (type) {
                NewsMultipleItem.VIDEO -> {
                    modelStr = "V视频"
                }

                NewsMultipleItem.NEWS -> {
                    modelStr = "新闻"
                }

                NewsMultipleItem.SHI_XUN -> {
                    modelStr = "视讯"
                }

                NewsMultipleItem.WEN_ZHENG -> {
                    modelStr = "问政"
                }

                NewsMultipleItem.JU_ZHENG -> {
                    modelStr = "矩阵"
                }

                NewsMultipleItem.WECHAT_MOMENTS -> {
                    modelStr = "原创"
                }

                NewsMultipleItem.READING -> {
                    modelStr = "悦读"
                }

                NewsMultipleItem.LISTEN -> {
                    modelStr = "悦听"
                }

                NewsMultipleItem.DANG_JIAN -> {
                    modelStr = "党建"
                }

                NewsMultipleItem.ZHUAN_LAN -> {
                    modelStr = "专栏"
                }

                NewsMultipleItem.GONG_GAO -> {
                    modelStr = "公告"
                }

                NewsMultipleItem.ZHI_BO -> {
                    modelStr = "直播"
                }

            }

            model.data.forEach {
                newList.add(NewsMultipleItem(modelStr, it))
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
            var modelStr = ""
            when (type) {
                NewsMultipleItem.TV -> {
                    modelStr = "濉溪TV"
                }
            }
            datas.forEach {
                newList.add(NewsMultipleItem(modelStr, it))
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

        }


    }

    private fun startNewsDetailActivity(context: Context, type: String, id: Int, position: Int) {
        val intent = Intent(context, NewsDetailActivity::class.java)
        intent.putExtra(NewsDetailActivity.INTENT_ID, id)
        intent.putExtra(NewsDetailActivity.INTENT_TYPE, type)
        intent.putExtra(NewsDetailActivity.POSITION, position)
        startActivityForResult(intent, 0)
    }

    private fun startDetailsActivity(context: Context, url: String) {
        val intent = Intent(context, ServiceWebviewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("other", true)
        startActivity(intent)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        if (view != null) {
            when (view.id) {

                //V视频 直播 播放按钮
                R.id.ivStartPlayer -> {
                    //打开新的Activity
                    val intent = Intent(context, VodActivity::class.java)
                    intent.putExtra("videoPath", ((adapter as NewsMultipleItemQuickAdapter).data[position].dataBean as NewsBean).video)
                    startActivity(intent)
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
                else -> {
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

            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        audioController?.release()
    }

}