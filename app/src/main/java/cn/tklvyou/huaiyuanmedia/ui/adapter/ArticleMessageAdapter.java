package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.varunest.sparkbutton.SparkButton;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.ArticleMessageModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;
import cn.tklvyou.huaiyuanmedia.ui.home.ImagePagerActivity;
import cn.tklvyou.huaiyuanmedia.ui.video_player.VodActivity;
import cn.tklvyou.huaiyuanmedia.utils.UrlUtils;
import cn.tklvyou.huaiyuanmedia.widget.ExpandTextView;
import cn.tklvyou.huaiyuanmedia.widget.MultiImageView;

/**
 * Created by yiwei on 16/5/17.
 */
public class ArticleMessageAdapter extends BaseQuickAdapter<ArticleMessageModel, BaseViewHolder> {


    public ArticleMessageAdapter(int layoutResId, @Nullable List<ArticleMessageModel> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, ArticleMessageModel item) {
        helper.setText(R.id.nameTv, item.getInteraction_nickname());
        helper.setText(R.id.timeTv, item.getInteraction_begintime());

        if (item.getInteraction_type() == 1) { // 1：点赞  2：评论
            helper.setGone(R.id.ivGoods, true);
            helper.setGone(R.id.tvCommentContent, false);
        } else {
            helper.setGone(R.id.ivGoods, false);
            helper.setGone(R.id.tvCommentContent, true);
            helper.setText(R.id.tvCommentContent, item.getInteraction_detail());
        }

        if (!StringUtils.isEmpty(item.getInteraction_avatar().trim())) {
            GlideManager.loadRoundImg(item.getInteraction_avatar(), helper.getView(R.id.headIv), 5f);
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
                    //imagesize是作为loading时的图片size
                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                    ImagePagerActivity.startImagePagerActivity(mContext, item.getImages(), position, imageSize);

                }
            });

        } else {
            //上传的是视频

            MultiImageView multiImageView = helper.getView(R.id.multiImagView);
            multiImageView.setVisibility(View.GONE);

            FrameLayout llVideo = helper.getView(R.id.llVideo);
            llVideo.setVisibility(View.VISIBLE);

            ImageView ivVideo = helper.getView(R.id.ivVideo);
            ivVideo.setBackgroundColor(Color.parseColor("#abb1b6"));
            ivVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, VodActivity.class);
                    intent.putExtra("videoPath", item.getVideo());
                    mContext.startActivity(intent);
                }
            });

            Glide.with(mContext).load(item.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.bg_no_photo)
                    .into(ivVideo);

        }

    }

}