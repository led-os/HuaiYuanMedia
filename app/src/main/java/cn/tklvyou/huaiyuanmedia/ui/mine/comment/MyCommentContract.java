package cn.tklvyou.huaiyuanmedia.ui.mine.comment;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.MyCommentModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

public interface MyCommentContract {


    interface View extends BaseContract.BaseView {
        void setMyConmmentList(int page, BasePageModel<MyCommentModel> pageModel);

        void cancelCommentSuccess(boolean isAll);

        void updateLikeStatus(boolean isLike, int position);
    }

    interface Presenter extends BaseContract.BasePresenter<MyCommentContract.View> {
        void getMyCommentPageList(int page);

        void cancelCommentList(String ids);

        void cancelCommentAll();

        void addLikeNews(int id, int position);

        void cancelLikeNews(int id, int position);


    }
}
