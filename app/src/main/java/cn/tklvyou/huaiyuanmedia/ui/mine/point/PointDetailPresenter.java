package cn.tklvyou.huaiyuanmedia.ui.mine.point;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.common.RequestConstant;
import cn.tklvyou.huaiyuanmedia.helper.AccountHelper;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2019年08月01日17:40
 * @Email: 971613168@qq.com
 */
@SuppressLint("CheckResult")
public class PointDetailPresenter extends BasePresenter<PointDetailContract.View> implements PointDetailContract.Presenter {

    @Override
    public void getPointPageList(int page) {
        RetrofitHelper.getInstance().getServer()
                .getMyPointList(page)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            mView.showSuccess(result.getMsg());
                            if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                                mView.setPointDetails(page, result.getData());
                            }
                        }, throwable -> {
                            mView.showFailed("");
                        }
                );
    }

    @Override
    public void getUser() {
        RetrofitHelper.getInstance().getServer()
                .getUser()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                        mView.setUser(result.getData());
                        AccountHelper.getInstance().setUserInfo(result.getData());
                        mView.setUser(result.getData());
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }

                }, throwable -> {
                    throwable.printStackTrace();
                });
    }
}
