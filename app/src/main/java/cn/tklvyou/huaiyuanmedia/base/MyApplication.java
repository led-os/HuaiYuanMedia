package cn.tklvyou.huaiyuanmedia.base;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import com.billy.android.loading.Gloading;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.smtt.sdk.QbSdk;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.common.Contacts;
import cn.tklvyou.huaiyuanmedia.crash.CrashManager;
import cn.tklvyou.huaiyuanmedia.manager.FrameLifecycleCallbacks;
import cn.tklvyou.huaiyuanmedia.utils.DateUtils;
import skin.support.SkinCompatManager;
import skin.support.app.SkinAppCompatViewInflater;
import skin.support.content.res.SkinCompatUserThemeManager;
import skin.support.design.app.SkinMaterialViewInflater;
import skin.support.utils.Slog;

/**
 * Created by Administrator on 2019/2/27.
 */

public class MyApplication extends MultiDexApplication {

    public static int screenWidth;
    public static int screenHeight;

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header（new BezierCircleHeader(context)）
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    private static Application mContext;

    private IWXAPI iwxapi;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        screenWidth = ScreenUtils.getScreenWidth();
        screenHeight = ScreenUtils.getScreenHeight();

        //异常处理初始化
        CrashManager.init(mContext);
        //初始化微信相关配置
        initWx();
        //初始化微博配置
        initWb();


        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        //初始化工具包
        Utils.init(this);

        GlobalClickCallbacks.init(this);

        //设置为true时Logcat会输出日志
        Gloading.debug(true);
        //初始化状态管理
        Gloading.initDefault(new GlobalAdapter());

        initTencentTBS();

//        initSkinManager();
    }

    private void initSkinManager() {
        // 框架换肤日志打印
        Slog.DEBUG = true;

//        SkinCompatManager.withoutActivity(this)
//                .addInflater(new SkinAppCompatViewInflater())           // 基础控件换肤初始化
//                .setSkinStatusBarColorEnable(false)                     // 关闭状态栏换肤，默认打开[可选]
//                .setSkinWindowBackgroundEnable(false)                   // 关闭windowBackground换肤，默认打开[可选]
//                .loadSkin();
        SkinCompatManager.withoutActivity(this)
                .addInflater(new SkinAppCompatViewInflater())   // 基础控件换肤
                .addInflater(new SkinMaterialViewInflater())    // material design
                .setSkinStatusBarColorEnable(true)              // 关闭状态栏换肤
//                .setSkinWindowBackgroundEnable(false)           // 关闭windowBackground换肤
//                .setSkinAllActivityEnable(false)                // true: 默认所有的Activity都换肤; false: 只有实现SkinCompatSupportable接口的Activity换肤
                .loadSkin();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (DateUtils.compareToDate(DateUtils.getNowDate(0), "2020-04-08")) {
            SkinCompatManager.getInstance().loadSkin("gray", new SkinCompatManager.SkinLoaderListener() {
                @Override
                public void onStart() {
                    System.out.println("===============  onStart ===================");
                }

                @Override
                public void onSuccess() {
                    System.out.println("===============  onSuccess ===================");
                }

                @Override
                public void onFailed(String errMsg) {
                    System.out.println("===============  " + errMsg + " ===================");
                }
            }, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);

        } else {
            // 清除所有已有颜色值。
            SkinCompatUserThemeManager.get().clearColors();
        }

//        SkinCompatManager.getInstance().notifyUpdateSkin();
//        SkinCompatUserThemeManager.get().apply();

    }


    private void initTencentTBS() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    private void initWb() {
        WbSdk.install(this, new AuthInfo(this, Contacts.WB_APP_KEY, Contacts.WB_REDIRECT_URL,
                Contacts.WB_SCOPE));
    }


    private void initWx() {
        iwxapi = WXAPIFactory.createWXAPI(this, Contacts.WX_APPID, true);
        iwxapi.registerApp(Contacts.WX_APPID);
    }


    public static Context getAppContext() {
        return mContext;
    }


    public static Application getInstance() {
        return mContext;
    }

}
