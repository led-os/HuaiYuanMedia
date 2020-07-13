package cn.tklvyou.huaiyuanmedia.ui.home.new_list;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BannerModel;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;
import cn.tklvyou.huaiyuanmedia.model.HaveSecondModuleNewsModel;
import cn.tklvyou.huaiyuanmedia.model.PingXuanModel;

public interface NewListContract {
    interface View extends BaseContract.BaseView {
        void setNewList(int p, BasePageModel<NewsBean> model);

        void setHaveSecondModuleNews(int p, List<HaveSecondModuleNewsModel> datas);

        void setBanner(List<BannerModel> bannerModelList);

        void deleteSuccess(int position);

        void setJuZhengHeader(List<HaveSecondModuleNewsModel.ModuleSecondBean> beans);

        void setVerticalHeader(List<NewsBean> beans);

        void updateLikeStatus(boolean isLike, int position);

        void setPingXuanList(int p, BasePageModel<PingXuanModel> model);

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getNewList(String module, String module_second, int p, boolean showLoading);

        void getHaveSecondModuleNews(int p, String module, boolean showLoading);

        void getBanner(String module);

        void getDetailsById(int id);

        void deleteArticle(int id, int position);

        void getJuZhengHeader(String module);

        void getVerticalHeader(String module);

        void addLikeNews(int id, int position);

        void cancelLikeNews(int id, int position);

        void getPingXuanList(int p,boolean showLoading);

    }
}
