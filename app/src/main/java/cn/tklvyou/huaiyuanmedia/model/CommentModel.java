package cn.tklvyou.huaiyuanmedia.model;

public class CommentModel {


    /**
     * id : 45
     * user_id : 10
     * article_id : 69
     * detail : 1231
     * nickname : 焱
     * avatar : http://cdn.duitang.com/uploads/item/201508/02/20150802155755_YCynL.thumb.700_0.jpeg
     * status : normal
     * createtime : 1564288609
     * updatetime : 1564288609
     */

    private int id;
    private int user_id;
    private int article_id;
    private String detail;
    private String nickname;
    private String avatar;
    private String status;
    private String createtime;
    private String updatetime;
    private int admin_status;

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

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public int getAdmin_status() {
        return admin_status;
    }

    public void setAdmin_status(int admin_status) {
        this.admin_status = admin_status;
    }
}
