package cn.tklvyou.huaiyuanmedia.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.PointModel;

/**
 * 积分商城
 * Created by yiwei on 16/5/17.
 */
public class PointRvAdapter extends BaseQuickAdapter<PointModel, BaseViewHolder> {

    public PointRvAdapter(int layoutResId, @Nullable List<PointModel> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, PointModel item) {
        GlideManager.loadTopRoundImg(item.getImage(),helper.getView(R.id.ivGoods));
        helper.setText(R.id.tvTitle,item.getName());
        helper.setText(R.id.tvPoint,item.getScore()+"石榴籽");
    }
}