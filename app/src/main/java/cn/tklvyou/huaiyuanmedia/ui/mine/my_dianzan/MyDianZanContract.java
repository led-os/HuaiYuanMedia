package cn.tklvyou.huaiyuanmedia.ui.mine.my_dianzan;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;


public interface MyDianZanContract {


    interface View extends BaseContract.BaseView {
        void setDianZanList(int page, BasePageModel<NewsBean> pageModel);
        void cancelDianZanSuccess(boolean isAll);
        void updateLikeStatus(boolean isLike, int position);
    }

    interface Presenter extends BaseContract.BasePresenter<MyDianZanContract.View> {
        void getDianZanPageList(int page);
        void cancelDianZanList(String ids);
        void cancelDianZanAll();

        void addLikeNews(int id, int position);

        void cancelLikeNews(int id, int position);

    }
}
