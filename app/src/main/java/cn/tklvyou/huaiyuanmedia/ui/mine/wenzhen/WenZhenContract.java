package cn.tklvyou.huaiyuanmedia.ui.mine.wenzhen;

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
public interface WenZhenContract {


    interface View extends BaseContract.BaseView {
        void setDataList(int page, BasePageModel<NewsBean> pageModel);
        void cancelArticleSuccess(boolean isAll);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getDataPageList(int page);
        void cancelArticleList(String ids);
        void cancelArticleAll();
    }
}
