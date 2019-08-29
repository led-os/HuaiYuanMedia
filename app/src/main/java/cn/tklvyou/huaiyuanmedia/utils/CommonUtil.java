package cn.tklvyou.huaiyuanmedia.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;

import cn.tklvyou.huaiyuanmedia.base.MyApplication;

/**
 * @author :JenkinsZhou
 * @description :公共工具类
 * @company :途酷科技
 * @date 2019年07月30日15:04
 * @Email: 971613168@qq.com
 */
public class CommonUtil {
    public static final String TAG = "CommonUtil";
    private static final int LENGTH_PHONE = 11;

    public static int getColor(int colorId) {
        return ContextCompat.getColor(MyApplication.getInstance(), colorId);
    }


    public static String getNotNullValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        return value;
    }


    /**
     * 获取应用名称
     *
     * @param context
     * @return
     */
    public static CharSequence getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getText(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.i(TAG, "TourCooUtil", "getAppName:" + e.getMessage());
        }
        return null;
    }


    /**
     * 获取Activity 根布局
     *
     * @param activity
     * @return
     */
    public static View getRootView(Activity activity) {
        if (activity == null) {
            return null;
        }
        if (activity.findViewById(android.R.id.content) == null) {
            return null;
        }
        return ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
    }


    public static Drawable getDrawable(int drawableId) {
        return ContextCompat.getDrawable(MyApplication.getInstance(), drawableId);
    }


    public static boolean isMobileNumber(String mobileNums) {
        if (TextUtils.isEmpty(mobileNums)) {
            return false;
        } else {
            String startValue = "1";
            return mobileNums.length() == LENGTH_PHONE && mobileNums.startsWith(startValue);
        }
    }


    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            if (null != packageManager) {
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                if (null != packageInfo) {
                    return packageInfo.versionName;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
