package cn.tklvyou.huaiyuanmedia.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.tklvyou.huaiyuanmedia.R;

public class JuzhengHeaderViewholder extends RecyclerView.ViewHolder {

    public ImageView ivAvatar;
    public TextView tvNickName;
    public RadioButton radioButton;

    public JuzhengHeaderViewholder(@NonNull View itemView) {
        super(itemView);
        ivAvatar = itemView.findViewById(R.id.ivAvatar);
        tvNickName = itemView.findViewById(R.id.tvNickName);
        radioButton = itemView.findViewById(R.id.rbButton);
    }


}
