package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;
import cn.tklvyou.huaiyuanmedia.widget.SwipeRevealLayout;

/**
 * @author :JenkinsZhou
 * @description :爆料适配器
 * @company :途酷科技
 * @date 2019年08月02日18:06
 * @Email: 971613168@qq.com
 */
public class WenZhenAdapter extends BaseQuickAdapter<NewsBean, BaseViewHolder> {

    private boolean isEdit = false;

    public WenZhenAdapter(List<NewsBean> data) {
        super(R.layout.item_edit_news_wen_zheng,data);
    }


    public void setEditModel(boolean isEdit) {
        this.isEdit = isEdit;
        for (int i = 0; i < getData().size(); i++) {
            getData().get(i).setSelect(false);
        }
        notifyDataSetChanged();
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, NewsBean bean) {

        helper.addOnClickListener(R.id.check_box, R.id.itemLayout);
        SwipeRevealLayout mSwipeLayout = helper.getView(R.id.mSwipeLayout);
        if (isEdit) {
            mSwipeLayout.open(true);
            ImageView mCheckBox = helper.getView(R.id.check_box);
            if (bean.isSelect()) {
                mCheckBox.setImageResource(R.mipmap.icon_selected);
            } else {
                mCheckBox.setImageResource(R.mipmap.icon_normal);
            }

        } else {
            mSwipeLayout.close(true);
        }


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
    }
}
