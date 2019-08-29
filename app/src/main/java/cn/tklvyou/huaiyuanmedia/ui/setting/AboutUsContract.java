package cn.tklvyou.huaiyuanmedia.ui.setting;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.SystemConfigModel;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2019年08月02日14:10
 * @Email: 971613168@qq.com
 */
public interface AboutUsContract {
    interface View extends BaseContract.BaseView {
      void setSystemConfig(SystemConfigModel model);
    }

    interface AboutPresenter extends BaseContract.BasePresenter<AboutUsContract.View> {
        void getSystemConfig();
    }
}
