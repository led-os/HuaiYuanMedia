package cn.tklvyou.huaiyuanmedia.ui.mine.browse

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.common.ModuleUtils
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.model.NewsMultipleItem
import cn.tklvyou.huaiyuanmedia.ui.adapter.MyCollectionAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.home.tv_news_detail.TVNewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.audio.ServiceWebviewActivity
import cn.tklvyou.huaiyuanmedia.ui.home.AudioController
import cn.tklvyou.huaiyuanmedia.ui.video_player.VodActivity
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_recent_browse.*

/**
 *@description :最近浏览
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2019年08月02日16:51
 * @Email: 971613168@qq.com
 */
class RecentBrowseActivity : BaseHttpRecyclerActivity<BrowsePresenter, NewsMultipleItem<NewsBean>, BaseViewHolder, MyCollectionAdapter>(), BrowseContract.View {

    override fun initPresenter(): BrowsePresenter {
        return BrowsePresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_recent_browse
    }

    override fun getListAsync(page: Int) {
        mPresenter.getBrowsePageList(page)
    }

    private var isEdit = false
    override fun initView(savedInstanceState: Bundle?) {
        initSmartRefreshLayout(smartLayoutRoot)
        initRecyclerView(recyclerViewRoot)

        setTitle("最近浏览")
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        setPositiveText("编辑")
        setPositiveOnClickListener {
            if (adapter != null) {
                if (isEdit) {
                    isEdit = false
                    editLayout.visibility = View.GONE
                    tvDeleteSelect.text = "删除"
                    tvDeleteSelect.setTextColor(resources.getColor(R.color.default_gray_text_color))
                    tvDeleteSelect.isEnabled = false
                    selectIds.clear()
                    setPositiveText("编辑")
                    adapter.setEditModel(false)

                } else {
                    isEdit = true
                    editLayout.visibility = View.VISIBLE
                    setPositiveText("取消")
                    adapter.setEditModel(true)
                }
            }
        }

        recyclerViewRoot.addItemDecoration(RecycleViewDivider(this, LinearLayout.VERTICAL))

        smartLayoutRoot.autoRefresh()

        tvClearAll.setOnClickListener {
            val dialog = CommonDialog(this)
            dialog.setMessage("确定要清空吗？清空后将永久无法找回，请谨慎操作。")
            dialog.setYesOnclickListener("确定") {
                mPresenter.cancelArticleAll()
                dialog.dismiss()
            }
            dialog.show()
        }

        tvDeleteSelect.setOnClickListener {
            val idsBuilder = StringBuilder()
            for (i in 0 until selectIds.size) {
                if (i == selectIds.size - 1) {
                    idsBuilder.append(selectIds[i])
                } else {
                    idsBuilder.append("" + selectIds[i] + ",")
                }
            }

            val dialog = CommonDialog(this)
            dialog.setMessage("确定要删除这${selectIds.size}个浏览记录吗？")
            dialog.setYesOnclickListener("确定") {
                mPresenter.cancelArticleList(idsBuilder.toString())
                dialog.dismiss()
            }
            dialog.show()

        }

    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getBrowsePageList(1)
    }


    override fun cancelArticleSuccess(isAll: Boolean) {
        if (isAll) {
            adapter.data.clear()
            adapter.notifyDataSetChanged()
            setDeleteSelectBtnBackground(0)
        } else {
            selectIds.clear()
            setDeleteSelectBtnBackground(selectIds.size)
            smartLayoutRoot.autoRefresh()
        }
    }

    //选中Id集合
    private var selectIds = ArrayList<Int>()

    override fun setBrowseList(page: Int, pageModel: BasePageModel<NewsBean>?) {
        if (pageModel != null) {
            if (page != 1) {
                sycnList(adapter.data)
            }

            val data = ArrayList<NewsMultipleItem<NewsBean>>()
            pageModel.data.forEach {
                data.add(NewsMultipleItem(it.module, it))
            }
            onLoadSucceed(page, data)
        } else {
            onLoadFailed(page, null)
        }
    }
    private var audioController: AudioController? = null
    override fun setList(list: MutableList<NewsMultipleItem<NewsBean>>?) {
        setList(object : AdapterCallBack<MyCollectionAdapter> {

            override fun createAdapter(): MyCollectionAdapter {
                val adapter = MyCollectionAdapter(list)
                audioController = AudioController(this@RecentBrowseActivity)
                adapter.setAudioController(audioController)
                return adapter
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        val bean = (adapter.data[position] as NewsMultipleItem<NewsBean>).dataBean
        val id = bean.id
        if (!isEdit) {
            when (view!!.id) {
                R.id.itemLayout -> {
                    val type = ModuleUtils.getTypeByNewsBean(bean)

                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(this, bean.url)
                    } else {
                        startNewsDetailActivity(this, type, id, position)
                    }
                }

                //V视频 直播 播放按钮
                R.id.ivStartPlayer -> {
                    audioController?.onPause()

                    val bean = (adapter.data[position] as NewsMultipleItem<NewsBean>).dataBean as NewsBean
                    if (bean.url.isNotEmpty()) {
                        startDetailsActivity(this, bean.url)
                    } else {
                        //打开新的Activity
                        val intent = Intent(this, VodActivity::class.java)
                        intent.putExtra("videoPath", bean.video)
                        startActivity(intent)
                    }

                }

                R.id.sparkButton,R.id.sparkButton1, R.id.tvGoodNum, R.id.tvGoodNum1 -> {
                    if (bean.like_status == 1) {
                        mPresenter.cancelLikeNews(bean.id, position)
                    } else {
                        mPresenter.addLikeNews(bean.id, position)
                    }
                }

            }
        }else{
            val isSelect = bean.isSelect
            if (!isSelect) {
                selectIds.add(id)
                bean.isSelect = true

            } else {
                selectIds.remove(id)
                bean.isSelect = false
            }
            setDeleteSelectBtnBackground(selectIds.size)
            adapter.notifyItemChanged(position)
        }

    }


    private fun setDeleteSelectBtnBackground(index: Int) {
        if (index != 0) {
            tvDeleteSelect.text = "删除（$index）"
            tvDeleteSelect.setTextColor(resources.getColor(R.color.redFF4A5C))
            tvDeleteSelect.isEnabled = true
        } else {
            tvDeleteSelect.text = "删除"
            tvDeleteSelect.setTextColor(resources.getColor(R.color.default_gray_text_color))
            tvDeleteSelect.isEnabled = false
        }
    }


    override fun updateLikeStatus(isLike: Boolean, position: Int) {
        if (isLike) {
            adapter.data[position].dataBean.like_status = 1
            adapter.data[position].dataBean.like_num = adapter.data[position].dataBean.like_num + 1
        } else {
            adapter.data[position].dataBean.like_status = 0
            adapter.data[position].dataBean.like_num = adapter.data[position].dataBean.like_num - 1
        }

        adapter.notifyItemChangedAnimal(position)
    }


    private fun startDetailsActivity(context: Context, url: String) {
        val intent = Intent(context, ServiceWebviewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("other", true)
        intent.putExtra("share_title", "")
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

            val bean = (adapter as MyCollectionAdapter).data[position].dataBean
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

    override fun onDestroy() {
        super.onDestroy()
        audioController?.release()
    }

}