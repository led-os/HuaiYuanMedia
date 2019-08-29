package cn.tklvyou.huaiyuanmedia.delegate;


import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

/**
 * @author :JenkinsZhou
 * @description :包含TitleBarView接口
 * @company :途酷科技
 * @date 2019年06月25日15:15
 * @Email: 971613168@qq.com
 */
public interface ITitleView {


    /**
     * 用于最终实现子类设置TitleBarView 其它属性
     *
     * @param titleBar
     */
    void setTitleBar(CommonTitleBar titleBar);
}
