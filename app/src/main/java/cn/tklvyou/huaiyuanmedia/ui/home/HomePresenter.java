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
        RetrofitHelper.getInstance().getServer()
                .getHomeChannel()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            if (result.getCode() == 1) {
                                mView.setHomeChannel(result.getData());
                                SPUtils.getInstance().put("channel", new HashSet<String>(result.getData()));
                            } else {
                                mView.setHomeChannel(new ArrayList<>(SPUtils.getInstance().getStringSet("channel")));
                                ToastUtils.showShort(result.getMsg());
                            }
                        }, throwable -> {
                            throwable.printStackTrace();
                            mView.setHomeChannel(new ArrayList<>(SPUtils.getInstance().getStringSet("channel")));
                        }

                );
    }

    @Override
    public void getTotalChannel() {
        RetrofitHelper.getInstance().getServer()
                .getTotalChannel()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            if (result.getCode() == 1) {
                                mView.setTotalChannel(result.getData());
                            } else {
                                ToastUtils.showShort(result.getMsg());
                            }
                        }, throwable -> {
                            throwable.printStackTrace();
                        }

                );
    }
}
