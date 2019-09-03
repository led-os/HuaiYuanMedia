package cn.tklvyou.huaiyuanmedia.ui.home.publish_news;

import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;

import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.utils.QiniuUploadManager;


public class PublishNewsPresenter extends BasePresenter<PublishNewsContract.View> implements PublishNewsContract.Presenter {

    private static final String TAG = "PublishNewsPresenter";

    @Override
    public void qiniuUploadMultiImage(List<File> files, String token, String uid, QiniuUploadManager manager) {
        List<QiniuUploadManager.QiniuUploadFile> bodys = new ArrayList<>();
        List<String> keys = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            String currentTim = String.valueOf(System.currentTimeMillis());
            String key = "qiniu/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + uid + "_" + currentTim + "_" + RandomStringUtils.randomAlphanumeric(6) + ".jpg";
            String mimeType = "image/jpeg";

            QiniuUploadManager.QiniuUploadFile param = new QiniuUploadManager.QiniuUploadFile(files.get(i).getAbsolutePath(), key, mimeType, token);
            bodys.add(param);
            keys.add(key);
        }

        manager.upload(bodys, new QiniuUploadManager.OnUploadListener() {
            @Override
            public void onStartUpload() {
                Log.e(TAG, "onStartUpload");
            }

            @Override
            public void onUploadProgress(String key, double percent) {
            }

            @Override
            public void onUploadFailed(String key, String err) {
                Log.e(TAG, "onUploadFailed:" + err);
                if (err.contains("no token")) {
                    getQiniuToken();
                }

                if(mView != null) {
                    mView.showSuccess("上传失败,请重新上传");
                    mView.publishError();
                }
            }

            @Override
            public void onUploadBlockComplete(String key) {
                Log.e(TAG, "onUploadBlockComplete");
            }

            @Override
            public void onUploadCompleted() {
                if(mView != null) {
                    mView.showSuccess("");
                    mView.uploadImagesSuccess(keys);
                }
            }

            @Override
            public void onUploadCancel() {
                Log.e(TAG, "onUploadCancel");
            }
        });

    }

    @Override
    public void publishVShi(String name, String video, String image, String time) {
        RetrofitHelper.getInstance().getServer()
                .publishVShi(name, video, image, time)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                            ToastUtils.showShort(result.getMsg());
                            if (result.getCode() == 1) {
                                mView.publishSuccess();
                            } else {
                                mView.publishError();
                            }
                        }, throwable -> {
                            mView.showFailed("");
                            mView.publishError();

                        }

                );
    }

    @Override
    public void publishSuiShouPai(String name, String images, String video, String image, String time) {
        RetrofitHelper.getInstance().getServer()
                .publishLifeMsg(name, images, video, image, time)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    ToastUtils.showShort(result.getMsg());
                    if (result.getCode() == 1) {
                        mView.publishSuccess();
                    } else {
                        mView.publishError();
                    }
                }, throwable -> {
                    mView.showFailed("");
                    mView.publishError();
                });
    }

    @Override
    public void getQiniuToken() {
        RetrofitHelper.getInstance().getServer()
                .getQiniuToken()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.setQiniuToken(result.getData().toString());
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    @Override
    public void qiniuUploadFile(File file, boolean isVideo, String token, String uid, QiniuUploadManager manager) {
        String currentTim = String.valueOf(System.currentTimeMillis());
        String key;
        String mimeType;
        if (isVideo) {
            key = "qiniu/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + uid + "_" + currentTim + "_" + RandomStringUtils.randomAlphanumeric(6) + ".mp4";
            mimeType = "video/webm";
        } else {
            key = "qiniu/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + uid + "_" + currentTim + "_" + RandomStringUtils.randomAlphanumeric(6) + ".jpg";
            mimeType = "image/jpeg";
        }
        QiniuUploadManager.QiniuUploadFile param = new QiniuUploadManager.QiniuUploadFile(file.getAbsolutePath(), key, mimeType, token);
        manager.upload(param, new QiniuUploadManager.OnUploadListener() {
            @Override
            public void onStartUpload() {
                Log.e(TAG, "onStartUpload");
            }

            @Override
            public void onUploadProgress(String key, double percent) {
            }

            @Override
            public void onUploadFailed(String key, String err) {
                Log.e(TAG, "onUploadFailed:" + err);
                if (err.contains("no token")) {
                    getQiniuToken();
                }

                if (mView != null) {
                    mView.showSuccess("上传失败,请重新上传");
                    mView.publishError();
                }
            }

            @Override
            public void onUploadBlockComplete(String key) {
                Log.e(TAG, "onUploadBlockComplete");
            }

            @Override
            public void onUploadCompleted() {
                if (mView != null) {
                    if (isVideo) {
                        mView.uploadVideoSuccess(key);
                    } else {
                        mView.uploadImageSuccess(key);
                    }
                }
            }

            @Override
            public void onUploadCancel() {
                Log.e(TAG, "onUploadCancel");
            }
        });
    }

}
