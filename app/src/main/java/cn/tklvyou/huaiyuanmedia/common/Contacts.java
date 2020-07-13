package cn.tklvyou.huaiyuanmedia.common;

import cn.tklvyou.huaiyuanmedia.BuildConfig;

/**
 * 常量
 */

public class Contacts {
    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    public static final String WX_APPID = "wxc41e2383f813afc5";

    public static final String QQ_APPID = "101788396";

    public static final String SHARE_DOWNLOAD_URL = "http://huaiyuanweb.dashuiniu.com.cn/#/upload";

    public static final String SHARE_BASE_URL = "http://huaiyuanweb.dashuiniu.com.cn/#/articleDetail/";

    public static final String SHARE_SELECTION_URL = "http://huaiyuanweb.dashuiniu.com.cn/#/selectionContent/";

    public static final String WB_APP_KEY = "4128160158";
    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     *
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String WB_REDIRECT_URL = "http://huaiyuan.dashuiniu.com.cn/api/third/weibocallback";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * <p>
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * <p>
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * <p>
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String WB_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";


    public static String DEV_BASE_URL = "http://huaiyuan.dashuiniu.com.cn/";
    public static String PRO_BASE_URL = "http://huaiyuan.dashuiniu.com.cn/";

    public static String WEB_URL = "http://huaiyuanweb.dashuiniu.com.cn/";



    public static boolean cache = true;//开启缓存
    public static boolean preload = true;//开启预加载

    public static final String SPLIT_TAG = "&###&";

}
