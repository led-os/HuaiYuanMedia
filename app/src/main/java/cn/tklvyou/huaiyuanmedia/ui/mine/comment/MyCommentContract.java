package cn.tklvyou.huaiyuanmedia.ui.mine.comment;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

public interface MyCommentContract {


    interface View extends BaseContract.BaseView {
        void setMyConmmentList(int page, BasePageModel<NewsBean> pageModel);

        void cancelCommentSuccess(boolean isAll);
    }

    interface Presenter extends BaseContract.BasePresenter<MyCommentContract.View> {
        void getMyCommentPageList(int page);

        void cancelCommentList(String ids);

        void cancelCommentAll();
    }
}
