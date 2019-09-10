package cn.tklvyou.huaiyuanmedia.ui.mine.exchange

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.ExchangeModel
import cn.tklvyou.huaiyuanmedia.ui.adapter.ExchangeRecordAdapter
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.layout_recycler.*
import kotlinx.android.synthetic.main.layout_refresh_recycler.*

/**
 *@description :
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2019年08月02日11:04
 * @Email: 971613168@qq.com
 */
class MyExchangeRecordActivity : BaseHttpRecyclerActivity<ExchangePresenter, ExchangeModel, BaseViewHolder, ExchangeRecordAdapter>(), ExchangeRecordContract.View {

    override fun initPresenter(): ExchangePresenter {
        return ExchangePresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.layout_refresh_recycler
    }

    override fun initView(savedInstanceState: Bundle?) {
        setTitle("兑换记录")
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        initSmartRefreshLayout(smartLayoutRoot)
        initRecyclerView(recyclerViewRoot)
        recyclerViewRoot.layoutManager = LinearLayoutManager(this)
        smartLayoutRoot.autoRefresh()
    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getExchangePageList(1)
    }

    override fun setList(list: MutableList<ExchangeModel>?) {
        setList(object : AdapterCallBack<ExchangeRecordAdapter> {

            override fun createAdapter(): ExchangeRecordAdapter {
                return ExchangeRecordAdapter(list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }


    override fun setExchangeList(page: Int, pageModel: BasePageModel<ExchangeModel>?) {
        if (pageModel != null) {
            onLoadSucceed(page, pageModel.data)
        } else {
            onLoadFailed(page, null)
        }
    }



    override fun getListAsync(page: Int) {
        mPresenter.getExchangePageList(page)
    }


    override fun receiveGoodsSuccess(position: Int) {
        adapter.data[position].status = "normal"
        adapter.notifyItemChanged(position)
    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        super.onItemChildClick(adapter, view, position)
        when (view.id) {
            R.id.tvWaitReceive -> {
                //todo 待领取逻辑
            }

            R.id.registerLayout -> {
                val dialog = CommonDialog(this)
                dialog.setTitle("温馨提示")
                dialog.setMessage("是否已在现场领取新人大礼包？")
                dialog.setYesOnclickListener("确认"){
                    mPresenter.receiveGoods((adapter as ExchangeRecordAdapter).data[position].id,position)
                    dialog.dismiss()
                }
                dialog.show()
            }

            else -> {
            }
        }
    }

}