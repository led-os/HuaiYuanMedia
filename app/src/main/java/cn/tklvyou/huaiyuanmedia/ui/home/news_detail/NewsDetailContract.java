package cn.tklvyou.huaiyuanmedia.ui.home.news_detail;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;
import cn.tklvyou.huaiyuanmedia.model.VoteOptionModel;


public interface NewsDetailContract {
    interface View extends BaseContract.BaseView{
        void setDetails(NewsBean bean);
        void updateLikeStatus(boolean isLike);
        void addCommentSuccess();
        void setCollectStatusSuccess(boolean isCollect);
        void sendVoteSuccess(List<VoteOptionModel> optionModelList);
    }
    interface Presenter extends BaseContract.BasePresenter<View>{
        void getDetailsById(int id,boolean showPageLoading);
        void addLikeNews(int id);
        void cancelLikeNews(int id);
        void addComment(int id,String detail);
        void setCollectStatus(int id,boolean isCollect);
        void sendVote(int vote_id,int option_id);
        void getScoreByRead(int id);
        void getScoreByShare(int id);
    }
}
