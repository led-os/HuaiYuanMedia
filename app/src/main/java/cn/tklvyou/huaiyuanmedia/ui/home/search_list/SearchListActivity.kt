package cn.tklvyou.huaiyuanmedia.ui.home.search_list

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.MyCollectionAdapter
import cn.tklvyou.huaiyuanmedia.ui.adapter.MySearchRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import kotlinx.android.synthetic.main.activity_search_list.*

class SearchListActivity : BaseHttpRecyclerActivity<SearchPresenter, NewsBean, BaseViewHolder, MySearchRvAdapter>(), SearchContract.View {


    override fun initPresenter(): SearchPresenter {
        return SearchPresenter()
    }


    override fun getActivityLayoutID(): Int {
        return R.layout.activity_search_list
    }


    private var searchStr = ""

    override fun initView(savedInstanceState: Bundle?) {
        searchStr = intent.getStringExtra("search")

        setNavigationImage()
        setNavigationOnClickListener { finish() }

        setPositiveText("搜索")
        setPositiveOnClickListener {

            if (commonTitleBar.searchKey.isEmpty()) {
                searchStr = commonTitleBar.centerSearchEditText.hint.toString()
            } else {
                searchStr = commonTitleBar.searchKey
            }

            mPresenter.searchNewList("", searchStr, 1)
            hideSoftInput(commonTitleBar.centerSearchEditText.windowToken)
        }

        commonTitleBar.setCenterContent(CommonTitleBar.TYPE_CENTER_SEARCHVIEW, "", "", 0, R.drawable.shape_title_bar_search_radius_5_bg, 0)

        initSmartRefreshLayout(smartRefreshLayout)
        initRecyclerView(recyclerView)


        commonTitleBar.centerSearchEditText.hint = searchStr
        commonTitleBar.centerSearchEditText.setText(searchStr)

    }


    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {
        if (model != null) {
            onLoadSucceed(p, model.data)
        } else {
            onLoadFailed(p, null)
        }
    }

    override fun setList(list: MutableList<NewsBean>?) {
        setList(object : AdapterCallBack<MySearchRvAdapter> {

            override fun createAdapter(): MySearchRvAdapter {
                return MySearchRvAdapter(list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }


    override fun getListAsync(page: Int) {
        mPresenter.searchNewList("", searchStr, page)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        val bean = (adapter as MySearchRvAdapter).data[position]
        val id = bean.id

        val type = "文章"
        NewsDetailActivity.startNewsDetailActivity(this, type, id)
    }


}
