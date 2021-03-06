package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.varunest.sparkbutton.SparkButton;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;
import cn.tklvyou.huaiyuanmedia.ui.home.ImagePagerActivity;
import cn.tklvyou.huaiyuanmedia.ui.video_player.VodActivity;
import cn.tklvyou.huaiyuanmedia.utils.UrlUtils;
import cn.tklvyou.huaiyuanmedia.widget.ExpandTextView;
import cn.tklvyou.huaiyuanmedia.widget.MultiImageView;
import cn.tklvyou.huaiyuanmedia.widget.SwipeRevealLayout;

/**
 * Created by yiwei on 16/5/17.
 */
public class WxCircleEditAdapter extends BaseQuickAdapter<NewsBean, BaseViewHolder> {

    private boolean isEdit = false;

    private boolean showAnimal = false;
    private int refreshPosition = -1;
    private boolean enableDelete = false;


    public WxCircleEditAdapter(int layoutResId, @Nullable List<NewsBean> data) {
        super(layoutResId, data);
    }

    public void notifyItemChangedAnimal(int position) {
        showAnimal = true;
        refreshPosition = position;
        notifyItemChanged(position);
    }

    public void setEnableDelete() {
        this.enableDelete = true;
    }


    public void setEditModel(boolean isEdit) {
        this.isEdit = isEdit;
        for (int i = 0; i < getData().size(); i++) {
            getData().get(i).setSelect(false);
        }
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NewsBean item) {

        helper.addOnClickListener(R.id.check_box, R.id.itemLayout);
        SwipeRevealLayout mSwipeLayout = helper.getView(R.id.mSwipeLayout);
        if (isEdit) {
            mSwipeLayout.open(true);
            ImageView mCheckBox = helper.getView(R.id.check_box);
            if (item.isSelect()) {
                mCheckBox.setImageResource(R.mipmap.icon_selected);
            } else {
                mCheckBox.setImageResource(R.mipmap.icon_normal);
            }

        } else {
            mSwipeLayout.close(true);
        }

        if (enableDelete) {
            helper.setVisible(R.id.deleteBtn, true);
        } else {
            helper.setVisible(R.id.deleteBtn, false);
        }

        helper.addOnClickListener(R.id.deleteBtn, R.id.sparkButton, R.id.tvGoodNum);
        helper.addOnClickListener(R.id.contentTv, R.id.contentText, R.id.textPlus);
        helper.setText(R.id.nameTv, item.getNickname());
        helper.setText(R.id.timeTv, item.getBegintime());

        helper.setText(R.id.tvCommentNum, "" + item.getComment_num());
        helper.setText(R.id.tvGoodNum, "" + item.getLike_num());

        SparkButton sparkButton = helper.getView(R.id.sparkButton);
        if (item.getLike_status() == 1) {
            sparkButton.setChecked(true);
            if (showAnimal && helper.getLayoutPosition() == refreshPosition) {
                refreshPosition = -1;
                showAnimal = false;
                sparkButton.playAnimation();
            }
        } else {
            refreshPosition = -1;
            showAnimal = false;
            sparkButton.setChecked(false);
        }


        if (!StringUtils.isEmpty(item.getAvatar().trim())) {
            GlideManager.loadRoundImg(item.getAvatar(), helper.getView(R.id.headIv), 5f);
        } else {
            GlideManager.loadRoundImg(R.mipmap.default_avatar, helper.getView(R.id.headIv), 5f);
        }

        ExpandTextView expandTextView = helper.getView(R.id.contentTv);
        if (!TextUtils.isEmpty(item.getName())) {
            expandTextView.setExpand(item.isExpand());
            expandTextView.setExpandStatusListener(new ExpandTextView.ExpandStatusListener() {
                @Override
                public void statusChange(boolean isExpand) {
                    item.setExpand(isExpand);
                }
            });

            expandTextView.setText(UrlUtils.formatUrlString(item.getName()));
        }
        expandTextView.setVisibility(TextUtils.isEmpty(item.getName()) ? View.GONE : View.VISIBLE);

        if (item.getImages() != null && item.getImages().size() > 0) {
            //上传的是图片
            ImageView ivVideo = helper.getView(R.id.ivVideo);
            FrameLayout llVideo = helper.getView(R.id.llVideo);
            llVideo.setVisibility(View.GONE);


            MultiImageView multiImageView = helper.getView(R.id.multiImagView);
            multiImageView.setVisibility(View.VISIBLE);
            multiImageView.setList(item.getImages());
            multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (isEdit) {
                        helper.getView(R.id.itemLayout).performClick();
                    } else {
                        //imagesize是作为loading时的图片size
                        ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                        ImagePagerActivity.startImagePagerActivity(mContext, item.getImages(), position, imageSize);
                    }
                }
            });

        } else {
            //上传的是视频

            MultiImageView multiImageView = helper.getView(R.id.multiImagView);
            multiImageView.setVisibility(View.GONE);

            FrameLayout llVideo = helper.getView(R.id.llVideo);
            if (!StringUtils.isEmpty(item.getVideo())) {
                llVideo.setVisibility(View.VISIBLE);
                ImageView ivVideo = helper.getView(R.id.ivVideo);
                ivVideo.setBackgroundColor(Color.parseColor("#abb1b6"));
                ivVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isEdit) {
                            helper.getView(R.id.itemLayout).performClick();
                        } else {
                            Intent intent = new Intent(mContext, VodActivity.class);
                            intent.putExtra("videoPath", item.getVideo());
                            mContext.startActivity(intent);
                        }
                    }
                });
                GlideManager.loadImg(item.getImage(), ivVideo);
            } else {
                //发的是纯文本
                llVideo.setVisibility(View.GONE);
            }
        }

    }

}