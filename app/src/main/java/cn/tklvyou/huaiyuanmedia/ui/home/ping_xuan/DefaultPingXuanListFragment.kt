package cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseHttpRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.PingXuanPersionModel
import cn.tklvyou.huaiyuanmedia.ui.adapter.PingXuanPersionRvAdapter
import cn.tklvyou.huaiyuanmedia.utils.GridDividerItemDecoration
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_default_ping_xuan_list.*

class DefaultPingXuanListFragment : BaseHttpRecyclerFragment<PingXuanListPresenter, PingXuanPersionModel, BaseViewHolder, PingXuanPersionRvAdapter>(), PingXuanContract.ListView {

    override fun initPresenter(): PingXuanListPresenter {
        return PingXuanListPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_default_ping_xuan_list
    }

    override fun getLoadingView(): View {
        return mRecylerView
    }

    override fun lazyData() {
        mPresenter.getPingXuanPersionList(pId, 1, null, sort)
    }

    private var pId = 0
    private var sort = "normal"
    override fun initView() {
        initSmartRefreshLayout(mSmartRefreshLayout)
        initRecyclerView(mRecylerView)

        pId = mBundle.getInt("id", 0)
        val isNormal = mBundle.getBoolean("isNormal", false)
        sort = if (isNormal) {
            "normal"
        } else {
            "rank"
        }

        if (isNormal) {
            mRecylerView.layoutManager = GridLayoutManager(context, 2)
            mRecylerView.addItemDecoration(GridDividerItemDecoration(ConvertUtils.dp2px(15f), Color.WHITE, true))
        } else {
            mRecylerView.layoutManager = LinearLayoutManager(context)
            mRecylerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL,
                    ConvertUtils.dp2px(1f), resources.getColor(R.color.common_bg), true))
        }

    }

    override fun getListAsync(page: Int) {
        mPresenter.getPingXuanPersionList(pId, page, null, sort)
    }

    override fun onUserVisible() {
        super.onUserVisible()
        mSmartRefreshLayout.autoRefresh()
    }

    override fun setPingXuanPageModel(p: Int, model: BasePageModel<PingXuanPersionModel>?) {
        if (model != null) {
            onLoadSucceed(p, model.data)
        } else {
            onLoadFailed(p, null)
        }
    }


    override fun setList(list: MutableList<PingXuanPersionModel>?) {

        setList(object : AdapterCallBack<PingXuanPersionRvAdapter> {

            override fun createAdapter(): PingXuanPersionRvAdapter {
                if (sort == "normal") {
                    return PingXuanPersionRvAdapter(R.layout.item_ping_xuan_list_view, list, true)
                } else {
                    return PingXuanPersionRvAdapter(R.layout.item_ping_xuan_rank_list_view, list, false)
                }
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        val bean = adapter!!.data[position] as PingXuanPersionModel
        val intent = Intent(context, PingXuanPersionActivity::class.java)
        intent.putExtra("id", bean.id)
        startActivity(intent)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        val bean = adapter!!.data[position] as PingXuanPersionModel
        mPresenter.vote(bean.id,position)
    }


    override fun voteSuccess(position: Int) {
        val bean = adapter!!.data[position] as PingXuanPersionModel
        bean.count++
        adapter.notifyItemChanged(position)
    }

    override fun voteFailed() {

    }

}
