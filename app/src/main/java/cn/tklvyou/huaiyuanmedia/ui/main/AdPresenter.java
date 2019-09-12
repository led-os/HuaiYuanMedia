package cn.tklvyou.huaiyuanmedia.ui.main;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.airbnb.lottie.L;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.cjt2325.cameralibrary.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.tklvyou.huaiyuanmedia.api.RetrofitHelper;
import cn.tklvyou.huaiyuanmedia.api.RxSchedulers;
import cn.tklvyou.huaiyuanmedia.base.BasePresenter;
import cn.tklvyou.huaiyuanmedia.model.AdModel;
import cn.tklvyou.huaiyuanmedia.utils.YBitmapUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class AdPresenter extends BasePresenter<MainContract.AdView> implements MainContract.ADPresenter {

    @Override
    public void getAdView() {
        RetrofitHelper.getInstance().getServer()
                .getAdView()
                .compose(RxSchedulers.applySchedulers())
                .compose(mView.bindToLife())
                .subscribe(result -> {
                    if (result.getCode() == 1) {
                        mView.setAdView(result.getData());
                    } else {
                        ToastUtils.showShort(result.getMsg());
                    }

                }, throwable -> {
                    throwable.printStackTrace();
                });
    }





}
