package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

/**
 * 首页 矩阵 头部适配器
 * Created by yiwei on 16/5/17.
 */
public class JuZhenHeaderRvAdapter extends BaseQuickAdapter<NewsBean, BaseViewHolder> {

    public JuZhenHeaderRvAdapter(int layoutResId, @Nullable List<NewsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NewsBean item) {

        helper.setText(R.id.tvSuixiHeaderName,item.getNickname());
        Glide.with(mContext).load(item.getAvatar()).into((ImageView) helper.getView(R.id.ivSuixiHeaderImage));
    }


}