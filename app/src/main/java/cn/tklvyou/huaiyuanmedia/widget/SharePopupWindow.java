package cn.tklvyou.huaiyuanmedia.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.tklvyou.huaiyuanmedia.R;


/**
 * popupWindow
 */
public class SharePopupWindow extends PopupWindow {
    private int popupWidth;
    private int popupHeight;
    private View parentView;
    private Context mContext;

    private TextView btnShareWX;
    private TextView btnShareQQ;
    private TextView btnShareWXFriend;
    private TextView btnShareWB;
    private TextView btnCancel;


    public SharePopupWindow(Context context) {
        super(context);
        this.mContext = context;
        initView(context);
        setPopConfig();
    }
    /**
     *   初始化控件
     * @param context
     */
    private void initView(Context context) {
        parentView = View.inflate(context, R.layout.layout_share_popup_window, null);
        setContentView(parentView);
        btnShareWX = parentView.findViewById(R.id.btnShareWX);
        btnShareWB = parentView.findViewById(R.id.btnShareWB);
        btnShareQQ = parentView.findViewById(R.id.btnShareQQ);
        btnShareWXFriend = parentView.findViewById(R.id.btnShareWXFriend);
        btnCancel = parentView.findViewById(R.id.btnCancel);

        btnShareQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onQQClick();
                }
            }
        });

        btnShareWX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onWxClick();
                }
            }
        });

        btnShareWB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onWBClick();
                }
            }
        });

        btnShareWXFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onWxFriendClick();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     *
     * 配置弹出框属性
     * @updateInfo (此处输入修改内容,若无修改可不写.)
     *
     */
    private void setPopConfig() {
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setOutsideTouchable(true);// 设置外部触摸会关闭窗口
        // 设置动画
//        this.setAnimationStyle(R.style.IosDialog);


        //获取自身的长宽高
        parentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = parentView.getMeasuredHeight();
        popupWidth = parentView.getMeasuredWidth();
    }



    /**
     * 显示在屏幕的下方
     */
    public void showAtScreenBottom(View parent){
        this.showAtLocation(parent, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
        popOutShadow();
    }


    /**
     * 设置显示在v上方(以v的左边距为开始位置)
     * @param v
     */
    public void showUp(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        showAtLocation(v, Gravity.NO_GRAVITY, (location[0]) - popupWidth / 2, location[1] - popupHeight);
        popOutShadow();
    }

    /**
     * 设置显示在v上方（以v的中心位置为开始位置）
     * @param v
     */
    public void showUp2(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
        popOutShadow();
    }


    /**
     * 让popupwindow以外区域阴影显示
     */
    private void popOutShadow() {
        final Window window = ((Activity) mContext).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.5f;//设置阴影透明度
        window.setAttributes(lp);
        setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.alpha = 1f;
                window.setAttributes(lp);
            }
        });
    }


    private ISharePopupWindowClickListener listener;

    public void setISharePopupWindowClickListener(ISharePopupWindowClickListener listener){
        this.listener = listener;
    }

    public interface ISharePopupWindowClickListener{
        void onWxClick();
        void onWxFriendClick();
        void onQQClick( );
        void onWBClick( );
    }

}