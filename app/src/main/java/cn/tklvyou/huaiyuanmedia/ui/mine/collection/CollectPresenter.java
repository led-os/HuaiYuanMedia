package cn.tklvyou.huaiyuanmedia.ui.mine.collection;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cjt2325.cameralibrary.util.LogUtil;
import com.google.gson.Gson;

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
public class CollectPresenter extends BasePresenter<CollectContract.View> implements CollectContract.Presenter {

    @Override
    public void getCollectPageList(int page) {
        RetrofitHelper.getInstance().getServer()
                .getMyCollectList(page)
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
                .subscribe(result -> {
                            LogUtils.e(new Gson().toJson(result));
                            if (mView != null) {
                                mView.showSuccess(result.getMsg());
                                if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                                    mView.setCollectList(page, result.getData());
                                }
                            }
                        }, throwable -> {
                            throwable.printStackTrace();
                            if (mView != null)
                                mView.showFailed("");
                        }

                );

    }

    @Override
    public void cancelCollectList(String ids) {
        RetrofitHelper.getInstance().getServer()
                .cancelCollectList(ids)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                        mView.cancelCollectSuccess(false);
                    }
                }, throwable -> mView.showFailed(""));
    }

    @Override
    public void cancelCollectAll() {
        RetrofitHelper.getInstance().getServer()
                .cancelCollectAll()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                        mView.cancelCollectSuccess(true);
                    }
                }, throwable -> mView.showFailed(""));
    }
}
