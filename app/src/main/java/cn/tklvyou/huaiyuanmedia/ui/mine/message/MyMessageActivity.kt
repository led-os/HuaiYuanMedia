package cn.tklvyou.huaiyuanmedia.ui.mine.message

/**
 *@description :
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2019年08月01日19:06
 * @Email: 971613168@qq.com
 */


import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.MessageModel
import cn.tklvyou.huaiyuanmedia.ui.adapter.SystemMsgAdapter
import cn.tklvyou.huaiyuanmedia.widget.dailog.CommonDialog
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.layout_recycler.*
import kotlinx.android.synthetic.main.layout_refresh_recycler.*

/**
 * @author :JenkinsZhou
 * @description :积分明细列表
 * @company :途酷科技
 * @date 2019年08月01日17:47
 * @Email: 971613168@qq.com
 */
class MyMessageActivity : BaseHttpRecyclerActivity<MessagePresenter, MessageModel, BaseViewHolder, SystemMsgAdapter>(), MessageContract.View {

    val EXTRA_KEY_MESSAGE_ID = "EXTRA_KEY_MESSAGE_ID"

    override fun initPresenter(): MessagePresenter {
        return MessagePresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.layout_refresh_recycler
    }

    override fun initView(savedInstanceState: Bundle?) {
        initSmartRefreshLayout(smartLayoutRoot)
        initRecyclerView(recyclerViewRoot)

        setTitle("我的消息")
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        setPositiveText("清空")
        setPositiveOnClickListener {
            val dialog = CommonDialog(this)
            dialog.setTitle("温馨提示")
            dialog.setMessage("是否清空所有消息？")
            dialog.setYesOnclickListener("确认") {
                mPresenter.clearMessage()
                dialog.dismiss()
            }
            dialog.show()
        }



        recyclerViewRoot.layoutManager = LinearLayoutManager(this)
        smartLayoutRoot.autoRefresh()
    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getMsgPageList(1)
    }

    override fun setList(list: MutableList<MessageModel>?) {

        setList(object : AdapterCallBack<SystemMsgAdapter> {

            override fun createAdapter(): SystemMsgAdapter {
                return SystemMsgAdapter(list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })

    }

    override fun setMessageList(page: Int, pageModel: BasePageModel<MessageModel>?) {
        if (pageModel != null) {
            onLoadSucceed(page, pageModel.data)
        } else {
            onLoadFailed(page, null)
        }
    }

    override fun getListAsync(page: Int) {
        mPresenter.getMsgPageList(page)
    }

    override fun clearSuccess() {
        if(adapter != null){
            adapter.setNewData(ArrayList<MessageModel>())
        }
    }


}