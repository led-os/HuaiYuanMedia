package cn.tklvyou.huaiyuanmedia.model;

public class ConcernModel {


    /**
     * id : 4
     * user_id : 10
     * pid : 5
     * type : 1
     * status : normal
     * createtime : 1567076184
     * updatetime : 1567076184
     * nickname : 濉溪政务
     * avatar : http://qiniu.dashuiniu.com.cn//qiniu/20190813/Fmy8evm3m6s8p3yFoB8ClGCjtww7.png
     */

    private int id;
    private int user_id;
    private int pid;
    private int type;
    private String url;
    private String status;
    private int createtime;
    private int updatetime;
    private String nickname;
    private String avatar;
    private boolean isNoConcern;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public boolean isNoConcern() {
        return isNoConcern;
    }

    public void setNoConcern(boolean noConcern) {
        isNoConcern = noConcern;
    }
}
