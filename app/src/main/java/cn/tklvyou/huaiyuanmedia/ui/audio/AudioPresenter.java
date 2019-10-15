package cn.tklvyou.huaiyuanmedia.ui.audio;


import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.ui.home.new_list.NewListContract;


public class AudioPresenter extends BasePresenter<AudioContract.View> implements AudioContract.Presenter {

    @Override
    public void getNewList(String module, String module_second, int p, boolean showLoading) {
        if (showLoading) {
            mView.showLoading();
        }
        RetrofitHelper.getInstance().getServer()
                .getNewList(module, module_second, p)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            mView.showSuccess("");
                            if (result.getCode() == 1) {
                                if (mView != null) {
                                    mView.setNewList(p, result.getData());
                                }
                            } else {
                                ToastUtils.showShort(result.getMsg());
                            }
                        }, throwable -> {
                            if (mView != null) {
                                mView.setNewList(p, null);
                                mView.showFailed("");
                            }
                        }
                );
    }

    @Override
    public void getDetailsById(int id) {
        mView.showLoading();
        RetrofitHelper.getInstance().getServer()
                .getArticleDetail(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            mView.showSuccess(result.getMsg());
                            if (result.getCode() == 1) {
                                mView.setDetails(result.getData());
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
                        mView.addCommentSuccess(id);
                    }
                }, throwable -> throwable.printStackTrace());
    }


}
