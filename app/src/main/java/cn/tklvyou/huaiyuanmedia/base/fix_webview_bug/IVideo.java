package cn.tklvyou.huaiyuanmedia.base.fix_webview_bug;

import android.view.View;
import android.webkit.WebChromeClient;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;

public interface IVideo {

    void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback);


    void onHideCustomView();


    boolean isVideoState();
}
