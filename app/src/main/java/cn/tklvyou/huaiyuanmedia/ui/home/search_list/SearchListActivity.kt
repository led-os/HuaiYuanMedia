package cn.tklvyou.huaiyuanmedia.ui.home.search_list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.GridLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.MyCollectionAdapter
import cn.tklvyou.huaiyuanmedia.ui.adapter.MySearchRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.adapter.SearchHistoryRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import kotlinx.android.synthetic.main.activity_search_list.*
import java.lang.StringBuilder

class SearchListActivity : BaseHttpRecyclerActivity<SearchPresenter, NewsBean, BaseViewHolder, MySearchRvAdapter>(), SearchContract.View {


    override fun initPresenter(): SearchPresenter {
        return SearchPresenter()
    }


    override fun getActivityLayoutID(): Int {
        return R.layout.activity_search_list
    }


    private var searchStr = ""
    private var searchHistoryList: MutableList<String> = ArrayList()
    private var stringBuilder = StringBuilder()

    override fun initView(savedInstanceState: Bundle?) {
        searchStr = intent.getStringExtra("search")

        setNavigationImage()
        setNavigationOnClickListener {
            for ((index, item) in searchHistoryList.withIndex()) {
                if (index == searchHistoryList.size - 1) {
                    stringBuilder.append("$item")
                } else {
                    stringBuilder.append("$item|+|")
                }
            }
            SPUtils.getInstance().put("history", stringBuilder.toString())
            finish()
        }

        setPositiveText("搜索")
        setPositiveOnClickListener {

            if (commonTitleBar.searchKey.isEmpty()) {
                commonTitleBar.centerSearchEditText.setText(commonTitleBar.centerSearchEditText.hint.toString())
                searchStr = commonTitleBar.centerSearchEditText.hint.toString()
            } else {
                searchStr = commonTitleBar.searchKey
            }

            searchView.visibility = View.INVISIBLE
            smartRefreshLayout.visibility = View.VISIBLE
            mPresenter.searchNewList("", searchStr, 1)
            hideSoftInput(commonTitleBar.centerSearchEditText.windowToken)

            if (searchHistoryList.contains(searchStr)) {
                return@setPositiveOnClickListener
            }

            if (searchHistoryList.size < 10) {
                searchHistoryList.add(searchStr)
            } else {
                searchHistoryList.add(0, searchStr)
            }
        }

        commonTitleBar.setCenterContent(CommonTitleBar.TYPE_CENTER_SEARCHVIEW, "", "", 0, R.drawable.shape_title_bar_search_radius_5_bg, 0)

        initSmartRefreshLayout(smartRefreshLayout)
        initRecyclerView(recyclerView)

        commonTitleBar.centerSearchEditText.hint = searchStr
        commonTitleBar.centerSearchEditText.setText(searchStr)
        commonTitleBar.centerSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.toString().isEmpty()) {
                    searchView.visibility = View.VISIBLE
                    smartRefreshLayout.visibility = View.INVISIBLE
                    mSearchListRecyclerView.adapter!!.notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        searchHistoryList = SPUtils.getInstance().getString("history", "").split("|+|").toMutableList()

        val iterator = searchHistoryList.iterator()
        while (iterator.hasNext()) {
            val value = iterator.next()
            if (value.isEmpty()) {
                iterator.remove()
            }
        }

        LogUtils.e(Gson().toJson(searchHistoryList))

        mSearchListRecyclerView.layoutManager = GridLayoutManager(this, 2)
        mSearchListRecyclerView.adapter = SearchHistoryRvAdapter(R.layout.item_search_history_layout, searchHistoryList)
        (mSearchListRecyclerView.adapter as SearchHistoryRvAdapter).onItemClickListener = object : BaseQuickAdapter.OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                val searchStr = adapter!!.data[position] as String
                commonTitleBar.centerSearchEditText.setText(searchStr)
            }
        }


        btnClear.setOnClickListener {
            val dialog = CommonDialog(this)
            dialog.setMessage("是否清空历史搜索记录")
            dialog.setYesOnclickListener("确定") {
                searchHistoryList.clear()
                mSearchListRecyclerView.adapter!!.notifyDataSetChanged()
                dialog.dismiss()
            }
            dialog.show()
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


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        when (view!!.id) {
            R.id.sparkButton, R.id.tvGoodNum -> {
                val bean = adapter!!.data[position] as NewsBean
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for ((index, item) in searchHistoryList.withIndex()) {
                if (index == searchHistoryList.size - 1) {
                    stringBuilder.append("$item")
                } else {
                    stringBuilder.append("$item|+|")
                }
            }
            SPUtils.getInstance().put("history", stringBuilder.toString())
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
