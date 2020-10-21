package cn.tklvyou.huaiyuanmedia.ui.mine.reward;

import android.annotation.SuppressLint;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2020年10月21日16:39
 * @Email: 971613168@qq.com
 */
@SuppressLint("CheckResult")
public class RewardPresenter extends BasePresenter<RewardContract.View> implements RewardContract.Presenter  {
    @Override
    public void getRewardListPageList(int page) {
        RetrofitHelper.getInstance().getServer().getAwardList(page,10) .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            mView.showSuccess("");
                            if (result.getCode() == 1) {
                                mView.setRewardList(page, result.getData());
                            }
                        }, throwable -> {
                            mView.setRewardList(page, null);
                            mView.showFailed("");
                        }
                );
    }
}
