package cn.tklvyou.huaiyuanmedia.ui.home.new_list;


import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;


public class NewListPresenter extends BasePresenter<NewListContract.View> implements NewListContract.Presenter {

    @Override
    public void getDetailsById(int id) {
        RetrofitHelper.getInstance().getServer()
                .getArticleDetail(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                }, throwable -> throwable.printStackTrace());
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
    public void getJuZhengHeader(String module) {
        RetrofitHelper.getInstance().getServer()
                .getJuZhengHeader(module)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.setJuZhengHeader(result.getData());
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> {
                    mView.setJuZhengHeader(null);
                    mView.showFailed("");
                });
    }


    @Override
    public void getVerticalHeader(String module) {
        RetrofitHelper.getInstance().getServer()
                .getZhaiYaoHeader(module)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.setVerticalHeader(result.getData());
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> {
                    mView.setVerticalHeader(null);
                    mView.showFailed("");
                });
    }

    @Override
    public void getNewList(String module, String module_second, int p, boolean showLoading) {
        if (showLoading) {
            mView.showLoading();
        }
        RetrofitHelper.getInstance().getServer()
                .getNewList(module, module_second, p)
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
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
    public void getHaveSecondModuleNews(int p, String module, boolean showLoading) {
        if (showLoading) {
            mView.showLoading();
        }
        RetrofitHelper.getInstance().getServer()
                .getHaveSecondModuleNews(module, p)
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
                .subscribe(result -> {
                            mView.showSuccess("");
                            if (result.getCode() == 1) {
                                if (mView != null) {
                                    mView.setHaveSecondModuleNews(p, result.getData());
                                }
                            } else {
                                ToastUtils.showShort(result.getMsg());
                            }
                        }, throwable -> {
                            if (mView != null) {
                                mView.setHaveSecondModuleNews(p, null);
                                mView.showFailed("");
                            }
                        }
                );
    }

    @Override
    public void getBanner(String module) {
        RetrofitHelper.getInstance().getServer()
                .getBanner(module)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.setBanner(result.getData());
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> {
                    mView.setBanner(null);
                    mView.showFailed("");
                });
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
    public void getPingXuanList(int p, boolean showLoading) {
        if (showLoading) {
            mView.showLoading();
        }
        RetrofitHelper.getInstance().getServer()
                .getPingXuanList("评选",p)
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
                .subscribe(result -> {
                            mView.showSuccess("");
                            if (result.getCode() == 1) {
                                if (mView != null) {
                                    mView.setPingXuanList(p, result.getData());
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


}
