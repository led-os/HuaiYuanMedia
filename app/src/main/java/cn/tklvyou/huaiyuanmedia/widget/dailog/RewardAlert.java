package cn.tklvyou.huaiyuanmedia.widget.dailog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cn.tklvyou.huaiyuanmedia.R;

/**
 * @author :JenkinsZhou
 * @description :中奖弹窗
 * @company :途酷科技
 * @date 2020/7/16 14:47
 * @Email: 971613168@qq.com
 */

public class RewardAlert extends Dialog {



    private TextView yes;//确定按钮
    private TextView no;//取消按钮
    private TextView titleTv;//消息标题文本
    private TextView messageTv;//消息提示文本
    private String titleStr;//从外界设置的title文本
    private String messageStr;//从外界设置的消息文本
    private SpannableStringBuilder styleMessage;//从外界设置的带样式消息文本
    //确定文本的显示内容
    private String yesStr,noStr;

    private CommonDialog.onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private CommonDialog.onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, CommonDialog.onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, CommonDialog.onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    public RewardAlert(Context context) {
        super(context, R.style.ConfirmDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reward_layout);
        //按空白处可以取消
        setCanceledOnTouchOutside(true);
        setCancelable(true);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }else {
                    dismiss();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }
        if (messageStr != null) {
            messageTv.setText(messageStr);
        }

        if (styleMessage != null) {
            messageTv.setText(styleMessage);
        }

        //如果设置按钮的文字
        if (yesStr != null) {
            yes.setText(yesStr);
        }

        if (noStr != null) {
            no.setText(noStr);
        }

    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        yes = findViewById(R.id.btnYes);
        no = findViewById(R.id.btnNo);
        titleTv = findViewById(R.id.tvTitle);
        messageTv = findViewById(R.id.tvMessage);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
        messageStr = message;
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param styleMessage
     */
    public void setStyleMessage(SpannableStringBuilder styleMessage) {
        this.styleMessage = styleMessage;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick();
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }
}
