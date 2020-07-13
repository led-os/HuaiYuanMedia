package cn.tklvyou.huaiyuanmedia.model;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.entity.SectionMultiEntity;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class SectionNewsMultipleItem<T> extends SectionMultiEntity<T> implements MultiItemEntity {

    public static final int VIDEO_IN_TITLE = 1;             //视频样式 标题在视频图层内部
    public static final int VIDEO_OUT_TITLE = 2;            //视频样式 标题在视频图层外部
    public static final int NEWS_NO_IMAGE = 3;              //新闻样式   无图片
    public static final int NEWS_LEFT_ONE_IMAGE = 4;        //新闻样式   一张在左边的图片
    public static final int NEWS_RIGHT_ONE_IMAGE = 5;       //新闻样式   一张在右边的图片
    public static final int NEWS_THREE_IMAGE = 6;           //新闻样式   三张在下边的图片
    public static final int WECHAT_MOMENTS = 7;             //微信朋友圈
    public static final int READING = 8;                    //悦读（瀑布流卡片）
    public static final int LISTEN = 9;                     //悦听
    public static final int PING_XUAN = 10;                 //评选

    private int itemType;
    private T dataBean;

    public SectionNewsMultipleItem(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public SectionNewsMultipleItem(String model, T dataBean) {
        super(dataBean);
        this.dataBean = dataBean;
        switch (model) {
            case "推荐":
            case "爆料":
            case "矩阵":
            case "专题":
                itemType = getNewsItemType((NewsBean) dataBean);
                break;
            case "视讯":
                itemType = VIDEO_OUT_TITLE;
                break;
            case "生活圈":
                itemType = WECHAT_MOMENTS;
                break;
            case "悦读":
                itemType = READING;
                break;
            case "悦听":
                itemType = LISTEN;
                break;
            case "直播":
                itemType = VIDEO_IN_TITLE;
                break;
            case "评选":
                itemType = PING_XUAN;
                break;
            default:
                itemType = getNewsItemType((NewsBean) dataBean);
                break;
        }
    }


    /**
     * 获取新闻类型的布局样式（视频，无图，单图，多图）
     *
     * @param bean 判断实体类
     * @return 布局类型
     */
    private int getNewsItemType(NewsBean bean) {
        int itemType;
        //视频
        if (!StringUtils.isEmpty(bean.getVideo())) {
            itemType = VIDEO_OUT_TITLE;
        } else {
            //一张图片
            if (!StringUtils.isEmpty(bean.getImage())) {
                itemType = NEWS_LEFT_ONE_IMAGE;
            } else {
                if (bean.getImages() == null || bean.getImages().size() == 0) {
                    //没有图片
                    itemType = NEWS_NO_IMAGE;
                } else {
                    if (bean.getImages().size() < 3) {
                        //一张图片
                        itemType = NEWS_LEFT_ONE_IMAGE;
                    } else {
                        //多张图片
                        itemType = NEWS_THREE_IMAGE;
                    }
                }
            }
        }
        return itemType;
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