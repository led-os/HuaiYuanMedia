package cn.tklvyou.huaiyuanmedia.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class NewsMultipleItem<T> implements MultiItemEntity {
    public static final int VIDEO = 1;                              //视频
    public static final int TV = 2;                                 //濉溪TV
    public static final int NEWS = 3;                               //新闻
    public static final int SHI_XUN = 4;                            //视讯
    public static final int WEN_ZHENG = 5;                          //问政
    public static final int JU_ZHENG = 6;                           //文字+单张靠右的图片
    public static final int WECHAT_MOMENTS = 7;                     //微信朋友圈
    public static final int READING = 8;                            //悦读（瀑布流卡片）
    public static final int LISTEN = 9;                             //悦听
    public static final int DANG_JIAN = 10;                         //悦听
    public static final int ZHUAN_LAN = 11;                         //专栏
    public static final int GONG_GAO = 12;                          //公告
    public static final int ZHI_BO = 13;                            //直播
    public static final int TUI_JIAN = 14;                          //推荐

    private int itemType;
    private T dataBean;

    public NewsMultipleItem(String model, T dataBean) {
        switch (model) {
            case "推荐":
                itemType = TUI_JIAN;
                break;
            case "V视频":
                itemType = VIDEO;
                break;
            case "濉溪TV":
                itemType = TV;
                break;
            case "新闻":
                itemType = NEWS;
                break;
            case "视讯":
                itemType = SHI_XUN;
                break;
            case "问政":
                itemType = WEN_ZHENG;
                break;
            case "矩阵":
                itemType = JU_ZHENG;
                break;
            case "原创":
                itemType = WECHAT_MOMENTS;
                break;
            case "悦读":
                itemType = READING;
                break;
            case "悦听":
                itemType = LISTEN;
                break;
            case "党建":
                itemType = DANG_JIAN;
                break;
            case "专栏":
                itemType = ZHUAN_LAN;
                break;
            case "公告":
                itemType = GONG_GAO;
                break;
            case "直播":
                itemType = ZHI_BO;
                break;
            default:
                itemType = WEN_ZHENG;
                break;
        }
        this.dataBean = dataBean;
    }

    public T getDataBean() {
        return dataBean;
    }

    public void setDataBean(T dataBean) {
        this.dataBean = dataBean;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}