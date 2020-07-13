package cn.tklvyou.huaiyuanmedia.model;

import java.util.List;

public class HaveSecondModuleNewsModel {


    /**
     * module_second : 濉溪新闻
     * data : [{"id":49,"module":"濉溪TV","module_second":"濉溪新闻","name":"濉溪20年变化 10年回顾","image":"http://medium.tklvyou.cn/uploads/20190719/50c9337f756d342b578b4da7be1ac2b2.jpeg","video":"http://222.207.48.30/hls/startv.m3u8","time":null,"createtime":1563520363,"type":""},{"id":48,"module":"濉溪TV","module_second":"濉溪新闻","name":"濉溪20年变化 10年回顾","image":"http://medium.tklvyou.cn/uploads/20190719/50c9337f756d342b578b4da7be1ac2b2.jpeg","video":"http://222.207.48.30/hls/startv.m3u8","time":null,"createtime":1563520348,"type":""}]
     */

    private ModuleSecondBean module_second;
    private List<NewsBean> data;

    public ModuleSecondBean getModule_second() {
        return module_second;
    }

    public void setModule_second(ModuleSecondBean module_second) {
        this.module_second = module_second;
    }

    public List<NewsBean> getData() {
        return data;
    }

    public void setData(List<NewsBean> data) {
        this.data = data;
    }

    public static class ModuleSecondBean {
        private int id;
        private String pname;
        private String nickname;
        private String avatar;
        private String image;
        private String detail;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPname() {
            return pname;
        }

        public void setPname(String pname) {
            this.pname = pname;
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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }


}
