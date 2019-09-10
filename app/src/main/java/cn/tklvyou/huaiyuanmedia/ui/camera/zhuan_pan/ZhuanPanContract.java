package cn.tklvyou.huaiyuanmedia.ui.camera.zhuan_pan;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.LotteryModel;
import cn.tklvyou.huaiyuanmedia.model.LotteryResultModel;


public interface ZhuanPanContract {
    interface View extends BaseContract.BaseView {
        void setLotteryModel(LotteryModel model);
        void setLotteryResult(LotteryResultModel model);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getLotteryModel();

        void startLottery();

    }

}
