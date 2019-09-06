package cn.tklvyou.huaiyuanmedia.ui.mine.wenzhen;

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
 * @date 2019年08月02日15:25
 * @Email: 971613168@qq.com
 */
@SuppressLint("CheckResult")
public class WenZhenPresenter extends BasePresenter<WenZhenContract.View> implements WenZhenContract.Presenter {


    @Override
    public void getDataPageList(int page) {
        RetrofitHelper.getInstance().getServer()
                .getMyArticleList(page, "问政")
                .compose(RxSchedulers.applySchedulers())
//                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (mView != null) {
                        mView.showSuccess(result.getMsg());
                        if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                            mView.setDataList(page, result.getData());
                        }
                    }
                }, throwable -> {
                    if (mView != null)
                        mView.showFailed("");
                });
    }
}
