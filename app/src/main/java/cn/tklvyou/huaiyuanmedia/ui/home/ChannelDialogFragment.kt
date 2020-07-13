package cn.tklvyou.huaiyuanmedia.ui.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.chad.library.adapter.base.BaseViewHolder
import java.util.ArrayList

import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseDialogFragment
import cn.tklvyou.huaiyuanmedia.model.Channel
import cn.tklvyou.huaiyuanmedia.model.ChannelModel
import cn.tklvyou.huaiyuanmedia.ui.adapter.ChannelAdapter
import cn.tklvyou.huaiyuanmedia.ui.listener.ItemDragHelperCallBack
import cn.tklvyou.huaiyuanmedia.ui.listener.OnChannelDragListener
import cn.tklvyou.huaiyuanmedia.ui.listener.OnChannelListener

import cn.tklvyou.huaiyuanmedia.model.Channel.TYPE_MY_CHANNEL
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_channel.*


class ChannelDialogFragment : BaseDialogFragment<HomeChannelPresenter>(), OnChannelDragListener, HomeContract.ChannelView {

    private val mDatas = ArrayList<Channel>()
    private var mAdapter: ChannelAdapter? = null

    private var mHelper: ItemTouchHelper? = null

    private var mOnChannelListener: OnChannelListener? = null


    private var mOnDismissListener: DialogInterface.OnDismissListener? = null


    override fun initPresenter(): HomeChannelPresenter {
        return HomeChannelPresenter()
    }


    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_channel
    }

    fun setOnChannelListener(onChannelListener: OnChannelListener) {
        mOnChannelListener = onChannelListener
    }

    override fun getLoadingView(): View {
        return mRecyclerView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomAppTheme)//android.R.style.Theme_Black_NoTitleBar_Fullscreen
    }

    override fun initView() {
        val dialog = dialog
        if (dialog != null) {
            //添加动画
            dialog.window!!.setWindowAnimations(R.style.dialogSlideAnim)
        }

        mTitleBar.setBackgroundResource(R.drawable.shape_gradient_common_titlebar)
        mTitleBar.setNavigationListener {
            if (mAdapter == null) {
                showSuccess("")
                ToastUtils.showShort("至少选择一个频道")
                return@setNavigationListener
            }

            if (mAdapter != null && mAdapter!!.myChannel.size > 0) {
                showSuccess("")
                dismiss()
            }else{
                ToastUtils.showShort("至少选择一个频道")
            }

        }

        mPresenter.getTotalChannel()

    }

    override fun lazyData() {

    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getTotalChannel()
    }

    override fun setTotalChannel(model: ChannelModel) {
        val selectChannel = ArrayList<Channel>()
        val unSelectChannel = ArrayList<Channel>()

        if (mOnChannelListener != null) {
            mOnChannelListener!!.initChannelList(model.selected, model.unselected)
        }

        model.selected.forEach {
            selectChannel.add(Channel(it))
        }

        model.unselected.forEach {
            unSelectChannel.add(Channel(it))
        }
        processLogic(selectChannel, unSelectChannel)
    }


    private fun setDataType(datas: List<Channel>, type: Int) {
        for (i in datas.indices) {
            datas[i].setItemType(type)
        }
    }

    private fun processLogic(selectedDatas: ArrayList<Channel>, unselectedDatas: List<Channel>) {
        mDatas.add(Channel(Channel.TYPE_MY, "已选频道"))
        setDataType(selectedDatas, TYPE_MY_CHANNEL)
        setDataType(unselectedDatas, Channel.TYPE_OTHER_CHANNEL)

        mDatas.addAll(selectedDatas)
        mDatas.add(Channel(Channel.TYPE_OTHER, "可选频道"))
        mDatas.addAll(unselectedDatas)


        mAdapter = ChannelAdapter(mDatas)
        val manager = GridLayoutManager(activity, 5)
        mRecyclerView!!.layoutManager = manager
        mRecyclerView!!.adapter = mAdapter
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val itemViewType = mAdapter!!.getItemViewType(position)
                return if (itemViewType == TYPE_MY_CHANNEL || itemViewType == Channel.TYPE_OTHER_CHANNEL) 1 else 5
            }
        }
        val callBack = ItemDragHelperCallBack(this)
        mHelper = ItemTouchHelper(callBack)
        mAdapter!!.setOnChannelDragListener(this)
        //attachRecyclerView
        mHelper!!.attachToRecyclerView(mRecyclerView)
    }

    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener) {
        mOnDismissListener = onDismissListener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        showSuccess("")
        if (mOnDismissListener != null)
            mOnDismissListener!!.onDismiss(dialog)
    }

    override fun onStarDrag(baseViewHolder: BaseViewHolder) {
        //开始拖动
        mHelper!!.startDrag(baseViewHolder)
    }

    override fun onItemMove(starPos: Int, endPos: Int) {
        //        if (starPos < 0||endPos<0) return;
        //我的频道之间移动
        if (mOnChannelListener != null)
            mOnChannelListener!!.onItemMove(starPos - 1, endPos - 1)//去除标题所占的一个index
        onMove(starPos, endPos)
    }

    private fun onMove(starPos: Int, endPos: Int) {
        val startChannel = mDatas[starPos]
        //先删除之前的位置
        mDatas.removeAt(starPos)
        //添加到现在的位置
        mDatas.add(endPos, startChannel)
        mAdapter!!.notifyItemMoved(starPos, endPos)
    }

    override fun onMoveToMyChannel(starPos: Int, endPos: Int) {
        //移动到我的频道
        onMove(starPos, endPos)
        if (mOnChannelListener != null)
            mOnChannelListener!!.onMoveToMyChannel(starPos - 1 - mAdapter!!.myChannelSize, endPos - 1)
    }

    override fun onMoveToOtherChannel(starPos: Int, endPos: Int) {
        //移动到推荐频道
        onMove(starPos, endPos)
        if (mOnChannelListener != null)
            mOnChannelListener!!.onMoveToOtherChannel(starPos - 1, endPos - 2 - mAdapter!!.myChannelSize)
    }

    override fun initChannelList(selectList: MutableList<String>?, unSelectList: MutableList<String>?) {
    }


}