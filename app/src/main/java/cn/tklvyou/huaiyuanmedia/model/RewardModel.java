package cn.tklvyou.huaiyuanmedia.model;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2020年10月21日17:11
 * @Email: 971613168@qq.com
 */
public class RewardModel {
    private long id;
    private String createtime;
    private String award;
    private int type;
    private String name;

    /*{
        "id": 177.0,
            "createtime": "2020-10-20 09:36",
            "award": "纸巾一盒",
            "type": 2.0
    }*/

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
