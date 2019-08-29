package cn.tklvyou.huaiyuanmedia.ui.service;


import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;


public class PointRulePresenter extends BasePresenter<PointRuleContract.View> implements PointRuleContract.Presenter{

    @Override
    public void getPointRule() {
        RetrofitHelper.getInstance().getServer()
                .getScoreRule()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.setPointRule(result.getData());
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }

                }, throwable -> {
                    mView.showFailed("");
                });
    }
}
