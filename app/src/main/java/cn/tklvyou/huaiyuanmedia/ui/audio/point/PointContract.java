package cn.tklvyou.huaiyuanmedia.ui.audio.point;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.PointModel;
import cn.tklvyou.huaiyuanmedia.model.User;


public interface PointContract {
    interface View extends BaseContract.BaseView {
        void setGoods(int page,BasePageModel<PointModel> pageModel);
        void setUser(User.UserinfoBean bean);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getGoodsPageList(int page);
        void getUser();
    }
}
