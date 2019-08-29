package cn.tklvyou.huaiyuanmedia.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;


/**
 * @author :JenkinsZhou
 * @description :屏幕尺寸相关方法
 * @company :途酷科技
 * @date 2019年06月25日14:33
 * @Email: 971613168@qq.com
 */
public class SizeUtil {

    public static DisplayMetrics getDisplayMetrics() {
        return Resources.getSystem().getDisplayMetrics();
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }

    /**
     * px转dp
     *
     * @param pxValue
     * @return
     */
    public static int px2dp(float pxValue) {
        final float scale = getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转px
     *
     * @param dpValue
     * @return
     */
    public static float dp2px(float dpValue) {
        final float scale = getDisplayMetrics().density;
        return  (dpValue * scale + 0.5f);
    }

    /**
     * px转sp
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        final float fontScale = getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param spValue
     * @return
     */
    public static float sp2px(float spValue) {
        final float fontScale = getDisplayMetrics().scaledDensity;
        return  (spValue * fontScale + 0.5f);
    }

}
