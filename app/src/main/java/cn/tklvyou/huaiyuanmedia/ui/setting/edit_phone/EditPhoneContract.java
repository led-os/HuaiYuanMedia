package cn.tklvyou.huaiyuanmedia.ui.setting.edit_phone;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;

/**
 * @author :JenkinsZhou
 * @description :修改密码契约类
 * @company :途酷科技
 * @date 2019年07月30日9:48
 * @Email: 971613168@qq.com
 */
public interface EditPhoneContract {

    interface EditView extends BaseContract.BaseView {
        void getCaptchaSuccess();

        void editSuccess(String mobile);

        void editFailed();
    }


    interface EditPassPresenter extends BaseContract.BasePresenter<EditView> {
        void getCaptcha(String mobile, String event);

        void edit(String mobile,  String vCode);
    }
}
