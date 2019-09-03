package cn.tklvyou.huaiyuanmedia.ui.camera;


import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.ui.home.new_list.NewListContract;


public class CameraPresenter extends BasePresenter<CameraContract.View> implements CameraContract.Presenter {

    @Override
    public void getLifeHotList(int p) {
        mView.showLoading();
        RetrofitHelper.getInstance().getServer()
                .getLifeHotList(p)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            mView.showSuccess("");
                            if (result.getCode() == 1) {
                                if (mView != null) {
                                    mView.setLifeHotList(p, result.getData());
                                }
                            } else {
                                ToastUtils.showShort(result.getMsg());
                            }
                        }, throwable -> {
                            if (mView != null) {
                                mView.setLifeHotList(p, null);
                                mView.showFailed("");
                            }
                        }
                );
    }

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
