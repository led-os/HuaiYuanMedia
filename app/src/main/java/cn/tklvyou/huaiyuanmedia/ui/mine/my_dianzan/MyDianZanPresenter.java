package cn.tklvyou.huaiyuanmedia.ui.mine.my_dianzan;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.common.RequestConstant;

@SuppressLint("CheckResult")
public class MyDianZanPresenter extends BasePresenter<MyDianZanContract.View> implements MyDianZanContract.Presenter {

    @Override
    public void getDianZanPageList(int page) {
        RetrofitHelper.getInstance().getServer()
                .getMyLikeList(page)
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
                .subscribe(result -> {
                            LogUtils.e(new Gson().toJson(result));
                            if (mView != null) {
                                mView.showSuccess(result.getMsg());
                                if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                                    mView.setDianZanList(page, result.getData());
                                }
                            }
                        }, throwable -> {
                            throwable.printStackTrace();
                            if (mView != null)
                                mView.showFailed("");
                        }

                );

    }

    @Override
    public void cancelDianZanList(String ids) {
        RetrofitHelper.getInstance().getServer()
                .cancelMyLikeList(ids)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                        mView.cancelDianZanSuccess(false);
                    }
                }, throwable -> mView.showFailed(""));
    }

    @Override
    public void cancelDianZanAll() {
        RetrofitHelper.getInstance().getServer()
                .cancelMyLikeAll()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                        mView.cancelDianZanSuccess(true);
                    }
                }, throwable -> mView.showFailed(""));
    }
}
