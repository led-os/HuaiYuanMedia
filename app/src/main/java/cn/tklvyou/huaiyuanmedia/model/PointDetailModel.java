package cn.tklvyou.huaiyuanmedia.model;

/**
 * @author :JenkinsZhou
 * @description :积分明细
 * @company :途酷科技
 * @date 2019年08月01日17:16
 * @Email: 971613168@qq.com
 */
public class PointDetailModel {

    /**
     * id : 81
     * user_id : 13
     * name : 转盘抽奖
     * type : 1
     * amount : 5
     * createtime : 2019-08-01 17:02:44
     * updatetime : 1564650164
     * score : 149
     * symbol : +
     * admin_id : 0
     */

    private int id;
    private int user_id;
    private String name;
    private int type;
    private int amount;
    private String createtime;
    private int updatetime;
    private int score;
    private String symbol;
    private int admin_id;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }
}
