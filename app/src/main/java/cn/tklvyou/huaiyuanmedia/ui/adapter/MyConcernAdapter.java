package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.ConcernModel;

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
        ImageView imageView = helper.getView(R.id.ivAvatar);
        GlideManager.setCirclePlaceholder(ContextCompat.getDrawable(imageView.getContext(),R.mipmap.default_avatar));
        GlideManager.loadCircleImg(item.getAvatar(),imageView);
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