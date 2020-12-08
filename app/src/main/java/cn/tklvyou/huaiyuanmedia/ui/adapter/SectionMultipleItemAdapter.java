package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.gson.Gson;
import com.varunest.sparkbutton.SparkButton;

import java.util.List;
import java.util.Locale;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.HaveSecondModuleNewsModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;
import cn.tklvyou.huaiyuanmedia.model.PingXuanModel;
import cn.tklvyou.huaiyuanmedia.model.SectionNewsMultipleItem;
import cn.tklvyou.huaiyuanmedia.model.TownDataModel;
import cn.tklvyou.huaiyuanmedia.ui.home.AudioController;
import cn.tklvyou.huaiyuanmedia.ui.home.ImagePagerActivity;
import cn.tklvyou.huaiyuanmedia.ui.video_player.VodActivity;
import cn.tklvyou.huaiyuanmedia.utils.UrlUtils;
import cn.tklvyou.huaiyuanmedia.widget.ExpandTextView;
import cn.tklvyou.huaiyuanmedia.widget.MultiImageView;

public class SectionMultipleItemAdapter extends BaseSectionMultiItemQuickAdapter<SectionNewsMultipleItem, BaseViewHolder> implements AudioController.AudioControlListener {

    private boolean showAnimal = false;
    private int refreshPosition = -1;

    public SectionMultipleItemAdapter(List data) {
        super(R.layout.item_section_head_view, data);
        addItemType(SectionNewsMultipleItem.VIDEO_IN_TITLE, R.layout.item_news_zhi_bo_layout);
        addItemType(SectionNewsMultipleItem.VIDEO_OUT_TITLE, R.layout.item_news_shi_xun_new);
        addItemType(SectionNewsMultipleItem.NEWS_NO_IMAGE, R.layout.item_news_no_image_view);
        addItemType(SectionNewsMultipleItem.NEWS_LEFT_ONE_IMAGE, R.layout.item_news_left_one_image_view);
        addItemType(SectionNewsMultipleItem.NEWS_RIGHT_ONE_IMAGE, R.layout.item_news_right_one_image_view);
        addItemType(SectionNewsMultipleItem.NEWS_THREE_IMAGE, R.layout.item_news_three_image_view);
        addItemType(SectionNewsMultipleItem.WECHAT_MOMENTS, R.layout.item_winxin_circle);
        addItemType(SectionNewsMultipleItem.READING, R.layout.item_news_reading);
        addItemType(SectionNewsMultipleItem.LISTEN, R.layout.item_news_listen);
        addItemType(SectionNewsMultipleItem.PING_XUAN, R.layout.item_news_ping_xuan_layout);
        addItemType(SectionNewsMultipleItem.TOWN_DEPT, R.layout.item_town_dept_layout);
    }

    private AudioController mAudioControl;

    public void setAudioController(AudioController audioController) {
        this.mAudioControl = audioController;
        mAudioControl.setOnAudioControlListener(this);
    }


    public void notifyItemChangedAnimal(int position) {
        showAnimal = true;
        refreshPosition = position;
        notifyItemChanged(position);
    }


    @Override
    protected void convertHead(BaseViewHolder helper, final SectionNewsMultipleItem item) {
        HaveSecondModuleNewsModel.ModuleSecondBean bean = new Gson().fromJson(item.header, HaveSecondModuleNewsModel.ModuleSecondBean.class);
        GlideManager.loadCircleImg(bean.getAvatar(), helper.getView(R.id.ivIcon));
        helper.setText(R.id.tvName, bean.getNickname());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SectionNewsMultipleItem item) {

        switch (helper.getItemViewType()) {
            case SectionNewsMultipleItem.VIDEO_IN_TITLE:
                NewsBean bean = (NewsBean) item.getDataBean();
                helper.setText(R.id.tvName, bean.getName());
                helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                helper.setText(R.id.tvTime, "" + bean.getBegintime());
                helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());

                GlideManager.loadImg(bean.getImage(), helper.getView(R.id.ivVideoBg));

                helper.addOnClickListener(R.id.ivStartPlayer);

                break;
            case SectionNewsMultipleItem.VIDEO_OUT_TITLE:
                bean = (NewsBean) item.getDataBean();

                helper.addOnClickListener(R.id.sparkButton, R.id.tvGoodNum);
                helper.setText(R.id.tvName, bean.getName());
                helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                helper.setText(R.id.tvTime, "" + bean.getBegintime());
                helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());
                helper.setText(R.id.tvVideoTime, formatTime(Double.valueOf(bean.getTime()).longValue()));
                helper.itemView.setTag(this);
                Glide.with(mContext).load(bean.getImage()).into((ImageView) helper.getView(R.id.ivVideoBg));

                helper.addOnClickListener(R.id.ivStartPlayer);

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
                break;
            case SectionNewsMultipleItem.NEWS_NO_IMAGE:

                bean = (NewsBean) item.getDataBean();

                helper.setText(R.id.tvTitle, bean.getName());
                helper.setText(R.id.tvTime, bean.getBegintime());

                if (bean.getModule().equals("爆料")) {
                    helper.setGone(R.id.tvSeeNum, false);
                    helper.setGone(R.id.tvGoodNum, false);
                    helper.setGone(R.id.sparkButton, false);
                    helper.setGone(R.id.tvCommentNum, true);

                    helper.setText(R.id.tvCommentNum, "" + bean.getComment_num());
                } else {
                    helper.setGone(R.id.tvSeeNum, true);
                    helper.setGone(R.id.tvGoodNum, true);
                    helper.setGone(R.id.sparkButton, true);
                    helper.setGone(R.id.tvCommentNum, false);

                    helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                    helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());
                }

                helper.addOnClickListener(R.id.sparkButton, R.id.tvGoodNum);

                sparkButton = helper.getView(R.id.sparkButton);
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

                break;
            case SectionNewsMultipleItem.NEWS_LEFT_ONE_IMAGE:
            case SectionNewsMultipleItem.NEWS_RIGHT_ONE_IMAGE:
                bean = (NewsBean) item.getDataBean();

                helper.setText(R.id.tvTitle, bean.getName());
                helper.setText(R.id.tvTime, bean.getBegintime());

                if (bean.getModule().equals("爆料")) {
                    helper.setGone(R.id.tvSeeNum, false);
                    helper.setGone(R.id.tvGoodNum, false);
                    helper.setGone(R.id.sparkButton, false);
                    helper.setGone(R.id.tvCommentNum, true);

                    helper.setText(R.id.tvCommentNum, "" + bean.getComment_num());
                } else {
                    helper.setGone(R.id.tvSeeNum, true);
                    helper.setGone(R.id.tvGoodNum, true);
                    helper.setGone(R.id.sparkButton, true);
                    helper.setGone(R.id.tvCommentNum, false);

                    helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                    helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());
                }

                helper.addOnClickListener(R.id.sparkButton, R.id.tvGoodNum);

                sparkButton = helper.getView(R.id.sparkButton);
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
                    GlideManager.loadRoundImg(bean.getImage(), helper.getView(R.id.ivImageOne));
                } else {
                    GlideManager.loadRoundImg(bean.getImages().get(0), helper.getView(R.id.ivImageOne));
                }

                break;
            case SectionNewsMultipleItem.NEWS_THREE_IMAGE:
                bean = (NewsBean) item.getDataBean();

                helper.setText(R.id.tvTitle, bean.getName());
                helper.setText(R.id.tvTime, bean.getBegintime());

                if (bean.getModule().equals("爆料")) {
                    helper.setGone(R.id.tvSeeNum, false);
                    helper.setGone(R.id.tvGoodNum, false);
                    helper.setGone(R.id.sparkButton, false);
                    helper.setGone(R.id.tvCommentNum, true);

                    helper.setText(R.id.tvCommentNum, "" + bean.getComment_num());
                } else {
                    helper.setGone(R.id.tvSeeNum, true);
                    helper.setGone(R.id.tvGoodNum, true);
                    helper.setGone(R.id.sparkButton, true);
                    helper.setGone(R.id.tvCommentNum, false);

                    helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                    helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());
                }

                helper.addOnClickListener(R.id.sparkButton, R.id.tvGoodNum);

                sparkButton = helper.getView(R.id.sparkButton);
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

                GlideManager.loadRoundImg(bean.getImages().get(0), helper.getView(R.id.ivImageFirst));
                GlideManager.loadRoundImg(bean.getImages().get(1), helper.getView(R.id.ivImageSecond));
                GlideManager.loadRoundImg(bean.getImages().get(2), helper.getView(R.id.ivImageThree));

                break;
            case SectionNewsMultipleItem.WECHAT_MOMENTS:
                bean = (NewsBean) item.getDataBean();

                helper.addOnClickListener(R.id.sparkButton, R.id.tvGoodNum);

                helper.setVisible(R.id.deleteBtn, false);
                helper.setText(R.id.nameTv, bean.getNickname());
                helper.setText(R.id.timeTv, bean.getBegintime());

                helper.setText(R.id.tvCommentNum, "" + bean.getComment_num());
                helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());

                sparkButton = helper.getView(R.id.sparkButton);
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


                if (!StringUtils.isEmpty(bean.getAvatar().trim())) {
                    GlideManager.loadRoundImg(bean.getAvatar(), helper.getView(R.id.headIv), 5f);
                } else {
                    GlideManager.loadRoundImg(R.mipmap.default_avatar, helper.getView(R.id.headIv));
                }

                ExpandTextView expandTextView = helper.getView(R.id.contentTv);
                if (!TextUtils.isEmpty(bean.getName())) {
                    expandTextView.setExpand(bean.isExpand());
                    expandTextView.setExpandStatusListener(new ExpandTextView.ExpandStatusListener() {
                        @Override
                        public void statusChange(boolean isExpand) {
                            bean.setExpand(isExpand);
                        }
                    });

                    expandTextView.setText(UrlUtils.formatUrlString(bean.getName()));
                }
                expandTextView.setVisibility(TextUtils.isEmpty(bean.getName()) ? View.GONE : View.VISIBLE);

                if (bean.getImages() != null && bean.getImages().size() > 0) {
                    //上传的是图片
                    ImageView ivVideo = helper.getView(R.id.ivVideo);
                    FrameLayout llVideo = helper.getView(R.id.llVideo);
                    llVideo.setVisibility(View.GONE);


                    MultiImageView multiImageView = helper.getView(R.id.multiImagView);
                    multiImageView.setVisibility(View.VISIBLE);
                    multiImageView.setList(bean.getImages());
                    multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            //imagesize是作为loading时的图片size
                            ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                            ImagePagerActivity.startImagePagerActivity(mContext, bean.getImages(), position, imageSize);

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
                            intent.putExtra("videoPath", bean.getVideo());
                            mContext.startActivity(intent);
                        }
                    });

                    GlideManager.loadImg(bean.getImage(), ivVideo);

                }
                break;
            case SectionNewsMultipleItem.READING:
                bean = (NewsBean) item.getDataBean();

                if (helper.getLayoutPosition() == 0) {
                    helper.getView(R.id.ivReadingImage).getLayoutParams().height = ConvertUtils.dp2px(90);
                } else {
                    helper.getView(R.id.ivReadingImage).getLayoutParams().height = ConvertUtils.dp2px(115);
                }

                helper.setText(R.id.tvName, bean.getName());
                helper.setText(R.id.tvBeginTime, bean.getBegintime());
                helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());

                GlideManager.loadImg(bean.getImage(), helper.getView(R.id.ivReadingImage));

                break;
            case SectionNewsMultipleItem.LISTEN:
                bean = (NewsBean) item.getDataBean();

                helper.addOnClickListener(R.id.sparkButton, R.id.tvGoodNum);
                helper.setText(R.id.tvName, bean.getName());
                helper.setText(R.id.tvBeginTime, bean.getBegintime());

                if (bean.getTime().contains(":") || bean.getTime().contains("：")) {
                    helper.setText(R.id.tvTime, bean.getTime());
                } else {
                    helper.setText(R.id.tvTime, formatTime(Double.valueOf(bean.getTime()).longValue()));
                }

                helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());

                sparkButton = helper.getView(R.id.sparkButton);
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

                if (bean.getPlayStatus()) {
                    helper.getView(R.id.play).setVisibility(View.GONE);
                    helper.getView(R.id.pause).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.play).setVisibility(View.VISIBLE);
                    helper.getView(R.id.pause).setVisibility(View.GONE);
                }

                helper.getView(R.id.play).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initStatus(mAudioControl.getPosition(), helper.getLayoutPosition());
                        mAudioControl.onPrepare(((NewsBean) mData.get(helper.getLayoutPosition()).getDataBean()).getAudio());
                        mAudioControl.onStart(helper.getLayoutPosition());
                    }
                });

                helper.getView(R.id.pause).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean playNClickIsSame = playNClickIsSame(mAudioControl.getPosition(), helper.getLayoutPosition());
                        if (playNClickIsSame) {
                            mAudioControl.onPause();
                        }
                    }
                });
                break;
            case SectionNewsMultipleItem.PING_XUAN:
                PingXuanModel pingXuanModel = (PingXuanModel) item.getDataBean();
                helper.setText(R.id.tvName, pingXuanModel.getName());
                helper.setText(R.id.tvContent, "简介：" + pingXuanModel.getDetail());
                helper.setText(R.id.tvAddress, "发起单位：" + pingXuanModel.getCompany());

                break;
            case SectionNewsMultipleItem.TOWN_DEPT:
                TownDataModel townDataModel = (TownDataModel) item.getDataBean();
                helper.getView(R.id.ivTown).getLayoutParams().height = ConvertUtils.dp2px(115);
                helper.setText(R.id.tvTownName, townDataModel.getModule_second());
                GlideManager.loadRoundImg(townDataModel.getAvatar(), helper.getView(R.id.ivTown),5);
                break;
        }
    }






    /*
     * ----------------------------------------------------------------------------------
     * 音频播放处理  start
     * ----------------------------------------------------------------------------------
     */

    private boolean playNClickIsSame(int playIndex, int clickIndex) {
        return playIndex == clickIndex ? true : false;
    }

    private void initStatus(int playIndex, int clickIndex) {
        if (mData.size() > playIndex) {
            NewsBean oldEntity = (NewsBean) mData.get(playIndex).getDataBean();
            oldEntity.setPlayStatus(false);
//        oldEntity.setTime("00:00");
            if (playIndex >= ((LinearLayoutManager) getRecyclerView().getLayoutManager())
                    .findFirstVisibleItemPosition()
                    && playIndex <= ((LinearLayoutManager) getRecyclerView().getLayoutManager())
                    .findLastVisibleItemPosition()) {
                if (getViewByPosition(getRecyclerView(), playIndex,
                        R.id.exo_progress) != null) {
                    DefaultTimeBar timeBar = (DefaultTimeBar) getViewByPosition(getRecyclerView(),
                            playIndex,
                            R.id.exo_progress);
                    timeBar.setPosition(0);
                    timeBar.setBufferedPosition(0);
                }
                if (getViewByPosition(getRecyclerView(), playIndex,
                        R.id.tvTime) != null) {
                    TextView startTime = (TextView) getViewByPosition(getRecyclerView(), playIndex,
                            R.id.tvTime);
                    startTime.setText(oldEntity.getTime());
                }
                if (getViewByPosition(getRecyclerView(), playIndex, R.id.play) != null) {
                    ImageView oldplay = (ImageView) getViewByPosition(getRecyclerView(), playIndex,
                            R.id.play);
                    oldplay.setVisibility(View.VISIBLE);
                }
                if (getViewByPosition(getRecyclerView(), playIndex,
                        R.id.pause) != null) {
                    ImageView oldpause = (ImageView) getViewByPosition(getRecyclerView(), playIndex,
                            R.id.pause);
                    oldpause.setVisibility(View.GONE);
                }
                if (getViewByPosition(getRecyclerView(), clickIndex, R.id.play) != null) {
                    ImageView newplay = (ImageView) getViewByPosition(getRecyclerView(), clickIndex,
                            R.id.play);
                    newplay.setVisibility(View.GONE);
                }
                if (getViewByPosition(getRecyclerView(), clickIndex, R.id.pause) != null) {
                    ImageView onewpause = (ImageView) getViewByPosition(getRecyclerView(), clickIndex,
                            R.id.pause);
                    onewpause.setVisibility(View.VISIBLE);
                }
            } else {
                notifyItemChanged(playIndex);
            }
        }
    }

    @Override
    public void setCurPositionTime(int position, long curPositionTime) {
//        if (getViewByPosition(getRecyclerView(), position,
//                R.id.exo_progress) != null) {
//            DefaultTimeBar timeBar = (DefaultTimeBar) getViewByPosition(getRecyclerView(), position,
//                    R.id.exo_progress);
//            timeBar.setPosition(curPositionTime);
//        }
    }

    @Override
    public void setDurationTime(int position, long durationTime) {
//        if (getViewByPosition(getRecyclerView(), position, R.id.exo_progress) != null) {
//            DefaultTimeBar timeBar = (DefaultTimeBar) getViewByPosition(getRecyclerView(), position,
//                    R.id.exo_progress);
//            timeBar.setDuration(durationTime);
//        }
    }

    @Override
    public void setBufferedPositionTime(int position, long bufferedPosition) {
//        if (getViewByPosition(getRecyclerView(), position,
//                R.id.exo_progress) != null) {
//            DefaultTimeBar timeBar = (DefaultTimeBar) getViewByPosition(getRecyclerView(), position,
//                    R.id.exo_progress);
//            timeBar.setBufferedPosition(bufferedPosition);
//        }
    }

    @Override
    public void setCurTimeString(int position, String curTimeString) {
        if (getViewByPosition(getRecyclerView(), position, R.id.tvTime) != null) {
            TextView startTime = (TextView) getViewByPosition(getRecyclerView(), position,
                    R.id.tvTime);
            startTime.setText(curTimeString);
        }
        int length = mData.size();
        if (mData != null && length > position && mData.get(position) != null) {
            NewsBean mediaEntity = (NewsBean) mData.get(position).getDataBean();
            mediaEntity.setTime(curTimeString);
        }
    }

    @Override
    public void isPlay(int position, boolean isPlay) {
        int length = mData.size();
        if (mData != null && length > position && mData.get(position) != null) {
            NewsBean mediaEntity = (NewsBean) mData.get(position).getDataBean();
            mediaEntity.setPlayStatus(isPlay);
            if (getViewByPosition(getRecyclerView(), position, R.id.play) != null
                    && getViewByPosition(getRecyclerView(), position, R.id.pause) != null) {
                ImageView play = (ImageView) getViewByPosition(getRecyclerView(), position, R.id.play);
                ImageView pause = (ImageView) getViewByPosition(getRecyclerView(), position,
                        R.id.pause);
                if (isPlay) {
                    if (play != null) {
                        play.setVisibility(View.GONE);
                    }
                    if (pause != null) {
                        pause.setVisibility(View.VISIBLE);
                    }


                } else {
                    if (play != null) {
                        play.setVisibility(View.VISIBLE);
                    }
                    if (pause != null) {
                        pause.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    @Override
    public void setDurationTimeString(int position, String durationTimeString) {
//        if (getViewByPosition(getRecyclerView(), position,
//                R.id.tv_end_time) != null) {
//            TextView endTime = (TextView) getViewByPosition(getRecyclerView(), position,
//                    R.id.tv_end_time);
//            endTime.setText(durationTimeString);
//        }
//        MediaEntity mediaEntity = mData.get(position);
//        mediaEntity.setEndTime(durationTimeString);
    }


    private String formatTime(Long position) {
        int totalSeconds = (int) (position + 0.5);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

}
