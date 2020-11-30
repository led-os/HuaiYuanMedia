package cn.tklvyou.huaiyuanmedia.ui.account;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.helper.AccountHelper;
import cn.tklvyou.huaiyuanmedia.utils.AESUtils;

import static cn.tklvyou.huaiyuanmedia.utils.AESUtils.AES_KEY;


/**
 * 描述
 */

public class AccountRegisterPresenter extends BasePresenter<AccountContract.RegisterView> implements AccountContract.RegisterPresenter {

    @Override
    public void getCaptcha(String mobile, String event) {
        mobile = AESUtils.encrypt(AES_KEY,mobile);
        RetrofitHelper.getInstance().getServer()
              .sendSms(mobile,event)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    ToastUtils.showShort(result.getMsg());
                    if(result.getCode() ==1){
                        mView.getCaptchaSuccess();
                    }
                }, throwable -> {
                });
    }

    @Override
    public void register(String mobile, String password, String captcha) {
        mView.showLoading();
        RetrofitHelper.getInstance().getServer()
                .register(mobile, password, captcha)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if(result.getCode() == 1){
                        mView.registerSuccess();
                        AccountHelper.getInstance().setUserInfo(result.getData().getUserinfo());
                        SPUtils.getInstance().put("token",result.getData().getUserinfo().getToken());
                        SPUtils.getInstance().put("login",true);
                    }
                }, throwable -> mView.showFailed(""));
    }
}
