package cn.tklvyou.huaiyuanmedia.common

import cn.tklvyou.huaiyuanmedia.model.NewsBean

object ModuleUtils {

    fun getTypeByNewsBean(bean: NewsBean): String {
        val type: String
        when (bean.module) {

            "生活圈" -> {
                type = if (bean.images != null && bean.images.size > 0) "图文" else "视频"
            }

            "视讯", "爆料", "悦读", "直播", "悦听" -> {
                type = bean.module
            }

            "矩阵", "专题", "推荐" -> {
                type = if (bean.video.isEmpty()) "文章" else "视讯"
            }

            else -> {
                type = if (bean.video.isEmpty()) "文章" else "视讯"
            }

        }
        return type
    }

}
