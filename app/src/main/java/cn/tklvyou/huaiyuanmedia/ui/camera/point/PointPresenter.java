package cn.tklvyou.huaiyuanmedia.ui.camera.point;


import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.helper.AccountHelper;


public class PointPresenter extends BasePresenter<PointContract.View> implements PointContract.Presenter{


    @Override
    public void getGoodsPageList(int page) {
        RetrofitHelper.getInstance().getServer()
                .getGoodsPageList(page)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if(result.getCode() ==1){
                        mView.setGoods(page,result.getData());
                    }else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> mView.showFailed(""));
    }

    @Override
    public void getUser() {
        RetrofitHelper.getInstance().getServer()
                .getUser()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.setUser(result.getData());
                        AccountHelper.getInstance().setUserInfo(result.getData());
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }

                }, throwable -> {
                    throwable.printStackTrace();
                });
    }
}
