package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;
import java.util.Locale;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

/**
 * @author :JenkinsZhou
 * @description :我都V视适配器
 * @company :途酷科技
 * @date 2019年08月05日14:33
 * @Email: 971613168@qq.com
 */
public class MyVideoAdapter extends BaseQuickAdapter<NewsBean, BaseViewHolder> {
    public MyVideoAdapter(List<NewsBean> data) {
        super(R.layout.item_news_video,data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NewsBean bean) {
        helper.setVisible(R.id.deleteBtn, true);
        helper.addOnClickListener(R.id.deleteBtn);

        helper.setText(R.id.tvNewsTitle, bean.getName());
        helper.setText(R.id.tvVideoTime, formatTime(Double.valueOf(bean.getTime()).longValue()));
        helper.setText(R.id.tvCommentNum, "" + bean.getComment_num());
        helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());
        helper.setText(R.id.tvName, bean.getNickname());

        TextView tvGoodNum = helper.getView(R.id.tvGoodNum);

        Drawable[] drawables = tvGoodNum.getCompoundDrawables();

        if (bean.getLike_status() == 1) {
            Drawable redGoodDrawable = mContext.getResources().getDrawable(R.mipmap.icon_red_good);
            redGoodDrawable.setBounds(drawables[0].getBounds());
            tvGoodNum.setCompoundDrawables(redGoodDrawable, drawables[1], drawables[2], drawables[3]);
        } else {
            Drawable grayGoodDrawable = mContext.getResources().getDrawable(R.mipmap.icon_good);
            grayGoodDrawable.setBounds(drawables[0].getBounds());
            tvGoodNum.setCompoundDrawables(grayGoodDrawable, drawables[1], drawables[2], drawables[3]);
        }

        // 加载网络图片
        if (!StringUtils.isEmpty(bean.getAvatar().trim())) {
            Glide.with(mContext).load(bean.getAvatar()).into((ImageView) helper.getView(R.id.ivAvatar));
        }

        Glide.with(mContext).load(bean.getImage()).into((ImageView) helper.getView(R.id.ivVideoBg));

        helper.addOnClickListener(R.id.ivStartPlayer);
    }

    private String formatTime(Long position) {
        int totalSeconds = (int) (position + 0.5);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }
}
