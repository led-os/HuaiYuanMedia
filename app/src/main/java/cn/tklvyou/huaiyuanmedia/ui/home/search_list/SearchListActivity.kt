package cn.tklvyou.huaiyuanmedia.ui.home.search_list

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import cn.tklvyou.huaiyuanmedia.common.ModuleUtils
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.MyCollectionAdapter
import cn.tklvyou.huaiyuanmedia.ui.adapter.MySearchRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.adapter.SearchHistoryRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.home.tv_news_detail.TVNewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.video_player.VodActivity
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
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
            search()
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

        commonTitleBar.setListener(object:CommonTitleBar.OnTitleBarListener{
            override fun onClicked(v: View?, action: Int, extra: String?) {
                if(action == CommonTitleBar.ACTION_SEARCH_SUBMIT){
                    search()
                }
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

    private fun search() {
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


        if (searchHistoryList.contains(searchStr.trim())) {
            return
        }

        if (searchHistoryList.size < 10) {
            searchHistoryList.add(searchStr)
        } else {
            searchHistoryList.set(0, searchStr)
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

        val type = ModuleUtils.getTypeByNewsBean(bean)

        if (bean.url.isNotEmpty()) {
            startDetailsActivity(this, bean.url)
        } else {
            startNewsDetailActivity(this, type, id, position)
        }

    }

    private fun startDetailsActivity(context: Context, url: String) {
        val intent = Intent(context, ServiceWebviewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("other", true)
        intent.putExtra("share_title", "")
        startActivity(intent)
    }

    private fun startNewsDetailActivity(context: Context, type: String, id: Int, position: Int) {
        val intent = Intent(context, NewsDetailActivity::class.java)
        intent.putExtra(NewsDetailActivity.INTENT_ID, id)
        intent.putExtra(NewsDetailActivity.INTENT_TYPE, type)
        intent.putExtra(NewsDetailActivity.POSITION, position)
        startActivityForResult(intent, 0)
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

            //V视频 直播 播放按钮
            R.id.ivStartPlayer -> {
                val bean = adapter!!.data[position] as NewsBean
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(this, bean.url)
                } else {
                    //打开新的Activity
                    val intent = Intent(this, VodActivity::class.java)
                    intent.putExtra("videoPath", bean.video)
                    startActivity(intent)
                }
            }
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

            val bean = adapter!!.data[position] as NewsBean
            bean.comment_num = commenNum
            bean.like_num = zanNum
            bean.visit_num = seeNum
            bean.like_status = like_status
            adapter.notifyItemChanged(position)

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
