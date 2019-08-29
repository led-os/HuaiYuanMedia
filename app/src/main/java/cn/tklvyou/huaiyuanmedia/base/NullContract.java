package cn.tklvyou.huaiyuanmedia.base;

/**
 * 空配置约定
 */

public interface NullContract {
    interface View extends BaseContract.BaseView{

    }
    interface Presenter extends BaseContract.BasePresenter<View>{
    }
}
