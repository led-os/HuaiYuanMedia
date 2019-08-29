package cn.tklvyou.huaiyuanmedia.ui.setting.edit_phone;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;

import static cn.tklvyou.huaiyuanmedia.common.RequestConstant.CODE_REQUEST_SUCCESS;

/**
 * @author :JenkinsZhou
 * @description :修改手机号
 * @company :途酷科技
 * @date 2019年07月30日10:15
 * @Email: 971613168@qq.com
 */
@SuppressLint("CheckResult")
public class EditPhonePresenter extends BasePresenter<EditPhoneContract.EditView> implements EditPhoneContract.EditPassPresenter {
     public static final String TAG = "EditPhonePresenter";
    @Override
    public void getCaptcha(String mobile, String event) {
        RetrofitHelper.getInstance().getServer()
                .sendSms(mobile, event)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    ToastUtils.showShort(result.getMsg());
                    if (result.getCode() == CODE_REQUEST_SUCCESS) {
                        mView.getCaptchaSuccess();
                    }
                }, throwable -> {
                });
    }

    @Override
    public void edit(String mobile, String vCode) {
        RetrofitHelper.getInstance().getServer()
                .chaneMobile(mobile, vCode)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == CODE_REQUEST_SUCCESS) {
                        mView.editSuccess(mobile);
                    } else {
                        ToastUtils.showShort(result.getMsg());
                        mView.editFailed();
                    }
                }, throwable -> {
                    LogUtils.e(TAG,"异常");
                });
    }


}
