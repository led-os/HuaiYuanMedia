package cn.tklvyou.huaiyuanmedia.model;

public class BannerModel {

    /**
     * id : 2
     * name : 222222
     * image : http://medium.tklvyou.cn/uploads/20190716/83ccbf35eb8d8e2d3228828dfc41c392.jpg
     * content : <p>2132111111<img src="/uploads/20190716/d60afd71a89e1289c7681604f7f64f95.jpg" style="width: 100%;" data-filename="filename"></p>
     * status : normal
     * createtime : 1563269758
     * updatetime : 1563269758
     * admin_id : 0
     * article_id : 4
     * module : all
     */

    private int id;
    private String name;
    private int type;
    private String image;
    private String content;
    private String status;
    private int createtime;
    private int updatetime;
    private int admin_id;
    private int article_id;
    private String module;
    private String url;
    private NewsBean article_info;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public NewsBean getArticle_info() {
        return article_info;
    }

    public void setArticle_info(NewsBean article_info) {
        this.article_info = article_info;
    }
}
