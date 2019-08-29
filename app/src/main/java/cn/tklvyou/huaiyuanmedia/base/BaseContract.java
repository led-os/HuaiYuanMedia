package cn.tklvyou.huaiyuanmedia.base;


import com.trello.rxlifecycle3.LifecycleTransformer;

/**
 * 基础配置约定
 */

public interface BaseContract {

    interface BasePresenter<T extends BaseView> {
        /**
         * view挂载
         *
         * @param view
         */
        void attachView(T view);

        /**
         * View卸载
         */
        void detachView();
    }

    interface BaseView {

        /**
         * 显示进度中
         */
        void showLoading();

        /**
         * 整个页面显示进度中
         */
        void showPageLoading();

        /**
         * 显示请求成功
         *
         * @param message
         */
        void showSuccess(String message);

        /**
         * 失败重试
         *
         * @param message
         */
        void showFailed(String message);

        /**
         * 显示当前网络不可用
         */
        void showNoNet();

        /**
         * 显示当前无数据
         */
        void showNoData();


        /**
         * 重试
         */
        void onRetry();

        /**
         * 绑定生命周期
         *
         * @param <T>
         * @return
         */
        <T> LifecycleTransformer<T> bindToLife();
    }
}