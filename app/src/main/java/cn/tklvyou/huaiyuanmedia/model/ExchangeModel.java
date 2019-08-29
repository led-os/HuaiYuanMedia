package cn.tklvyou.huaiyuanmedia.model;

/**
 * @author :JenkinsZhou
 * @description :兑换信息实体
 * @company :途酷科技
 * @date 2019年08月02日10:11
 * @Email: 971613168@qq.com
 */
public class ExchangeModel {


    /**
     * id : 12
     * user_id : 13
     * goods_id : 2
     * name : Tensorflow 入场劵
     * image : http://medium.tklvyou.cn/uploads/20190801/edf701419f569070b24fa42bd27ba32f.jpg
     * score : 5
     * status : hidden
     * createtime : 2019-08-02 10:05:16
     * updatetime : 1564711516
     * admin_id : 0
     */

    private int id;
    private int user_id;
    private int goods_id;
    private String name;
    private String image;
    private int score;
    private String status;
    private String createtime;
    private int updatetime;
    private int admin_id;
    private int register_status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
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

    public int getRegister_status() {
        return register_status;
    }

    public void setRegister_status(int register_status) {
        this.register_status = register_status;
    }
}
