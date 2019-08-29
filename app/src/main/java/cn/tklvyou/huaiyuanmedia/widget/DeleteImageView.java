package cn.tklvyou.huaiyuanmedia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.tklvyou.huaiyuanmedia.R;


/**
* 带删除图标的ImageView
*/
public class DeleteImageView extends RelativeLayout {

   private ImageView mImg;
   private ImageView mIv_delete;
   private FrameLayout delLayout;

   public DeleteImageView(Context context) {

       this(context, null);

   }

   public DeleteImageView(Context context, AttributeSet attrs) {
       this(context, attrs, 0);
   }

   public DeleteImageView(Context context, AttributeSet attrs, int defStyleAttr) {
       super(context, attrs, defStyleAttr);
       LayoutInflater inflater = LayoutInflater.from(context);
       View view= inflater.inflate(R.layout.delete_view,this);

       mImg = view.findViewById(R.id.iv_img);
       mIv_delete = view.findViewById(R.id.iv_delete);
       delLayout = view.findViewById(R.id.delLayout);
   }

   //向外部提供接口
   public ImageView getImg() {
       return mImg;
   }

    //向外部提供接口
    public ImageView getDelImg() {
        return mIv_delete;
    }

  public void setDeleteListener(OnClickListener onClickListener){
      delLayout.setOnClickListener(onClickListener);
   }

}