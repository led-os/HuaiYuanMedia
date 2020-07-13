package cn.tklvyou.huaiyuanmedia.ui.mine.wenzhen

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
import cn.tklvyou.huaiyuanmedia.ui.adapter.WenZhenAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_my_wenzheng.*


class MyWenZhenActivity : BaseHttpRecyclerActivity<WenZhenPresenter, NewsBean, BaseViewHolder, WenZhenAdapter>(), WenZhenContract.View {

    override fun initPresenter(): WenZhenPresenter {
        return WenZhenPresenter()
    }


    override fun getActivityLayoutID(): Int {
        return R.layout.activity_my_wenzheng
    }

    override fun getListAsync(page: Int) {
        mPresenter.getDataPageList(page)
    }

    private var isEdit = false
    //选中Id集合
    private var selectIds = ArrayList<Int>()

    override fun initView(savedInstanceState: Bundle?) {
        initSmartRefreshLayout(smartLayoutRoot)
        initRecyclerView(recyclerViewRoot)

        setTitle("爆料")
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
            dialog.setMessage("确定要删除这${selectIds.size}个爆料吗？")
            dialog.setYesOnclickListener("确定") {
                mPresenter.cancelArticleList(idsBuilder.toString())
                dialog.dismiss()
            }
            dialog.show()

        }

        recyclerViewRoot.addItemDecoration(RecycleViewDivider(this, LinearLayout.VERTICAL))
        smartLayoutRoot.autoRefresh()

    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getDataPageList(1)
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


    override fun setDataList(page: Int, pageModel: BasePageModel<NewsBean>?) {
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
        setList(object : AdapterCallBack<WenZhenAdapter> {

            override fun createAdapter(): WenZhenAdapter {
                return WenZhenAdapter(list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }

    private fun startDetailsActivity(context: Context, url: String) {
        val intent = Intent(context, ServiceWebviewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("other",true)
        intent.putExtra("share_title","")
        startActivity(intent)
    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        val bean = adapter.data[position] as NewsBean
        if (!isEdit) {
            when (view!!.id) {
                R.id.itemLayout -> {
                    val type = "爆料"
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(this,bean.url)
                    } else {
                        startNewsDetailActivity(this, type, bean.id, position)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val position = data.getIntExtra("position", 0)
            val seeNum = data.getIntExtra("seeNum", 0)
            val zanNum = data.getIntExtra("zanNum", 0)
            val commenNum = data.getIntExtra("commentNum", 0)
            val like_status = data.getIntExtra("like_status", 0)

            val bean = (adapter as WenZhenAdapter).data[position]
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