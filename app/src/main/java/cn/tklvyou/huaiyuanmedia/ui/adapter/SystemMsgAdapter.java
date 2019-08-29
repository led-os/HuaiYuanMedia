package cn.tklvyou.huaiyuanmedia.ui.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.model.MessageModel;


/**
 * @author :JenkinsZhou
 * @description :系统消息适配器
 * @company :途酷科技
 * @date 2019年03月18日13:59
 * @Email: 971613168@qq.com
 */
public class SystemMsgAdapter extends BaseQuickAdapter<MessageModel, BaseViewHolder> {
    public SystemMsgAdapter(List<MessageModel> data) {
        super(R.layout.item_system_msg,data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, MessageModel item) {
        helper.setText(R.id.tvMsgTime, item.getCreatetime());
        helper.setText(R.id.tvMsgContent, item.getDetail());
        if (item.getStatus() == 1) {
            helper.setVisible(R.id.tvRedDot, false);
        } else {
            helper.setVisible(R.id.tvRedDot, true);
        }

    }
}
