package cn.tklvyou.huaiyuanmedia.ui.mine.my_article

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.WxCircleAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_my_article.*

/**
 *@description :我的帖子
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2019年08月05日10:16
 * @Email: 971613168@qq.com
 */
class MyArticleActivity : BaseHttpRecyclerActivity<MyArticleListPresenter, NewsBean, BaseViewHolder, WxCircleAdapter>(), MyArticleContract.View  {

    override fun initPresenter(): MyArticleListPresenter {
        return MyArticleListPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_my_article
    }


    override fun getLoadingView(): View {
        return cameraRecyclerView
    }

    val moduleName= "随手拍"

    override fun initView(savedInstanceState: Bundle?) {
        setTitle("我的帖子")
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        cameraTitleBar.visibility = View.GONE
        initSmartRefreshLayout(cameraSmartRefreshLayout)
        initRecyclerView(cameraRecyclerView)

        cameraRecyclerView.addItemDecoration(RecycleViewDivider(this, LinearLayout.VERTICAL, 1, resources.getColor(R.color.common_bg)))

        mPresenter.getNewList(moduleName, 1)
    }




    override fun onRetry() {
        super.onRetry()
        mPresenter.getNewList(moduleName, 1)
    }


    override fun getListAsync(page: Int) {
        mPresenter.getNewList(moduleName, page)
    }


    override fun setNewList(p: Int, model: BasePageModel<NewsBean>?) {
        if (model != null) {
            onLoadSucceed(p, model.data)
        } else {
            onLoadFailed(p, null)
        }
    }

    override fun setList(list: MutableList<NewsBean>?) {
        setList(object : AdapterCallBack<WxCircleAdapter> {

            override fun createAdapter(): WxCircleAdapter {
                val adapter = WxCircleAdapter(R.layout.item_winxin_circle, list)
                adapter.setEnableDelete()
                return adapter
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })

    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)

        val bean = (adapter as WxCircleAdapter).data[position]
        val id = bean.id
        val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
        startNewsDetailActivity(this, type, id, position)

    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        if (view!!.id == R.id.deleteBtn) {
            val dialog = CommonDialog(this)
            dialog.setTitle("温馨提示")
            dialog.setMessage("是否删除？")
            dialog.setYesOnclickListener("确认") {
                val bean = (adapter as WxCircleAdapter).data[position]
                mPresenter.deleteArticle(bean.id, position)
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun deleteSuccess(position: Int) {
        adapter.remove(position)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val position = data.getIntExtra("position", 0)
            val seeNum = data.getIntExtra("seeNum", 0)
            val zanNum = data.getIntExtra("zanNum", 0)
            val commenNum = data.getIntExtra("commentNum", 0)
            val like_status = data.getIntExtra("like_status", 0)

            val bean = (adapter as WxCircleAdapter).data[position]
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