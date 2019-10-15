package cn.tklvyou.huaiyuanmedia.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.model.TelModel;

public class TVNewsDetailsRvAdapter extends BaseQuickAdapter<TelModel.ListBean, BaseViewHolder> {

    private boolean showTag = true;

    public TVNewsDetailsRvAdapter(int layoutRes, List<TelModel.ListBean> data) {
        super(layoutRes, data);
    }

    public void setShowTag(boolean showTag) {
        this.showTag = showTag;
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, TelModel.ListBean bean) {
        if (showTag) {

            if(bean.getNow() == 1){
                helper.setVisible(R.id.tvTag, true);
                helper.setTextColor(R.id.tvName, mContext.getResources().getColor(R.color.colorAccent));
                helper.setTextColor(R.id.tvDate, mContext.getResources().getColor(R.color.colorAccent));
            }else {
                helper.setVisible(R.id.tvTag, false);
                helper.setTextColor(R.id.tvName, mContext.getResources().getColor(R.color.default_black_text_color));
                helper.setTextColor(R.id.tvDate, mContext.getResources().getColor(R.color.default_black_text_color));
            }

        } else {
            helper.setVisible(R.id.tvTag, false);
            helper.setTextColor(R.id.tvName, mContext.getResources().getColor(R.color.default_black_text_color));
            helper.setTextColor(R.id.tvDate, mContext.getResources().getColor(R.color.default_black_text_color));
        }

        helper.setText(R.id.tvName, bean.getContent());
        helper.setText(R.id.tvDate, bean.getTime());


    }
}
