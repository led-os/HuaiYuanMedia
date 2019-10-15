package cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan;

import android.annotation.SuppressLint;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;


@SuppressLint("CheckResult")
public class PingXuanPersionPresenter extends BasePresenter<PingXuanContract.PersionView> implements PingXuanContract.PersionPresenter {

    @Override
    public void getPingXuanPersionDetails(int id) {
        mView.showPageLoading();
        RetrofitHelper.getInstance().getServer()
                .getPingXuanPersionDetails(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == 1) {
                        mView.setPingXuanPersionModel(result.getData());
                    }
                }, throwable -> {
                    mView.showFailed("");
                    throwable.printStackTrace();
                });
    }

    @Override
    public void vote(int id) {
        mView.showLoading();
        RetrofitHelper.getInstance().getServer()
                .vote(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if(result.getCode() == 1){
                        mView.voteSuccess();
                    }else {
                        mView.voteFailed();
                    }

                }, throwable -> {
                    mView.showFailed("");
                    throwable.printStackTrace();
                    mView.voteFailed();
                });
    }
}
