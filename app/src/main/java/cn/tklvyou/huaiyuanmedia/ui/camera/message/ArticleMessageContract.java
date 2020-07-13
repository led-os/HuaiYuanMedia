package cn.tklvyou.huaiyuanmedia.ui.camera.message;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.ArticleMessageModel;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.MyCommentModel;

public interface ArticleMessageContract {

    interface View extends BaseContract.BaseView {
        void setMessageList(int page, List<ArticleMessageModel> model);

        void clearSuccess();
    }

    interface Presenter extends BaseContract.BasePresenter<ArticleMessageContract.View> {
        void getMessagePageList(int page);

        void clearMessage();

    }
}
