package cn.tklvyou.huaiyuanmedia.ui.mine.concern;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.ConcernModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;


public interface MyConcernContract {

    interface View extends BaseContract.BaseView {

        void setConcernList(int p, BasePageModel<ConcernModel> model);

        void addConcernSuccess(int position);

        void cancelSuccess(int position);
    }

    interface Presenter extends BaseContract.BasePresenter<MyConcernContract.View> {
        void getConcernList(int type, int p);

        void addConcern(int id, int position, int type);

        void cancelConcern(int id, int position, int type);

    }
}
