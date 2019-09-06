package cn.tklvyou.huaiyuanmedia.ui.mine.browse;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.common.RequestConstant;

/**
 * @author :JenkinsZhou
 * @description :最近浏览
 * @company :途酷科技
 * @date 2019年08月02日15:25
 * @Email: 971613168@qq.com
 */
@SuppressLint("CheckResult")
public class BrowsePresenter extends BasePresenter<BrowseContract.View> implements BrowseContract.Presenter {

    @Override
    public void getBrowsePageList(int page) {
        RetrofitHelper.getInstance().getServer()
                .getRecentBrowseList(page)
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
                .subscribe(result -> {
                            if (mView != null) {
                                mView.showSuccess(result.getMsg());
                                if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                                    mView.setBrowseList(page, result.getData());
                                }
                            }

                        }, throwable -> {
                            if (mView != null)
                                mView.showFailed("");
                        }
                );
    }


}
