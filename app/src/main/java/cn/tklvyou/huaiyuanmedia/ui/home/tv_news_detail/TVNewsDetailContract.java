package cn.tklvyou.huaiyuanmedia.ui.home.tv_news_detail;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;


public interface TVNewsDetailContract {
    interface View extends BaseContract.BaseView{
        void setDetails(NewsBean bean);
        void updateLikeStatus(boolean isLike);
        void addCommentSuccess();
        void setCollectStatusSuccess(boolean isCollect);
    }
    interface Presenter extends BaseContract.BasePresenter<View>{
        void getDetailsById(int id);
        void addLikeNews(int id);
        void cancelLikeNews(int id);
        void addComment(int id, String detail);
        void setCollectStatus(int id, boolean isCollect);
        void getScoreByRead(int id);
    }
}
