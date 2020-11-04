package cn.tklvyou.huaiyuanmedia.ui.home.life_detail;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;

/**
 * @author :JenkinsZhou
 * @description :生活圈详情
 * @company :途酷科技
 * @date 2020年11月03日14:32
 * @Email: 971613168@qq.com
 */
@SuppressLint("CheckResult")
public class LifeDetailPresenter extends BasePresenter<LifeDetailContract.View> implements LifeDetailContract.Presenter {

    @Override
    public void addConcern(int id, int type) {
        RetrofitHelper.getInstance().getServer()
                .addConcern(id, type)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        ToastUtils.showShort("关注成功");
                        mView.addConcernSuccess();
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void cancelConcern(int id, int type) {
        RetrofitHelper.getInstance().getServer()
                .cancelConcern(id, type)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        ToastUtils.showShort("取消成功");
                        mView.cancelSuccess();
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }


    @Override
    public void getDetailsById(int id, boolean showPageLoading, boolean isLife) {
        if (showPageLoading) {
            mView.showPageLoading();
        } else {
            mView.showLoading();
        }

        if (isLife) {
            RetrofitHelper.getInstance().getServer()
                    .getLifeDetail(id)
                    .compose(RxSchedulers.applySchedulers())
                    .compose(mView.bindToLife())
                    .subscribe(result -> {
                        mView.showSuccess(result.getMsg());
                        if (result.getCode() == 1) {
                            mView.setDetails(result.getData());
                        }
                    }, throwable -> {
                        mView.showFailed(throwable.getMessage());
                    });
        } else {
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
                        mView.showFailed(throwable.getMessage());
                    });
        }

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
    public void sendVote(int vote_id, int option_id) {
        RetrofitHelper.getInstance().getServer()
                .sendVote(vote_id, option_id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    ToastUtils.showShort(result.getMsg());
                    if (result.getCode() == 1) {
                        mView.sendVoteSuccess(result.getData());
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void getScoreByRead(int id) {
        RetrofitHelper.getInstance().getServer()
                .getScoreByRead(id)
                .compose(RxSchedulers.applySchedulers())
                .subscribe(result -> {
                    if (!StringUtils.isEmpty(result.getMsg())) {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void getScoreByShare(int id) {
        RetrofitHelper.getInstance().getServer()
                .getScoreByShare(id)
                .compose(RxSchedulers.applySchedulers())
                .subscribe(result -> {
                    if (!StringUtils.isEmpty(result.getMsg())) {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void getCommentList(int article_id, int p) {
        RetrofitHelper.getInstance().getServer()
                .getCommentList(article_id, p)
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
                .subscribe(result -> {
                            if (mView != null) {
                                mView.showSuccess(result.getMsg());
                                if (result.getCode() == 1) {
                                    mView.setCommentList(p, result.getData());
                                }

                            }
                        }, throwable -> {
                            if (mView != null)
                                mView.showFailed("");
                        }
                );
    }

}
