package cn.tklvyou.huaiyuanmedia.base.activity;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.FragmentUtils;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;


/**
 * 基础底部标签Activity
 */
public abstract class BaseBottomTab2Activity<P extends BaseContract.BasePresenter> extends BaseActivity<P> {

    protected List<Fragment> fragments;

    /**
     * 当前显示的tab所在位置，对应fragment所在位置
     */
    protected int currentPosition = 0;

    /**
     * 初始化导航栏页面(必须调用）
     * @param fragments
     * @param containerId
     */
    public void addFragments(List<Fragment> fragments, @IdRes final int containerId) {
        this.fragments = fragments;
        FragmentUtils.add(fragmentManager,fragments, containerId, currentPosition);
    }

    /**
     * 选择并显示fragment
     * @param position 所需要显示的fragment下标
     */
    public void showCurrentFragment(int position) {
        currentPosition = position;
        FragmentUtils.showHide(position, fragments);
    }



    @Override
    protected void onDestroy() {
        if (fragments != null) {
            FragmentUtils.removeAll(fragmentManager);
            fragments.clear();
            fragments = null;
        }
        super.onDestroy();
    }
}