package cn.tklvyou.huaiyuanmedia.ui.main;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.SystemConfigModel;

public interface MainContract {
    interface View extends BaseContract.BaseView{
        void setSystemConfig(SystemConfigModel model);
    }
    interface Presenter extends BaseContract.BasePresenter<View>{
        void getSystemConfig();
    }
}
