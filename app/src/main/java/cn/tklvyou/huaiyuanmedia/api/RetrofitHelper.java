package cn.tklvyou.huaiyuanmedia.api;

import android.content.Intent;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import cn.tklvyou.huaiyuanmedia.BuildConfig;
import cn.tklvyou.huaiyuanmedia.base.BaseResult;
import cn.tklvyou.huaiyuanmedia.base.MyApplication;
import cn.tklvyou.huaiyuanmedia.common.Contacts;
import cn.tklvyou.huaiyuanmedia.ui.account.LoginActivity;
import cn.tklvyou.huaiyuanmedia.utils.JsonHandleUtils;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 辅助类
 */

public class RetrofitHelper {
    private static String TAG = "RetrofitHelper";
    private long CONNECT_TIMEOUT = 60L;
    private long READ_TIMEOUT = 30L;
    private long WRITE_TIMEOUT = 30L;
    private static RetrofitHelper mInstance = null;
    private Retrofit mRetrofit = null;

    public static RetrofitHelper getInstance() {
        synchronized (RetrofitHelper.class) {
            if (mInstance == null) {
                mInstance = new RetrofitHelper();
            }
        }
        return mInstance;
    }

    private RetrofitHelper() {
        init();
    }

    private void init() {
        resetApp();
    }

    private void resetApp() {
        String url;
        if (BuildConfig.DEBUG) {
            url = Contacts.DEV_BASE_URL;
        } else {
            url = Contacts.PRO_BASE_URL;
        }
        mRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 获取OkHttpClient实例
     *
     * @return
     */

    private OkHttpClient getOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)//默认重试一次，若需要重试N次，则要实现拦截器。
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new RqInterceptor())
                .addInterceptor(new ResponseInterceptor())
                .addInterceptor(new LoggingInterceptor())
                .build();
        return okHttpClient;
    }

    /**
     * 请求拦截器
     */
    private class RqInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("token", SPUtils.getInstance().getString("token"))
                    .addHeader("X-APP-TYPE", "android")
                    .build();
            Response response = chain.proceed(request);
            LogUtils.e(SPUtils.getInstance().getString("token"));
            return response;
        }
    }


    /**
     * 添加返回结果统一处理拦截器
     */
    private class ResponseInterceptor implements Interceptor {

        @Override
        public Response intercept(final Chain chain) throws IOException {
            // 原始请求
            Request request = chain.request();
            Response response = chain.proceed(request);
            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            String respString = source.buffer().clone().readString(Charset.defaultCharset());

            BaseResult<Object> result = new Gson().fromJson(respString, BaseResult.class);

            if (result != null && result.getCode() == 401) {
                Log.d(TAG, "--->登录失效，自动重新登录");
                Intent intent = new Intent(MyApplication.getAppContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getAppContext().startActivity(intent);
            }

            return response;

        }
    }

    /**
     * 日志拦截器
     */
    private class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();//请求发起的时间

            String method = request.method();
            if ("POST".equals(method)) {
                StringBuilder sb = new StringBuilder();
                if (request.body() instanceof FormBody) {
                    FormBody body = (FormBody) request.body();
                    for (int i = 0; i < body.size(); i++) {
                        sb.append(body.encodedName(i))
                                .append("=")
                                .append(body.encodedValue(i))
                                .append(",");
                    }
                    sb.delete(sb.length() - 1, sb.length());
                    Log.d("---POST---",
                            String.format("发送请求 %s on %s %n%s %nRequestParams:{%s}",
                                    request.url(),
                                    chain.connection(),
                                    request.headers(),
                                    sb.toString()));
                }
            } else {
                Log.d("---GET---", String.format("发送请求 %s on %s%n%s",
                        request.url(),
                        chain.connection(),
                        request.headers()));
            }

            Response response = chain.proceed(request);
            long t2 = System.nanoTime();//收到响应事件

            ResponseBody responseBody = response.peekBody(1024 * 1024);//关键代码

            String responseString = JsonHandleUtils.jsonHandle(responseBody.string());
            LogUtils.d(String.format("接收响应: [%s] %n返回json:【%s】 %.1fms %n%s",
                    response.request().url(),
                    responseString,
                    (t2 - t1) / 1e6d,
                    response.headers()
            ));

            return response;
        }
    }


    public ApiService getServer() {
        return mRetrofit.create(ApiService.class);
    }
}
