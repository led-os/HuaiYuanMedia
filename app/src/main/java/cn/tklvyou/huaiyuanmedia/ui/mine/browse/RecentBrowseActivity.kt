package cn.tklvyou.huaiyuanmedia.ui.mine.browse
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
import cn.tklvyou.huaiyuanmedia.ui.adapter.MyCollectionAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.home.tv_news_detail.TVNewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.layout_recycler.*
import kotlinx.android.synthetic.main.layout_refresh_recycler.*

/**
 *@description :最近浏览
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2019年08月02日16:51
 * @Email: 971613168@qq.com
 */
class RecentBrowseActivity : BaseHttpRecyclerActivity<BrowsePresenter, NewsBean, BaseViewHolder, MyCollectionAdapter>(), BrowseContract.View {

    override fun initPresenter(): BrowsePresenter {
        return BrowsePresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.layout_refresh_recycler
    }

    override fun getListAsync(page: Int) {
        mPresenter.getBrowsePageList(page)
    }

    override fun initView(savedInstanceState: Bundle?) {
        setTitle("最近浏览")
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        initSmartRefreshLayout(smartLayoutRoot)
        initRecyclerView(recyclerViewRoot)
        mPresenter.getBrowsePageList(1)
    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getBrowsePageList(1)
    }


    override fun setBrowseList(page: Int, pageModel: BasePageModel<NewsBean>?) {
        if (pageModel != null) {
            onLoadSucceed(page, pageModel.data)
        } else {
            onLoadFailed(page, null)
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



    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        val bean = (adapter as MyCollectionAdapter).data[position]
        val id = bean.id

        when(bean.module){
            "V视频" ->{
                val type = "视频"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(this,bean.url)
                } else {
                    startNewsDetailActivity(this, type, id, position)
                }
            }
            "濉溪TV" ->{
                if(bean.module_second =="置顶频道"){
                    val type = if (bean.type == "tv") "电视" else "广播"
                    TVNewsDetailActivity.startTVNewsDetailActivity(this, type, id)
                }else{
                    val type = "电视"
                    NewsDetailActivity.startNewsDetailActivity(this, type, id)
                }
            }
            "新闻","矩阵","专栏","党建" ->{
                val type = "文章"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(this,bean.url)
                } else {
                    startNewsDetailActivity(this, type, id, position)
                }
            }
            "视讯" ->{
                val type = "视讯"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(this,bean.url)
                } else {
                    startNewsDetailActivity(this, type, id, position)
                }
            }
            "问政" ->{
                val type = "问政"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(this,bean.url)
                } else {
                    startNewsDetailActivity(this, type, id, position)
                }
            }

            "原创","随手拍" ->{
                val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(this,bean.url)
                } else {
                    startNewsDetailActivity(this, type, id, position)
                }
            }
            "悦读" ->{
                val type = "悦读"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(this,bean.url)
                } else {
                    startNewsDetailActivity(this, type, id, position)
                }
            }
            "悦听" ->{
                val type = "悦听"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(this,bean.url)
                } else {
                    startNewsDetailActivity(this, type, id, position)
                }
            }
            "公告" ->{
                val type = "公告"
                if (bean.url.isNotEmpty()) {
                    startDetailsActivity(this,bean.url)
                } else {
                    startNewsDetailActivity(this, type, id, position)
                }
            }

        }


    }


    private fun startDetailsActivity(context: Context, url: String) {
        val intent = Intent(context, ServiceWebviewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("other",true)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val position = data.getIntExtra("position", 0)
            val seeNum = data.getIntExtra("seeNum", 0)
            val zanNum = data.getIntExtra("zanNum", 0)
            val commenNum = data.getIntExtra("commentNum", 0)
            val like_status = data.getIntExtra("like_status", 0)

            val bean = (adapter as MyCollectionAdapter).data[position]
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