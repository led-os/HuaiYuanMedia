package cn.tklvyou.huaiyuanmedia.utils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;

public class YFileUtils {

    /**
     * 截取视频图片
     *
     * @param videoPath
     * @return
     */
    public static String[] videoCatchImg(String videoPath, float cutTime) {
        String[] result = new String[2];
        File file = new File(videoPath);
        if (!file.exists()) {
            System.err.println();
            LogUtils.e("路径[" + videoPath + "]对应的视频文件不存在!");
            result[0] = "";
            result[1] = "";
            return result;
        }

        int totalSeconds = (int) Math.floor(cutTime);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;


        String fileName = videoPath.substring(0, videoPath.lastIndexOf(".")) + "-cover.jpeg";

        StringBuilder commands = new StringBuilder();
        //设置截取视频第3秒时的画面
        commands.append("-ss");
        commands.append(" " + String.format(Locale.US, "00:%02d:%02d", minutes, seconds));
        //输入文件
        commands.append(" -i ");
        commands.append(videoPath);
        commands.append(" -vframes ");
        commands.append("1");
        //输出文件若存在可以覆盖
        commands.append(" -y");
        //指定图片编码格式
        commands.append(" -f ");
        commands.append("image2 ");
        //截取的图片路径
        commands.append(fileName);
        System.out.println("commands:" + commands);
        result[0] = commands.toString();
        result[1] = fileName;
        return result;
    }


}


