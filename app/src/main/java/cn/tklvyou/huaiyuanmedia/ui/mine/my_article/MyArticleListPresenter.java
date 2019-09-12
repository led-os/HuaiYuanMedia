package cn.tklvyou.huaiyuanmedia.ui.mine.my_article;


import android.annotation.SuppressLint;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.common.RequestConstant;


@SuppressLint("CheckResult")
public class MyArticleListPresenter extends BasePresenter<MyArticleContract.View> implements MyArticleContract.Presenter {

    @Override
    public void getNewList(String module, int p) {
        RetrofitHelper.getInstance().getServer()
                .getMyArticleList(p, module)
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
                .subscribe(result -> {
                            if (mView != null) {
                                mView.showSuccess(result.getMsg());
                                if (result.getCode() == 1) {
                                    mView.setNewList(p, result.getData());
                                }
                            }
                        }, throwable -> {
                            if (mView != null)
                                mView.showFailed("");
                        }
                );
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


    @Override
    public void cancelArticleList(String ids) {
        RetrofitHelper.getInstance().getServer()
                .cancelArticleList(ids)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                        mView.cancelArticleSuccess(false);
                    }
                }, throwable -> mView.showFailed(""));
    }

    @Override
    public void cancelArticleAll() {
        RetrofitHelper.getInstance().getServer()
                .cancelArticleAll()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                        mView.cancelArticleSuccess(true);
                    }
                }, throwable -> mView.showFailed(""));
    }

}
