package cn.tklvyou.huaiyuanmedia.model;

import java.util.List;

public class AttentionModel {

    private List<ConcernModel> attention;
    private List<ConcernModel> noattention;

    public List<ConcernModel> getAttention() {
        return attention;
    }

    public void setAttention(List<ConcernModel> attention) {
        this.attention = attention;
    }

    public List<ConcernModel> getNoattention() {
        return noattention;
    }

    public void setNoattention(List<ConcernModel> noattention) {
        this.noattention = noattention;
    }


}
