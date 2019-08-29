package cn.tklvyou.huaiyuanmedia.model;

import java.util.List;

public class BasePageModel<T> {
    /**
     * total : 8
     * per_page : 10
     * current_page : 1
     * last_page : 1
     * data : [{"id":65,"user_id":2,"admin_id":0,"module":"V视频","module_second":"","name":"一直","image":"http://medium.tklvyou.cn/uploads/20190725/48cbffd3f99a96a9ac0922ecbe306652.jpeg","images":[],"video":"http://medium.tklvyou.cn/uploads/20190725/19792be0e507e7e24a736d363f798ad1.mp4","audio":"","content":null,"nickname":"Mason","avatar":" ","status":"normal","visit_num":20,"comment_num":0,"like_num":0,"createtime":1564037389,"updatetime":1564037389,"vote_id":0,"weigh":0,"time":"0","begintime":"6小时前","like_status":0,"collect_status":0},{"id":62,"user_id":2,"admin_id":0,"module":"V视频","module_second":"","name":"成交价","image":"http://medium.tklvyou.cn/uploads/20190725/5ef038247fa49da8c56960c8d9a3b3de.jpeg","images":[],"video":"http://medium.tklvyou.cn/uploads/20190725/064d400c9d3bfaf00fa4db7c48d1011e.mp4","audio":"","content":null,"nickname":"Mason","avatar":" ","status":"normal","visit_num":3,"comment_num":0,"like_num":0,"createtime":1564021584,"updatetime":1564021584,"vote_id":0,"weigh":0,"time":"0","begintime":"10小时前","like_status":0,"collect_status":0},{"id":47,"user_id":2,"admin_id":0,"module":"V视频","module_second":"","name":"咯破老婆撒撒娇","image":"http://medium.tklvyou.cn/uploads/20190719/f90d73dfe22be94c811a5265a89855a9.jpg","images":[],"video":"http://medium.tklvyou.cn/uploads/20190719/403ac90cfbab3cb60afac59b055de3a6.mp4","audio":"","content":null,"nickname":"Mason","avatar":" ","status":"normal","visit_num":33,"comment_num":0,"like_num":0,"createtime":1563517283,"updatetime":1563517283,"vote_id":0,"weigh":0,"time":"00:10","begintime":"6天前","like_status":0,"collect_status":0},{"id":41,"user_id":2,"admin_id":0,"module":"V视频","module_second":"","name":"精磨","image":"http://medium.tklvyou.cn/uploads/20190719/101a9a2354f51b3d8113c09e5b138d53.jpg","images":[],"video":"http://medium.tklvyou.cn/uploads/20190719/06c142c1ea72f18976ff9ddde4ddca71.mp4","audio":"","content":null,"nickname":"Mason","avatar":" ","status":"normal","visit_num":26,"comment_num":0,"like_num":0,"createtime":1563502976,"updatetime":1563502976,"vote_id":0,"weigh":0,"time":"00:01","begintime":"6天前","like_status":0,"collect_status":0},{"id":38,"user_id":2,"admin_id":0,"module":"V视频","module_second":"","name":"急急急","image":"http://medium.tklvyou.cn/uploads/20190719/f8a23f73a5599b805efc6363cfb07db1.jpg","images":[],"video":"http://medium.tklvyou.cn/uploads/20190719/68cf61860416bdcf15bc0cad79074f1a.mp4","audio":"","content":null,"nickname":"Mason","avatar":" ","status":"normal","visit_num":4,"comment_num":0,"like_num":0,"createtime":1563502441,"updatetime":1563502441,"vote_id":0,"weigh":0,"time":"00:01","begintime":"6天前","like_status":0,"collect_status":0},{"id":25,"user_id":2,"admin_id":0,"module":"V视频","module_second":"","name":"发布的心","image":"http://medium.tklvyou.cn/uploads/20190718/ba2649a19cb9cbd322f5589042205e6e.png","images":[],"video":"http://medium.tklvyou.cn/uploads/20190718/3f4256b9db7538d1996ccb3f15461dbd.mp4","audio":"","content":null,"nickname":"Mason","avatar":" ","status":"normal","visit_num":37,"comment_num":1,"like_num":2,"createtime":1563442175,"updatetime":1563442175,"vote_id":0,"weigh":0,"time":"1.63","begintime":"1周前","like_status":0,"collect_status":0},{"id":21,"user_id":2,"admin_id":0,"module":"V视频","module_second":"","name":"取消","image":"http://medium.tklvyou.cn/uploads/20190718/1d6ca90e3a94e3cfb6c10036e047d38f.jpg","images":[],"video":"http://medium.tklvyou.cn/uploads/20190718/818a613cdb0aa8365ee42fa13b689409.mp4","audio":"","content":null,"nickname":"Mason","avatar":" ","status":"normal","visit_num":22,"comment_num":1,"like_num":4,"createtime":1563436555,"updatetime":1563436555,"vote_id":0,"weigh":0,"time":"00:03","begintime":"1周前","like_status":0,"collect_status":0},{"id":19,"user_id":2,"admin_id":0,"module":"V视频","module_second":"","name":"红米","image":"http://medium.tklvyou.cn/uploads/20190718/304f8d9405f387ad0c63402fa8758665.jpg","images":[],"video":"http://medium.tklvyou.cn/uploads/20190718/cebb11026afec03a21256603865137dc.mp4","audio":"","content":null,"nickname":"Mason","avatar":" ","status":"normal","visit_num":28,"comment_num":2,"like_num":2,"createtime":1563434548,"updatetime":1563434548,"vote_id":0,"weigh":0,"time":"00:02","begintime":"1周前","like_status":0,"collect_status":0}]
     */

    private int total;
    private int per_page;
    private int current_page;
    private int last_page;
    private List<T> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

}
