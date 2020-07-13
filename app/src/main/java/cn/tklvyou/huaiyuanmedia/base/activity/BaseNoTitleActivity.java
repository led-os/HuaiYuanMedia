package cn.tklvyou.huaiyuanmedia.base.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.SkinAppCompatDelegateImpl;
import androidx.fragment.app.FragmentManager;

import com.billy.android.loading.Gloading;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.ArrayList;
import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.base.SpecialAdapter;
import cn.tklvyou.huaiyuanmedia.manager.ThreadManager;
import cn.tklvyou.huaiyuanmedia.widget.FrameLayout4Loading;


/**
 * 类描述
 */

public abstract class BaseNoTitleActivity<P extends BaseContract.BasePresenter> extends RxAppCompatActivity implements BaseContract.BaseView {

    protected FragmentManager fragmentManager;
    protected Activity mContext;
    protected P mPresenter;
    protected View mContentView;

    public FrameLayout4Loading mFrameLayout4Loading;


    private boolean isAlive = false;

    //整个页面的加载视图
    private Gloading.Holder pageHolder;

    //小范围加载视图
    private Gloading.Holder specialLoadingHolder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayoutID());

        isAlive = true;
        threadNameList = new ArrayList<String>();

        mContentView = View.inflate(this, getActivityLayoutID(), (ViewGroup) findViewById(R.id.base_content));


        specialLoadingHolder = Gloading.from(new SpecialAdapter()).wrap(getLoadingView()).withRetry(new Runnable() {
            @Override
            public void run() {
                specialLoadingHolder.showLoading();
                onRetry();
            }
        });

        //在Activity中显示, 父容器为: android.R.id.content
        pageHolder = Gloading.getDefault().wrap(mContentView).withRetry(new Runnable() {
            @Override
            public void run() {
                onRetry();
            }
        });


        initBaseView();
        attachView();

        initView(savedInstanceState);
    }

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        return SkinAppCompatDelegateImpl.get(this, this);
    }


    /**
     * 覆写此方法可以更改Loading绑定的View
     *
     * @return
     */
    protected View getLoadingView() {
        return mContentView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThreadManager.getInstance().destroyThread(threadNameList);
        isAlive = false;
        fragmentManager = null;
        threadNameList = null;
        detachView();
    }

    private void initBaseView() {
        mFrameLayout4Loading = findViewById(R.id.base_content);

        fragmentManager = getSupportFragmentManager();
        mPresenter = initPresenter();
        mContext = this;
    }


    /**
     * 挂载view
     */
    private void attachView() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }


    /**
     * 卸载view
     */
    private void detachView() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    /**
     * 初始化View
     *
     * @param savedInstanceState
     */
    protected abstract void initView(@Nullable Bundle savedInstanceState);

    /**
     * 在子View中初始化Presenter
     *
     * @return
     */
    protected abstract P initPresenter();

    /**
     * 设置Activity的布局ID
     *
     * @return
     */
    protected abstract int getActivityLayoutID();

    @Override
    public void showLoading() {
        runUiThread(() -> specialLoadingHolder.showLoading());
    }

    @Override
    public void showPageLoading() {
        runUiThread(() -> pageHolder.showLoading());
    }


    @Override
    public void showSuccess(String message) {
        runUiThread(() -> {
            specialLoadingHolder.showLoadSuccess();
            pageHolder.showLoadSuccess();
            if (!StringUtils.isEmpty(message)) {
                ToastUtils.showShort(message);
            }
        });
    }

    @Override
    public void showFailed(String message) {
        runUiThread(() -> {
            specialLoadingHolder.showLoadFailed();
            if (!StringUtils.isEmpty(message)) {
                ToastUtils.showShort(message);
            }
        });

    }

    @Override
    public void showNoNet() {
        runUiThread(() -> {
            specialLoadingHolder.showLoadFailed();
        });

    }

    @Override
    public void showNoData() {
        runUiThread(() -> {
            specialLoadingHolder.showEmpty();
        });

    }


    @Override
    public void onRetry() {

    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.bindUntilEvent(ActivityEvent.DESTROY);
    }


    /**
     * 显示软件盘
     */
    public void showSoftInput(EditText view) {
        if (view != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null) {
                im.showSoftInput(view, 0);
            }
        }
    }

    /**
     * 隐藏软件盘
     */
    public void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null) {
                im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    //运行线程 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 在UI线程中运行，建议用这个方法代替runOnUiThread
     *
     * @param action
     */
    public final void runUiThread(Runnable action) {
        if (!isAlive) {
            LogUtils.w("runUiThread  isAlive() == false >> return;");
            return;
        }
        runOnUiThread(action);
    }

    /**
     * 线程名列表
     */
    protected List<String> threadNameList;

    /**
     * 运行线程
     *
     * @param name
     * @param runnable
     * @return
     */
    public final Handler runThread(String name, Runnable runnable) {
        if (!isAlive) {
            LogUtils.w("runThread  isAlive() == false >> return null;");
            return null;
        }
        Handler handler = ThreadManager.getInstance().runThread(name, runnable);
        if (handler == null) {
            LogUtils.e("runThread handler == null >> return null;");
            return null;
        }

        if (threadNameList.contains(name) == false) {
            threadNameList.add(name);
        }
        return handler;
    }

    //运行线程 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}
