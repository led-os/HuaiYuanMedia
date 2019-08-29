package cn.tklvyou.huaiyuanmedia.utils;

import java.lang.reflect.Field;

import cn.tklvyou.huaiyuanmedia.R;

public class YResourceUtils {

    /**
     * 通过资源名称 获取对应的图片资源id
     * @param variableName
     * @return
     */
    public static int getMipmapResId(String variableName){
        return getResId(variableName, R.mipmap.class);
    }


    /**
     * 通过资源名称 获取对应的资源id
     * @param variableName
     * @param c
     * @return
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
