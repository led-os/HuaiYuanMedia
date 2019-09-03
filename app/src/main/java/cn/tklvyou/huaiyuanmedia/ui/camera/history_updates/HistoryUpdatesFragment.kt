package cn.tklvyou.huaiyuanmedia.ui.camera.history_updates

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseHttpRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.WxCircleAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_history_updates.*

/**
 * 最新动态  好友动态
 */
class HistoryUpdatesFragment : BaseHttpRecyclerFragment<HistoryUpdatesPresenter, NewsBean, BaseViewHolder, WxCircleAdapter>(), HistoryUpdatesContract.View {

    private var isMine = true
    override fun initPresenter(): HistoryUpdatesPresenter {
        return HistoryUpdatesPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_history_updates
    }

    override fun getLoadingView(): View {
        return mRecyclerView
    }

    override fun initView() {
        isMine = mBundle.getBoolean("isMine", true)
        initSmartRefreshLayout(mSmartRefreshLayout)
        initRecyclerView(mRecyclerView)

        mSmartRefreshLayout.setEnableRefresh(false)

        mRecyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))

        mPresenter.getNewList(isMine, 1)
    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getNewList(isMine, 1)
    }

    override fun lazyData() {
    }

    override fun getListAsync(page: Int) {
        mPresenter.getNewList(isMine, page)
    }


    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {
        if (model != null) {
            onLoadSucceed(p, model.data)
        } else {
            onLoadFailed(p, null)
        }
    }

    override fun setList(list: MutableList<NewsBean>?) {
        setList(object : AdapterCallBack<WxCircleAdapter> {

            override fun createAdapter(): WxCircleAdapter {
                val adapter = WxCircleAdapter(R.layout.item_winxin_circle, list)
                adapter.setEnableDelete()
                return adapter
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })

    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)

        val bean = (adapter as WxCircleAdapter).data[position]
        val id = bean.id
        val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
        startNewsDetailActivity(context!!, type, id, position)

    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)

        when (view!!.id) {
            R.id.deleteBtn -> {
                val dialog = CommonDialog(context)
                dialog.setTitle("温馨提示")
                dialog.setMessage("是否删除？")
                dialog.setYesOnclickListener("确认") {
                    val bean = (adapter as WxCircleAdapter).data[position]
                    mPresenter.deleteArticle(bean.id, position)
                    dialog.dismiss()
                }
                dialog.show()
            }

            R.id.sparkButton, R.id.tvGoodNum -> {
                val bean = (adapter as WxCircleAdapter).data[position] as NewsBean
                if (bean.like_status == 1) {
                    mPresenter.cancelLikeNews(bean.id, position)
                } else {
                    mPresenter.addLikeNews(bean.id, position)
                }

            }
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

            val bean = (adapter as WxCircleAdapter).data[position]
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