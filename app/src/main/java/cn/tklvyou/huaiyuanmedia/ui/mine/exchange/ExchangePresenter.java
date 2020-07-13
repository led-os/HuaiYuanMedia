package cn.tklvyou.huaiyuanmedia.ui.mine.exchange;

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
 * @date 2019年08月01日19:10
 * @Email: 971613168@qq.com
 */
@SuppressLint("CheckResult")
public class ExchangePresenter extends BasePresenter<ExchangeRecordContract.View> implements ExchangeRecordContract.Presenter {

    @Override
    public void getExchangePageList(int page) {
        RetrofitHelper.getInstance().getServer()
                .getExchangeList(page)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (mView != null) {
                        mView.showSuccess(result.getMsg());
                        if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                            mView.setExchangeList(page, result.getData());
                        }
                    }
                }, throwable -> {
                    if (mView != null)
                        mView.showFailed("");
                });
    }

    @Override
    public void receiveGoods(int id, int position) {
        RetrofitHelper.getInstance().getServer()
                .receiveGoods(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    ToastUtils.showShort(result.getMsg());
                    if (result.getCode() == 1) {
                        mView.receiveGoodsSuccess(position);
                    }
                }, throwable -> throwable.printStackTrace());
    }


}
