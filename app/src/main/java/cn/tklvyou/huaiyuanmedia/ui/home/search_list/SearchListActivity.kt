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
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_search_list.*

class SearchListActivity : BaseHttpRecyclerActivity<SearchPresenter, NewsBean, BaseViewHolder, MyCollectionAdapter>(), SearchContract.View {


    override fun initPresenter(): SearchPresenter {
        return SearchPresenter()
    }


    override fun getActivityLayoutID(): Int {
        return R.layout.activity_search_list
    }


    private var searchStr = ""

    override fun initView(savedInstanceState: Bundle?) {
        setTitle("搜索结果")
        setNavigationImage()
        setNavigationOnClickListener { finish() }

        initSmartRefreshLayout(smartRefreshLayout)
        initRecyclerView(recyclerView)
        searchStr = intent.getStringExtra("search")
        etSearch.hint = searchStr

        btnClear.setOnClickListener {
            etSearch.setText("")
        }

        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (etSearch.text.toString().isEmpty()) {
                    searchStr = etSearch.hint.toString()
                } else {
                    searchStr = etSearch.text.toString()
                }

                mPresenter.searchNewList("新闻", searchStr, 1)
                hideSoftInput(etSearch.windowToken)
            }
            return@setOnEditorActionListener true
        }

    }


    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {
        if (model != null) {
            onLoadSucceed(p, model.data)
        } else {
            onLoadFailed(p, null)
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


    override fun getListAsync(page: Int) {
        mPresenter.searchNewList("新闻", searchStr, page)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        val bean = (adapter as MyCollectionAdapter).data[position]
        val id = bean.id

        val type = "文章"
        NewsDetailActivity.startNewsDetailActivity(this, type, id)
    }


}
