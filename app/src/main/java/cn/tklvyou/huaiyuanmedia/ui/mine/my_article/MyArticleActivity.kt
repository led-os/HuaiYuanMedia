package cn.tklvyou.huaiyuanmedia.ui.mine.my_article

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.WxCircleEditAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_my_article.*

/**
 *@description :我的帖子
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2019年08月05日10:16
 * @Email: 971613168@qq.com
 */
class MyArticleActivity : BaseHttpRecyclerActivity<MyArticleListPresenter, NewsBean, BaseViewHolder, WxCircleEditAdapter>(), MyArticleContract.View {

    override fun initPresenter(): MyArticleListPresenter {
        return MyArticleListPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_my_article
    }


    override fun getLoadingView(): View {
        return recyclerViewRoot
    }

    val moduleName = ""
    private var isEdit = false
    //选中Id集合
    private var selectIds = ArrayList<Int>()


    override fun initView(savedInstanceState: Bundle?) {
        setTitle("我的帖子")
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

        recyclerViewRoot.addItemDecoration(RecycleViewDivider(this, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))

        smartLayoutRoot.autoRefresh()

        tvClearAll.setOnClickListener {
            val dialog = CommonDialog(this)
            dialog.setMessage("确定要清空吗？清空后将永久无法找回，请谨慎操作。")
            dialog.setYesOnclickListener("确定") {
                mPresenter.cancelArticleAll()
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
            dialog.setMessage("确定要删除这${selectIds.size}个帖子吗？")
            dialog.setYesOnclickListener("确定") {
                mPresenter.cancelArticleList(idsBuilder.toString())
                dialog.dismiss()
            }
            dialog.show()

        }

    }


    override fun onRetry() {
        super.onRetry()
        mPresenter.getNewList(moduleName, 1)
    }


    override fun cancelArticleSuccess(isAll: Boolean) {
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

    override fun getListAsync(page: Int) {
        mPresenter.getNewList(moduleName, page)
    }


    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {
        if (model != null) {
            if (p != 1) {
                sycnList(adapter.data)
            }

            onLoadSucceed(p, model.data)
        } else {
            onLoadFailed(p, null)
        }
    }

    override fun setList(list: MutableList<NewsBean>?) {
        setList(object : AdapterCallBack<WxCircleEditAdapter> {

            override fun createAdapter(): WxCircleEditAdapter {
                val adapter = WxCircleEditAdapter(R.layout.item_edit_winxin_circle, list)
                adapter.setEnableDelete()
                return adapter
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })

    }



    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        val bean = (adapter as WxCircleEditAdapter).data[position]

        if (!isEdit) {
            when (view!!.id) {
                R.id.itemLayout -> {
                    val id = bean.id
                    val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
                    startNewsDetailActivity(this, type, id, position)
                }

                R.id.deleteBtn -> {
                    val dialog = CommonDialog(this)
                    dialog.setTitle("温馨提示")
                    dialog.setMessage("是否删除？")
                    dialog.setYesOnclickListener("确认") {
                        mPresenter.deleteArticle(bean.id, position)
                        dialog.dismiss()
                    }
                    dialog.show()
                }

                R.id.sparkButton, R.id.tvGoodNum -> {
                    if (bean.like_status == 1) {
                        mPresenter.cancelLikeNews(bean.id, position)
                    } else {
                        mPresenter.addLikeNews(bean.id, position)
                    }
                }
            }
        }else{
            val isSelect = bean.isSelect
            if (!isSelect) {
                selectIds.add(bean.id)
                bean.isSelect = true

            } else {
                selectIds.remove(bean.id)
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


    override fun deleteSuccess(position: Int) {
        adapter.remove(position)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val position = data.getIntExtra("position", 0)
            val seeNum = data.getIntExtra("seeNum", 0)
            val zanNum = data.getIntExtra("zanNum", 0)
            val commenNum = data.getIntExtra("commentNum", 0)
            val like_status = data.getIntExtra("like_status", 0)

            val bean = (adapter as WxCircleEditAdapter).data[position]
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