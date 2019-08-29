package cn.tklvyou.huaiyuanmedia.ui.mine.exchange;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.ExchangeModel;

/**
 * @author :JenkinsZhou
 * @description :兑换记录
 * @company :途酷科技
 * @date 2019年08月02日10:02
 * @Email: 971613168@qq.com
 */
public interface ExchangeRecordContract {

    interface View extends BaseContract.BaseView {
        void setExchangeList(int page, BasePageModel<ExchangeModel> pageModel);
        void receiveGoodsSuccess(int position);
    }

    interface Presenter extends BaseContract.BasePresenter<ExchangeRecordContract.View> {
        void getExchangePageList(int page);
        void receiveGoods(int id,int position);
    }
}
