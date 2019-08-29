package cn.tklvyou.huaiyuanmedia.ui.home.all_juzheng

import android.view.View
import android.widget.LinearLayout
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseHttpRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.JuzhengRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_juzheng.*
import java.util.ArrayList


class JuzhengFragment : BaseHttpRecyclerFragment<JuzhengListPresenter, NewsBean, BaseViewHolder, JuzhengRvAdapter>(), JuzhengListContract.View {

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_juzheng
    }

    override fun initPresenter(): JuzhengListPresenter {
        return JuzhengListPresenter()
    }

    private var type = ""
    override fun initView() {
        type = mBundle.getString("type", "公安在线")
        initSmartRefreshLayout(smartRefreshLayout)
        initRecyclerView(mRecyclerView)
        mRecyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))

    }


    override fun lazyData() {
        mPresenter.getNewList("矩阵", type, 1)
    }

    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {
        if (model != null) {
            val newList = ArrayList<NewsBean>()
            model.data.forEach {
                newList.add(it)
            }
            onLoadSucceed(p, newList)
        } else {
            onLoadFailed(p, null)
        }
    }


    override fun getListAsync(page: Int) {
        mPresenter.getNewList("矩阵", type, page)
    }


    override fun setList(list: MutableList<NewsBean>?) {
        setList(object : AdapterCallBack<JuzhengRvAdapter> {

            override fun createAdapter(): JuzhengRvAdapter {
                return JuzhengRvAdapter(list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        val bean = (adapter as JuzhengRvAdapter).data[position]
        val id = bean.id
        val type = "文章"
        NewsDetailActivity.startNewsDetailActivity(mActivity!!, type, id)
    }


}
