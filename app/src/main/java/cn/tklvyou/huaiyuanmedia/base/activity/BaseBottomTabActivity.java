package cn.tklvyou.huaiyuanmedia.base.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.LogUtils;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;


/**
 * 基础底部标签Activity
 */
public abstract class BaseBottomTabActivity<P extends BaseContract.BasePresenter> extends BaseActivity<P> {

    protected List<Fragment> fragments;

    /**
     * == true >> 每次点击相应tab都加载，调用getFragment方法重新对点击的tab对应的fragment赋值。
     * 如果不希望重载，可以重写selectFragment。
     */
    protected boolean needReload = false;
    /**
     * 当前显示的tab所在位置，对应fragment所在位置
     */
    protected int currentPosition = 0;

    /**
     * 选择并显示fragment
     *
     * @param position`
     */
    public void selectFragment(int position) {
        fragments = getFragments();
        if (currentPosition == position) {
            if (needReload) {
                if (fragments.get(position) != null && fragments.get(position).isAdded()) {
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.remove(fragments.get(position)).commit();
                    fragments.set(position, null);
                }
            } else {
                if (fragments.get(position) != null && fragments.get(position).isVisible()) {
                    LogUtils.w("selectFragment currentPosition == position" +
                            " >> fragments[position] != null && fragments[position].isVisible()" +
                            " >> return;	");
                    homeDoubleClick(position);
                    return;
                }
            }
        }


        if (fragments.get(position) == null) {
            fragments.set(position, fragments.get(position));
        }

        //全局的fragmentTransaction因为already committed 崩溃
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.hide(fragments.get(currentPosition));
        if (!fragments.get(position).isAdded()) {
            ft.add(getFragmentContainerResId(), fragments.get(position));
        }
        ft.show(fragments.get(position)).commit();

        this.currentPosition = position;
    }


    /**
     * 获取Fragment容器的id
     *
     * @return
     */
    public abstract int getFragmentContainerResId();

    public void  homeDoubleClick(int position){

    }

    /**
     * 获取所有的Fragment
     */
    protected abstract List<Fragment> getFragments();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fragments != null) {
            fragments.clear();
            fragments = null;
        }
    }
}