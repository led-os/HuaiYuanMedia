package cn.tklvyou.huaiyuanmedia.model;

import java.util.List;

public class MyCommentModel {

    /**
     * id : 1052
     * user_id : 119
     * admin_id : 0
     * module : 爆料
     * module_second : 民政局
     * name : 关于人才补贴的问题
     * image :
     * images : ["http://qiniu.dashuiniu.com.cn/qiniu/20190906/119_1567752657792_moOJVu.jpg","http://qiniu.dashuiniu.com.cn/qiniu/20190906/119_1567752657799_Hrpi4Y.jpg","http://qiniu.dashuiniu.com.cn/qiniu/20190906/119_1567752657802_X9hEJG.jpg","http://qiniu.dashuiniu.com.cn/qiniu/20190906/119_1567752657805_sAJjLw.jpg","http://qiniu.dashuiniu.com.cn/qiniu/20190906/119_1567752657808_38Jby7.jpg","http://qiniu.dashuiniu.com.cn/qiniu/20190906/119_1567752657810_YsXrtv.jpg"]
     * video :
     * audio :
     * content : 人才补贴需要什么条件
     * nickname : 匿名用户阿里里查记录是来
     * avatar : http://huaiyuan.dashuiniu.com.cn/assets/img/avatar.png
     * status : normal
     * visit_num : 6
     * comment_num : 1
     * like_num : 0
     * createtime : 1567752658
     * updatetime : 1567752658
     * vote_id : 0
     * weigh : 1052
     * time :
     * type :
     * url :
     * zhuanma : 0
     * shuiyin : 0
     * like_num_today : 0
     * like_lastdate :
     * comment_id : 473
     * comment_avatar : http://huaiyuan.dashuiniu.com.cn/assets/img/avatar.png
     * comment_nickname : 匿名用户阿里里查记录是来
     * detail : 腊八醋
     * comment_createtime : 1567761948
     * comment_begintime : 18小时前
     * like_status : 0
     */

    private int id;
    private int user_id;
    private int admin_id;
    private String module;
    private String module_second;
    private String name;
    private String image;
    private String video;
    private String audio;
    private String content;
    private String nickname;
    private String avatar;
    private String status;
    private int visit_num;
    private int comment_num;
    private int like_num;
    private int createtime;
    private int updatetime;
    private int vote_id;
    private int weigh;
    private String time;
    private String type;
    private String url;
    private int zhuanma;
    private int shuiyin;
    private int like_num_today;
    private String like_lastdate;
    private int comment_id;
    private String comment_avatar;
    private String comment_nickname;
    private String detail;
    private int comment_createtime;
    private String begintime;
    private String comment_begintime;
    private int like_status;
    private List<String> images;
    private boolean isSelect;

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

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModule_second() {
        return module_second;
    }

    public void setModule_second(String module_second) {
        this.module_second = module_second;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getVisit_num() {
        return visit_num;
    }

    public void setVisit_num(int visit_num) {
        this.visit_num = visit_num;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public int getLike_num() {
        return like_num;
    }

    public void setLike_num(int like_num) {
        this.like_num = like_num;
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

    public int getVote_id() {
        return vote_id;
    }

    public void setVote_id(int vote_id) {
        this.vote_id = vote_id;
    }

    public int getWeigh() {
        return weigh;
    }

    public void setWeigh(int weigh) {
        this.weigh = weigh;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getZhuanma() {
        return zhuanma;
    }

    public void setZhuanma(int zhuanma) {
        this.zhuanma = zhuanma;
    }

    public int getShuiyin() {
        return shuiyin;
    }

    public void setShuiyin(int shuiyin) {
        this.shuiyin = shuiyin;
    }

    public int getLike_num_today() {
        return like_num_today;
    }

    public void setLike_num_today(int like_num_today) {
        this.like_num_today = like_num_today;
    }

    public String getLike_lastdate() {
        return like_lastdate;
    }

    public void setLike_lastdate(String like_lastdate) {
        this.like_lastdate = like_lastdate;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_avatar() {
        return comment_avatar;
    }

    public void setComment_avatar(String comment_avatar) {
        this.comment_avatar = comment_avatar;
    }

    public String getComment_nickname() {
        return comment_nickname;
    }

    public void setComment_nickname(String comment_nickname) {
        this.comment_nickname = comment_nickname;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getComment_createtime() {
        return comment_createtime;
    }

    public void setComment_createtime(int comment_createtime) {
        this.comment_createtime = comment_createtime;
    }

    public String getBegintime() {
        return begintime;
    }

    public void setBegintime(String begintime) {
        this.begintime = begintime;
    }

    public String getComment_begintime() {
        return comment_begintime;
    }

    public void setComment_begintime(String comment_begintime) {
        this.comment_begintime = comment_begintime;
    }

    public int getLike_status() {
        return like_status;
    }

    public void setLike_status(int like_status) {
        this.like_status = like_status;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
