package cn.tklvyou.huaiyuanmedia.base;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import static com.billy.android.loading.Gloading.STATUS_EMPTY_DATA;
import static com.billy.android.loading.Gloading.STATUS_LOADING;
import static com.billy.android.loading.Gloading.STATUS_LOAD_FAILED;
import static com.billy.android.loading.Gloading.STATUS_LOAD_SUCCESS;

import cn.tklvyou.huaiyuanmedia.R;

public class GlobalLoadingStatusView extends LinearLayout implements View.OnClickListener {

        private final TextView mTextView;
        private final Runnable mRetryTask;
        private final ImageView mImageView;


        public GlobalLoadingStatusView(Context context, Runnable retryTask) {
            super(context);
            //初始化LoadingView
            //如果需要支持点击重试，在适当的时机给对应的控件添加点击事件
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER_HORIZONTAL);
            LayoutInflater.from(context).inflate(R.layout.view_global_loading_status, this, true);
            mImageView = findViewById(R.id.image);
            mTextView = findViewById(R.id.text);
            this.mRetryTask = retryTask;
            setBackgroundColor(0xFFF0F0F0);
        }

        public void setMsgViewVisibility(boolean visible) {
            mTextView.setVisibility(visible ? VISIBLE : GONE);
        }

        public void setStatus(int status) {
            //设置当前的加载状态：加载中、加载失败、空数据等
            //其中，加载失败可判断当前是否联网，可现实无网络的状态
            //		属于加载失败状态下的一个分支,可自行决定是否实现
            boolean show = true;
            View.OnClickListener onClickListener = null;
            int image = R.drawable.loading;
            int str = R.string.str_none;
            switch (status) {
                case STATUS_LOAD_SUCCESS: show = false; break;
                case STATUS_LOADING: str = R.string.loading; break;
                case STATUS_LOAD_FAILED:
                    str = R.string.load_failed;
                    //加载失败
                    image = R.mipmap.img_no_network;
                    Boolean networkConn = isNetworkConnected(getContext());
                    if (networkConn != null && !networkConn) {
                        str = R.string.load_failed_no_network;
                        //没有网络
                        image = R.mipmap.img_no_network;
                    }
                    onClickListener = this;
                    break;
                case STATUS_EMPTY_DATA:
                    str = R.string.empty;
                    image = R.mipmap.img_no_content;
                    break;
                default: break;
            }
            mImageView.setImageResource(image);
            setOnClickListener(onClickListener);
            mTextView.setText(str);
            setVisibility(show ? View.VISIBLE : View.GONE);

        }

        @Override
        public void onClick(View v) {
            if (mRetryTask != null) {
                mRetryTask.run();
            }
        }

        private Boolean isNetworkConnected(Context context) {
            try {
                context = context.getApplicationContext();
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) {
                    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                    return networkInfo != null && networkInfo.isConnected();
                }
            } catch (Exception ignored) {
            }
            return null;
        }


    }