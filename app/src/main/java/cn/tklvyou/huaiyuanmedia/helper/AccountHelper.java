package cn.tklvyou.huaiyuanmedia.helper;


import cn.tklvyou.huaiyuanmedia.model.User;
import cn.tklvyou.huaiyuanmedia.utils.CommonUtil;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2019年07月30日16:05
 * @Email: 971613168@qq.com
 */
public class AccountHelper {
    private User.UserinfoBean userInfo;

    private static class SingletonInstance {
        private static final AccountHelper INSTANCE = new AccountHelper();
    }

    public static AccountHelper getInstance() {
        return SingletonInstance.INSTANCE;
    }


    public User.UserinfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(User.UserinfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isLogin() {
        return userInfo != null;
    }

    public int getUid() {
        if (userInfo != null) {
            return userInfo.getId();
        }
        return 0;
    }

    public int getGroupId() {
        if (userInfo != null) {
            return userInfo.getGroup_id();
        }
        return 0;
    }

    public String getPhone() {
        if (userInfo != null) {
            return CommonUtil.getNotNullValue(userInfo.getMobile());
        }
        return "";
    }

    public String getAvatar() {
        if (userInfo != null) {
            return CommonUtil.getNotNullValue(userInfo.getAvatar());
        }
        return "";
    }

    public String getNickName() {
        if (userInfo != null) {
            return CommonUtil.getNotNullValue(userInfo.getNickname());
        }
        return "";
    }

    public String getUserName() {
        if (userInfo != null) {
            return CommonUtil.getNotNullValue(userInfo.getUsername());
        }
        return "";
    }
}
