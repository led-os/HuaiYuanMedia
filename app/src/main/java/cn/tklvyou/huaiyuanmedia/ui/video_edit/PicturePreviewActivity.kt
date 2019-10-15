package cn.tklvyou.huaiyuanmedia.ui.video_edit

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.ui.home.publish_news.PublishNewsActivity
import com.blankj.utilcode.util.ImageUtils
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_picture_preview.*
import java.io.Serializable

class PicturePreviewActivity : BaseActivity<NullPresenter>() {

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_picture_preview
    }

    override fun initView(savedInstanceState: Bundle?) {
        baseTitleBar.hideTitleBar()
        mBtnBack.setOnClickListener {
            finish()
        }

        val filePath = intent.getStringExtra("path")
        val page = intent.getStringExtra("page")

        ivImage.setImageBitmap(BitmapFactory.decodeFile(filePath))

        mBtnSubmit.setOnClickListener {

            val intent = Intent(this@PicturePreviewActivity,PublishNewsActivity::class.java)
            intent.putExtra("page",page)
            intent.putExtra("isVideo",false)

            val selectList = ArrayList<LocalMedia>()
            val localMedia = LocalMedia()
            localMedia.setPosition(0)
            localMedia.path = filePath
            selectList.add(localMedia)

            intent.putExtra("data", selectList as Serializable)
            startActivity(intent)
            finish()
        }


    }


}
