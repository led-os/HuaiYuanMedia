package cn.tklvyou.huaiyuanmedia.ui.home;


import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cjt2325.cameralibrary.util.LogUtil;

import java.util.ArrayList;
import java.util.HashSet;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;


public class HomePresenter extends BasePresenter<HomeContract.View> implements HomeContract.Presenter {

    @Override
    public void getHomeChannel() {
        mView.showLoading();
        RetrofitHelper.getInstance().getServer()
                .getHomeChannel()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            mView.showSuccess(result.getMsg());
                            if (result.getCode() == 1) {
                                mView.setHomeChannel(result.getData());
                                SPUtils.getInstance().put("channel", new HashSet<String>(result.getData()));
                            } else {
                                mView.setHomeChannel(new ArrayList<>(SPUtils.getInstance().getStringSet("channel")));
                            }
                        }, throwable -> {
                            throwable.printStackTrace();
                            mView.showSuccess("");
                            mView.setHomeChannel(new ArrayList<>(SPUtils.getInstance().getStringSet("channel")));
                        }

                );
    }

    @Override
    public void saveHomeChannel(String channels) {
        mView.showLoading();
        RetrofitHelper.getInstance().getServer()
                .saveHomeChannel(channels)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            mView.showSuccess(result.getMsg());
                            getHomeChannel();
                        }, throwable -> {
                            throwable.printStackTrace();
                            mView.showFailed("");
                        }

                );
    }

}
