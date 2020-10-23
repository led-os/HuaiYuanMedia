package cn.tklvyou.huaiyuanmedia.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.HaveSecondModuleNewsModel;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2020年10月23日15:27
 * @Email: 971613168@qq.com
 */
public class ZhuanTiAdapter extends BaseQuickAdapter<HaveSecondModuleNewsModel.ModuleSecondBean, BaseViewHolder> {
    public ZhuanTiAdapter(List<HaveSecondModuleNewsModel.ModuleSecondBean> dataList) {
        super(R.layout.item_zhuanti_layout,dataList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, HaveSecondModuleNewsModel.ModuleSecondBean item) {
        helper.setText(R.id.tvNewsTitle, item.getNickname());
        GlideManager.loadImg(item.getAvatar(), helper.getView(R.id.ivNews));
    }
}
