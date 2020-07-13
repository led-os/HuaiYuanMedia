package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.varunest.sparkbutton.SparkButton;

import java.util.List;
import java.util.Locale;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;
import cn.tklvyou.huaiyuanmedia.widget.SwipeRevealLayout;

public class MySearchRvAdapter extends BaseQuickAdapter<NewsBean, BaseViewHolder> {

    private boolean showAnimal = false;
    private int refreshPosition = -1;

    public MySearchRvAdapter(List<NewsBean> data) {
        super(R.layout.item_news_news_layout, data);
    }

    public void notifyItemChangedAnimal(int position) {
        showAnimal = true;
        refreshPosition = position;
        notifyItemChanged(position);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, NewsBean bean) {
        helper.addOnClickListener(R.id.sparkButton, R.id.tvGoodNum);

        helper.setText(R.id.tvTitle, bean.getName());
        helper.setText(R.id.tvTime, bean.getBegintime());
        helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
        helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());

        SparkButton sparkButton = helper.getView(R.id.sparkButton);
        if (bean.getLike_status() == 1) {
            sparkButton.setChecked(true);
            if (showAnimal && helper.getLayoutPosition() == refreshPosition) {
                refreshPosition = -1;
                showAnimal = false;
                sparkButton.playAnimation();
            }
        } else {
            sparkButton.setChecked(false);
        }


        if (!StringUtils.isEmpty(bean.getVideo())) {
            helper.setGone(R.id.videoLayout, true);
            helper.setGone(R.id.llMultiImage, false);
            helper.setGone(R.id.ivImageOne, false);
            helper.setText(R.id.tvVideoTime, formatTime(Double.valueOf(bean.getTime()).longValue()));

            // 加载网络图片
            Glide.with(mContext).load(bean.getImage()).into((ImageView) helper.getView(R.id.ivVideoBg));

            helper.addOnClickListener(R.id.ivStartPlayer);
        } else {
            helper.setGone(R.id.videoLayout, false);

            if (!StringUtils.isEmpty(bean.getImage())) {
                //一张图片
                helper.setGone(R.id.llMultiImage, false);
                helper.setGone(R.id.ivImageOne, true);

                GlideManager.loadRoundImg(bean.getImage(), helper.getView(R.id.ivImageOne));

            } else {

                if (bean.getImages() == null || bean.getImages().size() == 0) {
                    //没有图片
                    helper.setGone(R.id.llMultiImage, false);
                    helper.setGone(R.id.ivImageOne, false);

                } else {
                    if (bean.getImages().size() < 3) {
                        //一张图片
                        helper.setGone(R.id.llMultiImage, false);
                        helper.setGone(R.id.ivImageOne, true);

                        GlideManager.loadRoundImg(bean.getImages().get(0), helper.getView(R.id.ivImageOne));
                    } else {
                        //多张图片
                        helper.setGone(R.id.llMultiImage, true);
                        helper.setGone(R.id.ivImageOne, false);

                        GlideManager.loadRoundImg(bean.getImages().get(0), helper.getView(R.id.ivImageFirst));
                        GlideManager.loadRoundImg(bean.getImages().get(1), helper.getView(R.id.ivImageSecond));
                        GlideManager.loadRoundImg(bean.getImages().get(2), helper.getView(R.id.ivImageThree));
                    }
                }
            }
        }
    }


    private String formatTime(Long position) {
        int totalSeconds = (int) (position + 0.5);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

}
