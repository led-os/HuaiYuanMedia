package cn.tklvyou.huaiyuanmedia.ui.service

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.ServiceModel
import cn.tklvyou.huaiyuanmedia.ui.adapter.ServiceRvAdapter
import cn.tklvyou.huaiyuanmedia.utils.JSON
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import com.blankj.utilcode.util.ResourceUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_service.*

class ServiceFragment : BaseRecyclerFragment<NullPresenter, ServiceModel, BaseViewHolder, ServiceRvAdapter>() {

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_service
    }

    override fun getLoadingView(): View {
        return serviceRecyclerView
    }

    override fun initView() {
        serviceTitleBar.setBackgroundResource(R.drawable.shape_gradient_common_titlebar)

        initRecyclerView(serviceRecyclerView)
        serviceRecyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.VERTICAL, 30, Color.WHITE))

//        {
//            "imagename": "big_shout_loud",
//            "title": "我要爆料",
//            "subTitle": "将身边事爆给我们",
//            "url": "http://m.api.cms.anhuinews.com/v2/report?app_version=1.0.1&clientid=1&device_id=F7E8907D-8187-4091-8B26-4E71BF19CA67&ip=192.168.0.102&modules=brand%3A2&sign=4c877149e9a524c16f7e994973582078&siteid=10015&system_name=1.0.1&time=1564402012000&type=ios"
//        },

        val json = ResourceUtils.readAssets2String("servicelist.json")
        val data = JSON.parseArray(json, ServiceModel::class.java)

        onLoadSucceed(1, data)
    }

    override fun lazyData() {

    }


    override fun getListAsync(page: Int) {

    }


    override fun setList(list: MutableList<ServiceModel>?) {
        setList(object : AdapterCallBack<ServiceRvAdapter> {

            override fun createAdapter(): ServiceRvAdapter {
                val adapter = ServiceRvAdapter(R.layout.item_main_service_view, list)
                val headerView = ImageView(context)
                headerView.setBackgroundResource(R.mipmap.icon_service_point)
                adapter.addHeaderView(headerView)
                headerView.setOnClickListener {
                    startActivity(Intent(context,PointActivity::class.java))
                }
                return adapter
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
    }


}