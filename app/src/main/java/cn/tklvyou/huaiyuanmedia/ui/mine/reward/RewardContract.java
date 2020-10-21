package cn.tklvyou.huaiyuanmedia.ui.mine.reward;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.RewardModel;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2020年10月21日16:23
 * @Email: 971613168@qq.com
 */
public interface RewardContract {
    interface View extends BaseContract.BaseView {
        void setRewardList(int page, BasePageModel<RewardModel> pageModel);
    }

    interface Presenter extends BaseContract.BasePresenter<RewardContract.View> {
        void getRewardListPageList(int page);
    }
}
