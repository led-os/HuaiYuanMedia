package cn.tklvyou.huaiyuanmedia.ui.account;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;

/**
 * @author :JenkinsZhou
 * @description :修改密码契约类
 * @company :途酷科技
 * @date 2019年07月30日9:48
 * @Email: 971613168@qq.com
 */
public interface BindPhoneContract {

    interface View extends BaseContract.BaseView {
        void getCaptchaSuccess();

        void bindSuccess();
    }


    interface BindPhonePresenter extends BaseContract.BasePresenter<View> {
        void getCaptcha(String mobile, String event);

        void bindMobile(int third_id,String mobile, String captcha);
    }
}
