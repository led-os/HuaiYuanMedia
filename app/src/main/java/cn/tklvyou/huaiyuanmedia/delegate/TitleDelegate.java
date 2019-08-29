package cn.tklvyou.huaiyuanmedia.delegate;

import android.app.Activity;

import com.blankj.utilcode.util.LogUtils;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.utils.CommonUtil;
import cn.tklvyou.huaiyuanmedia.utils.StackUtil;


/**
 * @author :JenkinsZhou
 * @description :带TitleBarView 的Activity及Fragment代理
 * @company :途酷科技
 * @date 2019年06月27日10:20
 * @Email: 971613168@qq.com
 */
public class TitleDelegate {
    public static final String TAG = "TitleDelegate";
    public CommonTitleBar mTitleBar;

    public TitleDelegate(CommonTitleBar titleBar, ITitleView iTitleBarView, final Class<?> cls) {
        mTitleBar = titleBar;

        if (mTitleBar == null) {
            return;
        }
        LogUtils.i("class:" + cls.getSimpleName());
        //设置TitleBarView 所有TextView颜色
        mTitleBar.setLeftContent(CommonTitleBar.TYPE_LEFT_IMAGEBUTTON, "", 0, R.mipmap.icon_titlebar_back, 0);
        mTitleBar.setNavigationListener(v -> {
            Activity activity = StackUtil.getInstance().getActivity(cls);
            //增加判断避免快速点击返回键造成崩溃
            if (activity == null) {
                return;
            }
            activity.onBackPressed();
        });

        iTitleBarView.setTitleBar(mTitleBar);
    }

    /**
     * 获取Activity 标题({@link Activity#getTitle()}获取不和应用名称一致才进行设置-因Manifest未设置Activity的label属性获取的是应用名称)
     *
     * @param activity
     * @return
     */
    private CharSequence getTitle(Activity activity) {
        if (activity != null) {
            CharSequence appName = CommonUtil.getAppName(activity);
            CharSequence label = activity.getTitle();
            if (label != null && !label.equals(appName)) {
                return label;
            }
        }
        return "";
    }
}
