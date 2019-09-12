package cn.tklvyou.huaiyuanmedia.ui.mine.comment

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.MyCommentModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.MyCollectionAdapter
import cn.tklvyou.huaiyuanmedia.ui.adapter.MyCommentAdapter
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.home.tv_news_detail.TVNewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.collection.CollectContract
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.blankj.utilcode.util.ConvertUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_my_comment.*

class MyCommentActivity : BaseHttpRecyclerActivity<MyCommentPresenter, MyCommentModel, BaseViewHolder, MyCommentAdapter>(), MyCommentContract.View {

    override fun initPresenter(): MyCommentPresenter {
        return MyCommentPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_my_comment

    }

    private var isEdit = false
    //选中Id集合
    private var selectIds = ArrayList<Int>()

    override fun initView(savedInstanceState: Bundle?) {
        setTitle("我的评论")
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
        recyclerViewRoot.addItemDecoration(RecycleViewDivider(this, LinearLayout.VERTICAL, ConvertUtils.dp2px(15f), resources.getColor(R.color.common_bg)))

        smartLayoutRoot.autoRefresh()

        tvClearAll.setOnClickListener {
            val dialog = CommonDialog(this)
            dialog.setMessage("确定要清空吗？清空后将永久无法找回，请谨慎操作。")
            dialog.setYesOnclickListener("确定") {
                mPresenter.cancelCommentAll()
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
            dialog.setMessage("确定要删除这${selectIds.size}个评论吗？")
            dialog.setYesOnclickListener("确定") {
                mPresenter.cancelCommentList(idsBuilder.toString())
                dialog.dismiss()
            }
            dialog.show()
        }

    }

    override fun onRetry() {
        super.onRetry()
        smartLayoutRoot.autoRefresh()
    }


    override fun setMyConmmentList(page: Int, pageModel: BasePageModel<MyCommentModel>?) {
        if (pageModel != null) {
            if (page != 1) {
                sycnList(adapter.data)
            }
            onLoadSucceed(page, pageModel.data)
        } else {
            onLoadFailed(page, null)
        }
    }

    override fun setList(list: MutableList<MyCommentModel>?) {
        setList(object : AdapterCallBack<MyCommentAdapter> {

            override fun createAdapter(): MyCommentAdapter {
                return MyCommentAdapter(list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }


    override fun getListAsync(page: Int) {
        mPresenter.getMyCommentPageList(page)
    }

    override fun cancelCommentSuccess(isAll: Boolean) {
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


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)

        val bean = adapter!!.data[position] as MyCommentModel
        val id = bean.id

        if (!isEdit) {

            when (view!!.id) {
                R.id.itemLayout -> {
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

                        "直播"->{
                            val type = "直播"
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
                }

                R.id.sparkButton, R.id.tvGoodNum -> {
                    if (bean.like_status == 1) {
                        mPresenter.cancelLikeNews(bean.id, position)
                    } else {
                        mPresenter.addLikeNews(bean.id, position)
                    }
                }
            }


        } else {
            val isSelect = bean.isSelect
            val commentId = bean.comment_id
            if (!isSelect) {
                selectIds.add(commentId)
                bean.isSelect = true

            } else {
                selectIds.remove(commentId)
                bean.isSelect = false
            }
            setDeleteSelectBtnBackground(selectIds.size)
            adapter.notifyItemChanged(position)
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

        adapter.notifyItemChangedAnimal(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val position = data.getIntExtra("position", 0)
            val seeNum = data.getIntExtra("seeNum", 0)
            val zanNum = data.getIntExtra("zanNum", 0)
            val commenNum = data.getIntExtra("commentNum", 0)
            val like_status = data.getIntExtra("like_status", 0)

            val bean = (adapter as MyCommentAdapter).data[position]
            bean.comment_num = commenNum
            bean.like_num = zanNum
            bean.visit_num = seeNum
            bean.like_status = like_status
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

    private fun startNewsDetailActivity(context: Context, type: String, id: Int, position: Int) {
        val intent = Intent(context, NewsDetailActivity::class.java)
        intent.putExtra(NewsDetailActivity.INTENT_ID, id)
        intent.putExtra(NewsDetailActivity.INTENT_TYPE, type)
        intent.putExtra(NewsDetailActivity.POSITION, position)
        startActivityForResult(intent, 0)
    }


}
