package cn.tklvyou.huaiyuanmedia.base.fragment;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.base.interfaces.OnStopLoadListener;


/**
 * 基础http网络列表的Fragment
 *
 * @param <T>  数据模型(model/JavaBean)类
 * @param <VH> ViewHolder或其子类
 * @param <A>  管理LV的Adapter
 * @author Lemon
 * @see #getListAsync(int)
 * @see #onHttpResponse(int, String, Exception)
 * @see <pre>
 *       基础使用：<br />
 *       extends BaseHttpRecyclerFragment 并在子类onCreateView中srlBaseHttpRecycler.autoRefresh(), 具体参考.UserRecyclerFragment
 *       <br /><br />
 *       列表数据加载及显示过程：<br />
 *       1.srlBaseHttpRecycler.autoRefresh触发刷新 <br />
 *       2.getListAsync异步获取列表数据 <br />
 *       3.onHttpResponse处理获取数据的结果 <br />
 *       4.setList把列表数据绑定到adapter <br />
 *   </pre>
 */
public abstract class BaseHttpRecyclerFragment<P extends BaseContract.BasePresenter, T, VH extends BaseViewHolder, A extends BaseQuickAdapter<T, VH>>
        extends BaseRecyclerFragment<P, T, VH, A>
        implements OnStopLoadListener, OnRefreshListener {

    private static final String TAG = "BaseHttpRecyclerFragment";

    protected SmartRefreshLayout srlBaseHttpRecycler;

    protected void initSmartRefreshLayout(SmartRefreshLayout smartRefreshLayout) {
        srlBaseHttpRecycler = smartRefreshLayout;
        setOnStopLoadListener(this);

        srlBaseHttpRecycler.setEnableLoadMore(false);
        srlBaseHttpRecycler.setOnRefreshListener(this);
    }

    @Override
    public void setAdapter(A adapter) {
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                onLoadMore();
            }
        }, rvBaseRecycler);
        adapter.disableLoadMoreIfNotFullPage(rvBaseRecycler);
        super.setAdapter(adapter);
    }


    /**
     * @param page 用-page作为requestCode
     */
    @Override
    public abstract void getListAsync(int page);


    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        onRefresh();
    }

    @Override
    public void onStopRefresh() {
        runUiThread(new Runnable() {
            @Override
            public void run() {
                srlBaseHttpRecycler.finishRefresh();
            }
        });
    }

    @Override
    public void onStopLoadMore(final boolean isHaveMore) {
        runUiThread(new Runnable() {

            @Override
            public void run() {
                if (isHaveMore) {
                    adapter.loadMoreComplete();
                } else {
                    adapter.loadMoreEnd();
                }
            }
        });
    }


    /**
     * 处理网络请求加载成功结果
     *
     * @param page
     * @param list
     */
    public void onLoadSuccess(int page, List<T> list) {
        onLoadSucceed(page, list);
    }

    /**
     * 处理网络请求加载失败结果
     *
     * @param page
     * @param e
     */
    public void onLoadError(int page, Exception e) {
        onLoadFailed(page, e);
    }

}