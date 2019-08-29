package cn.tklvyou.huaiyuanmedia.widget.dailog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.tklvyou.huaiyuanmedia.R;

public class SelectListDialog extends Dialog {

    private Context mContext;
    private TextView titleTv;//消息标题文本
    private String titleStr;//从外界设置的title文本
    private RecyclerView.LayoutManager manager;//从外界设置的LayoutManager
    private RecyclerView mRecyclerView;
    private TBaseAdapter adapter;

    private TBaseAdapter.OnAdapterItemClickListener itemOnclickListener;//按钮被点击了的监听器


    /**
     * 设置按钮的监听
     * @param itemOnclickListener
     */
    public void setItemOnclickListener(TBaseAdapter.OnAdapterItemClickListener itemOnclickListener) {
        this.itemOnclickListener = itemOnclickListener;
    }

    public SelectListDialog(Context context) {
        super(context, R.style.ConfirmDialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_list_layout);
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
        adapter.setOnAdapterItemClickListener(itemOnclickListener);
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }

        if(manager != null){
            mRecyclerView.setLayoutManager(manager);
        }

        mRecyclerView.setAdapter(adapter);

    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        titleTv = findViewById(R.id.tvTitle);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            return;
        }
        setHeight();
    }

    private void setHeight() {
        Window window = getWindow();
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (window.getDecorView().getHeight() >= (int) (displayMetrics.heightPixels * 0.6)) {
            attributes.height = (int) (displayMetrics.heightPixels * 0.5);
        }
        window.setAttributes(attributes);
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
     * 从外界Activity为Dialog设置LayoutManager
     *
     * @param manager
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        this.manager = manager;
    }

    public void setAdapter(TBaseAdapter adapter){
        this.adapter = adapter;
    }

    @Override
    public void show() {
        if(adapter == null){
            Log.d("SelectListDialog","列表弹窗需要先调用setAdapter()方法!");
        }else {
            super.show();
        }
    }
}