package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.helper.GlideManager;
import cn.tklvyou.huaiyuanmedia.model.NewsBean;


public class GuanZhuRvAdapter extends BaseQuickAdapter<NewsBean, GuanZhuViewHolder> {

    private IOnCancelClickListener listener;

    public GuanZhuRvAdapter(List<NewsBean> data) {
        super(R.layout.item_guan_zhu_layout, data);
    }

    @Override
    protected void convert(@NonNull GuanZhuViewHolder helper, NewsBean bean) {
        helper.setText(R.id.tvTitle, bean.getName());
        helper.setText(R.id.tvTime, bean.getBegintime());
        helper.setText(R.id.tvSeeNum, "" + bean.getVisit_num());
        helper.setText(R.id.tvGoodNum, "" + bean.getLike_num());
        helper.setText(R.id.tvModuleSecond, bean.getModule_second());

        TextView cancelBtn = helper.easyPopup.getContentView().findViewById(R.id.cancelBtn);

        cancelBtn.setText("取消关注："+bean.getModule_second());
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onCancelClick(bean.getAdmin_id(),helper.getLayoutPosition());
                }
                helper.easyPopup.dismiss();
            }
        });


        helper.getView(R.id.tvModuleSecond).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.easyPopup.showAtAnchorView(v, YGravity.CENTER, XGravity.RIGHT);
            }
        });



        TextView tvGoodNum = helper.getView(R.id.tvGoodNum);

        Drawable[] drawables = tvGoodNum.getCompoundDrawables();

        if (bean.getLike_status() == 1) {
            Drawable redGoodDrawable = mContext.getResources().getDrawable(R.mipmap.icon_mini_good);
            redGoodDrawable.setBounds(drawables[0].getBounds());
            tvGoodNum.setCompoundDrawables(redGoodDrawable, drawables[1], drawables[2], drawables[3]);
        } else {
            Drawable grayGoodDrawable = mContext.getResources().getDrawable(R.mipmap.icon_good);
            grayGoodDrawable.setBounds(drawables[0].getBounds());
            tvGoodNum.setCompoundDrawables(grayGoodDrawable, drawables[1], drawables[2], drawables[3]);
        }


        if (!StringUtils.isEmpty(bean.getImage())) {
            //一张图片
            helper.getView(R.id.tvTime).setVisibility(View.GONE);
            helper.getView(R.id.tvGoodNum).setVisibility(View.GONE);
            helper.getView(R.id.llMultiImage).setVisibility(View.GONE);
            helper.getView(R.id.ivImageOne).setVisibility(View.VISIBLE);

            GlideManager.loadRoundImg(bean.getImage(), helper.getView(R.id.ivImageOne));

        } else {
            if (bean.getImages() == null || bean.getImages().size() == 0) {
                //没有图片
                helper.getView(R.id.tvTime).setVisibility(View.VISIBLE);
                helper.getView(R.id.tvGoodNum).setVisibility(View.VISIBLE);
                helper.getView(R.id.llMultiImage).setVisibility(View.GONE);
                helper.getView(R.id.ivImageOne).setVisibility(View.GONE);
            } else {
                if (bean.getImages().size() < 3) {
                    //一张图片
                    helper.getView(R.id.tvTime).setVisibility(View.GONE);
                    helper.getView(R.id.tvGoodNum).setVisibility(View.GONE);
                    helper.getView(R.id.llMultiImage).setVisibility(View.GONE);
                    helper.getView(R.id.ivImageOne).setVisibility(View.VISIBLE);

                    GlideManager.loadRoundImg(bean.getImages().get(0), helper.getView(R.id.ivImageOne));
                } else {
                    //多张图片
                    helper.getView(R.id.tvTime).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tvGoodNum).setVisibility(View.VISIBLE);
                    helper.getView(R.id.ivImageOne).setVisibility(View.GONE);
                    helper.getView(R.id.llMultiImage).setVisibility(View.VISIBLE);

                    GlideManager.loadRoundImg(bean.getImages().get(0), helper.getView(R.id.ivImageFirst));
                    GlideManager.loadRoundImg(bean.getImages().get(1), helper.getView(R.id.ivImageSecond));
                    GlideManager.loadRoundImg(bean.getImages().get(2), helper.getView(R.id.ivImageThree));
                }
            }
        }
    }



    public interface IOnCancelClickListener{
        void onCancelClick(int id,int position);
    }

    public void setIOnCancelClickListener(IOnCancelClickListener listener){
        this.listener = listener;
    }

}
