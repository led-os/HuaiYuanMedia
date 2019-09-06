package cn.tklvyou.huaiyuanmedia.ui.mine.concern

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseHttpRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.ConcernModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.MyConcernAdapter
import cn.tklvyou.huaiyuanmedia.ui.adapter.WxCircleAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.my_article.MyArticleContract
import cn.tklvyou.huaiyuanmedia.ui.mine.my_article.MyArticleListPresenter
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_my_concern.*

/**
 * 我的关注
 */
class MyConcernFragment : BaseHttpRecyclerFragment<MyConcernPresenter, ConcernModel, BaseViewHolder, MyConcernAdapter>(), MyConcernContract.View {

    private var type = 1
    override fun initPresenter(): MyConcernPresenter {
        return MyConcernPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_my_concern
    }

    override fun getLoadingView(): View {
        return mRecyclerView
    }

    override fun initView() {
        type = mBundle.getInt("type", 1)

        initSmartRefreshLayout(mSmartRefreshLayout)
        initRecyclerView(mRecyclerView)

        mRecyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))

        mSmartRefreshLayout.autoRefresh()
    }

    override fun onRetry() {
        super.onRetry()
        mSmartRefreshLayout.autoRefresh()
    }

    override fun lazyData() {
    }

    override fun getListAsync(page: Int) {
        mPresenter.getConcernList(type, page)
    }


    override fun setConcernList(p: Int, model: BasePageModel<ConcernModel>?) {
        if (model != null) {
            onLoadSucceed(p, model.data)
        } else {
            onLoadFailed(p, null)
        }
    }

    override fun setList(list: MutableList<ConcernModel>?) {
        setList(object : AdapterCallBack<MyConcernAdapter> {

            override fun createAdapter(): MyConcernAdapter {
                return MyConcernAdapter(R.layout.item_my_concern, list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })

    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)

        if (view!!.id == R.id.cbCheck) {
            val bean = (adapter as MyConcernAdapter).data[position]

            if (!bean.isNoConcern) {
                val dialog = CommonDialog(context)
                dialog.setTitle("温馨提示")
                dialog.setMessage("是否取消关注？")
                dialog.setYesOnclickListener("确认") {
                    mPresenter.cancelConcern(bean.pid, position, type)

                    dialog.dismiss()
                }
                dialog.show()
            } else {
                mPresenter.addConcern(bean.pid, position, type)
            }

        }
    }

    override fun addConcernSuccess(position: Int) {
        adapter.data[position].isNoConcern = false
        adapter.notifyItemChanged(position)
    }


    override fun cancelSuccess(position: Int) {
        adapter.data[position].isNoConcern = true
        adapter.notifyItemChanged(position)
    }


}