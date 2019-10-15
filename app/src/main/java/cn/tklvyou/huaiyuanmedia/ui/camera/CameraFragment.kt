package cn.tklvyou.huaiyuanmedia.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseRecyclerFragment
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.account.LoginActivity
import cn.tklvyou.huaiyuanmedia.ui.adapter.CameraHotListAdapter
import cn.tklvyou.huaiyuanmedia.ui.adapter.ChannelPagerAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.home.publish_news.PublishNewsActivity
import cn.tklvyou.huaiyuanmedia.ui.camera.point.PointActivity
import cn.tklvyou.huaiyuanmedia.ui.camera.history_updates.HistoryUpdatesFragment
import cn.tklvyou.huaiyuanmedia.ui.camera.today_hot.TodayHotActivity
import cn.tklvyou.huaiyuanmedia.ui.video_edit.CameraActivity
import cn.tklvyou.huaiyuanmedia.utils.RecycleViewDivider
import com.adorkable.iosdialog.BottomSheetDialog
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.tbruyelle.rxpermissions2.RxPermissions
import com.trello.rxlifecycle3.components.support.RxFragment
import kotlinx.android.synthetic.main.fragment_camera.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import java.io.Serializable

class CameraFragment : BaseRecyclerFragment<CameraPresenter, NewsBean, BaseViewHolder, CameraHotListAdapter>(), CameraContract.View {

    override fun initPresenter(): CameraPresenter {
        return CameraPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_camera
    }


    private var isRefresh = false
    private var isChoose = false
    private val mFragments = ArrayList<Fragment>()
    private var mTabNameList = ArrayList<String>()
    private lateinit var commonNavigator: CommonNavigator
    private var mChannelPagerAdapter: ChannelPagerAdapter? = null

    override fun getLoadingView(): View {
        return mRecyclerView
    }

    override fun initView() {
        cameraTitleBar.setBackgroundResource(R.drawable.shape_gradient_common_titlebar)
        cameraTitleBar.rightCustomView.setOnClickListener {
            if(SPUtils.getInstance().getString("token","").isNotEmpty()) {
                RxPermissions(this)
                        .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                        .subscribe { granted ->
                            if (granted) {
                                // Always true pre-M
                                BottomSheetDialog(context)
                                        .init()
                                        .setCancelable(true)    //设置手机返回按钮是否有效
                                        .setCanceledOnTouchOutside(true)  //设置 点击空白处是否取消 Dialog 显示
                                        //如果条目样式一样，可以直接设置默认样式
                                        .setDefaultItemStyle(BottomSheetDialog.SheetItemTextStyle("#000000", 16))
                                        .setBottomBtnStyle(BottomSheetDialog.SheetItemTextStyle("#ff0000", 18))
                                        .addSheetItem("拍摄") { which ->
                                            val intent = Intent(context, CameraActivity::class.java)
                                            intent.putExtra("page", "生活圈")
                                            startActivity(intent)
                                            isRefresh = true
                                        }
                                        .addSheetItem("从手机相册选择") { which ->
                                            isChoose = true
                                            // 进入相册 以下是例子：不需要的api可以不写
                                            PictureSelector.create(this@CameraFragment)
                                                    .openGallery(PictureMimeType.ofImage())
                                                    .theme(R.style.picture_default_style)
                                                    .maxSelectNum(9)
                                                    .minSelectNum(1)
                                                    .selectionMode(PictureConfig.MULTIPLE)
                                                    .previewImage(true)
                                                    .isCamera(true)
                                                    .enableCrop(false)
                                                    .compress(true)
                                                    .previewEggs(true)
                                                    .openClickSound(false)
                                                    .forResult(PictureConfig.CHOOSE_REQUEST)
                                        }
                                        .show()
                            } else {
                                ToastUtils.showShort("权限拒绝，无法使用")
                            }
                        }
            }else{
                ToastUtils.showShort("请登录后操作")
                startActivity(Intent(context, LoginActivity::class.java))
            }


        }

        headerView.setOnClickListener {
            if(SPUtils.getInstance().getString("token","").isNotEmpty()) {
                startActivity(Intent(context, PointActivity::class.java))
            }else{
                ToastUtils.showShort("请登录后操作")
                startActivity(Intent(context, LoginActivity::class.java))
            }
        }

        mTabNameList.add("最新动态")
        mTabNameList.add("好友动态")
        mFragments.add(getFragmentByType(true))
        mFragments.add(getFragmentByType(false))
        initMagicIndicator()
        mChannelPagerAdapter = ChannelPagerAdapter(mFragments, childFragmentManager)
        mViewPager.adapter = mChannelPagerAdapter
        mViewPager.offscreenPageLimit = mTabNameList.size
        commonNavigator.notifyDataSetChanged()

        mSmartRefreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                isRefresh = true
                mPresenter.getLifeHotList(1)
            }

        })
        initRecyclerView(mRecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
        mRecyclerView.addItemDecoration(RecycleViewDivider(context, LinearLayout.HORIZONTAL, ConvertUtils.dp2px(10f), Color.WHITE, true))

        btnTodayHot.setOnClickListener {
            startActivity(Intent(context, TodayHotActivity::class.java))
        }



        mPresenter.getLifeHotList(1)
    }

    override fun lazyData() {

    }

    override fun onUserVisible() {
        super.onUserVisible()
        if (isChoose) {
            isChoose = false
            isRefresh = true
        } else {
            if (isRefresh) {
                isRefresh = false
                mSmartRefreshLayout.autoRefresh()
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && !isFirstResume) {
            mSmartRefreshLayout.autoRefresh()
        }
    }


    private fun initMagicIndicator() {
        commonNavigator = CommonNavigator(context)
        commonNavigator.isSkimOver = true
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return mTabNameList.size
            }


            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = ColorTransitionPagerTitleView(context)
                simplePagerTitleView.normalColor = resources.getColor(R.color.default_black_text_color)
                simplePagerTitleView.selectedColor = resources.getColor(R.color.colorAccent)
                simplePagerTitleView.text = mTabNameList[index]
                simplePagerTitleView.textSize = 15f
                simplePagerTitleView.setOnClickListener {
                    mViewPager.currentItem = index
                }

                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator? {
                val linePagerIndicator = LinePagerIndicator(context)
                linePagerIndicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                linePagerIndicator.lineHeight = UIUtil.dip2px(context, 3.0).toFloat()
                linePagerIndicator.setColors(resources.getColor(R.color.colorAccent))
                return linePagerIndicator
            }
        }
        commonNavigator.isAdjustMode = true
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, mViewPager)
    }


    override fun onRetry() {
        super.onRetry()
        mPresenter.getLifeHotList(1)
    }


    override fun getListAsync(page: Int) {

    }

    override fun setLifeHotList(p: Int, model: BasePageModel<NewsBean>?) {
        mSmartRefreshLayout.finishRefresh()

        if (model != null) {
            onLoadSucceed(p, model.data)
        } else {
            onLoadFailed(p, null)
        }

        if (isRefresh) {
            isRefresh = false
            (mFragments[mViewPager.currentItem] as HistoryUpdatesFragment).onRetry()
        }

    }

    override fun setList(list: MutableList<NewsBean>?) {
        setList(object : AdapterCallBack<CameraHotListAdapter> {

            override fun createAdapter(): CameraHotListAdapter {
                return CameraHotListAdapter(R.layout.item_camera_today_hot, list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })

    }

    override fun deleteSuccess(position: Int) {
        adapter.remove(position)
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)

        val bean = (adapter as CameraHotListAdapter).data[position]
        val id = bean.id
        val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
        startNewsDetailActivity(mActivity!!, type, id, position)

    }

    override fun updateLikeStatus(isLike: Boolean, position: Int) {
        if (isLike) {
            adapter.data[position].like_status = 1
        } else {
            adapter.data[position].like_status = 0
        }

        adapter.notifyItemChangedAnimal(position)

    }


    private fun startNewsDetailActivity(context: Context, type: String, id: Int, position: Int) {
        val intent = Intent(context, NewsDetailActivity::class.java)
        intent.putExtra(NewsDetailActivity.INTENT_ID, id)
        intent.putExtra(NewsDetailActivity.INTENT_TYPE, type)
        intent.putExtra(NewsDetailActivity.POSITION, position)
        startActivityForResult(intent, 10)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        if (view!!.id == R.id.sparkButton) {
            val bean = (adapter as CameraHotListAdapter).data[position] as NewsBean
            if (bean.like_status == 1) {
                mPresenter.cancelLikeNews(bean.id, position)
            } else {
                mPresenter.addLikeNews(bean.id, position)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {

            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    val intent = Intent(context, PublishNewsActivity::class.java)
                    intent.putExtra("page", "随手拍")
                    intent.putExtra("isVideo", false)
                    intent.putExtra("data", selectList as Serializable)
                    startActivity(intent)
                }

                10 -> {
                    val position = data.getIntExtra("position", 0)
                    val seeNum = data.getIntExtra("seeNum", 0)
                    val zanNum = data.getIntExtra("zanNum", 0)
                    val commenNum = data.getIntExtra("commentNum", 0)
                    val like_status = data.getIntExtra("like_status", 0)

                    if (adapter != null) {
                        val bean = (adapter as CameraHotListAdapter).data[position] as NewsBean
                        bean.comment_num = commenNum
                        bean.like_num = zanNum
                        bean.visit_num = seeNum
                        bean.like_status = like_status
                        adapter.notifyItemChanged(position)
                    }
                }

            }

        }

    }


    private fun getFragmentByType(isMine: Boolean): RxFragment {
        val newsFragment = HistoryUpdatesFragment()
        val bundle = Bundle()
        bundle.putBoolean("isMine", isMine)
        newsFragment.arguments = bundle
        return newsFragment
    }


}