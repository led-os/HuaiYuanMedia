package cn.tklvyou.huaiyuanmedia.ui.home.juzheng_details;

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
public interface ListContract {


    interface View extends BaseContract.BaseView {
        void setNewList(int p, BasePageModel<NewsBean> model);
        void updateLikeStatus(boolean isLike, int position);
    }

    interface Presenter extends BaseContract.BasePresenter<ListContract.View> {
        void getNewList(String module, String name, int p);
        void addLikeNews(int id, int position);

        void cancelLikeNews(int id, int position);

    }
}
