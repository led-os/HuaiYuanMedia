package cn.tklvyou.huaiyuanmedia.ui.mine.wenzhen;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.common.RequestConstant;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2019年08月02日15:25
 * @Email: 971613168@qq.com
 */
@SuppressLint("CheckResult")
public class WenZhenPresenter extends BasePresenter<WenZhenContract.View> implements WenZhenContract.Presenter {


    @Override
    public void getDataPageList(int page) {
        RetrofitHelper.getInstance().getServer()
                .getMyArticleList(page, "爆料")
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (mView != null) {
                        mView.showSuccess(result.getMsg());
                        if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                            mView.setDataList(page, result.getData());
                        }
                    }
                }, throwable -> {
                    if (mView != null)
                        mView.showFailed("");
                });
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
