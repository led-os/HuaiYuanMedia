package cn.tklvyou.huaiyuanmedia.model;

public class PointModel {


    /**
     * id : 2
     * name : Tensorflow 入场劵
     * image : http://medium.tklvyou.cn/uploads/20190801/edf701419f569070b24fa42bd27ba32f.jpg
     * score : 5
     * content :
     * stock : 3
     * weigh : 2
     * status : normal
     * createtime : 2019-08-01 08:50:43
     * updatetime : 1564621479
     * admin_id : 0
     * limit : 1000
     */

    private int id;
    private String name;
    private String image;
    private int score;
    private String content;
    private int stock;
    private String weigh;
    private String status;
    private String createtime;
    private int updatetime;
    private int admin_id;
    private int limit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getWeigh() {
        return weigh;
    }

    public void setWeigh(String weigh) {
        this.weigh = weigh;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(int updatetime) {
        this.updatetime = updatetime;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
