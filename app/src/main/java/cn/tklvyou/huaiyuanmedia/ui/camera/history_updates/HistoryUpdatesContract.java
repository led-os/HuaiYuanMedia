package cn.tklvyou.huaiyuanmedia.ui.camera.history_updates;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

public interface HistoryUpdatesContract {

    interface View extends BaseContract.BaseView {
        void setNewList(int p, BasePageModel<NewsBean> model);
        void deleteSuccess(int position);
        void updateLikeStatus(boolean isLike, int position);
    }

    interface Presenter extends BaseContract.BasePresenter<HistoryUpdatesContract.View> {
        void getNewList(boolean isMine, int p);
        void deleteArticle(int id, int position);
        void deleteArticles(int id, int position);

        void addLikeNews(int id, int position);

        void cancelLikeNews(int id, int position);


    }
}
