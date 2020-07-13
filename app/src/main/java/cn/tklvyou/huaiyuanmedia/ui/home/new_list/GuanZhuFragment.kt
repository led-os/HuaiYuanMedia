package cn.tklvyou.huaiyuanmedia.ui.home.new_list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseHttpRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.helper.GlideManager
import cn.tklvyou.huaiyuanmedia.model.*
import cn.tklvyou.huaiyuanmedia.ui.adapter.GuanZhuRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.adapter.GuanZhuViewHolder
import cn.tklvyou.huaiyuanmedia.ui.adapter.JuzhengHeaderViewholder
import cn.tklvyou.huaiyuanmedia.ui.adapter.MyConcernAdapter
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.video_player.VodActivity
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import cn.tklvyou.huaiyuanmedia.widget.page_recycler.PageRecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_guan_zhu.*
import kotlinx.android.synthetic.main.fragment_news_list.*

class GuanZhuFragment : BaseHttpRecyclerFragment<GuanZhuPresenter, NewsBean, GuanZhuViewHolder, GuanZhuRvAdapter>(), GuanZhuContract.View {

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_guan_zhu
    }

    override fun initPresenter(): GuanZhuPresenter {
        return GuanZhuPresenter()
    }


    override fun getLoadingView(): View {
        return mRecyclerView
    }

    private var enableRefresh = false
    private var guanZhuAdapter: MyConcernAdapter? = null
    private var flush = false

    override fun initView() {
        initSmartRefreshLayout(mSmartRefreshLayout)
        initRecyclerView(mRecyclerView)

        mAttentionRecyclerView.layoutManager = LinearLayoutManager(context)
        mAttentionRecyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))
        mRecyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))

    }

    override fun lazyData() {
        mPresenter.getAttentionList()
    }

    public fun refreshData() {
        flushHeader = true
        lazyData()
    }

    override fun onRetry() {
        super.onRetry()
        lazyData()
    }


    private var attentionList: MutableList<ConcernModel>? = null

    override fun setAttentionList(model: AttentionModel?) {
        if (model != null) {
            attentionList = model.attention.toMutableList()

            val concernAllModel = ConcernModel()
            concernAllModel.nickname = "全部"
            concernAllModel.id = R.drawable.icon_see_more
            attentionList!!.add(0, concernAllModel)

            if (model.noattention.isNotEmpty()) {
                val concernModel = ConcernModel()
                concernModel.nickname = "关注更多"
                concernModel.id = R.mipmap.icon_attention_more
                attentionList!!.add(concernModel)
            }


            if (model.attention == null || model.attention.size == 0) {
                mAttentionRecyclerView.visibility = View.VISIBLE
                mSmartRefreshLayout.visibility = View.GONE
                enableRefresh = true
            } else {
                enableRefresh = false
                mSmartRefreshLayout.visibility = View.VISIBLE
                mAttentionRecyclerView.visibility = View.GONE
            }


            for (i in 0 until model.noattention.size) {
                model.noattention[i].isNoConcern = true
            }

            guanZhuAdapter = MyConcernAdapter(R.layout.item_my_concern, model.noattention)
            mAttentionRecyclerView.adapter = guanZhuAdapter
            guanZhuAdapter!!.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
                if (view!!.id == R.id.cbCheck) {
                    val bean = (adapter as MyConcernAdapter).data[position]

                    if (!bean.isNoConcern) {
                        val dialog = CommonDialog(context)
                        dialog.setTitle("温馨提示")
                        dialog.setMessage("是否取消关注？")
                        dialog.setYesOnclickListener("确认") {
                            mPresenter.cancelConcern(bean.id, position, 1)
                            dialog.dismiss()
                        }
                        dialog.show()
                    } else {
                        mPresenter.addConcern(bean.id, position, 1)
                    }


                }
            }


            if (model.attention != null && model.attention.size > 0) {
                mSmartRefreshLayout.autoRefresh()
            }
        }

    }

    private var module = ""
    private var flushHeader = true
    private fun initHeaderView(headerList: MutableList<ConcernModel>): View {
        val view = View.inflate(context, R.layout.item_juzheng_header_layout, null)
        val mRecyclerView = view.findViewById<PageRecyclerView>(R.id.customSwipeView)
        // 设置指示器
        mRecyclerView.setIndicator(view.findViewById(R.id.indicator))
        // 设置行数和列数
        mRecyclerView.setPageSize(1, 4)
        mRecyclerView.adapter = mRecyclerView.PageAdapter(headerList, object : PageRecyclerView.CallBack {
            //记录选中的RadioButton的位置
            private var mSelectedItem = -1

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                return JuzhengHeaderViewholder(LayoutInflater.from(context).inflate(R.layout.item_juzheng_header_child_layout, parent, false))
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
                val bean = headerList[position]
                if (bean.nickname == "关注更多" || bean.nickname == "全部") {
                    GlideManager.loadCircleImg(bean.id, (holder as JuzhengHeaderViewholder).ivAvatar)
                } else {
                    GlideManager.loadCircleImg(bean.avatar, (holder as JuzhengHeaderViewholder).ivAvatar)
                }

                holder.tvNickName.text = bean.nickname
                holder.radioButton.isChecked = position == mSelectedItem
            }

            override fun onItemClickListener(view: View?, position: Int) {
                if (position != mSelectedItem) {
                    if (headerList[position].nickname != "关注更多") {
                        mSelectedItem = position
                        module = if (headerList[position].nickname == "全部") "" else headerList[position].nickname
                        mRecyclerView.adapter!!.notifyDataSetChanged()
                        flushHeader = false
                        mPresenter.getGuanZhuNews(1, module, false)
                    } else {
                        enableRefresh = true
                        mAttentionRecyclerView.visibility = View.VISIBLE
                        mSmartRefreshLayout.visibility = View.GONE
                    }
                } else {
                    //重复点击同一个，不执行任何操作
                }

            }

            override fun onItemLongClickListener(view: View?, position: Int) {
            }


        })

        return view
    }

    override fun setGuanZhuNews(p: Int, model: BasePageModel<NewsBean>?) {
        if (model != null) {
            onLoadSucceed(p, model.data)
        } else {
            onLoadFailed(p, null)
        }
    }


    override fun setList(list: MutableList<NewsBean>) {
        setList(object : AdapterCallBack<GuanZhuRvAdapter> {

            override fun createAdapter(): GuanZhuRvAdapter {
                val adapter = GuanZhuRvAdapter(list)
                adapter.addHeaderView(initHeaderView(attentionList!!))
                adapter.setIOnCancelClickListener { id, position ->
                    flush = true
                    mPresenter.cancelConcern(id, position, 1)
                }
                return adapter
            }

            override fun refreshAdapter() {
                if (flushHeader) {
                    adapter.removeAllHeaderView()
                    adapter.addHeaderView(initHeaderView(attentionList!!))
                }
                adapter.setNewData(list)
            }
        })


    }

    override fun getListAsync(page: Int) {
        mPresenter.getGuanZhuNews(page, module, false)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        val bean = (adapter as GuanZhuRvAdapter).data[position] as NewsBean
        val id = bean.id
        val type =if (bean.video.isEmpty()) "文章" else "视讯"
        if (bean.url.isNotEmpty()) {
            startDetailsActivity(context!!, bean.url)
        } else {
            startNewsDetailActivity(context!!, type, id, position)
        }

    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        when (view!!.id) {
            R.id.sparkButton, R.id.tvGoodNum -> {
                val bean = adapter!!.data[position] as NewsBean
                if (bean.like_status == 1) {
                    mPresenter.cancelLikeNews(bean.id, position)
                } else {
                    mPresenter.addLikeNews(bean.id, position)
                }

            }

            //V视频 直播 播放按钮
            R.id.ivStartPlayer -> {

                val bean = adapter!!.data[position] as NewsBean
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(context!!, bean.url)
                } else {
                    //打开新的Activity
                    val intent = Intent(context, VodActivity::class.java)
                    intent.putExtra("videoPath", bean.video)
                    startActivity(intent)
                }

            }
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
            adapter.data[position].like_status = 1
            adapter.data[position].like_num = adapter.data[position].like_num + 1
        } else {
            adapter.data[position].like_status = 0
            adapter.data[position].like_num = adapter.data[position].like_num - 1
        }

        adapter.notifyItemChangedAnimal(position + 1)
    }


    override fun addConcernSuccess(position: Int) {
        guanZhuAdapter!!.data[position].isNoConcern = false
        guanZhuAdapter!!.notifyItemChanged(position)
    }


    override fun cancelSuccess(position: Int) {
        if (flush) {
            mTipView.show()
            flush = false
            flushHeader = true
            module = ""
            lazyData()
        } else {
            guanZhuAdapter!!.data[position].isNoConcern = true
            guanZhuAdapter!!.notifyItemChanged(position)
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

            val bean = (adapter as GuanZhuRvAdapter).data[position] as NewsBean
            bean.comment_num = commenNum
            bean.like_num = zanNum
            bean.visit_num = seeNum
            bean.like_status = like_status
            adapter.notifyItemChanged(position + 1)


        }
    }


}
