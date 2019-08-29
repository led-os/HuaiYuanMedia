package cn.tklvyou.huaiyuanmedia.ui.setting.edit_pass;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;

/**
 * @author :JenkinsZhou
 * @description :修改密码契约类
 * @company :途酷科技
 * @date 2019年07月30日9:48
 * @Email: 971613168@qq.com
 */
public interface EditPassContract {

    interface EditView extends BaseContract.BaseView {
        void getCaptchaSuccess();

        void editSuccess();

        void editFailed();
    }


    interface EditPassPresenter extends BaseContract.BasePresenter<EditView> {
        void getCaptcha(String mobile, String event);

        void edit(String mobile, String newPass, String vCode);
    }
}
