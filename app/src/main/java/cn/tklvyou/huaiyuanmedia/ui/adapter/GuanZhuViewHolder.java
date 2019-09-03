package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.graphics.Color;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;
import com.zyyoona7.popup.EasyPopup;

import cn.tklvyou.huaiyuanmedia.R;

public class GuanZhuViewHolder extends BaseViewHolder {

    public EasyPopup easyPopup;

    public GuanZhuViewHolder(View view) {
        super(view);

        easyPopup =  EasyPopup.create()
                .setContentView(view.getContext(), R.layout.social_sns_popupwindow)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                //允许背景变暗
                .setBackgroundDimEnable(true)
                //变暗的透明度(0-1)，0为完全透明
                .setDimValue(0.4f)
                //变暗的背景颜色
                .setDimColor(Color.GRAY)
                .apply();

    }


}
