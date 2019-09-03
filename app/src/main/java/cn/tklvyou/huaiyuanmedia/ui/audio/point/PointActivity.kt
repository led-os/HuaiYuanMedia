package cn.tklvyou.huaiyuanmedia.ui.audio.point

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.helper.GlideManager
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.PointModel
import cn.tklvyou.huaiyuanmedia.model.User
import cn.tklvyou.huaiyuanmedia.ui.adapter.PointRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.audio.point_rule.PointRuleActivity
import cn.tklvyou.huaiyuanmedia.ui.audio.zhuan_pan.ZhuanPanActivity
import cn.tklvyou.huaiyuanmedia.ui.audio.goods_detail.GoodsDetailsActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.point.MyPointDetailActivity
import cn.tklvyou.huaiyuanmedia.utils.GridDividerItemDecoration
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_point.*

class PointActivity : BaseHttpRecyclerActivity<PointPresenter, PointModel, BaseViewHolder, PointRvAdapter>(), PointContract.View {


    override fun initPresenter(): PointPresenter {
        return PointPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_point
    }

    override fun getLoadingView(): View {
        return pointRecyclerView
    }

    override fun initView(savedInstanceState: Bundle?) {
        hideTitleBar()
        pointTitleBar.setBackgroundColor(Color.TRANSPARENT)
        pointTitleBar.setNavigationListener {
            finish()
        }


        initSmartRefreshLayout(pointRefreshLayout)
        initRecyclerView(pointRecyclerView)

        pointRecyclerView.layoutManager = GridLayoutManager(this, 2)
        pointRecyclerView.addItemDecoration(GridDividerItemDecoration(30, resources.getColor(R.color.common_bg)))

        btnLuck.setOnClickListener {
            startActivity(Intent(this, ZhuanPanActivity::class.java))
        }

        btnRule.setOnClickListener {
            startActivity(Intent(this, PointRuleActivity::class.java))
        }

        tvPointScore.setOnClickListener {
            startActivity(Intent(this,MyPointDetailActivity::class.java))
        }

        mPresenter.getGoodsPageList(1)
    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getGoodsPageList(1)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.getUser()
    }

    override fun setUser(bean: User.UserinfoBean) {
        if (bean.avatar.trim().isNotEmpty()) {
            GlideManager.loadCircleImg(bean.avatar, ivAvatar)
        }

        tvNickName.text = bean.nickname
        tvPointScore.text = "积分：${bean.score}"
    }

    override fun setGoods(page: Int, pageModel: BasePageModel<PointModel>?) {
        if (pageModel != null) {
            onLoadSucceed(page, pageModel.data)
        } else {
            onLoadFailed(page, null)
        }
    }

    override fun getListAsync(page: Int) {
        mPresenter.getGoodsPageList(page)
    }

    override fun setList(list: MutableList<PointModel>?) {
        setList(object : AdapterCallBack<PointRvAdapter> {

            override fun createAdapter(): PointRvAdapter {
                return PointRvAdapter(R.layout.item_point_grid_rv_view, list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
        val intent = Intent(this, GoodsDetailsActivity::class.java)
        intent.putExtra("id", (adapter as PointRvAdapter).data[position].id)
        startActivity(intent)
    }


}
