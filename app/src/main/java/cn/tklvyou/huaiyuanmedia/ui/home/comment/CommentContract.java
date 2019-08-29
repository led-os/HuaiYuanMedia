package cn.tklvyou.huaiyuanmedia.ui.home.comment;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.CommentModel;

public interface CommentContract {


    interface View extends BaseContract.BaseView {
        void setCommentList(int p, BasePageModel<CommentModel> model);
    }

    interface Presenter extends BaseContract.BasePresenter<CommentContract.View> {
        void getCommentList(int article_id, int p);
    }
}
