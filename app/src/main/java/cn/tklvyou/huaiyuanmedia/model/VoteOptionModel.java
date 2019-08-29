package cn.tklvyou.huaiyuanmedia.model;

import java.io.Serializable;

public class VoteOptionModel implements Serializable {


    /**
     * id : 1
     * vote_id : 1
     * name : 投票选项1
     * image :
     * count : 0
     * status : normal
     * createtime : 0
     */

    private int id;
    private int vote_id;
    private String name;
    private String image;
    private int count;
    private int check;
    private String status;
    private int createtime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVote_id() {
        return vote_id;
    }

    public void setVote_id(int vote_id) {
        this.vote_id = vote_id;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public int getCreatetime() {
        return createtime;
    }

    public void setCreatetime(int createtime) {
        this.createtime = createtime;
    }
}
