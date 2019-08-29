package cn.tklvyou.huaiyuanmedia.ui.account;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;


/**
 * 描述
 */

public class AccountForgetPresenter extends BasePresenter<AccountContract.ForgetView> implements AccountContract.ForgetPresenter {

    @Override
    public void getCaptcha(String mobile, String event) {
        RetrofitHelper.getInstance().getServer()
                .sendSms(mobile, event)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    ToastUtils.showShort(result.getMsg());
                    if (result.getCode() == 1) {
                        mView.getCaptchaSuccess();
                    }
                }, throwable -> {
                });
    }

    @Override
    public void resetpwd(String mobile, String newpassword, String captcha) {
        mView.showLoading();
        RetrofitHelper.getInstance().getServer()
                .resetpwd(mobile, newpassword, captcha)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == 1) {
                        mView.resetpwdSuccess();
                    }
                }, throwable -> mView.showFailed(""));
    }
}
