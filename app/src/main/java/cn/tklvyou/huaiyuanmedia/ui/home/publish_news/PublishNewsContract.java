package cn.tklvyou.huaiyuanmedia.ui.home.publish_news;

import java.io.File;
import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.utils.QiniuUploadManager;


public interface PublishNewsContract {
    interface View extends BaseContract.BaseView {
        void publishSuccess();
        void publishError();

        void uploadImageSuccess(String url);

        void uploadVideoSuccess(String url);

        void uploadImagesSuccess(List<String> urls);

        void setQiniuToken(String token);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void qiniuUploadMultiImage(List<File> files, String token, String uid, QiniuUploadManager manager);

        void publishYuanChuang(String name, String images, String video, String image, String time);

        void publishLifeMsg(String name, String images, String video, String image, String time);

        void getQiniuToken();

        void qiniuUploadFile(File file, boolean isVideo, String token, String uid, QiniuUploadManager manager);
    }
}
