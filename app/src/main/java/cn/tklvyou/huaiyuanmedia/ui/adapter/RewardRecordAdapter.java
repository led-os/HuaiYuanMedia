package cn.tklvyou.huaiyuanmedia.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.model.RewardModel;

/**
 * @author :JenkinsZhou
 * @description :中奖纪录
 * @company :途酷科技
 * @date 2020年10月21日16:25
 * @Email: 971613168@qq.com
 */
public class RewardRecordAdapter extends BaseQuickAdapter<RewardModel, BaseViewHolder> {

    public RewardRecordAdapter(List<RewardModel> dataList) {
        super(R.layout.item_reward_layout, dataList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, RewardModel item) {
        helper.setText(R.id.tvRewardName, item.getAward());
        helper.setText(R.id.tvRewardDate, item.getCreatetime());
        helper.setVisible(R.id.ivArrowRight, item.getType() == 2);

    }
}
