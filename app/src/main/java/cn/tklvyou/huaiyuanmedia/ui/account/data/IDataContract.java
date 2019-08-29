package cn.tklvyou.huaiyuanmedia.ui.account.data;

import java.io.File;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.utils.QiniuUploadManager;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2019年07月31日16:57
 * @Email: 971613168@qq.com
 */
public interface IDataContract {

    interface DataView extends BaseContract.BaseView {
        void setQiniuToken(String token);
        void showInputDialog(String content);
        void editSuccess();
        void uploadSuccess(String url);
    }


    interface IDataPresenter extends BaseContract.BasePresenter<DataView> {
        void getQiniuToken();

        void editUserInfo(String avatar,String newNickName,String userName,String bio);

        void doUploadImage(File file, String token, String uid, QiniuUploadManager manager);
    }





}
