package cn.tklvyou.huaiyuanmedia.ui.home.search_list;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2019年08月02日15:25
 * @Email: 971613168@qq.com
 */
@SuppressLint("CheckResult")
public class SearchPresenter extends BasePresenter<SearchContract.View> implements SearchContract.Presenter {

    @Override
    public void searchNewList(String module, String name, int p) {
        mView.showLoading();
        RetrofitHelper.getInstance().getServer()
                .searchNewList(module, name, p)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            mView.showSuccess(result.getMsg());
                            if (result.getCode() == 1) {
                                mView.setNewList(p, result.getData());
                            }
                        }, throwable -> {
                            mView.setNewList(p,null);
                            mView.showFailed("");
                        }
                );
    }

    @Override
    public void addLikeNews(int id, int position) {
        RetrofitHelper.getInstance().getServer()
                .addLikeNews(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.updateLikeStatus(true, position);
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void cancelLikeNews(int id, int position) {
        RetrofitHelper.getInstance().getServer()
                .cancelLikeNews(id)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.updateLikeStatus(false, position);
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> throwable.printStackTrace());
    }

}
