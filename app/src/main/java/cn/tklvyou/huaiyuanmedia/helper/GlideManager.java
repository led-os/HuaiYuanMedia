package cn.tklvyou.huaiyuanmedia.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.security.MessageDigest;

import cn.tklvyou.huaiyuanmedia.R;


/**
 * @author :JenkinsZhou
 * @description :图片加载
 * @company :途酷科技
 * @date 2019年06月27日10:20
 * @Email: 971613168@qq.com
 */
public class GlideManager {

    private static int sCommonPlaceholder = -1;
    private static int sCirclePlaceholder = -1;
    private static int sRoundPlaceholder = -1;

    private static Drawable sCommonPlaceholderDrawable;
    private static Drawable sCirclePlaceholderDrawable;
    private static Drawable sRoundPlaceholderDrawable;
    @ColorInt
    private static int mPlaceholderColor = Color.LTGRAY;
    private static float mPlaceholderRoundRadius = 4f;

    private static void setDrawable(GradientDrawable gd, float radius) {
        gd.setColor(mPlaceholderColor);
        gd.setCornerRadius(radius);

    }

    /**
     * 设置默认颜色
     *
     * @param placeholderColor
     */
    public static void setPlaceholderColor(@ColorInt int placeholderColor) {
        mPlaceholderColor = placeholderColor;
        sCommonPlaceholderDrawable = new GradientDrawable();
        sCirclePlaceholderDrawable = new GradientDrawable();
        sRoundPlaceholderDrawable = new GradientDrawable();
        setDrawable((GradientDrawable) sCommonPlaceholderDrawable, 0);
        setDrawable((GradientDrawable) sCirclePlaceholderDrawable, 10000);
        setDrawable((GradientDrawable) sRoundPlaceholderDrawable, mPlaceholderRoundRadius);
    }

    /**
     * 设置圆角图片占位背景图圆角幅度
     *
     * @param placeholderRoundRadius
     */
    public static void setPlaceholderRoundRadius(float placeholderRoundRadius) {
        mPlaceholderRoundRadius = placeholderRoundRadius;
        setPlaceholderColor(mPlaceholderColor);
    }

    /**
     * 设置圆形图片的占位图
     *
     * @param circlePlaceholder
     */
    public static void setCirclePlaceholder(int circlePlaceholder) {
        sCirclePlaceholder = circlePlaceholder;
    }

    public static void setCirclePlaceholder(Drawable circlePlaceholder) {
        sCirclePlaceholderDrawable = circlePlaceholder;
    }

    /**
     * 设置正常图片的占位符
     *
     * @param commonPlaceholder
     */
    public static void setCommonPlaceholder(int commonPlaceholder) {
        sCommonPlaceholder = commonPlaceholder;
    }

    public static void setCommonPlaceholder(Drawable commonPlaceholder) {
        sCommonPlaceholderDrawable = commonPlaceholder;
    }

    /**
     * 设置圆角图片的占位符
     *
     * @param roundPlaceholder
     */
    public static void setRoundPlaceholder(int roundPlaceholder) {
        sRoundPlaceholder = roundPlaceholder;
    }

    public static void setRoundPlaceholder(Drawable roundPlaceholder) {
        sRoundPlaceholderDrawable = roundPlaceholder;
    }

    /**
     * 普通加载图片
     *
     * @param obj
     * @param iv
     * @param placeholder
     */
    public static void loadImg(Object obj, ImageView iv, Drawable placeholder) {
        Glide.with(iv.getContext()).load(obj).apply(getRequestOptions()
                .error(placeholder)
                .placeholder(placeholder)
                .fallback(placeholder)
                .dontAnimate()).into(iv);
    }

    public static void loadImgCenterInside(Object obj, ImageView iv, Drawable placeholder) {
        Glide.with(iv.getContext()).load(obj).apply(getRequestOptionsCenterInside()
                .error(placeholder)
                .placeholder(placeholder)
                .fallback(placeholder)
                .dontAnimate()).into(iv);
    }

    public static void loadImg(Object obj, ImageView iv, int placeholderResource) {
        Drawable drawable = getDrawable(iv.getContext(), placeholderResource);
        loadImg(obj, iv, drawable != null ? drawable : sCommonPlaceholderDrawable);
    }

    public static void loadImgCenterInside(Object obj, ImageView iv, int placeholderResource) {
        Drawable drawable = getDrawable(iv.getContext(), placeholderResource);
        loadImgCenterInside(obj, iv, drawable != null ? drawable : sCommonPlaceholderDrawable);
    }

    public static void loadImg(Object obj, ImageView iv) {
        sCommonPlaceholder = R.mipmap.img_logo;
        loadImg(obj, iv, sCommonPlaceholder);
    }

    /**
     * 加载圆形图片
     *
     * @param obj
     * @param iv
     * @param placeholder 占位图
     */
    public static void loadCircleImg(Object obj, ImageView iv, Drawable placeholder) {
        Glide.with(iv.getContext()).load(obj).apply(getRequestOptions()
                .error(placeholder)
                .placeholder(placeholder)
                .fallback(placeholder)
                .dontAnimate()
                .transform(new CircleCrop())).into(iv);
    }

    public static void loadCircleImg(Object obj, ImageView iv, int placeholderResource) {
        Drawable drawable = getDrawable(iv.getContext(), placeholderResource);
        loadCircleImg(obj, iv, drawable != null ? drawable : sCirclePlaceholderDrawable);
    }

    public static void loadCircleImg(Object obj, ImageView iv) {
        loadCircleImg(obj, iv, sCirclePlaceholder);
    }

    /**
     * 加载圆角图片
     *
     * @param obj                 加载的图片资源
     * @param iv
     * @param dp                  圆角尺寸-dp
     * @param placeholder         -占位图
     * @param isOfficial-是否官方模式圆角
     */
    public static void loadRoundImg(Object obj, ImageView iv, float dp, Drawable placeholder, boolean isOfficial) {
        Glide.with(iv.getContext()).load(obj).apply(getRequestOptions()
                .error(placeholder)
                .placeholder(placeholder)
                .fallback(placeholder)
                .dontAnimate()
                .transform(isOfficial ? new RoundedCorners(dp2px(dp)) : new GlideRoundTransform(dp2px(dp)))).into(iv);
    }

    public static void loadRoundImg(Object obj, ImageView iv, float dp, int placeholderResource, boolean isOfficial) {
        Drawable drawable = getDrawable(iv.getContext(), placeholderResource);
        loadRoundImg(obj, iv, dp, drawable != null ? drawable : sRoundPlaceholderDrawable, isOfficial);
    }

    public static void loadRoundImg(Object obj, ImageView iv, float dp, boolean isOfficial) {
        loadRoundImg(obj, iv, dp, sRoundPlaceholder, isOfficial);
    }

    public static void loadRoundImg(Object obj, ImageView iv, float dp) {
        loadRoundImg(obj, iv, dp, true);
    }

    public static void loadRoundImg(Object obj, ImageView iv, boolean isOfficial) {
        loadRoundImg(obj, iv, mPlaceholderRoundRadius, isOfficial);
    }

    public static void loadRoundImg(Object obj, ImageView iv) {
        loadRoundImg(obj, iv, true);
    }

    public static void loadTopRoundImg(Object obj, ImageView iv) {
        RoundedCornersTransform transform = new RoundedCornersTransform(iv.getContext(), dp2px(10));
        transform.setNeedCorner(true, true, false, false);

//        RequestOptions options = new RequestOptions().placeholder(R.color.color_eee).transform(transform);

        Glide.with(iv.getContext()).asBitmap().load(obj).apply(getRequestOptions()
                .error(sRoundPlaceholderDrawable)
                .placeholder(sRoundPlaceholderDrawable)
                .fallback(sRoundPlaceholderDrawable)
                .dontAnimate()
                .transform(transform)).into(iv);
    }

    private static RequestOptions getRequestOptions() {
        RequestOptions requestOptions = new RequestOptions()
                // 填充方式
                .centerCrop()
                //优先级
                .priority(Priority.HIGH)
                //缓存策略
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        return requestOptions;
    }


    private static RequestOptions getRequestOptionsCenterInside() {
        return new RequestOptions()
                // 填充方式
                .centerInside()
                //优先级
                .priority(Priority.HIGH)
                //缓存策略
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }

    private static int dp2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private static Drawable getDrawable(Context context, @DrawableRes int res) {
        Drawable drawable = null;
        try {
            drawable = context.getResources().getDrawable(res);
        } catch (Exception e) {

        }
        return drawable;
    }

    private static class GlideRoundTransform extends BitmapTransformation {
        int radius;

        public GlideRoundTransform(int dp) {
            super();
            this.radius = dp;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {

        }
    }


    /**
     * 设置图片部分圆角
     */
    public static class RoundedCornersTransform implements Transformation<Bitmap> {
        private BitmapPool mBitmapPool;

        private float radius;

        private boolean isLeftTop, isRightTop, isLeftBottom, isRightBotoom;

        /**
         * 需要设置圆角的部分
         *
         * @param leftTop     左上角
         * @param rightTop    右上角
         * @param leftBottom  左下角
         * @param rightBottom 右下角
         */
        public void setNeedCorner(boolean leftTop, boolean rightTop, boolean leftBottom, boolean rightBottom) {
            isLeftTop = leftTop;
            isRightTop = rightTop;
            isLeftBottom = leftBottom;
            isRightBotoom = rightBottom;
        }

        /**
         * @param context 上下文
         * @param radius  圆角幅度
         */
        public RoundedCornersTransform(Context context, float radius) {
            this.mBitmapPool = Glide.get(context).getBitmapPool();
            this.radius = radius;
        }

        @NonNull
        @Override
        public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {

            Bitmap source = resource.get();
            int finalWidth, finalHeight;
            //输出目标的宽高或高宽比例
            float scale;
            if (outWidth > outHeight) {
                //如果 输出宽度 > 输出高度 求高宽比

                scale = (float) outHeight / (float) outWidth;
                finalWidth = source.getWidth();
                //固定原图宽度,求最终高度
                finalHeight = (int) ((float) source.getWidth() * scale);
                if (finalHeight > source.getHeight()) {
                    //如果 求出的最终高度 > 原图高度 求宽高比

                    scale = (float) outWidth / (float) outHeight;
                    finalHeight = source.getHeight();
                    //固定原图高度,求最终宽度
                    finalWidth = (int) ((float) source.getHeight() * scale);
                }
            } else if (outWidth < outHeight) {
                //如果 输出宽度 < 输出高度 求宽高比

                scale = (float) outWidth / (float) outHeight;
                finalHeight = source.getHeight();
                //固定原图高度,求最终宽度
                finalWidth = (int) ((float) source.getHeight() * scale);
                if (finalWidth > source.getWidth()) {
                    //如果 求出的最终宽度 > 原图宽度 求高宽比

                    scale = (float) outHeight / (float) outWidth;
                    finalWidth = source.getWidth();
                    finalHeight = (int) ((float) source.getWidth() * scale);
                }
            } else {
                //如果 输出宽度=输出高度
                finalHeight = source.getHeight();
                finalWidth = finalHeight;
            }

            //修正圆角
            this.radius *= (float) finalHeight / (float) outHeight;
            Bitmap outBitmap = this.mBitmapPool.get(finalWidth, finalHeight, Bitmap.Config.ARGB_8888);
            if (outBitmap == null) {
                outBitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(outBitmap);
            Paint paint = new Paint();
            //关联画笔绘制的原图bitmap
            BitmapShader shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            //计算中心位置,进行偏移
            int width = (source.getWidth() - finalWidth) / 2;
            int height = (source.getHeight() - finalHeight) / 2;
            if (width != 0 || height != 0) {
                Matrix matrix = new Matrix();
                matrix.setTranslate((float) (-width), (float) (-height));
                shader.setLocalMatrix(matrix);
            }

            paint.setShader(shader);
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0.0F, 0.0F, (float) canvas.getWidth(), (float) canvas.getHeight());
            //先绘制圆角矩形
            canvas.drawRoundRect(rectF, this.radius, this.radius, paint);

            //左上角圆角
            if (!isLeftTop) {
                canvas.drawRect(0, 0, radius, radius, paint);
            }
            //右上角圆角
            if (!isRightTop) {
                canvas.drawRect(canvas.getWidth() - radius, 0, canvas.getWidth(), radius, paint);
            }
            //左下角圆角
            if (!isLeftBottom) {
                canvas.drawRect(0, canvas.getHeight() - radius, radius, canvas.getHeight(), paint);
            }
            //右下角圆角
            if (!isRightBotoom) {
                canvas.drawRect(canvas.getWidth() - radius, canvas.getHeight() - radius, canvas.getWidth(), canvas.getHeight(), paint);
            }

            return BitmapResource.obtain(outBitmap, this.mBitmapPool);
        }


        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        }
    }

}