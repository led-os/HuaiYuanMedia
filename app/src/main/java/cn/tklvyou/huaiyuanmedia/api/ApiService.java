package cn.tklvyou.huaiyuanmedia.api;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseResult;
import cn.tklvyou.huaiyuanmedia.model.AdModel;
import cn.tklvyou.huaiyuanmedia.model.AttentionModel;
import cn.tklvyou.huaiyuanmedia.model.BannerModel;
import cn.tklvyou.huaiyuanmedia.model.BasePageModel;
import cn.tklvyou.huaiyuanmedia.model.ChannelModel;
import cn.tklvyou.huaiyuanmedia.model.CommentModel;
import cn.tklvyou.huaiyuanmedia.model.ConcernModel;
import cn.tklvyou.huaiyuanmedia.model.ExchangeModel;
import cn.tklvyou.huaiyuanmedia.model.HaveSecondModuleNewsModel;
import cn.tklvyou.huaiyuanmedia.model.LotteryModel;
import cn.tklvyou.huaiyuanmedia.model.LotteryResultModel;
import cn.tklvyou.huaiyuanmedia.model.MessageModel;
import cn.tklvyou.huaiyuanmedia.model.MyCommentModel;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;
import cn.tklvyou.huaiyuanmedia.model.PingXuanModel;
import cn.tklvyou.huaiyuanmedia.model.PingXuanPersionModel;
import cn.tklvyou.huaiyuanmedia.model.PointDetailModel;
import cn.tklvyou.huaiyuanmedia.model.PointModel;
import cn.tklvyou.huaiyuanmedia.model.PointRuleModel;
import cn.tklvyou.huaiyuanmedia.model.SystemConfigModel;
import cn.tklvyou.huaiyuanmedia.model.UploadModel;
import cn.tklvyou.huaiyuanmedia.model.User;
import cn.tklvyou.huaiyuanmedia.model.VoteOptionModel;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    /**
     * 下载网络图片
     * @param fileUrl
     * @return
     */
    @GET
    @Streaming
    Observable<ResponseBody> downLoadFile(@Url String fileUrl);


    /**
     * 广告页数据
     */
    @POST("api/index/start")
    Observable<BaseResult<List<AdModel>>> getAdView();


    /**
     * 获取七牛云上传token
     */
    @POST("api/index/qiniutoken")
    Observable<BaseResult<Object>> getQiniuToken();

    /**
     * 单文件上传接口
     */
    @Multipart
    @POST("api/common/upload")
    Observable<BaseResult<UploadModel>> upload(@Part MultipartBody.Part file);

    /**
     * 多文件上传接口
     */
    @Multipart
    @POST("api/common/uploads")
    Observable<BaseResult<List<String>>> uploadFiles(@Part List<MultipartBody.Part> files);


    /**
     * 会员中心
     */
    @POST("api/user/index")
    Observable<BaseResult<User.UserinfoBean>> getUser();

    /**
     * 注册会员
     */
    @POST("api/user/register")
    Observable<BaseResult<User>> register(@Query("mobile") String mobile, @Query("password") String password, @Query("captcha") String captcha);

    /**
     * 会员登录
     */
    @POST("api/user/login")
    Observable<BaseResult<User>> login(@Query("account") String account, @Query("password") String password);


    /**
     * 验证码登录
     */
    @POST("api/user/mobilelogin")
    Observable<BaseResult<User>> codeLogin(@Query("mobile") String mobile, @Query("captcha") String captcha);


    /**
     * 修改密码
     */
    @POST("api/user/resetpwd")
    Observable<BaseResult<Object>> resetPass(@Query("mobile") String mobile, @Query("newpassword") String password, @Query("captcha") String captcha);


    /**
     * 修改手机号
     */
    @POST("api/user/changemobile")
    Observable<BaseResult<Object>> chaneMobile(@Query("mobile") String mobile, @Query("captcha") String captcha);

    /**
     * 第三方登录
     *
     * @param platform wechat
     */
    @POST("api/third/login")
    Observable<BaseResult<User>> thirdLogin(@Query("platform") String platform, @Query("code") String code);

    /**
     * 绑定手机号
     */
    @POST("api/third/bindmobile")
    Observable<BaseResult<User>> bindMobile(@Query("third_id") int third_id, @Query("mobile") String mobile, @Query("captcha") String captcha);

    /**
     * 注销登录
     */
    @POST("api/user/logout")
    Observable<BaseResult<Object>> logout();

    /**
     * 获取系统配置信息
     */
    @POST("api/index/sysconfig")
    Observable<BaseResult<SystemConfigModel>> getSystemConfig();

    /**
     * 重置密码
     */
    @POST("api/user/resetpwd")
    Observable<BaseResult<Object>> resetpwd(@Query("mobile") String mobile, @Query("newpassword") String newpassword, @Query("captcha") String captcha);

    /**
     * 发送验证码
     */
    @POST("api/sms/send")
    Observable<BaseResult<Object>> sendSms(@Query("mobile") String mobile, @Query("event") String event);

    /**
     * 首页频道
     */
    @POST("api/module/index")
    Observable<BaseResult<List<String>>> getHomeChannel();

    /**
     * 修改频道
     */
    @POST("api/module/add")
    Observable<BaseResult<Object>> saveHomeChannel(@Query("module") String module);


    /**
     * 所有频道
     */
    @POST("api/module/module")
    Observable<BaseResult<ChannelModel>> getTotalChannel();

    /**
     * 积分明细
     *
     * @return
     */
    @POST("api/score/index")
    Observable<BaseResult<BasePageModel<PointDetailModel>>> getMyPointList(@Query("p") int p);

    /**
     * 阅读内容获取积分
     *
     * @return
     */
    @POST("api/score/read")
    Observable<BaseResult<BasePageModel<Object>>> getScoreByRead(@Query("id") int id);

    /**
     * 分享转发获取积分
     *
     * @return
     */
    @POST("api/score/share")
    Observable<BaseResult<BasePageModel<Object>>> getScoreByShare(@Query("id") int id);

    /**
     * 现场领取
     *
     * @return
     */
    @POST("api/goods/receive")
    Observable<BaseResult<BasePageModel<Object>>> receiveGoods(@Query("id") int id);


    /**
     * 内容列表
     */
    @POST("api/article/index")
    Observable<BaseResult<BasePageModel<NewsBean>>> getNewList(@Query("module") String module, @Query("module_second") String module_second, @Query("p") int p);

    /**
     * 搜索文章
     */
    @POST("api/article/index")
    Observable<BaseResult<BasePageModel<NewsBean>>> searchNewList(@Query("module") String module, @Query("name") String name, @Query("p") int p);

    /**
     * 获取评论列表
     */
    @POST("api/comment/index")
    Observable<BaseResult<BasePageModel<CommentModel>>> getCommentList(@Query("article_id") int article_id, @Query("p") int p);


    /**
     * 消息列表
     *
     * @param p
     * @return
     */
    @POST("api/msg/index")
    Observable<BaseResult<BasePageModel<MessageModel>>> getSystemMsgList(@Query("p") int p);

    /**
     * 清空消息
     */
    @POST("api/msg/deleteall")
    Observable<BaseResult<Object>> clearMessage();

    /**
     * 兑换记录
     *
     * @param p
     * @return
     */
    @POST("api/goods/record")
    Observable<BaseResult<BasePageModel<ExchangeModel>>> getExchangeList(@Query("p") int p);

    /**
     * 濉溪TV,矩阵
     */
    @POST("api/article/index")
    Observable<BaseResult<List<HaveSecondModuleNewsModel>>> getHaveSecondModuleNews(@Query("module") String module, @Query("p") int p);


    /**
     * 顶部banner
     */
    @POST("api/banner/index")
    Observable<BaseResult<List<BannerModel>>> getBanner(@Query("module") String module);


    /**
     * 获取矩阵号数据
     */
    @POST("api/article/admin")
    Observable<BaseResult<List<NewsBean>>> getJuZhengHeader(@Query("module") String module);

    /**
     * 获取摘要数据
     */
    @POST("api/article/zhaiyao")
    Observable<BaseResult<List<NewsBean>>> getZhaiYaoHeader(@Query("module") String module);

    /**
     * 文章详情
     */
    @POST("api/article/detail")
    Observable<BaseResult<NewsBean>> getArticleDetail(@Query("id") int id);

    /**
     * 生活圈详情
     */
    @POST("api/life/detail")
    Observable<BaseResult<NewsBean>> getLifeDetail(@Query("id") int id);

    /**
     * 删除文章
     */
    @POST("api/article/del")
    Observable<BaseResult<Object>> deleteArticle(@Query("id") int id);

    /**
     * 点赞
     */
    @POST("api/like/add")
    Observable<BaseResult<Object>> addLikeNews(@Query("article_id") int article_id);

    /**
     * 取消点赞
     */
    @POST("api/like/cancel")
    Observable<BaseResult<Object>> cancelLikeNews(@Query("article_id") int article_id);

    /**
     * 发表评论
     */
    @POST("api/comment/add")
    Observable<BaseResult<Object>> addComment(@Query("article_id") int article_id, @Query("detail") String detail);


    /**
     * 发布V视
     */
    @POST("api/article/addv")
    Observable<BaseResult<Object>> publishVShi(@Query("name") String name, @Query("video") String video,
                                               @Query("image") String image, @Query("time") String time);


    /**
     * 发布原创
     */
    @POST("api/article/addy")
    Observable<BaseResult<Object>> publishYuanChuang(@Query("name") String name, @Query("images") String images,
                                                     @Query("video") String video, @Query("image") String image, @Query("time") String time);


    /**
     * 发布生活圈
     */
    @POST("api/life/add")
    Observable<BaseResult<Object>> publishLifeMsg(@Query("name") String name, @Query("images") String images,
                                                  @Query("video") String video, @Query("image") String image, @Query("time") String time
    );


    /**
     * 发布爆料
     */
    @POST("api/article/addw")
    Observable<BaseResult<Object>> publishWenZheng(@Query("module_second") String module_second, @Query("name") String name,
                                                   @Query("content") String content, @Query("images") String images);

    /**
     * 发起投票
     */
    @POST("api/vote/vote")
    Observable<BaseResult<List<VoteOptionModel>>> sendVote(@Query("id") int id, @Query("option_id") int option_id);


    /**
     * 商品列表
     */
    @POST("api/goods/index")
    Observable<BaseResult<BasePageModel<PointModel>>> getGoodsPageList(@Query("p") int p);


    /**
     * 商品详情
     */
    @POST("api/goods/detail")
    Observable<BaseResult<PointModel>> getGoodsDetail(@Query("id") int id);

    /**
     * 积分规则
     */
    @POST("api/score/rule")
    Observable<BaseResult<PointRuleModel>> getScoreRule();

    /**
     * 分享得积分
     */
    @POST("api/score/share_award")
    Observable<BaseResult<Object>> shareAward();

    /**
     * 积分抽奖奖品列表
     */
    @POST("api/award/index")
    Observable<BaseResult<LotteryModel>> getLotteryModel();

    /**
     * 抽奖
     */
    @POST("api/award/draw")
    Observable<BaseResult<LotteryResultModel>> startLottery();

    /**
     * 积分商品兑换
     */
    @POST("api/goods/exchange")
    Observable<BaseResult<Object>> exchangeGoods(@Query("id") int id);


    @POST("api/user/profile")
    Observable<BaseResult<Object>> editUserInfo(@Query("avatar") String avatar, @Query("username") String username,
                                                @Query("nickname") String nickname, @Query("bio") String bio
    );


    /**
     * 我的收藏
     *
     * @param p
     * @return
     */
    @POST("api/collect/index")
    Observable<BaseResult<BasePageModel<NewsBean>>> getMyCollectList(@Query("p") int p);


    /**
     * 添加收藏
     *
     * @param article_id
     * @return
     */
    @POST("api/collect/add")
    Observable<BaseResult<BasePageModel<Object>>> addCollect(@Query("article_id") int article_id);


    /**
     * 取消收藏
     *
     * @param article_id
     * @return
     */
    @POST("api/collect/cancel")
    Observable<BaseResult<BasePageModel<Object>>> cancelCollect(@Query("article_id") int article_id);

    /**
     * 批量取消收藏
     *
     * @param article_id
     * @return
     */
    @POST("api/collect/multi")
    Observable<BaseResult<BasePageModel<Object>>> cancelCollectList(@Query("article_id") String article_id);

    /**
     * 一键清空收藏
     *
     * @return
     */
    @POST("api/collect/all")
    Observable<BaseResult<BasePageModel<Object>>> cancelCollectAll();


    /**
     * 我的评论
     *
     * @param p
     * @return
     */
    @POST("api/comment/my")
    Observable<BaseResult<BasePageModel<MyCommentModel>>> getMyCommentList(@Query("p") int p);

    /**
     * 批量取消评论
     *
     * @param id
     * @return
     */
    @POST("api/comment/multi")
    Observable<BaseResult<BasePageModel<Object>>> cancelMyCommentList(@Query("id") String id);

    /**
     * 一键清空评论
     *
     * @return
     */
    @POST("api/comment/all")
    Observable<BaseResult<BasePageModel<Object>>> cancelMyCommentAll();


    /**
     * 我的点赞
     *
     * @param p
     * @return
     */
    @POST("api/like/index")
    Observable<BaseResult<BasePageModel<NewsBean>>> getMyLikeList(@Query("p") int p);

    /**
     * 批量取消点赞
     *
     * @param article_id
     * @return
     */
    @POST("api/like/multi")
    Observable<BaseResult<BasePageModel<Object>>> cancelMyLikeList(@Query("article_id") String article_id);

    /**
     * 一键清空点赞
     *
     * @return
     */
    @POST("api/like/all")
    Observable<BaseResult<BasePageModel<Object>>> cancelMyLikeAll();


    /**
     * 最近浏览
     *
     * @param p
     * @return
     */
    @POST("api/userlog/index")
    Observable<BaseResult<BasePageModel<NewsBean>>> getRecentBrowseList(@Query("p") int p);

    /**
     * 批量取消最近浏览
     *
     * @param article_id
     * @return
     */
    @POST("api/userlog/multi")
    Observable<BaseResult<BasePageModel<Object>>> cancelUserlogList(@Query("article_id") String article_id);

    /**
     * 一键清空最近浏览
     *
     * @return
     */
    @POST("api/userlog/all")
    Observable<BaseResult<BasePageModel<Object>>> cancelUserlogAll();

    /**
     * 我的帖子
     *
     * @param p
     * @return
     */
    @POST("api/article/my")
    Observable<BaseResult<BasePageModel<NewsBean>>> getMyArticleList(@Query("p") int p, @Query("module") String module);


    /**
     * 批量取消我的帖子
     *
     * @param id
     * @return
     */
    @POST("api/article/multi")
    Observable<BaseResult<BasePageModel<Object>>> cancelArticleList(@Query("id") String id);

    /**
     * 一键清空我的帖子
     *
     * @return
     */
    @POST("api/article/all")
    Observable<BaseResult<BasePageModel<Object>>> cancelArticleAll();


    /**
     * 最新动态
     *
     * @param p
     * @return
     */
    @POST("api/life/index")
    Observable<BaseResult<BasePageModel<NewsBean>>> getLastLifeList(@Query("p") int p);


    /**
     * 好友动态
     *
     * @param p
     * @return
     */
    @POST("api/life/friend")
    Observable<BaseResult<BasePageModel<NewsBean>>> getFriendLifeList(@Query("p") int p);


    /**
     * 首页关注栏内容数据
     */
    @POST("api/attention/index")
    Observable<BaseResult<BasePageModel<NewsBean>>> getGuanZhuNews(@Query("module_second") String module_second, @Query("p") int p);


    /**
     * 栏目账号数据
     *
     * @return
     */
    @POST("api/attention/all")
    Observable<BaseResult<AttentionModel>> getAttentionList();


    /**
     * 我的关注列表
     *
     * @param p
     * @return
     */
    @POST("api/attention/my")
    Observable<BaseResult<BasePageModel<ConcernModel>>> getMyConcernList(@Query("p") int p, @Query("type") int type);


    /**
     * 添加关注
     */
    @POST("api/attention/add")
    Observable<BaseResult<Object>> addConcern(@Query("id") int id, @Query("type") int type);


    /**
     * 取消关注
     */
    @POST("api/attention/cancel")
    Observable<BaseResult<Object>> cancelConcern(@Query("id") int id, @Query("type") int type);


    /**
     * 今日热门
     */
    @POST("api/life/hot")
    Observable<BaseResult<BasePageModel<NewsBean>>> getLifeHotList(@Query("p") int p);


    /**
     * 评选列表
     */
    @POST("api/selection/index")
    Observable<BaseResult<BasePageModel<PingXuanModel>>> getPingXuanList(@Query("module") String module, @Query("p") int p);


    /**
     * 评选活动详情
     */
    @POST("api/selection/selection")
    Observable<BaseResult<PingXuanModel>> getPingXuanDetails(@Query("id") int id);


    /**
     * 评选选手详情
     */
    @POST("api/selection/option")
    Observable<BaseResult<PingXuanPersionModel>> getPingXuanPersionDetails(@Query("id") int id);


    /**
     * 投票
     */
    @POST("api/selection/vote")
    Observable<BaseResult<Object>> vote(@Query("id") int id);


    /**
     * 评选选手列表
     */
    @POST("api/selection/option_list")
    Observable<BaseResult<BasePageModel<PingXuanPersionModel>>> getPingXuanPersionList(@Query("id") int id, @Query("p") int p, @Query("search") String search, @Query("sort") String sort);

}