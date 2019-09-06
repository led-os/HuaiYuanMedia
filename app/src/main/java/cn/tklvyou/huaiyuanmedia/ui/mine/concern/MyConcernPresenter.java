package cn.tklvyou.huaiyuanmedia.ui.mine.concern;


import android.annotation.SuppressLint;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.ui.mine.my_article.MyArticleContract;


@SuppressLint("CheckResult")
public class MyConcernPresenter extends BasePresenter<MyConcernContract.View> implements MyConcernContract.Presenter {


    @Override
    public void getConcernList(int type, int p) {
        RetrofitHelper.getInstance().getServer()
                .getMyConcernList(p, type)
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
                .subscribe(result -> {
                            if (mView != null) {
                                mView.showSuccess(result.getMsg());
                                if (result.getCode() == 1) {
                                    mView.setConcernList(p, result.getData());
                                }
                            }

                        }, throwable -> {
                            if (mView != null) {
                                mView.showFailed("");
                                mView.setConcernList(p, null);
                            }
                        }
                );
    }

    @Override
    public void addConcern(int id, int position, int type) {
        RetrofitHelper.getInstance().getServer()
                .addConcern(id, type)
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
    public void cancelConcern(int id, int position, int type) {
        RetrofitHelper.getInstance().getServer()
                .cancelConcern(id, type)
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
}
