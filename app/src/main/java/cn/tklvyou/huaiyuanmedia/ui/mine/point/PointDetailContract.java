package cn.tklvyou.huaiyuanmedia.ui.mine.point;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.PointDetailModel;
import cn.tklvyou.huaiyuanmedia.model.User;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2019年08月01日17:37
 * @Email: 971613168@qq.com
 */
public interface PointDetailContract {
    interface View extends BaseContract.BaseView {
        void setPointDetails(int page, BasePageModel<PointDetailModel> pageModel);

        void setUser(User.UserinfoBean bean);

    }

    interface Presenter extends BaseContract.BasePresenter<PointDetailContract.View> {
        void getPointPageList(int page);

        void getUser();
    }
}
