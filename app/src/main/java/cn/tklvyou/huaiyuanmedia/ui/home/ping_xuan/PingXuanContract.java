package cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.PingXuanModel;
import cn.tklvyou.huaiyuanmedia.model.PingXuanPersionModel;

public interface PingXuanContract {


    interface View extends BaseContract.BaseView {
        void setPingXuanModel(PingXuanModel model);
    }


    interface Presenter extends BaseContract.BasePresenter<PingXuanContract.View> {
        void getPingXuanDetails(int id,boolean showPage);
    }


    interface PersionView extends BaseContract.BaseView {
        void setPingXuanPersionModel(PingXuanPersionModel model);
        void voteSuccess();
        void voteFailed();
    }


    interface PersionPresenter extends BaseContract.BasePresenter<PingXuanContract.PersionView> {
        void getPingXuanPersionDetails(int id);
        void vote(int id);
    }

    interface ListView extends BaseContract.BaseView {
        void setPingXuanPageModel(int p, BasePageModel<PingXuanPersionModel> model);
        void voteSuccess(int position);
        void voteFailed();
    }


    interface ListViewPresenter extends BaseContract.BasePresenter<PingXuanContract.ListView> {
        void getPingXuanPersionList(int id, int p, String search, String sort);
        void vote(int id,int position);
    }


}
