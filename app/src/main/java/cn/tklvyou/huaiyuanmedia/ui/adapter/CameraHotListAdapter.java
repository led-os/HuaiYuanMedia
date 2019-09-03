package cn.tklvyou.huaiyuanmedia.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cjt2325.cameralibrary.util.LogUtil;
import com.varunest.sparkbutton.SparkButton;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;


/**
 * 生活圈 - 今日热门
 */
public class CameraHotListAdapter extends BaseQuickAdapter<NewsBean, BaseViewHolder> {

    private boolean showAnimal = false;
    private int refreshPosition = -1;

    public CameraHotListAdapter(int layoutResId, @Nullable List<NewsBean> data) {
        super(layoutResId, data);
    }


    public void notifyItemChangedAnimal(int position) {
        showAnimal = true;
        refreshPosition = position;
        notifyItemChanged(position);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NewsBean item) {

        helper.addOnClickListener(R.id.sparkButton);

        GlideManager.loadImg(item.getImage(), helper.getView(R.id.ivImage));
        helper.setText(R.id.tvNickName, item.getNickname());
        SparkButton sparkButton = helper.getView(R.id.sparkButton);
        if (item.getLike_status() == 1) {
            sparkButton.setChecked(true);
            if (showAnimal && helper.getLayoutPosition() == refreshPosition) {
                refreshPosition = -1;
                showAnimal = false;
                sparkButton.playAnimation();
            }
        } else {
            sparkButton.setChecked(false);
        }

    }

}