package cn.tklvyou.huaiyuanmedia.ui.setting;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2019年07月30日17:12
 * @Email: 971613168@qq.com
 */
public interface SettingContract {
    interface LogoutView extends BaseContract.BaseView {
        void logoutSuccess();
    }

    interface LogoutPresenter extends BaseContract.BasePresenter<SettingContract.LogoutView> {
        void logout();
    }


}
