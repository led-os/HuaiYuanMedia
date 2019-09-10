package cn.tklvyou.huaiyuanmedia.ui.camera.goods_detail;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.PointModel;


public interface GoodsDetailContract {
    interface View extends BaseContract.BaseView {
        void setGoodsDetail(PointModel model);
        void exchangeSuccess();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getGoodsDetails(int id);
        void exchangeGoods(int id);
    }
}
