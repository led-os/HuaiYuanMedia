package cn.tklvyou.huaiyuanmedia.ui.camera.today_hot

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseHttpRecyclerActivity
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack
import cn.tklvyou.huaiyuanmedia.model.BasePageModel
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.account.LoginActivity
import cn.tklvyou.huaiyuanmedia.ui.adapter.TodayHotRvAdapter
import cn.tklvyou.huaiyuanmedia.ui.camera.CameraContract
import cn.tklvyou.huaiyuanmedia.ui.camera.TakePhotoActivity
import cn.tklvyou.huaiyuanmedia.ui.home.news_detail.NewsDetailActivity
import cn.tklvyou.huaiyuanmedia.ui.home.publish_news.PublishNewsActivity
import cn.tklvyou.huaiyuanmedia.utils.GridDividerItemDecoration
import com.adorkable.iosdialog.BottomSheetDialog
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_today_hot.*
import java.io.Serializable

class TodayHotActivity : BaseHttpRecyclerActivity<TodayHotPresenter, NewsBean, BaseViewHolder, TodayHotRvAdapter>(), CameraContract.TodayHotView {

    override fun initPresenter(): TodayHotPresenter {
        return TodayHotPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_today_hot
    }

    override fun getLoadingView(): View {
        return mRecyclerView
    }

    override fun initView(savedInstanceState: Bundle?) {
        setTitle("今日热门")
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        setPositiveCustom(R.layout.custom_camera_right_title_bar)
        commonTitleBar.rightCustomView.setOnClickListener {
            if(SPUtils.getInstance().getString("token","").isNotEmpty()) {
                RxPermissions(this)
                        .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                        .subscribe { granted ->
                            if (granted) {
                                // Always true pre-M
                                BottomSheetDialog(this)
                                        .init()
                                        .setCancelable(true)    //设置手机返回按钮是否有效
                                        .setCanceledOnTouchOutside(true)  //设置 点击空白处是否取消 Dialog 显示
                                        //如果条目样式一样，可以直接设置默认样式
                                        .setDefaultItemStyle(BottomSheetDialog.SheetItemTextStyle("#000000", 16))
                                        .setBottomBtnStyle(BottomSheetDialog.SheetItemTextStyle("#ff0000", 18))
                                        .addSheetItem("拍摄") { which ->
                                            val intent = Intent(this, TakePhotoActivity::class.java)
                                            intent.putExtra("page", "随手拍")
                                            startActivity(intent)
                                        }
                                        .addSheetItem("从手机相册选择") { which ->
                                            // 进入相册 以下是例子：不需要的api可以不写
                                            PictureSelector.create(this)
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
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        initSmartRefreshLayout(mRefreshLayout)
        initRecyclerView(mRecyclerView)

        mRecyclerView.layoutManager = GridLayoutManager(this, 2)
        mRecyclerView.addItemDecoration(GridDividerItemDecoration(30, resources.getColor(R.color.common_bg)))

        mPresenter.getLifeHotList(1)
    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getLifeHotList(1)
    }

    override fun setLifeHotList(p: Int, model: BasePageModel<NewsBean>?) {
        if (model != null) {
            onLoadSucceed(p, model.data)
        } else {
            onLoadFailed(p, null)
        }
    }


    override fun getListAsync(page: Int) {
        mPresenter.getLifeHotList(page)
    }

    override fun setList(list: MutableList<NewsBean>?) {
        setList(object : AdapterCallBack<TodayHotRvAdapter> {

            override fun createAdapter(): TodayHotRvAdapter {
                return TodayHotRvAdapter(R.layout.item_today_hot_grid_rv_view, list)
            }

            override fun refreshAdapter() {
                adapter.setNewData(list)
            }
        })
    }



    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemClick(adapter, view, position)

        val bean = (adapter as TodayHotRvAdapter).data[position]
        val id = bean.id
        val type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
        startNewsDetailActivity(this, type, id, position)

    }



    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        super.onItemChildClick(adapter, view, position)
        if (view!!.id == R.id.sparkButton) {
            val bean = (adapter as TodayHotRvAdapter).data[position] as NewsBean
            if (bean.like_status == 1) {
                mPresenter.cancelLikeNews(bean.id, position)
            } else {
                mPresenter.addLikeNews(bean.id, position)
            }
        }
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
        startActivityForResult(intent, 0)
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
                    val intent = Intent(this, PublishNewsActivity::class.java)
                    intent.putExtra("page", "随手拍")
                    intent.putExtra("isVideo", false)
                    intent.putExtra("data", selectList as Serializable)
                    startActivity(intent)
                }

                0 ->{
                    val position = data.getIntExtra("position", 0)
                    val seeNum = data.getIntExtra("seeNum", 0)
                    val zanNum = data.getIntExtra("zanNum", 0)
                    val commenNum = data.getIntExtra("commentNum", 0)
                    val like_status = data.getIntExtra("like_status", 0)

                    val bean = (adapter as TodayHotRvAdapter).data[position]
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
