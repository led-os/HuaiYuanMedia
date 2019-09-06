package cn.tklvyou.huaiyuanmedia.ui.listener;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.model.Channel;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public interface OnChannelListener {
    void initChannelList(List<String> selectList,List<String> unSelectList);
    void onItemMove(int starPos, int endPos);
    void onMoveToMyChannel(int starPos, int endPos);
    void onMoveToOtherChannel(int starPos, int endPos);
}