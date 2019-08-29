package cn.tklvyou.huaiyuanmedia.ui.account;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;

public class AgreenmentPresenter extends BasePresenter<AccountContract.AgreenmentView> implements AccountContract.AgreenmentPresenter {

    @Override
    public void getSystemConfig() {
        RetrofitHelper.getInstance().getServer()
                .getSystemConfig()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.setSystemConfig(result.getData());
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> mView.showFailed(""));
    }
}
