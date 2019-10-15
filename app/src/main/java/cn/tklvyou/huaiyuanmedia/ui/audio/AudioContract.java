package cn.tklvyou.huaiyuanmedia.ui.audio;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

public interface AudioContract {
    interface View extends BaseContract.BaseView {
        void setNewList(int p, BasePageModel<NewsBean> model);
        void setDetails(NewsBean bean);
        void updateLikeStatus(boolean isLike);
        void addCommentSuccess(int id);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getNewList(String module, String module_second, int p, boolean showLoading);
        void getDetailsById(int id);
        void addLikeNews(int id);
        void cancelLikeNews(int id);
        void addComment(int id, String detail);
    }
}
