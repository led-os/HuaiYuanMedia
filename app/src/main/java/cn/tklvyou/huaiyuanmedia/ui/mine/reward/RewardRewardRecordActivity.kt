package cn.tklvyou.huaiyuanmedia.ui.mine.reward

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.RewardModel
import cn.tklvyou.huaiyuanmedia.ui.adapter.RewardRecordAdapter
import cn.tklvyou.huaiyuanmedia.ui.camera.zhuan_pan.FillAddressActivity
import cn.tklvyou.huaiyuanmedia.ui.mine.reward.RewardConstant.*
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import cn.tklvyou.huaiyuanmedia.utils.SizeUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_ju_zheng_details.*

/**
 *@description :
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2020年10月21日16:18
 * @Email: 971613168@qq.com
 */
class RewardRewardRecordActivity : BaseHttpRecyclerActivity<RewardPresenter, RewardModel, BaseViewHolder, RewardRecordAdapter>(), RewardContract.View {
    override fun initPresenter(): RewardPresenter {
        return RewardPresenter()
    }

    override fun setList(list: MutableList<RewardModel>?) {
        setList(object : AdapterCallBack<RewardRecordAdapter> {

            override fun createAdapter(): RewardRecordAdapter {
                return RewardRecordAdapter(list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_ju_zheng_details
    }

    override fun getListAsync(page: Int) {
        mPresenter.getRewardListPageList(page)
    }

    override fun initView(savedInstanceState: Bundle?) {
        setTitle("中奖记录")
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        initSmartRefreshLayout(mSmartRefreshLayout)
        initRecyclerView(mRecyclerView)
        mPresenter.getRewardListPageList(1)
        val dividerHeight = SizeUtil.dp2px(20f).toInt()
        mRecyclerView.addItemDecoration(RecycleViewDivider(this, LinearLayout.VERTICAL, dividerHeight, ContextCompat.getColor(mContext, R.color.background_color_gray)))
    }

    override fun setRewardList(page: Int, pageModel: BasePageModel<RewardModel>?) {
        if (pageModel != null) {
            onLoadSucceed(page, pageModel.data)
        } else {
            onLoadFailed(page, null)
        }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        var model = adapter.data[position]
        if (model.type == REWARD_TYPE_GOODS) {
            val intent = Intent(mContext, FillAddressActivity::class.java)
            intent.putExtra(EXTRA_RECORD_ID, model.id)
            if (model.name == null) {
                intent.putExtra(EXTRA_GOODS_NAME, "")
            } else {
                intent.putExtra(EXTRA_GOODS_NAME, model.name)
            }

            startActivity(intent)
        }
    }
}