package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

public class AudioRvAdapter extends BaseQuickAdapter<NewsBean, BaseViewHolder> {

    public AudioRvAdapter(int layoutRes, List<NewsBean> data) {
        super(layoutRes, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NewsBean bean) {
        helper.setText(R.id.tvName, bean.getName());
        helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());

        GlideManager.loadRoundImg(bean.getImage(), helper.getView(R.id.ivImage));

    }
}
