package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.varunest.sparkbutton.SparkButton;

import java.util.List;

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


        if (!StringUtils.isEmpty(bean.getImage())) {
            //一张图片
            helper.getView(R.id.llMultiImage).setVisibility(View.GONE);
            helper.getView(R.id.ivImageOne).setVisibility(View.VISIBLE);

            GlideManager.loadRoundImg(bean.getImage(), helper.getView(R.id.ivImageOne));

        } else {
            if (bean.getImages() == null || bean.getImages().size() == 0) {
                //没有图片
                helper.getView(R.id.llMultiImage).setVisibility(View.GONE);
                helper.getView(R.id.ivImageOne).setVisibility(View.GONE);
            } else {
                if (bean.getImages().size() < 3) {
                    //一张图片
                    helper.getView(R.id.llMultiImage).setVisibility(View.GONE);
                    helper.getView(R.id.ivImageOne).setVisibility(View.VISIBLE);

                    GlideManager.loadRoundImg(bean.getImages().get(0), helper.getView(R.id.ivImageOne));
                } else {
                    //多张图片
                    helper.getView(R.id.ivImageOne).setVisibility(View.GONE);
                    helper.getView(R.id.llMultiImage).setVisibility(View.VISIBLE);

                    GlideManager.loadRoundImg(bean.getImages().get(0), helper.getView(R.id.ivImageFirst));
                    GlideManager.loadRoundImg(bean.getImages().get(1), helper.getView(R.id.ivImageSecond));
                    GlideManager.loadRoundImg(bean.getImages().get(2), helper.getView(R.id.ivImageThree));
                }
            }
        }
    }

}
