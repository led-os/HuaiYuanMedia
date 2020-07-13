package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.model.HaveSecondModuleNewsModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

/**
 * 首页 濉溪TV 头部适配器
 * Created by yiwei on 16/5/17.
 */
public class SuixiHeaderRvAdapter extends BaseQuickAdapter<NewsBean, BaseViewHolder> {

    public SuixiHeaderRvAdapter(int layoutResId, @Nullable List<NewsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NewsBean item) {

        helper.setText(R.id.tvSuixiHeaderName,item.getName());
        Glide.with(mContext).load(item.getImage()).into((ImageView) helper.getView(R.id.ivSuixiHeaderImage));
    }


}