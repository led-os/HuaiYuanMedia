package com.tencent.liteav.demo.player.superplayer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tencent.liteav.demo.player.R;
import com.tencent.liteav.demo.player.utils.TCUtils;

import java.util.ArrayList;

/**
 * Created by liyuejiao on 2018/7/3.
 */

public class TCVodPlayerListAdapter extends RecyclerView.Adapter<TCVodPlayerListAdapter.ViewHolder> {
    private Context                 mContext;
    private ArrayList<VideoModel>   mSuperPlayerModelList;
    private OnItemClickListener     mOnItemClickListener;

    public TCVodPlayerListAdapter(Context context) {
        mContext = context;
        mSuperPlayerModelList = new ArrayList<VideoModel>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.superplayer_item_new_vod, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final VideoModel videoModel = mSuperPlayerModelList.get(position);
        Glide.with(mContext).load(videoModel.placeholderImage).into(holder.thumb);
        if (videoModel.duration > 0) {
            holder.duration.setText(TCUtils.formattedTime(videoModel.duration));
        } else {
            holder.duration.setText("");
        }
        if (videoModel.title != null) {
            holder.title.setText(videoModel.title);
        }
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, videoModel);
                }
            }
        });
        holder.thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, videoModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSuperPlayerModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView    duration;
        private TextView    title;
        private ImageView   thumb;

        public ViewHolder(final View itemView) {
            super(itemView);
            thumb = (ImageView) itemView.findViewById(R.id.superplayer_iv);
            title = (TextView) itemView.findViewById(R.id.superplayer_tv);
            duration = (TextView) itemView.findViewById(R.id.superplayer_tv_duration);
        }
    }

    /**
     * 添加一个SuperPlayerModel
     *
     * @param superPlayerModel
     */
    public void addSuperPlayerModel(VideoModel superPlayerModel) {
        notifyItemInserted(mSuperPlayerModelList.size());
        mSuperPlayerModelList.add(superPlayerModel);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void clear() {
        mSuperPlayerModelList.clear();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, VideoModel superPlayerModel);
    }
}
