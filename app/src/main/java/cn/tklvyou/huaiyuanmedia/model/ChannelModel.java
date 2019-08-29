package cn.tklvyou.huaiyuanmedia.model;

import java.util.List;

public class ChannelModel {


    private List<String> selected;
    private List<String> unselected;

    public List<String> getSelected() {
        return selected;
    }

    public void setSelected(List<String> selected) {
        this.selected = selected;
    }

    public List<String> getUnselected() {
        return unselected;
    }

    public void setUnselected(List<String> unselected) {
        this.unselected = unselected;
    }
}
