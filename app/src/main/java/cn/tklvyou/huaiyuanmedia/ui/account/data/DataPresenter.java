package cn.tklvyou.huaiyuanmedia.ui.account.data;


import android.annotation.SuppressLint;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;

import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.common.RequestConstant;
import cn.tklvyou.huaiyuanmedia.utils.QiniuUploadManager;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2019年07月31日17:20
 * @Email: 971613168@qq.com
 */
@SuppressLint("CheckResult")
public class DataPresenter extends BasePresenter<IDataContract.DataView> implements IDataContract.IDataPresenter {

    private static final String TAG = "DataPresenter";

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
    public void editUserInfo(String avatar, String newNickName, String userName, String bio) {
        mView.showLoading();
        RetrofitHelper.getInstance().getServer()
                .editUserInfo(avatar, userName, newNickName, bio)
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    mView.showSuccess(result.getMsg());
                    if (result.getCode() == RequestConstant.CODE_REQUEST_SUCCESS) {
                        mView.editSuccess();
                    }
                }, throwable -> mView.showFailed(""));
    }

    @Override
    public void doUploadImage(File file, String token, String uid, QiniuUploadManager manager) {
        String currentTim = String.valueOf(System.currentTimeMillis());

        String key = "qiniu/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + uid + "_" + currentTim + "_" + RandomStringUtils.randomAlphanumeric(6) + ".jpg";
        String mimeType = "image/jpeg";

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
                mView.showSuccess("");
            }

            @Override
            public void onUploadBlockComplete(String key) {
                Log.e(TAG, "onUploadBlockComplete");
            }

            @Override
            public void onUploadCompleted() {
                mView.uploadSuccess(key);
            }

            @Override
            public void onUploadCancel() {
                Log.e(TAG, "onUploadCancel");
            }
        });

    }
}
