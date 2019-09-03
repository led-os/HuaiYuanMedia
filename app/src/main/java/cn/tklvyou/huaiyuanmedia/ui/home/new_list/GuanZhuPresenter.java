package cn.tklvyou.huaiyuanmedia.ui.home.new_list;


import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;


public class GuanZhuPresenter extends BasePresenter<GuanZhuContract.View> implements GuanZhuContract.Presenter {

    @Override
    public void addConcern(int id,int position, int type) {
        RetrofitHelper.getInstance().getServer()
                .addConcern(id,type)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        ToastUtils.showShort("关注成功");
                        mView.addConcernSuccess(position);
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void cancelConcern(int id, int position,int type) {
        RetrofitHelper.getInstance().getServer()
                .cancelConcern(id,type)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        ToastUtils.showShort("取消成功");
                        mView.cancelSuccess(position);
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void getAttentionList() {
        if (mView != null) {
            mView.showLoading();
        }
        RetrofitHelper.getInstance().getServer()
                .getAttentionList()
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (mView != null) {
                        mView.showSuccess(result.getMsg());
                        if (result.getCode() == 1) {
                            mView.setAttentionList(result.getData());
                        }
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    if (mView != null) {
                        mView.setAttentionList(null);
                        mView.showFailed("");
                    }
                });
    }

    @Override
    public void getGuanZhuNews(int p, String module, boolean showLoading) {
        if (showLoading) {
            mView.showLoading();
        }
        RetrofitHelper.getInstance().getServer()
                .getGuanZhuNews(module, p)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            mView.showSuccess("");
                            if (result.getCode() == 1) {
                                if (mView != null) {
                                    mView.setGuanZhuNews(p, result.getData());
                                }
                            }
                        }, throwable -> {
                            throwable.printStackTrace();
                            if (mView != null) {
                                mView.setGuanZhuNews(p, null);
                                mView.showFailed("");
                            }
                        }
                );
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
