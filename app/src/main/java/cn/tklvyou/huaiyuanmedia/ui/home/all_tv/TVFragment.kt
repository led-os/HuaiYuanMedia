package cn.tklvyou.huaiyuanmedia.ui.home.all_tv

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseHttpRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.SuixiTVGridRvAdpater
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import cn.tklvyou.huaiyuanmedia.ui.video_player.VodActivity
import cn.tklvyou.huaiyuanmedia.utils.GridDividerItemDecoration
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_tv.*
import java.util.ArrayList


class TVFragment : BaseHttpRecyclerFragment<TVListPresenter, NewsBean, BaseViewHolder, SuixiTVGridRvAdpater>(), TVListContract.View {

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_tv
    }

    override fun initPresenter(): TVListPresenter {
        return TVListPresenter()
    }

    override fun getLoadingView(): View {
        return mRecyclerView
    }

    private var type = ""
    override fun initView() {
        type = mBundle.getString("type", "濉溪新闻")
        initSmartRefreshLayout(smartRefreshLayout)
        initRecyclerView(mRecyclerView)

        mRecyclerView.layoutManager = GridLayoutManager(context, 2)
        mRecyclerView.addItemDecoration(GridDividerItemDecoration(30, Color.WHITE, true))

        mPresenter.getNewList("濉溪TV", type, 1)
    }


    override fun lazyData() {

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
        mPresenter.getNewList("濉溪TV", type, page)
    }


    override fun setList(list: MutableList<NewsBean>?) {
        setList(object : AdapterCallBack<SuixiTVGridRvAdpater> {

            override fun createAdapter(): SuixiTVGridRvAdpater {
                return SuixiTVGridRvAdpater(R.layout.item_suixi_tv_all_layout, list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        val bean = (adapter as SuixiTVGridRvAdpater).data[position]
        val id = bean.id
        val type = "电视"
        if (bean.url.isNotEmpty()) {
            startDetailsActivity(context!!,bean.url)
        } else {
            NewsDetailActivity.startNewsDetailActivity(mActivity!!, type, id)
        }
    }

    private fun startDetailsActivity(context: Context, url: String) {
        val intent = Intent(context, ServiceWebviewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("other",true)
        intent.putExtra("share_title","")
        startActivity(intent)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        if (view != null) {
            when (view.id) {
                R.id.ivSuiXiTVStartPlayer -> {
                    val intent = Intent(context, VodActivity::class.java)
                    intent.putExtra("videoPath", (adapter as SuixiTVGridRvAdpater).data[position].video)
                    startActivity(intent)
                }
            }
        }
    }

}
