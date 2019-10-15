package cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan.search

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.PingXuanPersionModel
import cn.tklvyou.huaiyuanmedia.ui.adapter.PingXuanPersionRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan.PingXuanContract
import cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan.PingXuanListPresenter
import cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan.PingXuanPersionActivity
import cn.tklvyou.huaiyuanmedia.utils.GridDividerItemDecoration
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import kotlinx.android.synthetic.main.activity_pingxuan_search_list.*

class PingXuanSearchListActivity : BaseHttpRecyclerActivity<PingXuanListPresenter, PingXuanPersionModel, BaseViewHolder, PingXuanPersionRvAdapter>(), PingXuanContract.ListView {


    override fun initPresenter(): PingXuanListPresenter {
        return PingXuanListPresenter()
    }


    override fun getActivityLayoutID(): Int {
        return R.layout.activity_pingxuan_search_list
    }


    override fun getLoadingView(): View {
        return recyclerView
    }

    private var searchStr = ""
    private var id = 0
    override fun initView(savedInstanceState: Bundle?) {
        id = intent.getIntExtra("id", 0)

        setNavigationImage()
        setNavigationOnClickListener {
            finish()
        }

        setPositiveText("搜索")
        setPositiveOnClickListener {
            search()
        }

        commonTitleBar.setCenterContent(CommonTitleBar.TYPE_CENTER_SEARCHVIEW, "", "", 0, R.drawable.shape_title_bar_search_radius_5_bg, 0)

        initSmartRefreshLayout(smartRefreshLayout)
        initRecyclerView(recyclerView)


        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.addItemDecoration(GridDividerItemDecoration(ConvertUtils.dp2px(15f), Color.WHITE, true))

        commonTitleBar.centerSearchEditText.hint = "请输入编号或姓名"

        commonTitleBar.setListener(object : CommonTitleBar.OnTitleBarListener {
            override fun onClicked(v: View?, action: Int, extra: String?) {
                if (action == CommonTitleBar.ACTION_SEARCH_SUBMIT) {
                    search()
                }
            }

        })

    }

    private fun search() {
        if (commonTitleBar.searchKey.isEmpty()) {
            ToastUtils.showShort("请输入搜索内容")
            return
        } else {
            searchStr = commonTitleBar.searchKey
        }

        smartRefreshLayout.visibility = View.VISIBLE
        mPresenter.getPingXuanPersionList(id, 1, searchStr, "normal")
        hideSoftInput(commonTitleBar.centerSearchEditText.windowToken)

    }


    override fun setPingXuanPageModel(p: Int, model: BasePageModel<PingXuanPersionModel>?) {
        if (model != null) {
            onLoadSucceed(p, model.data)
        } else {
            onLoadFailed(p, null)
        }
    }

    override fun voteSuccess(position: Int) {
        val bean = adapter!!.data[position] as PingXuanPersionModel
        bean.count++
        adapter.notifyItemChanged(position)
    }

    override fun voteFailed() {
    }

    override fun setList(list: MutableList<PingXuanPersionModel>?) {

        setList(object : AdapterCallBack<PingXuanPersionRvAdapter> {

            override fun createAdapter(): PingXuanPersionRvAdapter {
                return PingXuanPersionRvAdapter(R.layout.item_ping_xuan_list_view, list, true)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        val bean = adapter!!.data[position] as PingXuanPersionModel
        val intent = Intent(this, PingXuanPersionActivity::class.java)
        intent.putExtra("id", bean.id)
        startActivity(intent)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        val bean = adapter!!.data[position] as PingXuanPersionModel
        mPresenter.vote(bean.id,position)
    }

    override fun getListAsync(page: Int) {
        mPresenter.getPingXuanPersionList(id, page, searchStr, "normal")
    }


}
