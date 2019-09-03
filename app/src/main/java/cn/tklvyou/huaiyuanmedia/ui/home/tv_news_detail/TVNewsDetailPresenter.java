package cn.tklvyou.huaiyuanmedia.ui.home.tv_news_detail;


import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;


public class TVNewsDetailPresenter extends BasePresenter<TVNewsDetailContract.View> implements TVNewsDetailContract.Presenter {

    @Override
    public void getDetailsById(int id) {
        RetrofitHelper.getInstance().getServer()
                .getArticleDetail(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            if (result.getCode() == 1) {
                                mView.setDetails(result.getData());
                            } else {
                                ToastUtils.showShort(result.getMsg());
                            }
                        }, throwable -> {
                            throwable.printStackTrace();
                            mView.showFailed("");
                        }
                );
    }

    @Override
    public void addLikeNews(int id) {
        RetrofitHelper.getInstance().getServer()
                .addLikeNews(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.updateLikeStatus(true);
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void cancelLikeNews(int id) {
        RetrofitHelper.getInstance().getServer()
                .cancelLikeNews(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.updateLikeStatus(false);
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void addComment(int id, String detail) {
        RetrofitHelper.getInstance().getServer()
                .addComment(id, detail)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    ToastUtils.showShort(result.getMsg());
                    if (result.getCode() == 1) {
                        mView.addCommentSuccess();
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void setCollectStatus(int id, boolean isCollect) {
        if (isCollect) {
            RetrofitHelper.getInstance().getServer()
                    .addCollect(id)
                    .compose(RxSchedulers.applySchedulers())
                    .compose(mView.bindToLife())
                    .subscribe(result -> {
                        ToastUtils.showShort(result.getMsg());
                        if (result.getCode() == 1) {
                            mView.setCollectStatusSuccess(true);
                        }
                    }, throwable -> throwable.printStackTrace());
        } else {
            RetrofitHelper.getInstance().getServer()
                    .cancelCollect(id)
                    .compose(RxSchedulers.applySchedulers())
                    .compose(mView.bindToLife())
                    .subscribe(result -> {
                        ToastUtils.showShort(result.getMsg());
                        if (result.getCode() == 1) {
                            mView.setCollectStatusSuccess(false);
                        }
                    }, throwable -> throwable.printStackTrace());
        }

    }

    @Override
    public void getScoreByRead(int id) {
        RetrofitHelper.getInstance().getServer()
                .getScoreByRead(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (!StringUtils.isEmpty(result.getMsg())) {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

}
