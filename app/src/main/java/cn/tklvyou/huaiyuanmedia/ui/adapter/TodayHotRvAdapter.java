package cn.tklvyou.huaiyuanmedia.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.varunest.sparkbutton.SparkButton;

import java.util.List;
import java.util.Locale;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;


public class TodayHotRvAdapter extends BaseQuickAdapter<NewsBean, BaseViewHolder> {

    private boolean showAnimal = false;
    private int refreshPosition = -1;

    public TodayHotRvAdapter(int layoutResId, @Nullable List<NewsBean> data) {
        super(layoutResId, data);
    }


    public void notifyItemChangedAnimal(int position) {
        showAnimal = true;
        refreshPosition = position;
        notifyItemChanged(position);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NewsBean item) {
        GlideManager.loadTopRoundImg(item.getImage(), helper.getView(R.id.ivImage));
        GlideManager.loadCircleImg(item.getAvatar(), helper.getView(R.id.ivAvatar));
        helper.setText(R.id.tvTitle, item.getName());
        helper.setText(R.id.tvNickname, item.getNickname());

        if (StringUtils.isEmpty(item.getTime())) {
            helper.setVisible(R.id.tvVideoTime, false);
        } else {
            helper.setVisible(R.id.tvVideoTime, true);
            helper.setText(R.id.tvVideoTime, formatTime(Double.valueOf(item.getTime()).longValue()));
        }

        helper.addOnClickListener(R.id.sparkButton);

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


    private String formatTime(Long position) {
        int totalSeconds = (int) (position + 0.5);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

}