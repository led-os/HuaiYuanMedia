package cn.tklvyou.huaiyuanmedia.model;

import java.util.List;

public class LotteryModel {


    /**
     * data : [{"id":1,"name":"5积分","score":5,"ratio":300,"createtime":1564568263,"updatetime":1564568353,"admin_id":0},{"id":2,"name":"10积分","score":10,"ratio":95,"createtime":1564568274,"updatetime":1564568335,"admin_id":0},{"id":3,"name":"15积分","score":15,"ratio":80,"createtime":1564568287,"updatetime":1564568341,"admin_id":0},{"id":4,"name":"20积分","score":20,"ratio":25,"createtime":1564568298,"updatetime":1564568298,"admin_id":0},{"id":5,"name":"66积分","score":66,"ratio":5,"createtime":1564568308,"updatetime":1564568308,"admin_id":0},{"id":6,"name":"99积分","score":99,"ratio":2,"createtime":1564568326,"updatetime":1564568326,"admin_id":0}]
     * num : 100
     */

    private int num;
    private List<DataBean> data;
    private int exchange;

    public int getExchange() {
        return exchange;
    }

    public void setExchange(int exchange) {
        this.exchange = exchange;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * name : 5积分
         * score : 5
         * ratio : 300
         * createtime : 1564568263
         * updatetime : 1564568353
         * admin_id : 0
         */

        private int id;
        private String name;
        private int score;
        private String ratio;
        private int createtime;
        private int updatetime;
        private int admin_id;
        private int type;

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

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getRatio() {
            return ratio;
        }

        public void setRatio(String ratio) {
            this.ratio = ratio;
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

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }


    }
}
