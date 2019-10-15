package cn.tklvyou.huaiyuanmedia.ui.video_edit.localEdit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;

/**
 * description:
 * Created by aserbao on 2018/2/6.
 */
public class ThumbAdapter extends RecyclerView.Adapter<ThumbAdapter.MyViewHolder> {

    private Context mContext;
    private List<Bitmap> mBitmapList = new ArrayList<>();

    public ThumbAdapter(Context context) {
        mContext = context;
    }
    public void setLoadSuccessCallBack(LoadSuccessCallBack loadSuccessCallBack){
        mLoadSuccessCallBack = loadSuccessCallBack;
    }

    public void addThumb (List<Bitmap> bitmaps){
        if (mBitmapList != null) {
            mBitmapList.addAll(bitmaps);
            notifyDataSetChanged();
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.thumb_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Bitmap bitmap = mBitmapList.get(position);
        holder.mItemThumbIv.setImageBitmap(bitmap);
        if(position == mBitmapList.size()-1 && mLoadSuccessCallBack != null ){
            mLoadSuccessCallBack.callback();
        }
    }

    @Override
    public int getItemCount() {
        int ret = 0;
        if (mBitmapList != null) {
            ret = mBitmapList.size();
        }
        return ret;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mItemThumbIv;
        public MyViewHolder(View itemView) {
            super(itemView);
            mItemThumbIv = itemView.findViewById(R.id.item_thumb_iv);
        }
    }
    private LoadSuccessCallBack mLoadSuccessCallBack;
    public interface LoadSuccessCallBack{
        void callback();
    }
}
