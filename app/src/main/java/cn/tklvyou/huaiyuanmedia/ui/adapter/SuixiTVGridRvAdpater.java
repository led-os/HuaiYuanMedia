package cn.tklvyou.huaiyuanmedia.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

public class SuixiTVGridRvAdpater extends BaseQuickAdapter<NewsBean, BaseViewHolder> {

    public SuixiTVGridRvAdpater(int layoutResId, @Nullable List<NewsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NewsBean item) {
        helper.setText(R.id.tvName, item.getName());
        helper.setText(R.id.tvTime, item.getTime());
        GlideManager.loadImg(item.getImage(), helper.getView(R.id.ivImage));
        helper.addOnClickListener(R.id.ivSuiXiTVStartPlayer);

    }

}
