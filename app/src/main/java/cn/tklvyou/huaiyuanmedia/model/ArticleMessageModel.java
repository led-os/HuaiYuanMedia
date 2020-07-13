package cn.tklvyou.huaiyuanmedia.model;

import java.io.Serializable;
import java.util.List;

public class ArticleMessageModel implements Serializable {

    /**
     * id : 1599
     * user_id : 123
     * admin_id : 0
     * module : 生活圈
     * module_second :
     * name : 陈会计
     * image :
     * images : ["http://qiniu.dashuiniu.com.cn/qiniu/20191114/0_1573700932203_0GNetp.jpg"]
     * video :
     * audio :
     * content : null
     * nickname : 匿名用户5578
     * avatar : http://huaiyuan.dashuiniu.com.cn/assets/img/avatar.png
     * status : normal
     * visit_num : 8
     * comment_num : 0
     * like_num : 3
     * createtime : 1573700930
     * updatetime : 1573700930
     * vote_id : 0
     * weigh : 1599
     * time : null
     * type :
     * url :
     * zhuanma : 0
     * shuiyin : 0
     * like_num_today : 3
     * like_lastdate :
     * label : null
     * interaction_user_id : 10
     * interaction_avatar : http://medium2.tklvyou.cn/qiniu/20190814/10_1565747398107_9TBxf1.jpg
     * interaction_nickname : 焱少
     * interaction_createtime : 1573713127
     * interaction_updatetime : 1573713127
     * begintime : 3小时前
     * interaction_begintime : 9秒前
     * like_status : 1
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
    private int interaction_user_id;
    private String interaction_avatar;
    private String interaction_nickname;
    private String interaction_detail;
    private int interaction_createtime;
    private int interaction_updatetime;
    private int interaction_type;
    private String begintime;
    private String interaction_begintime;
    private int like_status;
    private List<String> images;
    private boolean isExpand;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInteraction_type() {
        return interaction_type;
    }

    public void setInteraction_type(int interaction_type) {
        this.interaction_type = interaction_type;
    }

    public String getInteraction_detail() {
        return interaction_detail;
    }

    public void setInteraction_detail(String interaction_detail) {
        this.interaction_detail = interaction_detail;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
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

    public int getInteraction_user_id() {
        return interaction_user_id;
    }

    public void setInteraction_user_id(int interaction_user_id) {
        this.interaction_user_id = interaction_user_id;
    }

    public String getInteraction_avatar() {
        return interaction_avatar;
    }

    public void setInteraction_avatar(String interaction_avatar) {
        this.interaction_avatar = interaction_avatar;
    }

    public String getInteraction_nickname() {
        return interaction_nickname;
    }

    public void setInteraction_nickname(String interaction_nickname) {
        this.interaction_nickname = interaction_nickname;
    }

    public int getInteraction_createtime() {
        return interaction_createtime;
    }

    public void setInteraction_createtime(int interaction_createtime) {
        this.interaction_createtime = interaction_createtime;
    }

    public int getInteraction_updatetime() {
        return interaction_updatetime;
    }

    public void setInteraction_updatetime(int interaction_updatetime) {
        this.interaction_updatetime = interaction_updatetime;
    }

    public String getBegintime() {
        return begintime;
    }

    public void setBegintime(String begintime) {
        this.begintime = begintime;
    }

    public String getInteraction_begintime() {
        return interaction_begintime;
    }

    public void setInteraction_begintime(String interaction_begintime) {
        this.interaction_begintime = interaction_begintime;
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
}
