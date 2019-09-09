package cn.tklvyou.huaiyuanmedia.ui.mine.my_dianzan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.MyCollectionAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.home.tv_news_detail.TVNewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_my_collect.*

class MyDianZanActivity : BaseHttpRecyclerActivity<MyDianZanPresenter, NewsBean, BaseViewHolder, MyCollectionAdapter>(), MyDianZanContract.View {

    override fun initPresenter(): MyDianZanPresenter {
        return MyDianZanPresenter()
    }


    override fun getActivityLayoutID(): Int {
        return R.layout.activity_my_collect
    }

    override fun getListAsync(page: Int) {
        mPresenter.getDianZanPageList(page)
    }

    private var isEdit = false
    override fun initView(savedInstanceState: Bundle?) {
        setTitle("我的点赞")
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        setPositiveText("编辑")
        setPositiveOnClickListener {
            if (adapter != null) {
                if (isEdit) {
                    isEdit = false
                    editLayout.visibility = View.GONE
                    tvDeleteSelect.text = "删除"
                    tvDeleteSelect.setTextColor(resources.getColor(R.color.default_gray_text_color))
                    tvDeleteSelect.isEnabled = false
                    selectIds.clear()
                    setPositiveText("编辑")
                    adapter.setEditModel(false)

                } else {
                    isEdit = true
                    editLayout.visibility = View.VISIBLE
                    setPositiveText("取消")
                    adapter.setEditModel(true)
                }
            }
        }


        initSmartRefreshLayout(smartLayoutRoot)
        initRecyclerView(recyclerViewRoot)
        recyclerViewRoot.addItemDecoration(RecycleViewDivider(this, LinearLayout.VERTICAL))
        smartLayoutRoot.autoRefresh()

        tvClearAll.setOnClickListener {
            val dialog = CommonDialog(this)
            dialog.setMessage("确定要清空吗？清空后将永久无法找回，请谨慎操作。")
            dialog.setYesOnclickListener("确定") {
                mPresenter.cancelDianZanAll()
                dialog.dismiss()
            }
            dialog.show()
        }

        tvDeleteSelect.setOnClickListener {
            val idsBuilder = StringBuilder()
            for (i in 0 until selectIds.size) {
                if (i == selectIds.size - 1) {
                    idsBuilder.append(selectIds[i])
                } else {
                    idsBuilder.append("" + selectIds[i] + ",")
                }
            }

            val dialog = CommonDialog(this)
            dialog.setMessage("确定要删除这${selectIds.size}个点赞吗？")
            dialog.setYesOnclickListener("确定") {
                mPresenter.cancelDianZanList(idsBuilder.toString())
                dialog.dismiss()
            }
            dialog.show()

        }

    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getDianZanPageList(1)
    }


    override fun cancelDianZanSuccess(isAll: Boolean) {
        if (isAll) {
            adapter.data.clear()
            adapter.notifyDataSetChanged()
            setDeleteSelectBtnBackground(0)
        } else {
            selectIds.clear()
            setDeleteSelectBtnBackground(selectIds.size)
            smartLayoutRoot.autoRefresh()
        }
    }


    override fun setList(list: MutableList<NewsBean>?) {
        setList(object : AdapterCallBack<MyCollectionAdapter> {

            override fun createAdapter(): MyCollectionAdapter {
                return MyCollectionAdapter(list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }

    override fun setDianZanList(page: Int, pageModel: BasePageModel<NewsBean>?) {
        if (pageModel != null) {
            if (page != 1) {
                sycnList(adapter.data)
            }
            onLoadSucceed(page, pageModel.data)
        } else {
            onLoadFailed(page, null)
        }
    }


    //选中Id集合
    private var selectIds = ArrayList<Int>()

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)

        val bean = adapter!!.data[position] as NewsBean
        val id = bean.id

        if (!isEdit) {

            when (bean.module) {
                "V视频" -> {
                    val type = "视频"
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(this, bean.url)
                    } else {
                        startNewsDetailActivity(this, type, id, position)
                    }
                }
                "濉溪TV" -> {
                    if (bean.module_second == "置顶频道") {
                        val type = if (bean.type == "tv") "电视" else "广播"
                        TVNewsDetailActivity.startTVNewsDetailActivity(this, type, id)
                    } else {
                        val type = "电视"
                        NewsDetailActivity.startNewsDetailActivity(this, type, id)
                    }
                }
                "新闻", "矩阵", "专栏", "党建", "专题" -> {
                    val type = "文章"
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(this, bean.url)
                    } else {
                        startNewsDetailActivity(this, type, id, position)
                    }
                }
                "视讯" -> {
                    val type = "视讯"
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(this, bean.url)
                    } else {
                        startNewsDetailActivity(this, type, id, position)
                    }
                }
                "问政" -> {
                    val type = "问政"
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(this, bean.url)
                    } else {
                        startNewsDetailActivity(this, type, id, position)
                    }
                }

                "原创", "随手拍" -> {
                    val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(this, bean.url)
                    } else {
                        startNewsDetailActivity(this, type, id, position)
                    }
                }
                "悦读" -> {
                    val type = "悦读"
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(this, bean.url)
                    } else {
                        startNewsDetailActivity(this, type, id, position)
                    }
                }
                "悦听" -> {
                    val type = "悦听"
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(this, bean.url)
                    } else {
                        startNewsDetailActivity(this, type, id, position)
                    }
                }
                "公告" -> {
                    val type = "公告"
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(this, bean.url)
                    } else {
                        startNewsDetailActivity(this, type, id, position)
                    }
                }
                else -> {
                    val type = "文章"
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(this, bean.url)
                    } else {
                        startNewsDetailActivity(this, type, id, position)
                    }
                }

            }
        } else {
            val isSelect = bean.isSelect
            if (!isSelect) {
                selectIds.add(id)
                bean.isSelect = true

            } else {
                selectIds.remove(id)
                bean.isSelect = false
            }
            setDeleteSelectBtnBackground(selectIds.size)
            adapter.notifyItemChanged(position)
        }

    }

    private fun setDeleteSelectBtnBackground(index: Int) {
        if (index != 0) {
            tvDeleteSelect.text = "删除（$index）"
            tvDeleteSelect.setTextColor(resources.getColor(R.color.redFF4A5C))
            tvDeleteSelect.isEnabled = true
        } else {
            tvDeleteSelect.text = "删除"
            tvDeleteSelect.setTextColor(resources.getColor(R.color.default_gray_text_color))
            tvDeleteSelect.isEnabled = false
        }
    }


    private fun startDetailsActivity(context: Context, url: String) {
        val intent = Intent(context, ServiceWebviewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("other", true)
        intent.putExtra("share_title", "")
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val position = data.getIntExtra("position", 0)
            val seeNum = data.getIntExtra("seeNum", 0)
            val zanNum = data.getIntExtra("zanNum", 0)
            val commenNum = data.getIntExtra("commentNum", 0)
            val like_status = data.getIntExtra("like_status", 0)

            val bean = (adapter as MyCollectionAdapter).data[position]
            bean.comment_num = commenNum
            bean.like_num = zanNum
            bean.visit_num = seeNum
            bean.like_status = like_status
            adapter.notifyItemChanged(position)

        }
    }

    private fun startNewsDetailActivity(context: Context, type: String, id: Int, position: Int) {
        val intent = Intent(context, NewsDetailActivity::class.java)
        intent.putExtra(NewsDetailActivity.INTENT_ID, id)
        intent.putExtra(NewsDetailActivity.INTENT_TYPE, type)
        intent.putExtra(NewsDetailActivity.POSITION, position)
        startActivityForResult(intent, 0)
    }

}