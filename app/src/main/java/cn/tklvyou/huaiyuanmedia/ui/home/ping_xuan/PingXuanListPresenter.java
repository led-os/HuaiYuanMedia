package cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;


@SuppressLint("CheckResult")
public class PingXuanListPresenter extends BasePresenter<PingXuanContract.ListView> implements PingXuanContract.ListViewPresenter {


    @Override
    public void getPingXuanPersionList(int id, int p, String search, String sort) {
        RetrofitHelper.getInstance().getServer()
                .getPingXuanPersionList(id, p, search, sort)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.setPingXuanPageModel(p,result.getData());
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }


    @Override
    public void vote(int id,int position) {
        mView.showLoading();
        RetrofitHelper.getInstance().getServer()
                .vote(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if(result.getCode() == 1){
                        mView.voteSuccess(position);
                    }else {
                        mView.voteFailed();
                    }

                }, throwable -> {
                    mView.showFailed("");
                    throwable.printStackTrace();
                    mView.voteFailed();
                });
    }
}
