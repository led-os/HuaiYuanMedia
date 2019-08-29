package cn.tklvyou.huaiyuanmedia.ui.account;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.SystemConfigModel;

/**
 * 账号管理：登录注册配置约定
 */

public interface AccountContract {

    interface View extends BaseContract.BaseView {
        void getCaptchaSuccess();
        void loginSuccess();

        void loginError();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void login(String name, String password);
        void codeLogin(String mobile,String code);
        void getCaptcha(String mobile, String event);
    }

    interface LoginView extends BaseContract.BaseView {
        void bindMobile(int third_id);
        void loginSuccess();
        void loginError();
    }

    interface LoginPresenter extends BaseContract.BasePresenter<LoginView> {
        void thirdLogin(String platform, String code);
    }

    interface RegisterView extends BaseContract.BaseView {
        void registerSuccess();

        void getCaptchaSuccess();
    }


    interface RegisterPresenter extends BaseContract.BasePresenter<RegisterView> {
        void getCaptcha(String mobile, String event);

        void register(String mobile, String password, String captcha);
    }

    interface ForgetView extends BaseContract.BaseView {
        void getCaptchaSuccess();

        void resetpwdSuccess();
    }

    interface ForgetPresenter extends BaseContract.BasePresenter<ForgetView> {
        void getCaptcha(String mobile, String event);

        void resetpwd(String mobile, String newpassword, String captcha);
    }

    interface AgreenmentView extends BaseContract.BaseView {
        void setSystemConfig(SystemConfigModel model);
    }

    interface AgreenmentPresenter extends BaseContract.BasePresenter<AgreenmentView> {
        void getSystemConfig();
    }

}
