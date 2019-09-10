package cn.tklvyou.huaiyuanmedia.ui.audio

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseHttpRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.AudioRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.tv_news_detail.TVNewsDetailActivity
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_audio.*

class AudioFragment : BaseHttpRecyclerFragment<AudioPresenter, NewsBean, BaseViewHolder, AudioRvAdapter>(), AudioContract.View {

    override fun initPresenter(): AudioPresenter {
        return AudioPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_audio
    }

    override fun getLoadingView(): View {
        return mRecyclerView
    }

    override fun initView() {
        serviceTitleBar.setBackgroundResource(R.drawable.shape_gradient_common_titlebar)

        initSmartRefreshLayout(mSmartRefreshLayout)
        initRecyclerView(mRecyclerView)
        mRecyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL))

        mPresenter.getNewList("视听", "", 1, true)
    }

    override fun lazyData() {

    }


    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {
        if (model != null) {
            onLoadSucceed(p, model.data)
        } else {
            onLoadFailed(p, null)
        }
    }


    override fun getListAsync(page: Int) {
        mPresenter.getNewList("视听", "", page, false)
    }


    override fun setList(list: MutableList<NewsBean>?) {
        setList(object : AdapterCallBack<AudioRvAdapter> {

            override fun createAdapter(): AudioRvAdapter {
                return AudioRvAdapter(R.layout.item_audio_list_view, list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        val bean = (adapter as AudioRvAdapter).data[position]
        val id = bean.id
        val type = if (bean.type == "tv") "电视" else "广播"
        startTVNewsDetailActivity(type, id, position)

    }

    private fun startTVNewsDetailActivity(type: String, id: Int, position: Int) {
        val intent = Intent(context, TVNewsDetailActivity::class.java)
        intent.putExtra(TVNewsDetailActivity.INTENT_ID, id)
        intent.putExtra(TVNewsDetailActivity.INTENT_TYPE, type)
        intent.putExtra(TVNewsDetailActivity.POSITION, position)
        startActivityForResult(intent, 11)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            val position = data.getIntExtra("position", 0)
            val seeNum = data.getIntExtra("seeNum", 0)

            if (adapter != null) {
                val bean = (adapter as AudioRvAdapter).data[position] as NewsBean
                bean.visit_num = seeNum
                adapter.notifyItemChanged(position)
            }
        }

    }


}