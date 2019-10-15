package cn.tklvyou.huaiyuanmedia.model;

import java.util.List;

public class PingXuanModel {


    /**
     * id : 1
     * admin_id : 0
     * name : 顶哥的由来
     * detail : 顶哥的由来顶哥的由来顶哥的由来顶哥的由来顶哥的由来顶哥的由来顶哥的由来顶哥的由来顶哥的由来
     * company : 研究院
     * image : /qiniu/20191014/FtxRuZJYr7dLUR73X3AiTbrf-ZPw.png
     * images : []
     * content : <p>阿斯顿发水电费</p>
     * weigh : 1
     * status : normal
     * createtime : 1571043937
     * updatetime : 1571043937
     * endtime : 1571216628
     * record_num : 0
     * visit_num : 4
     * option_num : 0
     * limit_today_option : 1
     * limit_all_option : 1
     * limit_today_user : 1
     * limit_all_user : 1
     */

    private int id;
    private String name;
    private String detail;
    private String company;
    private String image;
    private String content;
    private int endtime;
    private int record_num;
    private int visit_num;
    private int option_num;
    private List<String> images;

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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public int getEndtime() {
        return endtime;
    }

    public void setEndtime(int endtime) {
        this.endtime = endtime;
    }

    public int getRecord_num() {
        return record_num;
    }

    public void setRecord_num(int record_num) {
        this.record_num = record_num;
    }

    public int getVisit_num() {
        return visit_num;
    }

    public void setVisit_num(int visit_num) {
        this.visit_num = visit_num;
    }

    public int getOption_num() {
        return option_num;
    }

    public void setOption_num(int option_num) {
        this.option_num = option_num;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
