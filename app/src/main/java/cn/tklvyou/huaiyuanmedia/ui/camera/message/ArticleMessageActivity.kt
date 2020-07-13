package cn.tklvyou.huaiyuanmedia.ui.camera.message

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.ArticleMessageModel
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.MyCommentModel
import cn.tklvyou.huaiyuanmedia.ui.adapter.ArticleMessageAdapter
import cn.tklvyou.huaiyuanmedia.ui.adapter.MyCommentAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.comment.MyCommentPresenter
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_ju_zheng_details.*

class ArticleMessageActivity : BaseHttpRecyclerActivity<ArticleMessagePresenter, ArticleMessageModel, BaseViewHolder, ArticleMessageAdapter>(), ArticleMessageContract.View {

    override fun initPresenter(): ArticleMessagePresenter {
        return ArticleMessagePresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_article_message
    }

    override fun initView(savedInstanceState: Bundle?) {
        setTitle("消息")
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        setPositiveText("清空")
        setPositiveOnClickListener {
            val dialog = CommonDialog(this)
            dialog.setMessage("是否清空消息列表?")
            dialog.setYesOnclickListener("确认") {
                mPresenter.clearMessage()
                dialog.dismiss()
            }
            dialog.setNoOnclickListener("取消") {
                dialog.dismiss()
            }
            dialog.show()
        }


        initSmartRefreshLayout(mSmartRefreshLayout)
        initRecyclerView(mRecyclerView)
        mRecyclerView.addItemDecoration(RecycleViewDivider(this, LinearLayout.VERTICAL, ConvertUtils.dp2px(15f), resources.getColor(R.color.common_bg)))


        mSmartRefreshLayout.autoRefresh()
    }

    override fun getListAsync(page: Int) {
        mPresenter.getMessagePageList(page)
    }

    override fun setMessageList(page: Int, model: List<ArticleMessageModel>?) {
        if (model != null) {
            onLoadSucceed(page, model)
        } else {
            onLoadFailed(page, null)
        }
    }

    override fun setList(list: MutableList<ArticleMessageModel>?) {
        setList(object : AdapterCallBack<ArticleMessageAdapter> {

            override fun createAdapter(): ArticleMessageAdapter {
                return ArticleMessageAdapter(R.layout.item_article_message_view, list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }

    override fun clearSuccess() {
        adapter.data.clear()
        adapter.notifyDataSetChanged()
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)

        val bean = (adapter as ArticleMessageAdapter).data[position]
        val id = bean.id
        val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
        startNewsDetailActivity(this, type, id)
    }

    private fun startNewsDetailActivity(context: Context, type: String, id: Int) {
        val intent = Intent(context, NewsDetailActivity::class.java)
        intent.putExtra(NewsDetailActivity.INTENT_ID, id)
        intent.putExtra(NewsDetailActivity.INTENT_TYPE, type)
        startActivity(intent)
    }

}
