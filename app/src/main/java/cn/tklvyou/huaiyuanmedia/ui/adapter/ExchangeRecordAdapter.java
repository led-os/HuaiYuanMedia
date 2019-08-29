package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.ExchangeModel;

/**
 * @author :JenkinsZhou
 * @description :兑换记录适配器
 * @company :途酷科技
 * @date 2019年08月02日10:43
 * @Email: 971613168@qq.com
 */
public class ExchangeRecordAdapter extends BaseQuickAdapter<ExchangeModel, BaseViewHolder> {
    private static final String STATUS_HIDDEN = "hidden";

    public ExchangeRecordAdapter(List<ExchangeModel> data) {
        super(R.layout.item_exchange_record,data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ExchangeModel item) {
        if (item == null) {
            return;
        }

        if(item.getRegister_status() == 1){
            helper.getView(R.id.registerLayout).setVisibility(View.VISIBLE);
            helper.getView(R.id.normalLayout).setVisibility(View.GONE);

            boolean ishidden = STATUS_HIDDEN.equalsIgnoreCase(item.getStatus());
            if(!ishidden){
                helper.setText(R.id.tvTip,"我已现场领取");
                helper.getView(R.id.registerLayout).setOnClickListener(null);
            }else {
                helper.setText(R.id.tvTip,"请在现场领取礼包");
                helper.addOnClickListener(R.id.registerLayout);
            }

        }else {
            helper.getView(R.id.registerLayout).setVisibility(View.GONE);
            helper.getView(R.id.normalLayout).setVisibility(View.VISIBLE);

            ImageView roundedImageView = helper.getView(R.id.rvGoodsImage);
            GlideManager.loadRoundImg(item.getImage(), roundedImageView, 4);
            helper.setText(R.id.tvGoodsDesc, item.getName());
            helper.setText(R.id.tvDeduction, "消耗：" + item.getScore() + "石榴籽");
            boolean buttonEnable = STATUS_HIDDEN.equalsIgnoreCase(item.getStatus());
            if(!buttonEnable){
                ((TextView)helper.getView(R.id.tvWaitReceive)).setTextColor(Color.BLACK);
                helper.setBackgroundRes(R.id.tvWaitReceive,R.drawable.bg_radius_15_top_bottom_gray);
                ((TextView)helper.getView(R.id.tvWaitReceive)).setText("已领取");
            }else {
                helper.setBackgroundRes(R.id.tvWaitReceive,R.drawable.bg_radius_15_top_bottom_accent);
                ((TextView)helper.getView(R.id.tvWaitReceive)).setTextColor(Color.WHITE);
                ((TextView)helper.getView(R.id.tvWaitReceive)).setText("待领取");
            }
            helper.addOnClickListener(R.id.tvWaitReceive);

        }


    }

}
