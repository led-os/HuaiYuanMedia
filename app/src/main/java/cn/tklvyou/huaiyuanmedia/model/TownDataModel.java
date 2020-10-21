package cn.tklvyou.huaiyuanmedia.model;

import java.io.Serializable;

/**
 * @author :JenkinsZhou
 * @description :乡镇部门实体
 * @company :途酷科技
 * @date 2020年10月20日11:10
 * @Email: 971613168@qq.com
 */


public class TownDataModel implements Serializable {


    private String module_second;
    private String avatar;

    public String getModule_second() {
        return module_second;
    }

    public void setModule_second(String module_second) {
        this.module_second = module_second;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }




}
