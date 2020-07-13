package cn.tklvyou.huaiyuanmedia.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.PingXuanModel;
import cn.tklvyou.huaiyuanmedia.model.PingXuanPersionModel;
import cn.tklvyou.huaiyuanmedia.model.PointModel;


public class PingXuanPersionRvAdapter extends BaseQuickAdapter<PingXuanPersionModel, BaseViewHolder> {

    private boolean isNormal;

    public PingXuanPersionRvAdapter(int layoutResId, @Nullable List<PingXuanPersionModel> data, boolean isNormal) {
        super(layoutResId, data);
        this.isNormal = isNormal;
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, PingXuanPersionModel item) {
        if (isNormal) {
            GlideManager.loadImg(item.getImage(), helper.getView(R.id.ivImage));
            helper.setText(R.id.tvNum, item.getCode());
            helper.setText(R.id.tvName, item.getName());
            helper.setText(R.id.tvRecordNum, item.getCount() + "票");
            helper.addOnClickListener(R.id.btnSubmit);
        } else {
            switch (item.getRank()) {
                case 1:
                    helper.setGone(R.id.tvTag, false);
                    helper.setGone(R.id.ivTag, true);
                    helper.setImageResource(R.id.ivTag,R.mipmap.ic_number_one);
                    break;
                case 2:
                    helper.setGone(R.id.tvTag, false);
                    helper.setGone(R.id.ivTag, true);
                    helper.setImageResource(R.id.ivTag,R.mipmap.ic_number_two);
                    break;
                case 3:
                    helper.setGone(R.id.tvTag, false);
                    helper.setGone(R.id.ivTag, true);
                    helper.setImageResource(R.id.ivTag,R.mipmap.ic_number_three);
                    break;
                default:
                    helper.setGone(R.id.tvTag, true);
                    helper.setGone(R.id.ivTag, false);
                    helper.setText(R.id.tvTag, "" + item.getRank());
                    break;
            }

            GlideManager.loadCircleImg(item.getImage(), helper.getView(R.id.ivImage));
            helper.setText(R.id.tvName, item.getName());
            helper.setText(R.id.tvRecordNum, item.getCount() + "票");
        }
    }

}