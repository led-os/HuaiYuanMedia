package cn.tklvyou.huaiyuanmedia.ui.home.publish_wenzheng;

import java.io.File;
import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;
import cn.tklvyou.huaiyuanmedia.utils.QiniuUploadManager;


public interface PublishWenzhengContract {
    interface View extends BaseContract.BaseView{
        void setQiniuToken(String token);
        void setJuZhengHeader(List<NewsBean> beans);
        void publishSuccess();
        void uploadImagesSuccess(List<String> urls);
    }
    interface Presenter extends BaseContract.BasePresenter<View>{
        void getQiniuToken();
        void getJuZhengHeader(String module);
        void qiniuUploadMultiImage(List<File> files, String token, String uid, QiniuUploadManager manager);
        void publishWenZheng(String module_second, String name, String content, String images);

    }
}
