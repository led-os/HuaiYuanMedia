package cn.tklvyou.huaiyuanmedia.ui.mine.browse;

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
public interface BrowseContract {


    interface View extends BaseContract.BaseView {
        void setBrowseList(int page, BasePageModel<NewsBean> pageModel);
        void updateLikeStatus(boolean isLike, int position);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getBrowsePageList(int page);
        void addLikeNews(int id, int position);

        void cancelLikeNews(int id, int position);
    }
}
