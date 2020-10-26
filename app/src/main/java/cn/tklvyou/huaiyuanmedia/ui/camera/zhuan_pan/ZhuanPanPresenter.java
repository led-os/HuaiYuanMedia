package cn.tklvyou.huaiyuanmedia.ui.camera.zhuan_pan;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;

public class ZhuanPanPresenter extends BasePresenter<ZhuanPanContract.View> implements ZhuanPanContract.Presenter {

    @Override
    public void getLotteryModel() {
        RetrofitHelper.getInstance().getServer()
                .getLotteryModel()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.setLotteryModel(result.getData());
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void getLotteryNum() {
        RetrofitHelper.getInstance().getServer()
                .getLotteryModel()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.setLotteryNum(result.getData());
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void startLottery() {
        RetrofitHelper.getInstance().getServer()
                .startLottery()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.setLotteryResult(result.getData());
                    } else {
                        mView.setLotteryResult(null);
                        ToastUtils.showShort(result.getMsg());
                    }

                }, throwable -> {
                    throwable.printStackTrace();
                    mView.setLotteryResult(null);
                });
    }

    @Override
    public void shareAward() {
        RetrofitHelper.getInstance().getServer()
                .shareAward()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == 1) {
                        mView.addNum();
                    }

                }, throwable -> {
                    throwable.printStackTrace();
                });
    }
}
