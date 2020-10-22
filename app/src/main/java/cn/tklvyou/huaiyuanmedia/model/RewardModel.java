package cn.tklvyou.huaiyuanmedia.model;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2020年10月21日17:11
 * @Email: 971613168@qq.com
 */
public class RewardModel {
    /**
     *  {
     *         "id": 175,
     *         "createtime": 1603093816,
     *         "updatetime": 1603093816,
     *         "award": "纸巾一盒",
     *         "type": 2,
     *         "mobile": "18556070563",
     *         "name": "图",
     *         "address": "厕所",
     *         "area": "安徽省合肥市瑶海区"
     *     }
     */
    private long id;
    private String createtime;
    private String award;
    private int type;
    /**
     * 收货人
     */
    private String name;
    private String mobile;
    private String area;
    private String address;

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
