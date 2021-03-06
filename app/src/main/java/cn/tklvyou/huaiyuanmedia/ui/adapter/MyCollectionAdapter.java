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

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;
import cn.tklvyou.huaiyuanmedia.model.NewsMultipleItem;
import cn.tklvyou.huaiyuanmedia.ui.home.AudioController;
import cn.tklvyou.huaiyuanmedia.ui.home.ImagePagerActivity;
import cn.tklvyou.huaiyuanmedia.ui.video_player.VodActivity;
import cn.tklvyou.huaiyuanmedia.utils.UrlUtils;
import cn.tklvyou.huaiyuanmedia.widget.ExpandTextView;
import cn.tklvyou.huaiyuanmedia.widget.MultiImageView;
import cn.tklvyou.huaiyuanmedia.widget.SwipeRevealLayout;
import cn.tklvyou.huaiyuanmedia.widget.TagTextView;

/**
 * @author :JenkinsZhou
 * @description :我的收藏和 最近浏览 公用的适配器
 * @company :途酷科技
 * @date 2019年08月02日15:00
 * @Email: 971613168@qq.com
 */
public class MyCollectionAdapter extends BaseMultiItemQuickAdapter<NewsMultipleItem<NewsBean>, BaseViewHolder> implements AudioController.AudioControlListener {

    private boolean isEdit = false;

    private boolean showAnimal = false;
    private int refreshPosition = -1;

    private AudioController mAudioControl;

    public void setAudioController(AudioController audioController) {
        this.mAudioControl = audioController;
        mAudioControl.setOnAudioControlListener(this);
    }

    public MyCollectionAdapter(List data) {
        super(data);
//        super(R.layout.item_news_collect_layout, data);
        addItemType(NewsMultipleItem.VIDEO, R.layout.item_edit_news_video);
//        addItemType(NewsMultipleItem.TV, R.layout.item_edit_news_suixi_tv);
        addItemType(NewsMultipleItem.NEWS, R.layout.item_edit_news_news_layout);
        addItemType(NewsMultipleItem.SHI_XUN, R.layout.item_edit_news_shi_xun);
        addItemType(NewsMultipleItem.WEN_ZHENG, R.layout.item_edit_news_wen_zheng);
        addItemType(NewsMultipleItem.JU_ZHENG, R.layout.item_edit_news_news_layout);
        addItemType(NewsMultipleItem.WECHAT_MOMENTS, R.layout.item_edit_winxin_circle);
        addItemType(NewsMultipleItem.READING, R.layout.item_edit_news_reading);
        addItemType(NewsMultipleItem.LISTEN, R.layout.item_edit_news_listen);
        addItemType(NewsMultipleItem.DANG_JIAN, R.layout.item_edit_news_news_layout);
        addItemType(NewsMultipleItem.ZHUAN_LAN, R.layout.item_edit_news_news_layout);
        addItemType(NewsMultipleItem.GONG_GAO, R.layout.item_edit_news_news_layout);
        addItemType(NewsMultipleItem.ZHI_BO, R.layout.item_edit_news_zhi_bo_layout);
        addItemType(NewsMultipleItem.TUI_JIAN, R.layout.item_edit_news_tui_jian_layout);
        addItemType(NewsMultipleItem.ZHUAN_TI, R.layout.item_edit_news_news_layout);
    }

    public void notifyItemChangedAnimal(int position) {
        showAnimal = true;
        refreshPosition = position;
        notifyItemChanged(position);
    }


    public void setEditModel(boolean isEdit) {
        this.isEdit = isEdit;
        for (int i = 0; i < getData().size(); i++) {
            getData().get(i).getDataBean().setSelect(false);
        }
        notifyDataSetChanged();
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, NewsMultipleItem<NewsBean> item) {

        helper.addOnClickListener(R.id.check_box, R.id.itemLayout);
        SwipeRevealLayout mSwipeLayout = helper.getView(R.id.mSwipeLayout);
        if (isEdit) {
            mSwipeLayout.open(true);
            ImageView mCheckBox = helper.getView(R.id.check_box);
            if (item.getDataBean().isSelect()) {
                mCheckBox.setImageResource(R.mipmap.icon_selected);
            } else {
                mCheckBox.setImageResource(R.mipmap.icon_normal);
            }

        } else {
            mSwipeLayout.close(true);
        }


        switch (helper.getItemViewType()) {
            case NewsMultipleItem.VIDEO:
                NewsBean bean = item.getDataBean();
                helper.setText(R.id.tvNewsTitle, bean.getName());
                helper.setText(R.id.tvVideoTime, formatTime(Double.valueOf(bean.getTime()).longValue()));
                helper.setText(R.id.tvCommentNum, "" + bean.getComment_num());
                helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());
                helper.setText(R.id.tvName, bean.getNickname());

                helper.addOnClickListener(R.id.sparkButton, R.id.tvGoodNum);
                SparkButton sparkButton = helper.getView(R.id.sparkButton);
                if (bean.getLike_status() == 1) {
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

                // 加载网络图片
                if (!StringUtils.isEmpty(bean.getAvatar().trim())) {
                    GlideManager.loadCircleImg(bean.getAvatar(), helper.getView(R.id.ivAvatar), R.mipmap.default_avatar);
                } else {
                    GlideManager.loadCircleImg(R.mipmap.default_avatar, helper.getView(R.id.ivAvatar));
                }

                Glide.with(mContext).load(bean.getImage()).into((ImageView) helper.getView(R.id.ivVideoBg));

                helper.addOnClickListener(R.id.ivStartPlayer);
                break;
//            case NewsMultipleItem.TV:
//                HaveSecondModuleNewsModel suixiTvModel = (HaveSecondModuleNewsModel) item.getDataBean();
//
//                if (suixiTvModel.getData().size() == 1) {
//                    helper.getView(R.id.llSuixiTvSecond).setVisibility(View.INVISIBLE);
//                }
//
//                helper.setText(R.id.tvModuleSecond, suixiTvModel.getModule_second());
//
//                for (int i = 0; i < suixiTvModel.getData().size(); i++) {
//                    if (i == 0) {
//                        helper.setText(R.id.tvNameFirst, suixiTvModel.getData().get(0).getName());
//                        helper.setText(R.id.tvTimeFirst, suixiTvModel.getData().get(0).getTime());
//                        Glide.with(mContext).load(suixiTvModel.getData().get(0).getImage()).into((ImageView) helper.getView(R.id.ivImageFirst));
//                    } else if (i == 1) {
//                        helper.setText(R.id.tvNameSecond, suixiTvModel.getData().get(1).getName());
//                        helper.setText(R.id.tvTimeSecond, suixiTvModel.getData().get(1).getTime());
//                        Glide.with(mContext).load(suixiTvModel.getData().get(1).getImage()).into((ImageView) helper.getView(R.id.ivImageSecond));
//                    }
//                }
//
//                helper.addOnClickListener(R.id.ivSuiXiTVFirstStartPlayer);
//                helper.addOnClickListener(R.id.ivSuiXiTVSecondStartPlayer);
//                helper.addOnClickListener(R.id.llSuixiTvFirst);
//                helper.addOnClickListener(R.id.llSuixiTvSecond);
//                helper.addOnClickListener(R.id.tvSuixiTvAll);
//
//                break;

            case NewsMultipleItem.NEWS:
                bean = (NewsBean) item.getDataBean();
                helper.setText(R.id.tvTitle, bean.getName());
                helper.setText(R.id.tvTime, bean.getBegintime());
                helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());

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
                    refreshPosition = -1;
                    showAnimal = false;
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
                break;
            case NewsMultipleItem.SHI_XUN:
                bean = (NewsBean) item.getDataBean();

                helper.addOnClickListener(R.id.sparkButton, R.id.tvGoodNum);
                helper.setText(R.id.tvName, bean.getName());
                helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                helper.setText(R.id.tvTime, "" + bean.getBegintime());
                helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());
                helper.setText(R.id.tvVideoTime, formatTime(Double.valueOf(bean.getTime()).longValue()));

                Glide.with(mContext).load(bean.getImage()).into((ImageView) helper.getView(R.id.ivVideoBg));

                helper.addOnClickListener(R.id.ivStartPlayer);

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
            case NewsMultipleItem.WEN_ZHENG:
                bean = (NewsBean) item.getDataBean();
                helper.setText(R.id.tvTitle, bean.getName());
                helper.setText(R.id.tvTime, bean.getBegintime());
                helper.setText(R.id.tvCommentNum, "" + bean.getComment_num());

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
                break;
            case NewsMultipleItem.JU_ZHENG:
                bean = (NewsBean) item.getDataBean();
                helper.setText(R.id.tvTitle, bean.getName());
                helper.setText(R.id.tvTime, bean.getBegintime());
                helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());
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

                break;
            case NewsMultipleItem.WECHAT_MOMENTS:
                bean = (NewsBean) item.getDataBean();

                helper.addOnClickListener(R.id.sparkButton, R.id.tvGoodNum);
                helper.addOnClickListener(R.id.contentTv);
                helper.addOnClickListener(R.id.contentText,R.id.textPlus);
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
                    refreshPosition = -1;
                    showAnimal = false;
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
                            if (isEdit) {
                                helper.getView(R.id.itemLayout).performClick();
                            } else {
                                //imagesize是作为loading时的图片size
                                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                                ImagePagerActivity.startImagePagerActivity(mContext, bean.getImages(), position, imageSize);
                            }

                        }
                    });

                } else {
                    //上传的是视频
                        if(!StringUtils.isEmpty(bean.getVideo())){
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
                        }else {
                            //上传的纯文本
                            MultiImageView multiImageView = helper.getView(R.id.multiImagView);
                            multiImageView.setVisibility(View.GONE);
                            FrameLayout llVideo = helper.getView(R.id.llVideo);
                            llVideo.setVisibility(View.GONE);
                        }


                }
                break;
            case NewsMultipleItem.READING:
                bean = (NewsBean) item.getDataBean();

                helper.setText(R.id.tvName, bean.getName());
                helper.setText(R.id.tvBeginTime, bean.getBegintime());
                helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());

                GlideManager.loadImg(bean.getImage(), helper.getView(R.id.ivReadingImage));
                break;
            case NewsMultipleItem.LISTEN:
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

            case NewsMultipleItem.DANG_JIAN:
                bean = (NewsBean) item.getDataBean();

                if (helper.getLayoutPosition() == 0) {
                    TagTextView tagTextView = helper.getView(R.id.tvTitle);
                    List<String> tags = new ArrayList<>();
                    tags.add("置顶");
                    tagTextView.setContentAndTag(bean.getName(), tags);
                } else {
                    helper.setText(R.id.tvTitle, bean.getName());
                }


                helper.setText(R.id.tvTime, bean.getBegintime());
                helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());

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
                    refreshPosition = -1;
                    showAnimal = false;
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

                break;
            case NewsMultipleItem.ZHUAN_LAN:
                bean = (NewsBean) item.getDataBean();
                helper.setText(R.id.tvTitle, bean.getName());
                helper.setText(R.id.tvTime, bean.getBegintime());
                helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());

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
                    refreshPosition = -1;
                    showAnimal = false;
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
                break;
            case NewsMultipleItem.GONG_GAO:
                bean = (NewsBean) item.getDataBean();
                helper.setText(R.id.tvTitle, bean.getName());
                helper.setText(R.id.tvTime, bean.getBegintime());
                helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());

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
                    refreshPosition = -1;
                    showAnimal = false;
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
                break;
            case NewsMultipleItem.ZHI_BO:
                bean = (NewsBean) item.getDataBean();
                helper.setText(R.id.tvName, bean.getName());
                helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                helper.setText(R.id.tvTime, "" + bean.getBegintime());
                helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());

                Glide.with(mContext).load(bean.getImage()).into((ImageView) helper.getView(R.id.ivVideoBg));

                helper.addOnClickListener(R.id.ivStartPlayer);
                break;
            case NewsMultipleItem.TUI_JIAN:
                bean = (NewsBean) item.getDataBean();

                if (bean.getModule().equals("原创")) {
                    helper.setGone(R.id.wechatLayout, true);
                    helper.setGone(R.id.newsLayout, false);

                    helper.addOnClickListener(R.id.sparkButton, R.id.tvGoodNum);

                    helper.setGone(R.id.deleteBtn, false);
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

                    expandTextView = helper.getView(R.id.contentTv);
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

                } else {
                    helper.setGone(R.id.wechatLayout, false);
                    helper.setGone(R.id.newsLayout, true);

                    helper.setText(R.id.tvTitle, bean.getName());
                    helper.setText(R.id.tvTime, bean.getBegintime());
                    helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                    helper.setText(R.id.tvGoodNum1, "" + bean.getLike_num());

                    helper.addOnClickListener(R.id.sparkButton1, R.id.tvGoodNum1);

                    sparkButton = helper.getView(R.id.sparkButton1);
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
                break;
            case NewsMultipleItem.ZHUAN_TI:
                bean = (NewsBean) item.getDataBean();
                helper.setText(R.id.tvTitle, bean.getName());
                helper.setText(R.id.tvTime, bean.getBegintime());
                helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
                helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());
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
                break;
            default:
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

    }

    @Override
    public void setDurationTime(int position, long durationTime) {

    }

    @Override
    public void setBufferedPositionTime(int position, long bufferedPosition) {

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

    /*
     * ----------------------------------------------------------------------------------
     * 音频播放处理  end
     * ----------------------------------------------------------------------------------
     */

    private String formatTime(Long position) {
        int totalSeconds = (int) (position + 0.5);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

}
