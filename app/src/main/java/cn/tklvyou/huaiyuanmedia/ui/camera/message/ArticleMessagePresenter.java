package cn.tklvyou.huaiyuanmedia.ui.camera.message;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.common.RequestConstant;

@SuppressLint("CheckResult")
public class ArticleMessagePresenter extends BasePresenter<ArticleMessageContract.View> implements ArticleMessageContract.Presenter {

    @Override
    public void getMessagePageList(int page) {
        RetrofitHelper.getInstance().getServer()
                .getArticleMessage(page)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            mView.showSuccess(result.getMsg());
                            if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                                mView.setMessageList(page, result.getData().getData());
                            }
                        }, throwable -> {
                            throwable.printStackTrace();
                            mView.setMessageList(page,null);
                            if (page <= 1) {
                                mView.showFailed("");
                            }
                        }

                );

    }


    @Override
    public void clearMessage() {
        mView.showLoading();
        RetrofitHelper.getInstance().getServer()
                .clearArticleMessage()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                        mView.clearSuccess();
                    }
                }, throwable -> throwable.printStackTrace());
    }


}
