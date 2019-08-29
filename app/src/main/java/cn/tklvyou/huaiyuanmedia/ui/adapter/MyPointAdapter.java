package cn.tklvyou.huaiyuanmedia.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.model.PointDetailModel;

/**
 * @author :JenkinsZhou
 * @description :积分明细适配器
 * @company :途酷科技
 * @date 2019年08月01日16:50
 * @Email: 971613168@qq.com
 */
public class MyPointAdapter extends BaseQuickAdapter<PointDetailModel, BaseViewHolder> {

    public MyPointAdapter(List<PointDetailModel> data) {
        super(R.layout.item_my_integral_detail,data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PointDetailModel item) {
        if (item == null) {
            return;
        }
        helper.setText(R.id.tvCreateTime, item.getCreatetime());
        helper.setText(R.id.tvIntegralDesc, item.getName());
        helper.setText(R.id.tvIntegralChangeAmount, item.getSymbol() + item.getAmount());

    }
}
