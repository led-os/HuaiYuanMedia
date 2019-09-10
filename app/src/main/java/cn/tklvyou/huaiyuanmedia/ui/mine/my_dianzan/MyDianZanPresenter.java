package cn.tklvyou.huaiyuanmedia.ui.mine.my_dianzan;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
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


    @Override
    public void addLikeNews(int id, int position) {
        RetrofitHelper.getInstance().getServer()
                .addLikeNews(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.updateLikeStatus(true, position);
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void cancelLikeNews(int id, int position) {
        RetrofitHelper.getInstance().getServer()
                .cancelLikeNews(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.updateLikeStatus(false, position);
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

}
