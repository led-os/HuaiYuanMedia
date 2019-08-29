package cn.tklvyou.huaiyuanmedia.model;

import java.io.Serializable;

public class VoteModel implements Serializable {


    /**
     * id : 1
     * admin_id : 0
     * name : 测试
     * image : /uploads/20190802/c1d3aca7ef2b78b407afc9162048c089.png
     * content : <p>啊手动阀手动阀</p>
     * status : normal
     * createtime : 1564810263
     * updatetime : 1564810263
     */

    private int id;
    private int admin_id;
    private String name;
    private String image;
    private String content;
    private String status;
    private int createtime;
    private int updatetime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
