package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cjt2325.cameralibrary.util.LogUtil;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.ConcernModel;
import cn.tklvyou.huaiyuanmedia.model.MineRvModel;
import cn.tklvyou.huaiyuanmedia.utils.YResourceUtils;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * 我的关注
 */
public class MyConcernAdapter extends BaseQuickAdapter<ConcernModel, BaseViewHolder> {


    public MyConcernAdapter(int layoutResId, @Nullable List<ConcernModel> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, ConcernModel item) {
        helper.setText(R.id.tvNickName, item.getNickname());
        GlideManager.loadCircleImg(item.getAvatar(),helper.getView(R.id.ivAvatar));
        helper.addOnClickListener(R.id.cbCheck);

        if(!item.isNoConcern()){
            helper.setText(R.id.cbCheck,"已关注");
            helper.setBackgroundRes(R.id.cbCheck,R.drawable.shape_gray_stroke_radius_5_bg);
            helper.setTextColor(R.id.cbCheck,mContext.getResources().getColor(R.color.default_gray_text_color));
        }else {
            helper.setText(R.id.cbCheck,"关注");
            helper.setBackgroundRes(R.id.cbCheck,R.drawable.shape_color_accent_radius_5_bg);
            helper.setTextColor(R.id.cbCheck,mContext.getResources().getColor(R.color.white));
        }

    }

}