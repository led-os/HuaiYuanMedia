package cn.tklvyou.huaiyuanmedia.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qiangshen on 2017/5/3.
 * 接口回调的工具类，当两个activity之间不方便传输数据的时候可以使用
 */

public class InterfaceUtils {

    private static InterfaceUtils sInterfaceUtils;

    private InterfaceUtils() {
        mResults = new ArrayList<>();
    }

    public static InterfaceUtils getInstance() {
        if (sInterfaceUtils == null) {
            sInterfaceUtils = new InterfaceUtils();
        }
        return sInterfaceUtils;
    }

    public interface OnClickResult {
        void onResult(String msg);
    }

    private List<OnClickResult> mResults;

    public void add(OnClickResult activity) {
        mResults.add(activity);
    }

    public void remove(OnClickResult activity) {
        mResults.remove(activity);
    }

    public void clear() {
        mResults.clear();
    }

    /**
     * 可以修改，让使用者根据需求进行设置（如需要数据可以使用参数）
     */
    public void onClick(String msg) {
        for (OnClickResult result : mResults) {
            result.onResult(msg);
        }
    }
}