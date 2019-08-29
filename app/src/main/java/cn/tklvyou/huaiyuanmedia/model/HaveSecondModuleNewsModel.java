package cn.tklvyou.huaiyuanmedia.model;

import java.util.List;

public class HaveSecondModuleNewsModel {


    /**
     * module_second : 濉溪新闻
     * data : [{"id":49,"module":"濉溪TV","module_second":"濉溪新闻","name":"濉溪20年变化 10年回顾","image":"http://medium.tklvyou.cn/uploads/20190719/50c9337f756d342b578b4da7be1ac2b2.jpeg","video":"http://222.207.48.30/hls/startv.m3u8","time":null,"createtime":1563520363,"type":""},{"id":48,"module":"濉溪TV","module_second":"濉溪新闻","name":"濉溪20年变化 10年回顾","image":"http://medium.tklvyou.cn/uploads/20190719/50c9337f756d342b578b4da7be1ac2b2.jpeg","video":"http://222.207.48.30/hls/startv.m3u8","time":null,"createtime":1563520348,"type":""}]
     */

    private String module_second;
    private List<DataBean> data;

    public String getModule_second() {
        return module_second;
    }

    public void setModule_second(String module_second) {
        this.module_second = module_second;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 49
         * module : 濉溪TV
         * module_second : 濉溪新闻
         * name : 濉溪20年变化 10年回顾
         * image : http://medium.tklvyou.cn/uploads/20190719/50c9337f756d342b578b4da7be1ac2b2.jpeg
         * video : http://222.207.48.30/hls/startv.m3u8
         * time : null
         * createtime : 1563520363
         * type :
         */

        private int id;
        private String module;
        private String nickname;
        private String avatar;
        private String module_second;
        private String name;
        private String image;
        private String video;
        private String time;
        private int createtime;
        private String type;
        private String url;
        private String begintime;
        private String visit_num;
        private String like_num;
        private int like_status;
        private List<String> images;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
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

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getCreatetime() {
            return createtime;
        }

        public void setCreatetime(int createtime) {
            this.createtime = createtime;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBegintime() {
            return begintime;
        }

        public void setBegintime(String begintime) {
            this.begintime = begintime;
        }

        public String getVisit_num() {
            return visit_num;
        }

        public void setVisit_num(String visit_num) {
            this.visit_num = visit_num;
        }

        public String getLike_num() {
            return like_num;
        }

        public void setLike_num(String like_num) {
            this.like_num = like_num;
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
}
