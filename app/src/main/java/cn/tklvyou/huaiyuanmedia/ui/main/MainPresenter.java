package cn.tklvyou.huaiyuanmedia.ui.main;


import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;

import cn.tklvyou.huaiyuanmedia.api.BaseResult;
import cn.tklvyou.huaiyuanmedia.api.Entity2;
import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.LifeInfo;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;
import cn.tklvyou.huaiyuanmedia.model.SystemConfigModel;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function4;


public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    @Override
    public void getSystemConfig() {

        if (SPUtils.getInstance().getString("token", "").isEmpty()) {
            RetrofitHelper.getInstance().getServer()
                    .getSystemConfig()
                    .compose(RxSchedulers.applySchedulers())
                    .compose(mView.bindToLife())
                    .subscribe(result -> {
                        if (result.getCode() == 1) {
                            mView.setSystemConfig(result.getData(), null);
                        } else {
                            ToastUtils.showShort(result.getMsg());
                        }

                    }, throwable -> {
                        throwable.printStackTrace();
                    });
        } else {
            Observable config = RetrofitHelper.getInstance().getServer().getSystemConfig();
            Observable life_info = RetrofitHelper.getInstance().getServer().getLifeInfo();
            Observable.zip(config, life_info,
                    (BiFunction<BaseResult<SystemConfigModel>, BaseResult<LifeInfo>, Entity2<BaseResult<SystemConfigModel>, BaseResult<LifeInfo>>>)
                            (systemConfigModelBaseResult, lifeInfoBaseResult) -> new Entity2(systemConfigModelBaseResult, lifeInfoBaseResult))
                    .compose(RxSchedulers.applySchedulers())
                    .compose(mView.bindToLife())
                    .subscribe(result -> {
                        Entity2<BaseResult<SystemConfigModel>, BaseResult<LifeInfo>> res = (Entity2) result;
                        mView.setSystemConfig(res.getValue1().getCode() == 0 ? null : res.getValue1().getData(),
                                res.getValue2().getCode() == 0 ? null : res.getValue2().getData());
                    }, throwable -> {
                        ((Throwable) throwable).printStackTrace();
                    });
        }

    }

    private boolean isEnable = true;

    @Override
    public void getLifeInfo() {
        if (SPUtils.getInstance().getString("token", "").isEmpty()) {
            return;
        }
        if (isEnable) {
            isEnable = false;
            RetrofitHelper.getInstance().getServer()
                    .getLifeInfo()
                    .compose(RxSchedulers.applySchedulers())
                    .compose(mView.bindToLife())
                    .subscribe(result -> {
                        isEnable = true;
                        if (result.getCode() == 1) {
                            mView.setSystemConfig(null, result.getData());
                        } else {
                            ToastUtils.showShort(result.getMsg());
                        }
                    }, throwable -> {
                        isEnable = true;
                        throwable.printStackTrace();
                    });
        }


    }


}
