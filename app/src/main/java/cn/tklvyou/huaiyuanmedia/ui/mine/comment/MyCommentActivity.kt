package cn.tklvyou.huaiyuanmedia.ui.mine.comment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.MyCollectionAdapter
import cn.tklvyou.huaiyuanmedia.ui.adapter.MyCommentAdapter
import cn.tklvyou.huaiyuanmedia.ui.mine.collection.CollectContract
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_my_comment.*

class MyCommentActivity : BaseHttpRecyclerActivity<MyCommentPresenter, NewsBean, BaseViewHolder, MyCommentAdapter>(), MyCommentContract.View {

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


    override fun setMyConmmentList(page: Int, pageModel: BasePageModel<NewsBean>?) {
        if (pageModel != null) {
            if (page != 1) {
                sycnList(adapter.data)
            }
            onLoadSucceed(page, pageModel.data)
        } else {
            onLoadFailed(page, null)
        }
    }

    override fun setList(list: MutableList<NewsBean>?) {
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


}
