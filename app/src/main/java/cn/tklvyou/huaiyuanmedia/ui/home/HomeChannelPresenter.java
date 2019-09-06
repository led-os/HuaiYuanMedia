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


public class HomeChannelPresenter extends BasePresenter<HomeContract.ChannelView> implements HomeContract.ChannelPresenter {

    @Override
    public void getTotalChannel() {
        mView.showLoading();
        RetrofitHelper.getInstance().getServer()
                .getTotalChannel()
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
                .subscribe(result -> {
                            if (mView != null) {
                                mView.showSuccess(result.getMsg());
                                if (result.getCode() == 1) {
                                    mView.setTotalChannel(result.getData());
                                }
                            }

                        }, throwable -> {
                            throwable.printStackTrace();
                            if (mView != null)
                                mView.showFailed("");
                        }

                );
    }
}
