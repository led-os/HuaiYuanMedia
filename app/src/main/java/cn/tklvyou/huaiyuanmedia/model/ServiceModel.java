package cn.tklvyou.huaiyuanmedia.model;

import java.util.List;

public class ServiceModel {

    /**
     * name : party_build_header
     * data : [{"imagename":"learning_plarform","title":"学习平台","subTitle":"全国党员在线学习"},{"imagename":"learning_country","title":"学习强国","subTitle":"学习强国"},{"imagename":"party_database","title":"党建数据库","subTitle":"党建数据库"},{"imagename":"book_list_icon","title":"学习安徽","subTitle":"安徽学习平台"},{"imagename":"cloud_class","title":"云课堂","subTitle":"党课在线学习"},{"imagename":"leader_learning_line","title":"干部教育在线","subTitle":"安徽干部教育在线"}]
     */

    private String name;
    private List<DataBean> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * imagename : learning_plarform
         * title : 学习平台
         * subTitle : 全国党员在线学习
         */

        private String imagename;
        private String title;
        private String subTitle;
        private String url;

        public String getImagename() {
            return imagename;
        }

        public void setImagename(String imagename) {
            this.imagename = imagename;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
