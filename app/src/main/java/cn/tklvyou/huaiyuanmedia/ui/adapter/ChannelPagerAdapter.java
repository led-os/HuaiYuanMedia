package cn.tklvyou.huaiyuanmedia.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * @description: 频道的adapter
 */

public class ChannelPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments;

    public ChannelPagerAdapter(List<Fragment> fragmentList, FragmentManager fm) {
        super(fm);
        mFragments = fragmentList != null ? fragmentList : new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}