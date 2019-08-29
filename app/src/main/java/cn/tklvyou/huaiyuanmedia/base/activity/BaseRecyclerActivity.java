package cn.tklvyou.huaiyuanmedia.base.activity;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.base.BaseContract;
import cn.tklvyou.huaiyuanmedia.common.Contacts;
import cn.tklvyou.huaiyuanmedia.base.interfaces.AdapterCallBack;
import cn.tklvyou.huaiyuanmedia.base.interfaces.CacheCallBack;
import cn.tklvyou.huaiyuanmedia.base.interfaces.OnStopLoadListener;
import cn.tklvyou.huaiyuanmedia.manager.CacheManager;


/**
 * 基础RecyclerView Activity
 *
 * @param <P>  Presenter类
 * @param <T>  数据模型(model/JavaBean)类
 * @param <VH> ViewHolder或其子类
 * @param <A>  管理LV的Adapter
 * @author Lemon
 * @see #rvBaseRecycler
 * @see #initCache
 * @see #initView
 * @see #getListAsync
 * @see #onRefresh
 * @see <pre>
 *       基础使用：<br />
 *       extends BaseRecyclerActivity 并在子类onCreate中调用onRefresh(...), 具体参考.DemoRecyclerActivity
 *       <br /><br />
 *       缓存使用：<br />
 *       在initData前调用initCache(...), 具体参考 .UserRecyclerFragment(onCreateView方法内)
 *       <br /><br />
 *       列表数据加载及显示过程：<br />
 *       1.onRefresh触发刷新 <br />
 *       2.getListAsync异步获取列表数据 <br />
 *       3.onLoadSucceed处理获取数据的结果 <br />
 *       4.setList把列表数据绑定到adapter <br />
 *   </pre>
 */
public abstract class BaseRecyclerActivity<P extends BaseContract.BasePresenter, T, VH extends BaseViewHolder, A extends BaseQuickAdapter<T, VH>>
        extends BaseActivity<P> implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemLongClickListener , BaseQuickAdapter.OnItemChildClickListener {

    private static final String TAG = "BaseRecyclerActivity";

    protected RecyclerView rvBaseRecycler;
    /**
     * 管理LV的Item的Adapter
     */
    protected A adapter;

    private boolean isToSaveCache;
    private boolean isToLoadCache;


    /**
     * 列表首页页码。有些服务器设置为0，即列表页码从0开始
     */
    public static final int PAGE_NUM_1 = 1;

    /**
     * 数据列表
     */
    private List<T> list;
    /**
     * 正在加载
     */
    protected boolean isLoading = false;
    /**
     * 还有更多可加载数据
     */
    protected boolean isHaveMore = true;
    /**
     * 加载页码，每页对应一定数量的数据
     */
    private int page;
    private int loadCacheStart;

    public void initRecyclerView(RecyclerView recyclerView) {
        rvBaseRecycler = recyclerView;
        rvBaseRecycler.setLayoutManager(new LinearLayoutManager(this));
    }


    /**
     * 设置adapter
     *
     * @param adapter
     */
    public void setAdapter(A adapter) {
        adapter.setOnItemClickListener(this);
        adapter.setOnItemChildClickListener(this);
        adapter.setOnItemLongClickListener(this);
        this.adapter = adapter;
        rvBaseRecycler.setAdapter(this.adapter);
        this.adapter.setEmptyView(R.layout.common_empty_view,rvBaseRecycler);
        this.adapter.setHeaderAndEmpty(true);

    }

    /**
     * 刷新列表数据（已在UI线程中），一般需求建议直接调用setList(List<T> l, AdapterCallBack<A> callBack)
     *
     * @param list
     */
    public abstract void setList(List<T> list);


    /**
     * 显示列表（已在UI线程中）
     *
     * @param callBack
     */
    public void setList(AdapterCallBack<A> callBack) {
        if (adapter == null) {
            setAdapter(callBack.createAdapter());
        }else {
            callBack.refreshAdapter();
        }
    }


    /**
     * 获取列表，在非UI线程中
     *
     * @param page 在onLoadSucceed中传回来保证一致性
     * @must 获取成功后调用onLoadSucceed
     */
    public abstract void getListAsync(int page);


    public void loadData(int page) {
        loadData(page, isToLoadCache);
    }

    /**
     * 加载数据，用getListAsync方法发请求获取数据
     *
     * @param page_
     * @param isCache
     */
    private void loadData(int page_, final boolean isCache) {
        if (isLoading) {
            LogUtils.w("loadData  isLoading >> return;");
            return;
        }
        isLoading = true;
        isSucceed = false;

        if (page_ <= PAGE_NUM_1) {
            page_ = PAGE_NUM_1;
            isHaveMore = true;
            loadCacheStart = 0;//使用则可像网络正常情况下的重载，不使用则在网络异常情况下不重载（导致重载后加载数据下移）
        } else {
            if (isHaveMore == false) {
                stopLoadData(page_);
                return;
            }
            loadCacheStart = list == null ? 0 : list.size();
        }
        this.page = page_;
        LogUtils.i("loadData  page_ = " + page_ + "; isCache = " + isCache
                + "; isHaveMore = " + isHaveMore + "; loadCacheStart = " + loadCacheStart);

        runThread(TAG + "loadData", new Runnable() {

            @Override
            public void run() {
                if (isCache == false) {//从网络获取数据
                    getListAsync(page);
                } else {//从缓存获取数据
                    onLoadSucceed(page, CacheManager.getInstance().getList(cacheCallBack.getCacheClass()
                            , cacheCallBack.getCacheGroup(), loadCacheStart, cacheCallBack.getCacheCount()),
                            true);
                    if (page <= PAGE_NUM_1) {
                        isLoading = false;//stopLoadeData在其它线程isLoading = false;后这个线程里还是true
                        loadData(page, false);
                    }
                }
            }
        });
    }

    /**
     * 停止加载数据
     * isCache = false;
     *
     * @param page
     */
    public synchronized void stopLoadData(int page) {
        stopLoadData(page, false);
    }

    /**
     * 停止加载数据
     *
     * @param page
     * @param isCache
     */
    private synchronized void stopLoadData(int page, boolean isCache) {
        LogUtils.i("stopLoadData  isCache = " + isCache);
        isLoading = false;

        if (isCache) {
            LogUtils.d("stopLoadData  isCache >> return;");
            return;
        }

        if (onStopLoadListener == null) {
            LogUtils.w("stopLoadData  onStopLoadListener == null >> return;");
            return;
        }
        onStopLoadListener.onStopRefresh();
        if (page > PAGE_NUM_1) {
            onStopLoadListener.onStopLoadMore(isHaveMore);
        }
    }


    private boolean isSucceed = false;

    /**
     * 处理列表
     *
     * @param page
     * @param newList 新数据列表
     * @param isCache
     * @return
     */
    public synchronized void handleList(int page, List<T> newList, boolean isCache) {
        if (newList == null) {
            newList = new ArrayList<T>();
        }
        isSucceed = !newList.isEmpty();
        LogUtils.i("\n\n<<<<<<<<<<<<<<<<<\n handleList  newList.size = " + newList.size() + "; isCache = " + isCache
                + "; page = " + page + "; isSucceed = " + isSucceed);

        if (page <= PAGE_NUM_1) {
            LogUtils.i("handleList  page <= PAGE_NUM_1 >>>>  ");
            saveCacheStart = 0;
            list = new ArrayList<T>(newList);
            if (isCache == false && list.isEmpty() == false) {
                LogUtils.i("handleList  isCache == false && list.isEmpty() == false >>  isToLoadCache = false;");
                isToLoadCache = false;
            }
        } else {
            LogUtils.i("handleList  page > PAGE_NUM_1 >>>>  ");
            if (list == null) {
                list = new ArrayList<T>();
            }
            saveCacheStart = list.size();
            isHaveMore = !newList.isEmpty();
            if (isHaveMore) {
                list.addAll(newList);
            }
        }

        LogUtils.i("handleList  list.size = " + list.size() + "; isHaveMore = " + isHaveMore
                + "; isToLoadCache = " + isToLoadCache + "; saveCacheStart = " + saveCacheStart
                + "\n>>>>>>>>>>>>>>>>>>\n\n");
    }


    /**
     * 加载成功
     * isCache = false;
     *
     * @param page
     * @param newList
     */
    public synchronized void onLoadSucceed(final int page, final List<T> newList) {
        onLoadSucceed(page, newList, false);
    }

    /**
     * 加载成功
     *
     * @param page
     * @param newList
     * @param isCache newList是否为缓存
     */
    private synchronized void onLoadSucceed(final int page, final List<T> newList, final boolean isCache) {
        runThread(TAG + "onLoadSucceed", new Runnable() {
            @Override
            public void run() {
                LogUtils.i("onLoadSucceed  page = " + page + "; isCache = " + isCache + " >> handleList...");
                handleList(page, newList, isCache);

                runUiThread(new Runnable() {

                    @Override
                    public void run() {
                        stopLoadData(page, isCache);
                        setList(list);
                    }
                });

                if (isToSaveCache && isCache == false) {
                    saveCache(newList);
                }
            }
        });
    }

    /**
     * 加载失败
     *
     * @param page
     * @param e
     */
    public synchronized void onLoadFailed(int page, Exception e) {
        LogUtils.e("onLoadFailed page = " + page + "; e = " + (e == null ? null : e.getMessage()));
        stopLoadData(page);
        ToastUtils.showShort("加载失败");
    }


    //缓存<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //	/**
    //	 * 获取缓存每页数量
    //	 * @return > 0 ？缓存 : 不缓存
    //	 */
    //	public int getCacheCount() {
    //		//让给服务器返回每页数量为count的数据，不行的话在子类重写 Math.max(10, newList == null ? 0 : newList.size());
    //		return CacheManager.MAX_PAGE_SIZE;
    //	}

    private int saveCacheStart;

    /**
     * 保存缓存
     *
     * @param newList
     */
    public synchronized void saveCache(List<T> newList) {
        if (cacheCallBack == null || newList == null || newList.isEmpty()) {
            LogUtils.e("saveCache  cacheCallBack == null || newList == null || newList.isEmpty() >> return;");
            return;
        }

        LinkedHashMap<String, T> map = new LinkedHashMap<String, T>();
        for (T data : newList) {
            if (data != null) {
                map.put(cacheCallBack.getCacheId(data), data);//map.put(null, data);不会崩溃
            }
        }

        CacheManager.getInstance().saveList(cacheCallBack.getCacheClass(), cacheCallBack.getCacheGroup()
                , map, saveCacheStart, cacheCallBack.getCacheCount());
    }
    //缓存>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    private OnStopLoadListener onStopLoadListener;

    /**
     * 设置停止加载监听
     *
     * @param onStopLoadListener
     */
    protected void setOnStopLoadListener(OnStopLoadListener onStopLoadListener) {
        this.onStopLoadListener = onStopLoadListener;
    }

    private CacheCallBack<T> cacheCallBack;

    /**
     * 初始化缓存
     *
     * @param cacheCallBack
     * @warn 在initData前使用才有效
     */
    protected void initCache(CacheCallBack<T> cacheCallBack) {
        this.cacheCallBack = cacheCallBack;
        isToSaveCache = Contacts.cache && cacheCallBack != null && cacheCallBack.getCacheClass() != null;
        isToLoadCache = isToSaveCache && !StringUtils.isEmpty(cacheCallBack.getCacheGroup());
    }


    /**
     * 刷新（从头加载）
     *
     * @must 在子类onCreate中调用，建议放在最后
     */
    public void onRefresh() {
        loadData(PAGE_NUM_1);
    }

    /**
     * 加载更多
     */
    public void onLoadMore() {
        if (isSucceed == false && page <= PAGE_NUM_1) {
            LogUtils.w("onLoadMore  isSucceed == false && page <= PAGE_NUM_1 >> return;");
            return;
        }
        loadData(page + (isSucceed ? 1 : 0));
    }


    /**
     * 重写后可自定义对这个事件的处理
     *
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }

    /**
     * 重写后可自定义对这个事件的处理，如果要在长按后不触发onItemClick，则需要 return true;
     *
     * @param view
     * @param position
     */
    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        return false;
    }


    @Override
    protected void onDestroy() {
        isLoading = false;
        isHaveMore = false;
        isToSaveCache = false;
        isToLoadCache = false;

        super.onDestroy();

        rvBaseRecycler = null;
        list = null;

        onStopLoadListener = null;
        cacheCallBack = null;
    }

}