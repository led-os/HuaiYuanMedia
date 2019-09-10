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

public class SearchHistoryRvAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public SearchHistoryRvAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        helper.setText(R.id.tvName, item);
    }

}