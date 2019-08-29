package cn.tklvyou.huaiyuanmedia.ui.mine;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.User;

public interface MineContract {
    interface View extends BaseContract.BaseView{
        void setUser(User.UserinfoBean user);
    }
    interface Presenter extends BaseContract.BasePresenter<View>{
        void getUser();
    }
}
