package cn.tklvyou.huaiyuanmedia.ui.home;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.ChannelModel;


public interface HomeContract {
    interface View extends BaseContract.BaseView{
        void setHomeChannel(List<String> channelList);
    }
    interface Presenter extends BaseContract.BasePresenter<View>{
        void getHomeChannel();
        void saveHomeChannel(String channels);
    }


    interface ChannelView extends BaseContract.BaseView{
        void setTotalChannel(ChannelModel model);
    }
    interface ChannelPresenter extends BaseContract.BasePresenter<ChannelView>{
        void getTotalChannel();
    }

}
