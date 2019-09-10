package cn.tklvyou.huaiyuanmedia.ui.mine.wenzhen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.WenZhenAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.layout_recycler.*
import kotlinx.android.synthetic.main.layout_refresh_recycler.*

/**
 *@description :问政记录
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2019年08月02日11:04
 * @Email: 971613168@qq.com
 */
class MyWenZhenActivity : BaseHttpRecyclerActivity<WenZhenPresenter, NewsBean, BaseViewHolder, WenZhenAdapter>(), WenZhenContract.View {

    override fun initPresenter(): WenZhenPresenter {
        return WenZhenPresenter()
    }


    override fun getActivityLayoutID(): Int {
        return R.layout.layout_refresh_recycler
    }

    override fun getListAsync(page: Int) {
        mPresenter.getDataPageList(page)
    }

    override fun initView(savedInstanceState: Bundle?) {
        setTitle("问政")
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        initSmartRefreshLayout(smartLayoutRoot)
        initRecyclerView(recyclerViewRoot)
        smartLayoutRoot.autoRefresh()
    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getDataPageList(1)
    }

    override fun setDataList(page: Int, pageModel: BasePageModel<NewsBean>?) {
        if (pageModel != null) {
            onLoadSucceed(page, pageModel.data)
        } else {
            onLoadFailed(page, null)
        }
    }

    override fun setList(list: MutableList<NewsBean>?) {
        setList(object : AdapterCallBack<WenZhenAdapter> {

            override fun createAdapter(): WenZhenAdapter {
                return WenZhenAdapter(list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }

    private fun startDetailsActivity(context: Context, url: String) {
        val intent = Intent(context, ServiceWebviewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("other",true)
        intent.putExtra("share_title","")
        startActivity(intent)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        val bean = (adapter as WenZhenAdapter).data[position] as NewsBean
        val id = bean.id
        val type = "问政"
        if (bean.url.isNotEmpty()) {
            startDetailsActivity(this,bean.url)
        } else {
            startNewsDetailActivity(this, type, id, position)
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

            val bean = (adapter as WenZhenAdapter).data[position]
            bean.comment_num = commenNum
            bean.like_num = zanNum
            bean.visit_num = seeNum
            bean.like_status = like_status
            adapter.notifyItemChanged(position)

        }
    }

    private fun startNewsDetailActivity(context: Context, type: String, id: Int, position: Int) {
        val intent = Intent(context, NewsDetailActivity::class.java)
        intent.putExtra(NewsDetailActivity.INTENT_ID, id)
        intent.putExtra(NewsDetailActivity.INTENT_TYPE, type)
        intent.putExtra(NewsDetailActivity.POSITION, position)
        startActivityForResult(intent, 0)
    }


}