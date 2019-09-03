package cn.tklvyou.huaiyuanmedia.ui.home.new_list;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.AttentionModel;
import cn.tklvyou.huaiyuanmedia.model.BannerModel;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.HaveSecondModuleNewsModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

public interface GuanZhuContract {
    interface View extends BaseContract.BaseView {

        void setAttentionList(AttentionModel model);

        void setGuanZhuNews(int p, BasePageModel<NewsBean> model);

        void updateLikeStatus(boolean isLike, int position);

        void addConcernSuccess(int position);

        void cancelSuccess(int position);

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getAttentionList();

        void getGuanZhuNews(int p, String module, boolean showLoading);

        void addLikeNews(int id, int position);

        void cancelLikeNews(int id, int position);

        void addConcern(int id, int position, int type);

        void cancelConcern(int id, int position, int type);

    }
}
