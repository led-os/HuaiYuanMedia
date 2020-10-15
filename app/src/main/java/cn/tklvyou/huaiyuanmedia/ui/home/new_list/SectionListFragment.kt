package cn.tklvyou.huaiyuanmedia.ui.home.new_list


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseHttpRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.common.ModuleUtils
import cn.tklvyou.huaiyuanmedia.helper.GlideManager
import cn.tklvyou.huaiyuanmedia.model.*
import cn.tklvyou.huaiyuanmedia.ui.account.LoginActivity
import cn.tklvyou.huaiyuanmedia.ui.adapter.JuzhengHeaderViewholder
import cn.tklvyou.huaiyuanmedia.ui.adapter.SectionMultipleItemAdapter
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import cn.tklvyou.huaiyuanmedia.ui.home.AudioController
import cn.tklvyou.huaiyuanmedia.ui.home.BannerDetailsActivity
import cn.tklvyou.huaiyuanmedia.ui.home.all_juzheng.AllJuZhengActivity
import cn.tklvyou.huaiyuanmedia.ui.home.juzheng_details.JuZhengDetailsActivity
import cn.tklvyou.huaiyuanmedia.ui.home.new_list.NewsTypeConstant.NEWS_TYPE_SHI_XUN
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan.PingXuanDetailsActivity
import cn.tklvyou.huaiyuanmedia.ui.home.publish_wenzheng.PublishWenzhengActivity
import cn.tklvyou.huaiyuanmedia.utils.BannerGlideImageLoader
import cn.tklvyou.huaiyuanmedia.utils.GridDividerItemDecoration
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.utils.dkplayer.util.Tag
import cn.tklvyou.huaiyuanmedia.utils.dkplayer.util.Utils
import cn.tklvyou.huaiyuanmedia.widget.page_recycler.PageRecyclerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dueeeke.videocontroller.StandardVideoController
import com.dueeeke.videocontroller.component.*
import com.dueeeke.videoplayer.ijk.IjkPlayer
import com.dueeeke.videoplayer.player.VideoView
import com.dueeeke.videoplayer.player.VideoView.SimpleOnStateChangeListener
import com.dueeeke.videoplayer.player.VideoViewManager
import com.google.gson.Gson
import com.tencent.liteav.demo.player.activity.SuperPlayerActivity
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.fragment_news_list.*
import java.io.Serializable

/**
 * 分组布局类型的列表
 * 例：专题，矩阵
 */
class SectionListFragment : BaseHttpRecyclerFragment<NewListPresenter, SectionNewsMultipleItem<Any>, BaseViewHolder, SectionMultipleItemAdapter>(), NewListContract.View {
    private var mController: StandardVideoController? = null
    private var mPlayerContainer: FrameLayout? = null
    private var mIvVideoBg: ImageView? = null
    private var ivStartPlayer: ImageView? = null

    private var mVideoView: VideoView<IjkPlayer>? = null
    private var mErrorView: ErrorView? = null
    private var mCompleteView: CompleteView? = null
    private var mTitleView: TitleView? = null

    /**
     * 当前播放的位置
     */
    private var mCurPos = -1

    /**
     * 上次播放的位置，用于页面切回来之后恢复播放
     */
    private var mLastPos = mCurPos
    override fun deleteSuccess(position: Int) {
    }

    override fun initPresenter(): NewListPresenter {
        return NewListPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_news_list
    }

    override fun getLoadingView(): View {
        return recyclerView
    }

    private var showLoading = false

    private var param = ""
    private var isRefresh = false
    private var audioController: AudioController? = null

    override fun initView() {
        param = mBundle.getString("param", "")
        initSmartRefreshLayout(refreshLayout)
        initRecyclerView(recyclerView)
        if (NEWS_TYPE_SHI_XUN == param) {
            initVideoView()
            recyclerView.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
                override fun onChildViewAttachedToWindow(view: View) {}
                override fun onChildViewDetachedFromWindow(view: View) {
                    mPlayerContainer = view.findViewById(R.id.player_container)
                    if (mPlayerContainer != null) {
                        val v = mPlayerContainer!!.getChildAt(0)
                        if (v != null && v === mVideoView && !mVideoView!!.isFullScreen) {
                            releaseVideoView()
                            mPlayerContainer?.visibility = View.GONE
                            mIvVideoBg?.visibility = View.VISIBLE
                        }
                    }

                }
            })
        }


        val firstPage = mBundle.getBoolean("is_first", false)

        if (!firstPage) {
            refreshLayout.autoRefreshAnimationOnly()
        }

        when (param) {
            "推荐", "视讯", "爆料", "矩阵", "专题" -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))
            }

            "直播" -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 20, resources.getColor(R.color.common_bg)))
            }

            "悦读" -> {
                recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                recyclerView.addItemDecoration(GridDividerItemDecoration(30, resources.getColor(R.color.common_bg), true))
            }

            "悦听", "评选" -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 30, resources.getColor(R.color.common_bg), true))
            }

            else -> {
                recyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))
            }
        }

    }

    override fun onRetry() {
        super.onRetry()
        showLoading = true
        lazyData()
    }

    override fun lazyData() {
        when (param) {
            "评选" -> {
                mPresenter.getPingXuanList(1, showLoading)
            }

            "矩阵", "专题", "推荐" -> {
                mPresenter.getBanner(param)
            }

            "爆料" -> {
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

            "生活圈", "悦读", "悦听", "直播", "视讯" -> {
                mPresenter.getNewList(param, null, 1, showLoading)
            }

            else -> {
                mPresenter.getNewList(param, null, 1, showLoading)
            }

        }

    }

    public fun refreshData() {
        recyclerView.scrollToPosition(0)
        when (param) {
            "推荐" -> {
                if (::bannerModelList.isInitialized) {
                    refreshLayout.autoRefresh()
                } else {
                    showLoading = true
                    mPresenter.getBanner(param)
                }
            }

            "矩阵", "专题" -> {
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
        when (param) {
            "爆料" -> {
                if (isRefresh) {
                    isRefresh = false
                    refreshLayout.autoRefresh()
                }
            }
            NEWS_TYPE_SHI_XUN ->{
                //恢复上次播放的位置
                startPlay(mLastPos)
            }
        }
    }

    override fun onUserInvisible() {
        super.onUserInvisible()
        closeAudioController()
    }


    public fun closeAudioController() {
        if (param == "悦听") {
            audioController?.onPause()
        }
    }

    private lateinit var juzhengHeaderList: MutableList<HaveSecondModuleNewsModel.ModuleSecondBean>
    override fun setJuZhengHeader(beans: MutableList<HaveSecondModuleNewsModel.ModuleSecondBean>?) {
        if (beans != null) {
            refreshLayout.setEnableRefresh(true)
            this.juzhengHeaderList = beans
            when (param) {
                "矩阵", "专题" -> {
                    mPresenter.getHaveSecondModuleNews(1, param, showLoading)
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
            when (param) {
                "专题" -> {
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
            when (param) {
                "推荐" -> {
                    mPresenter.getNewList(param, null, 1, showLoading)
                }

                "矩阵", "专题" -> {
                    mPresenter.getJuZhengHeader(param)
                }

//                "专题" -> {
//                    mPresenter.getVerticalHeader(param)
//                }
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
                } else {
                    val intent = Intent(context, SuperPlayerActivity::class.java)
                    startActivity(intent)

                }

            }

        })
        //banner设置方法全部调用完毕时最后调用
        banner.start()
    }

    private var juzhengList: MutableList<String> = ArrayList<String>()

    override fun setList(list: MutableList<SectionNewsMultipleItem<Any>>) {
        setList(object : AdapterCallBack<SectionMultipleItemAdapter> {

            override fun createAdapter(): SectionMultipleItemAdapter {
                val adapter = SectionMultipleItemAdapter(list)

                when (param) {
                    "矩阵", "专题" -> {
                        val bannerView = View.inflate(context, R.layout.item_normal_banner, null)
                        initBannerView(bannerView, bannerModelList)

                        val view = View.inflate(context, R.layout.item_juzheng_header_layout, null)
                        initJuzhengHeaderView(view, juzhengHeaderList)

                        adapter.addHeaderView(bannerView)
                        adapter.addHeaderView(view)
                        adapter.loadMoreEnd()
                    }

                    "悦听" -> {
                        audioController = AudioController(context)
                        adapter.setAudioController(audioController)
                    }

//                    "专题" -> {
//                        val bannerView = View.inflate(context, R.layout.item_normal_banner, null)
//                        initBannerView(bannerView, bannerModelList)
//
//                        val view = View.inflate(context, R.layout.item_viewfilper_header, null)
//                        initVerticalHeaderView(view, verticalHeaderList)
//
//                        adapter.addHeaderView(bannerView)
//                        adapter.addHeaderView(view)
//                    }

                    "推荐" -> {
                        val view = View.inflate(context, R.layout.item_normal_banner, null)
                        initBannerView(view, bannerModelList)
                        adapter.addHeaderView(view)
                    }

                }
                return adapter
            }

            override fun refreshAdapter() {
                when (param) {
                    "视讯", "爆料", "生活圈", "悦读", "直播", "推荐" -> {
                        adapter.setNewData(list)
                    }

                    "矩阵", "专题" -> {
                        adapter.setNewData(list)
                        adapter.loadMoreEnd()
                    }


                    "悦听" -> {
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

    /*   private fun initVerticalHeaderView(view: View, verticalHeaderList: MutableList<NewsBean>) {
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
                           val type = if (bean.video.isEmpty()) "文章" else "视讯"
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
                       "爆料" -> {
                           val type = "爆料"
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
       }*/

    /*private fun initSearchView(view: View) {
        val searchLayout = view.findViewById<LinearLayout>(R.id.searchLayout)
        val etSearch = view.findViewById<TextView>(R.id.etSearch)
        etSearch.hint = SPUtils.getInstance().getString("search", "")
        searchLayout.setOnClickListener {
            val intent = Intent(context, SearchListActivity::class.java)
            intent.putExtra("search", etSearch.hint.toString())
            startActivity(intent)
        }
    }*/

    private var juzhengSecondModule = ""
    private fun initJuzhengHeaderView(view: View, juzhengHeaderList: MutableList<HaveSecondModuleNewsModel.ModuleSecondBean>) {
        val mRecyclerView = view.findViewById<PageRecyclerView>(R.id.customSwipeView)
        // 设置指示器
        mRecyclerView.setIndicator(view.findViewById(R.id.indicator))
        // 设置行数和列数
        mRecyclerView.setPageSize(1, 4)
        mRecyclerView.adapter = mRecyclerView.PageAdapter(juzhengHeaderList, object : PageRecyclerView.CallBack {
            //记录选中的RadioButton的位置
//            private var mSelectedItem = -1

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                return JuzhengHeaderViewholder(LayoutInflater.from(context).inflate(R.layout.item_juzheng_header_child_layout, parent, false), true)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
                val bean = juzhengHeaderList[position]
                GlideManager.loadCircleImg(bean.avatar, (holder as JuzhengHeaderViewholder).ivAvatar)
                holder.tvNickName.text = bean.nickname
//                holder.radioButton.isChecked = position == mSelectedItem
            }

            override fun onItemClickListener(view: View?, position: Int) {
                val bean = juzhengHeaderList[position]
                bean.pname = param
//                val secondModuleNewsModel = HaveSecondModuleNewsModel.ModuleSecondBean()
//                secondModuleNewsModel.pname = param
//                secondModuleNewsModel.nickname = bean.nickname
//                secondModuleNewsModel.avatar = bean.avatar
//                secondModuleNewsModel.image = bean.image
//                secondModuleNewsModel.detail = bean.detail

                val intent = Intent(mActivity, JuZhengDetailsActivity::class.java)
                intent.putExtra("model", Gson().toJson(bean))
                startActivity(intent)


//                if (position != mSelectedItem) {
//                    if (juzhengHeaderList[position].url.isNullOrEmpty()) {
//                        mSelectedItem = position
//                        juzhengSecondModule = if (juzhengHeaderList[position].nickname == "全部") "" else juzhengHeaderList[position].nickname
//                        mRecyclerView.adapter!!.notifyDataSetChanged()
//                        showLoading()
//                        mPresenter.getNewList(param, juzhengSecondModule, 1, false)
//                    } else {
//                        startDetailsActivity(context!!, juzhengHeaderList[position].url)
//                    }
//                } else {
//                    //重复点击同一个，不执行任何操作
//                }

            }

            override fun onItemLongClickListener(view: View?, position: Int) {
            }


        })

    }

    /* private fun initZhuanLanHeaderView(view: View, juzhengHeaderList: MutableList<NewsBean>) {
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
                 GlideManager.loadCircleImg(bean.avatar, (holder as JuzhengHeaderViewholder).ivAvatar)
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

     }*/


    /**
     * 分页加载 自动调用的方法
     *
     *
     *
     */
    override fun getListAsync(page: Int) {
        when (param) {
            "评选" -> {
                mPresenter.getPingXuanList(page, false)
            }

            "视讯", "爆料", "生活圈", "悦读", "悦听", "直播", "推荐" -> {
                mPresenter.getNewList(param, null, page, false)
            }

            "矩阵", "专题" -> {
                mPresenter.getHaveSecondModuleNews(page, param, false)
            }

            else -> {
                mPresenter.getNewList(param, null, page, false)
            }

        }
    }

    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {
        showLoading = false
        if (model != null) {
            val newList = ArrayList<SectionNewsMultipleItem<Any>>()

            model.data.forEach {
                newList.add(SectionNewsMultipleItem(param, it))
            }

            onLoadSucceed(p, newList)
        } else {
            onLoadFailed(p, null)
        }
    }

    override fun setPingXuanList(p: Int, model: BasePageModel<PingXuanModel>?) {
        showLoading = false
        if (model != null) {
            val newList = ArrayList<SectionNewsMultipleItem<Any>>()

            model.data.forEach {
                newList.add(SectionNewsMultipleItem("评选", it))
            }

            onLoadSucceed(p, newList)
        } else {
            onLoadFailed(p, null)
        }
    }

    override fun setHaveSecondModuleNews(p: Int, datas: MutableList<HaveSecondModuleNewsModel>?) {
        showLoading = false
        if (datas != null) {
            val newList = ArrayList<SectionNewsMultipleItem<Any>>()

            datas.forEach {
                it.module_second.pname = param
                val customItem = SectionNewsMultipleItem<Any>(true, Gson().toJson(it.module_second))
                newList.add(customItem)

                it.data.forEach { child ->
                    newList.add(SectionNewsMultipleItem(param, child))
                }
            }

            onLoadSucceed(p, newList)
        } else {
            onLoadFailed(p, null)
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)

        if (adapter == null || adapter !is SectionMultipleItemAdapter) {
            return
        }

        val isHeader = adapter.data[position].isHeader
        if (isHeader) {
            val intent = Intent(mActivity, JuZhengDetailsActivity::class.java)
            intent.putExtra("model", adapter.data[position].header)
            startActivity(intent)
            return
        }

        when (param) {
            "评选" -> {
                val bean = adapter.data[position].dataBean as PingXuanModel
                val id = bean.id
                val intent = Intent(context, PingXuanDetailsActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            }

            else -> {
                val bean = adapter.data[position].dataBean as NewsBean

                val id = bean.id
                val type: String

                when (param) {
                    "生活圈" -> {
                        type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
                    }

                    "视讯", "爆料", "悦读", "直播" -> {
                        type = param
                    }

                    "悦听" -> {
                        type = param
                        audioController?.onPause()
                    }

                    "矩阵", "专题", "推荐" -> {
                        type = if (bean.video.isEmpty()) "文章" else "视讯"
                    }

                    else -> {
                        type = if (bean.video.isEmpty()) "文章" else "视讯"
                    }

                }

                if (bean.url.isNotEmpty()) {
                    if (bean.name.isNotEmpty()) {
                        startDetailsActivity(context!!, bean.url, bean.name)
                    } else {
                        startDetailsActivity(context!!, bean.url, type)
                    }
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

    private fun startDetailsActivity(context: Context, url: String, title: String = "") {
        val intent = Intent(context, ServiceWebviewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("other", true)
        intent.putExtra("share_title", title)
        startActivity(intent)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        if (view == null) {
            return
        }

        if (adapter == null || adapter !is SectionMultipleItemAdapter) {
            return
        }

        when (view.id) {

            //V视频 直播 播放按钮
            R.id.ivStartPlayer -> {
                val bean = adapter.data[position].dataBean as NewsBean
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    //打开新的Activity
                    /*      val intent = Intent(context, VodActivity::class.java)
                          intent.putExtra("videoPath", bean.video)
                          startActivity(intent)*/
                    startPlay(position)
                }

            }

            //矩阵 全部
            R.id.tvJuzhengModuleSecond -> {
                val intent = Intent(context!!, AllJuZhengActivity::class.java)
                intent.putExtra("position", position)
                intent.putExtra("list", juzhengList as Serializable)
                startActivity(intent)
            }


            R.id.sparkButton, R.id.tvGoodNum -> {
                val bean = adapter.data[position].dataBean as NewsBean
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


    override fun updateLikeStatus(isLike: Boolean, position: Int) {
        if (isLike) {
            (adapter.data[position].dataBean as NewsBean).like_status = 1
            (adapter.data[position].dataBean as NewsBean).like_num = (adapter.data[position].dataBean as NewsBean).like_num + 1
        } else {
            (adapter.data[position].dataBean as NewsBean).like_status = 0
            (adapter.data[position].dataBean as NewsBean).like_num = (adapter.data[position].dataBean as NewsBean).like_num - 1
        }

        when (param) {
            "矩阵", "专题", "推荐" -> {
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

            when (param) {

                "生活圈", "视讯", "爆料", "悦读", "悦听", "直播" -> {
                    val bean = (adapter as SectionMultipleItemAdapter).data[position].dataBean as NewsBean
                    bean.comment_num = commenNum
                    bean.like_num = zanNum
                    bean.visit_num = seeNum
                    bean.like_status = like_status
                    adapter.notifyItemChanged(position)
                }


                "矩阵", "推荐", "专题" -> {
                    val bean = (adapter as SectionMultipleItemAdapter).data[position].dataBean as NewsBean
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
            val type = ModuleUtils.getTypeByNewsBean(bean)
            startNewsDetailActivity(context!!, type, bean.id)
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


    private fun initVideoView() {
        mVideoView = VideoView(mActivity!!)
        mVideoView!!.setOnStateChangeListener(object : SimpleOnStateChangeListener() {
            override fun onPlayStateChanged(playState: Int) {
                //监听VideoViewManager释放，重置状态
                if (playState == VideoView.STATE_IDLE) {
                    Utils.removeViewFormParent(mVideoView)
                    mLastPos = mCurPos
                    mCurPos = -1
                }
            }
        })
        mController = StandardVideoController(mActivity!!)
        mErrorView = ErrorView(mActivity)
        mController!!.addControlComponent(mErrorView)
        mCompleteView = CompleteView(mActivity!!)
        mController!!.addControlComponent(mCompleteView)
        mTitleView = TitleView(mActivity!!)
        mController!!.addControlComponent(mTitleView)
        mController!!.addControlComponent(VodControlView(mActivity!!))
        mController!!.addControlComponent(GestureView(mActivity!!))
        mController!!.setEnableOrientation(true)
        mVideoView?.setVideoController(mController)
    }


    private fun releaseVideoView() {
        mVideoView!!.release()
        controlPlayUiShow(false)
        if (mVideoView!!.isFullScreen) {
            mVideoView!!.stopFullScreen()
        }
        if (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT !== mActivity?.requestedOrientation) {
            mActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        mCurPos = -1
    }


    /**
     * 开始播放
     * @param position 列表位置
     */
    private fun startPlay(position: Int) {
        if (mCurPos == position) {
            LogUtils.i("播放器", "已被拦截")
            return
        }

        if (mCurPos != -1) {
            releaseVideoView()
        }
        val bean = adapter.data[position].dataBean as NewsBean
        mVideoView!!.setUrl(bean.video)
        mTitleView!!.setTitle(bean.name)
        mPlayerContainer = adapter?.getViewByPosition(position, R.id.player_container) as FrameLayout
        mIvVideoBg =  adapter?.getViewByPosition(position, R.id.ivVideoBg) as ImageView
        ivStartPlayer = adapter?.getViewByPosition(position, R.id.ivStartPlayer) as ImageView
        val prepareView = adapter?.getViewByPosition(position, R.id.prepare_view) as PrepareView
        controlPlayUiShow(true)
        //把列表中预置的PrepareView添加到控制器中，注意isPrivate此处只能为true。
        mController!!.addControlComponent(prepareView, true)
        Utils.removeViewFormParent(mVideoView)
        mPlayerContainer?.addView(mVideoView, 0)
        //播放之前将VideoView添加到VideoViewManager以便在别的页面也能操作它
        getVideoViewManager()?.add(mVideoView, Tag.LIST)
        mVideoView?.start()
        mCurPos = position
    }


    /**
     * 子类可通过此方法直接拿到VideoViewManager
     */
    private fun getVideoViewManager(): VideoViewManager? {
        return VideoViewManager.instance()
    }


    /**
     * 由于onResume必须调用super。故增加此方法，
     * 子类将会重写此方法，改变onResume的逻辑
     */
    private fun resumePlay() {
      /*  if (mLastPos == -1) return
        if (MainActivity.mCurrentIndex !== 1) return
        //恢复上次播放的位置
        startPlay(mLastPos)*/
    }



    private fun controlPlayUiShow(isShow : Boolean){
        if(isShow){
            mIvVideoBg?.visibility = View.GONE
            mPlayerContainer?.visibility = View.VISIBLE
            ivStartPlayer?.visibility = View.GONE
        }else{
            mIvVideoBg?.visibility = View.VISIBLE
            mPlayerContainer?.visibility = View.GONE
            ivStartPlayer?.visibility = View.VISIBLE
        }

    }
}