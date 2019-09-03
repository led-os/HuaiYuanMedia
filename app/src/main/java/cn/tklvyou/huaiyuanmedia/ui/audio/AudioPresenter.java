package cn.tklvyou.huaiyuanmedia.ui.audio;


import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.ui.home.new_list.NewListContract;


public class AudioPresenter extends BasePresenter<AudioContract.View> implements AudioContract.Presenter {

    @Override
    public void getNewList(String module, String module_second, int p, boolean showLoading) {
        if (showLoading) {
            mView.showLoading();
        }
        RetrofitHelper.getInstance().getServer()
                .getNewList(module, module_second, p)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            mView.showSuccess("");
                            if (result.getCode() == 1) {
                                if (mView != null) {
                                    mView.setNewList(p, result.getData());
                                }
                            } else {
                                ToastUtils.showShort(result.getMsg());
                            }
                        }, throwable -> {
                            if (mView != null) {
                                mView.setNewList(p, null);
                                mView.showFailed("");
                            }
                        }
                );
    }

}
