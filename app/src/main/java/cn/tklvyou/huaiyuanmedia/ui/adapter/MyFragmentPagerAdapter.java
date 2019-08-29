package cn.tklvyou.huaiyuanmedia.ui.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.trello.rxlifecycle3.components.support.RxFragment;

import java.util.List;

/**
 * Created by Administrator on 2018/11/28.
 */

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<RxFragment> data;

    public MyFragmentPagerAdapter(FragmentManager fm, List<RxFragment> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }


    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
