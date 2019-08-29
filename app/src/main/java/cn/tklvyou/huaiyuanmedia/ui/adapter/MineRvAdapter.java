package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.model.MineRvModel;
import cn.tklvyou.huaiyuanmedia.utils.YResourceUtils;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * 我的页面
 * Created by yiwei on 16/5/17.
 */
public class MineRvAdapter extends BaseQuickAdapter<MineRvModel, BaseViewHolder> {

    private Badge badge;
    private int number = 0;


    public MineRvAdapter(int layoutResId, @Nullable List<MineRvModel> data) {
        super(layoutResId, data);
    }

    public void setBadgeNumber(int number){
        this.number = number;
        notifyItemChanged(4);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MineRvModel item) {
        ((ImageView) helper.getView(R.id.ivImage)).setImageResource(YResourceUtils.getMipmapResId(item.getLocalImage()));
        helper.setText(R.id.tvName, item.getName());

        if(helper.getLayoutPosition() == 4){
            if(helper.getView(R.id.ivImage).getTag() == null){
                badge = new QBadgeView(mContext).bindTarget(helper.getView(R.id.ivImage));
                badge.setBadgeGravity(Gravity.END | Gravity.TOP);
                badge.setBadgeTextSize(10, true);
                badge.setBadgeBackgroundColor(Color.parseColor("#FEA841"));
                badge.setBadgeNumber(number);
                helper.setTag(R.id.ivImage,badge);
            }else {
                badge = (Badge) helper.getView(R.id.ivImage).getTag();
                badge.setBadgeNumber(number);
            }
        }
    }

}