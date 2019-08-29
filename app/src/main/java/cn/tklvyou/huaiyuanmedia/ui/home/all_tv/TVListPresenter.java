package cn.tklvyou.huaiyuanmedia.ui.home.all_tv;


import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;


public class TVListPresenter extends BasePresenter<TVListContract.View> implements TVListContract.Presenter {

    @Override
    public void getNewList(String module, String module_second, int p) {
        RetrofitHelper.getInstance().getServer()
                .getNewList(module, module_second, p)
                .compose(RxSchedulers.applySchedulers())
                .subscribe(result -> {
                            if (result.getCode() == 1) {
                                if (mView != null) {
                                    mView.setNewList(p, result.getData());
                                }
                            } else {
                                ToastUtils.showShort(result.getMsg());
                            }
                        }, throwable -> {
                            if (mView != null) {
                                mView.showFailed("");
                            }
                        }
                );
    }

}
