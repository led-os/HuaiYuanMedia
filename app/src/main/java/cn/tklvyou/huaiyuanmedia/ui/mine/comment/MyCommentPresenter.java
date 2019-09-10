package cn.tklvyou.huaiyuanmedia.ui.mine.comment;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.common.RequestConstant;
import cn.tklvyou.huaiyuanmedia.ui.mine.collection.CollectContract;

@SuppressLint("CheckResult")
public class MyCommentPresenter extends BasePresenter<MyCommentContract.View> implements MyCommentContract.Presenter {

    @Override
    public void getMyCommentPageList(int page) {
        RetrofitHelper.getInstance().getServer()
                .getMyCommentList(page)
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
                .subscribe(result -> {
                            LogUtils.e(new Gson().toJson(result));
                            if (mView != null) {
                                mView.showSuccess(result.getMsg());
                                if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                                    mView.setMyConmmentList(page, result.getData());
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
    public void cancelCommentList(String ids) {
        RetrofitHelper.getInstance().getServer()
                .cancelMyCommentList(ids)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                        mView.cancelCommentSuccess(false);
                    }
                }, throwable -> mView.showFailed(""));
    }

    @Override
    public void cancelCommentAll() {
        RetrofitHelper.getInstance().getServer()
                .cancelMyCommentAll()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                        mView.cancelCommentSuccess(true);
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
