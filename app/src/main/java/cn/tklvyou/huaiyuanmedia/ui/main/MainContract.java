package cn.tklvyou.huaiyuanmedia.ui.main;

import android.graphics.Bitmap;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.AdModel;
import cn.tklvyou.huaiyuanmedia.model.LifeInfo;
import cn.tklvyou.huaiyuanmedia.model.SystemConfigModel;

public interface MainContract {
    interface View extends BaseContract.BaseView{
        void setSystemConfig(SystemConfigModel model,LifeInfo info);
    }
    interface Presenter extends BaseContract.BasePresenter<View>{
        void getSystemConfig();
        void getLifeInfo();
    }


    interface AdView extends BaseContract.BaseView{
        void setAdView(List<AdModel> data);
    }

    interface ADPresenter extends BaseContract.BasePresenter<AdView>{
        void getAdView();
    }


}
