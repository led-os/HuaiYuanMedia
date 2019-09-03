package cn.tklvyou.huaiyuanmedia.ui.audio;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BannerModel;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.HaveSecondModuleNewsModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

public interface AudioContract {
    interface View extends BaseContract.BaseView {
        void setNewList(int p, BasePageModel<NewsBean> model);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getNewList(String module, String module_second, int p, boolean showLoading);
    }
}
