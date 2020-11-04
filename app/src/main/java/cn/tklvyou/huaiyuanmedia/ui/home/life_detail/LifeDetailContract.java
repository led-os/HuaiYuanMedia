package cn.tklvyou.huaiyuanmedia.ui.home.life_detail;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.CommentModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;
import cn.tklvyou.huaiyuanmedia.model.VoteOptionModel;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2020年11月03日14:31
 * @Email: 971613168@qq.com
 */
public interface LifeDetailContract {
    interface View extends BaseContract.BaseView{
        void setDetails(NewsBean bean);
        void updateLikeStatus(boolean isLike);
        void addCommentSuccess();
        void setCollectStatusSuccess(boolean isCollect);
        void sendVoteSuccess(List<VoteOptionModel> optionModelList);
        void addConcernSuccess();

        void cancelSuccess();

        void setCommentList(int p, BasePageModel<CommentModel> model);
    }
    interface Presenter extends BaseContract.BasePresenter<LifeDetailContract.View>{
        void getDetailsById(int id,boolean showPageLoading,boolean isLife);
        void addLikeNews(int id);
        void cancelLikeNews(int id);
        void addComment(int id,String detail);
        void setCollectStatus(int id,boolean isCollect);
        void sendVote(int vote_id,int option_id);
        void getScoreByRead(int id);
        void getScoreByShare(int id);
        void addConcern(int id, int type);
        void cancelConcern(int id, int type);
        void getCommentList(int article_id, int p);
    }
}
