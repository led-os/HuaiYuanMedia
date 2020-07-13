package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.varunest.sparkbutton.SparkButton;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import java.util.List;
import java.util.Locale;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;


public class GuanZhuRvAdapter extends BaseQuickAdapter<NewsBean, GuanZhuViewHolder> {

    private boolean showAnimal = false;
    private int refreshPosition = -1;
    private IOnCancelClickListener listener;

    public GuanZhuRvAdapter(List<NewsBean> data) {
        super(R.layout.item_guan_zhu_layout, data);
    }

    public void notifyItemChangedAnimal(int position) {
        showAnimal = true;
        refreshPosition = position;
        notifyItemChanged(position);
    }

    @Override
    protected void convert(@NonNull GuanZhuViewHolder helper, NewsBean bean) {
        helper.addOnClickListener(R.id.sparkButton, R.id.tvGoodNum);

        helper.setText(R.id.tvTitle, bean.getName());
        helper.setText(R.id.tvTime, bean.getBegintime());
        helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
        helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());
        helper.setText(R.id.tvModuleSecond, bean.getModule_second());

        TextView cancelBtn = helper.easyPopup.getContentView().findViewById(R.id.cancelBtn);

        cancelBtn.setText("取消关注："+bean.getModule_second());
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onCancelClick(bean.getAdmin_id(),helper.getLayoutPosition());
                }
                helper.easyPopup.dismiss();
            }
        });


        helper.getView(R.id.tvModuleSecond).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.easyPopup.showAtAnchorView(v, YGravity.CENTER, XGravity.RIGHT);
            }
        });



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
            helper.setGone(R.id.tvTime, true);
            helper.setGone(R.id.tvGoodNum, true);
            helper.setGone(R.id.sparkButton, true);

            helper.setText(R.id.tvVideoTime, formatTime(Double.valueOf(bean.getTime()).longValue()));

            // 加载网络图片
            Glide.with(mContext).load(bean.getImage()).into((ImageView) helper.getView(R.id.ivVideoBg));

            helper.addOnClickListener(R.id.ivStartPlayer);

        } else {
            helper.setGone(R.id.videoLayout, false);

            if (!StringUtils.isEmpty(bean.getImage())) {
                //一张图片
                helper.getView(R.id.tvTime).setVisibility(View.GONE);
                helper.getView(R.id.tvGoodNum).setVisibility(View.GONE);
                helper.getView(R.id.sparkButton).setVisibility(View.GONE);
                helper.getView(R.id.llMultiImage).setVisibility(View.GONE);
                helper.getView(R.id.ivImageOne).setVisibility(View.VISIBLE);

                GlideManager.loadRoundImg(bean.getImage(), helper.getView(R.id.ivImageOne));

            } else {
                if (bean.getImages() == null || bean.getImages().size() == 0) {
                    //没有图片
                    helper.getView(R.id.tvTime).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tvGoodNum).setVisibility(View.VISIBLE);
                    helper.getView(R.id.sparkButton).setVisibility(View.VISIBLE);
                    helper.getView(R.id.llMultiImage).setVisibility(View.GONE);
                    helper.getView(R.id.ivImageOne).setVisibility(View.GONE);
                } else {
                    if (bean.getImages().size() < 3) {
                        //一张图片
                        helper.getView(R.id.tvTime).setVisibility(View.GONE);
                        helper.getView(R.id.tvGoodNum).setVisibility(View.GONE);
                        helper.getView(R.id.sparkButton).setVisibility(View.GONE);
                        helper.getView(R.id.llMultiImage).setVisibility(View.GONE);
                        helper.getView(R.id.ivImageOne).setVisibility(View.VISIBLE);

                        GlideManager.loadRoundImg(bean.getImages().get(0), helper.getView(R.id.ivImageOne));
                    } else {
                        //多张图片
                        helper.getView(R.id.tvTime).setVisibility(View.VISIBLE);
                        helper.getView(R.id.tvGoodNum).setVisibility(View.VISIBLE);
                        helper.getView(R.id.sparkButton).setVisibility(View.VISIBLE);
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



    public interface IOnCancelClickListener{
        void onCancelClick(int id,int position);
    }

    public void setIOnCancelClickListener(IOnCancelClickListener listener){
        this.listener = listener;
    }

    private String formatTime(Long position) {
        int totalSeconds = (int) (position + 0.5);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

}
