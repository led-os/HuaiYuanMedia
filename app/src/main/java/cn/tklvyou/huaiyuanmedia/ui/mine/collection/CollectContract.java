package cn.tklvyou.huaiyuanmedia.ui.mine.collection;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2019年08月02日15:21
 * @Email: 971613168@qq.com
 */
public interface CollectContract {


    interface View extends BaseContract.BaseView {
        void setCollectList(int page, BasePageModel<NewsBean> pageModel);
        void cancelCollectSuccess(boolean isAll);
        void updateLikeStatus(boolean isLike, int position);
    }

    interface Presenter extends BaseContract.BasePresenter<CollectContract.View> {
        void getCollectPageList(int page);
        void cancelCollectList(String ids);
        void cancelCollectAll();

        void addLikeNews(int id, int position);

        void cancelLikeNews(int id, int position);


    }
}
