package cn.tklvyou.huaiyuanmedia.model;

import java.io.Serializable;

public class TelModel implements Serializable {

    /**
     * id : 16
     * article_id : 105
     * date : 2019-08-04
     * content : <p>阿斯蒂芬&nbsp;</p>
     * admin_id : 1
     * createtime : 1564820618
     * updatetime : 1564820618
     */

    private int id;
    private int article_id;
    private String date;
    private String content;
    private int admin_id;
    private int createtime;
    private int updatetime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public int getCreatetime() {
        return createtime;
    }

    public void setCreatetime(int createtime) {
        this.createtime = createtime;
    }

    public int getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(int updatetime) {
        this.updatetime = updatetime;
    }
}
