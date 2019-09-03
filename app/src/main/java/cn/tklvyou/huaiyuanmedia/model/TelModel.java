package cn.tklvyou.huaiyuanmedia.model;

import java.io.Serializable;
import java.util.List;

public class TelModel implements Serializable {

    /**
     * date : 2019-09-03
     * list : [{"id":44,"article_id":105,"date":"2019-09-03","time":"10:00:00","content":"新闻111","admin_id":0,"createtime":1567411104,"updatetime":1567411129},{"id":42,"article_id":105,"date":"2019-09-02","time":"15:00:00","content":"新闻一号1111","admin_id":0,"createtime":1567411065,"updatetime":1567411065},{"id":43,"article_id":105,"date":"2019-09-02","time":"16:00:00","content":"电视剧1号111","admin_id":0,"createtime":1567411083,"updatetime":1567411083}]
     */

    private String date;
    private List<ListBean> list;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 44
         * article_id : 105
         * date : 2019-09-03
         * time : 10:00:00
         * content : 新闻111
         * admin_id : 0
         * createtime : 1567411104
         * updatetime : 1567411129
         */

        private int id;
        private int article_id;
        private String date;
        private String time;
        private String content;
        private int admin_id;
        private int createtime;
        private int updatetime;
        private int now;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getArticle_id() {
            return article_id;
        }

        public void setArticle_id(int article_id) {
            this.article_id = article_id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getAdmin_id() {
            return admin_id;
        }

        public void setAdmin_id(int admin_id) {
            this.admin_id = admin_id;
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

        public int getNow() {
            return now;
        }

        public void setNow(int now) {
            this.now = now;
        }
    }
}
