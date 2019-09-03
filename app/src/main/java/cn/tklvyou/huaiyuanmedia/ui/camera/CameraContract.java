package cn.tklvyou.huaiyuanmedia.ui.camera;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BannerModel;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.HaveSecondModuleNewsModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

public interface CameraContract {
    interface View extends BaseContract.BaseView {
        void setLifeHotList(int p, BasePageModel<NewsBean> model);

        void deleteSuccess(int position);

        void updateLikeStatus(boolean isLike, int position);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getLifeHotList(int p);

        void getDetailsById(int id);

        void deleteArticle(int id, int position);

        void addLikeNews(int id, int position);

        void cancelLikeNews(int id, int position);

    }


    interface TodayHotView extends BaseContract.BaseView {
        void setLifeHotList(int p, BasePageModel<NewsBean> model);
        void updateLikeStatus(boolean isLike, int position);
    }

    interface TodayHotPresenter extends BaseContract.BasePresenter<TodayHotView> {
        void getLifeHotList(int p);

        void addLikeNews(int id, int position);

        void cancelLikeNews(int id, int position);


    }

}
