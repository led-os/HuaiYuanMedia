package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

/**
 * 视听 频道列表
 * Created by yiwei on 16/5/17.
 */
public class AudioListRvAdapter extends BaseQuickAdapter<NewsBean, BaseViewHolder> {

    private Map<Integer, Boolean> map = new HashMap<>();
    private boolean onBind;
    private int checkedPosition = 0;

    public AudioListRvAdapter(int layoutResId, @Nullable List<NewsBean> data) {
        super(layoutResId, data);
        //默认勾选第一条数据
        if (data != null && data.size() > 0)
            map.put(0, true);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NewsBean item) {
        helper.setText(R.id.mTvName, item.getName());
        RadioButton mTvName = helper.getView(R.id.mTvName);
        mTvName.setOnClickListener(v -> {
            int position = helper.getLayoutPosition();
            if (map != null && map.containsKey(position)) {
                mTvName.setClickable(false);
            } else {
                map.clear();
                map.put(position, true);
                checkedPosition = position;
                mTvName.setChecked(true);
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }

            if (!onBind) {
                notifyDataSetChanged();
            }
        });

        onBind = true;
        if (map != null && map.containsKey(helper.getLayoutPosition())) {
            mTvName.setChecked(true);
        } else {
            mTvName.setChecked(false);
        }
        onBind = false;
    }


    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


}