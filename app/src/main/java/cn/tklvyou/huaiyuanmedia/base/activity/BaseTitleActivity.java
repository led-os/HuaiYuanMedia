package cn.tklvyou.huaiyuanmedia.base.activity;


import android.os.Bundle;

import androidx.annotation.Nullable;

import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.delegate.ITitleView;
import cn.tklvyou.huaiyuanmedia.delegate.TitleDelegate;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2019年08月01日10:38
 * @Email: 971613168@qq.com
 */
public abstract class BaseTitleActivity<P extends BaseContract.BasePresenter> extends BaseActivity<P> implements ITitleView {
    protected TitleDelegate mTitleDelegate;
    protected CommonTitleBar titleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleDelegate = new TitleDelegate(getBaseTitleBar(), this, this.getClass());
        titleBar = mTitleDelegate.mTitleBar;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
