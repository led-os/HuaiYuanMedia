package cn.tklvyou.huaiyuanmedia.ui.camera.history_updates;


import android.annotation.SuppressLint;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;


@SuppressLint("CheckResult")
public class HistoryUpdatesPresenter extends BasePresenter<HistoryUpdatesContract.View> implements HistoryUpdatesContract.Presenter {

    @Override
    public void getNewList(boolean isMine, int p) {
        if(isMine) {
            RetrofitHelper.getInstance().getServer()
                    .getLastLifeList(p)
                    .compose(RxSchedulers.applySchedulers())
                    .compose(mView.bindToLife())
                    .subscribe(result -> {
                                if (result.getCode() == 1) {
                                    mView.setNewList(p, result.getData());
                                } else {
                                    ToastUtils.showShort(result.getMsg());
                                }
                            }, throwable -> {
                                mView.showFailed("");
                            }
                    );
        }else {
            RetrofitHelper.getInstance().getServer()
                    .getFriendLifeList(p)
                    .compose(RxSchedulers.applySchedulers())
                    .compose(mView.bindToLife())
                    .subscribe(result -> {
                                if (result.getCode() == 1) {
                                    mView.setNewList(p, result.getData());
                                } else {
                                    ToastUtils.showShort(result.getMsg());
                                }
                            }, throwable -> {
                                mView.showFailed("");
                            }
                    );
        }
    }

    @Override
    public void deleteArticle(int id, int position) {
        RetrofitHelper.getInstance().getServer()
                .deleteArticle(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        ToastUtils.showShort("删除成功");
                        mView.deleteSuccess(position);
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void deleteArticles(int id, int position) {
        RetrofitHelper.getInstance().getServer()
                .deleteArticles(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        ToastUtils.showShort("删除成功");
                        mView.deleteSuccess(position);
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
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
