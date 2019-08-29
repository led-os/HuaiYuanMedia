package cn.tklvyou.huaiyuanmedia.ui.setting;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2019年07月30日17:25
 * @Email: 971613168@qq.com
 */
public class SettingPresenter extends BasePresenter<SettingContract.LogoutView>implements SettingContract.LogoutPresenter  {

    @Override
    public void logout() {
        RetrofitHelper.getInstance().getServer()
                .logout()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                         if(result.getCode() == 1){
                             mView.logoutSuccess();
                         }else {
                             ToastUtils.showShort(result.getMsg());
                         }
                        }, throwable -> {
                            throwable.printStackTrace();
                        }

                );
    }
}
