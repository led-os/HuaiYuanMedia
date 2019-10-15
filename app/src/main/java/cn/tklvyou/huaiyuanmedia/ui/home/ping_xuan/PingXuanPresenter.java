package cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;


@SuppressLint("CheckResult")
public class PingXuanPresenter extends BasePresenter<PingXuanContract.View> implements PingXuanContract.Presenter {

    @Override
    public void getPingXuanDetails(int id,boolean showPage) {
        if(showPage){
            mView.showPageLoading();
        }
        RetrofitHelper.getInstance().getServer()
                .getPingXuanDetails(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == 1) {
                        mView.setPingXuanModel(result.getData());
                    }
                }, throwable -> {
                    mView.showFailed("");
                    throwable.printStackTrace();
                });
    }
}
