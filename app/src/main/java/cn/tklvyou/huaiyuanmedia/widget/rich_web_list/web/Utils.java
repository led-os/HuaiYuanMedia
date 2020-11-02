package cn.tklvyou.huaiyuanmedia.widget.rich_web_list.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public final class Utils {

  public static String toBase64(Bitmap bitmap) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
    byte[] bytes = baos.toByteArray();

    return Base64.encodeToString(bytes, Base64.NO_WRAP);
  }

  public final static float raid = 0.1f;

  public static Bitmap toBitmap(Drawable drawable) {
    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    }

    int width = drawable.getIntrinsicWidth();
    width = width > 0 ? width : 1;
    int height = drawable.getIntrinsicHeight();
    height = height > 0 ? height : 1;

    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, (int)(canvas.getWidth() * raid), (int)(canvas.getHeight() * raid));
    drawable.draw(canvas);

    return bitmap;
  }

  public static Bitmap decodeResource(Context context, int resId) {
    return BitmapFactory.decodeResource(context.getResources(), resId);
  }

  public static long getCurrentTime() {
    return System.currentTimeMillis();
  }


  /**
   * 适配api19及以上,根据uri获取图片的绝对路径
   *
   * @param activity 上下文对象
   * @param uri      图片的Uri
   * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
   */
  @SuppressLint("NewApi")
  public static String getRealPathFromUriAboveApi19(Activity activity, Uri uri) {
    String filePath = null;
    if (DocumentsContract.isDocumentUri(activity, uri)) {
      // 如果是document类型的 uri, 则通过document id来进行处理
      String documentId = DocumentsContract.getDocumentId(uri);
      if (isMediaDocument(uri)) {
        // MediaProvider
        // 使用':'分割
        String id = documentId.split(":")[1];
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = {id};
        filePath = getDataColumn(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
      } else if (isDownloadsDocument(uri)) {
        // DownloadsProvider
        Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
        filePath = getDataColumn(activity, contentUri, null, null);
      }
    } else if ("content".equalsIgnoreCase(uri.getScheme())) {
      // 如果是 content 类型的 Uri
      filePath = getDataColumn(activity, uri, null, null);
    } else if ("file".equals(uri.getScheme())) {
      // 如果是 file 类型的 Uri,直接获取图片对应的路径
      filePath = uri.getPath();
    }
    return filePath;
  }

  private static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  private static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  /**
   * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
   *
   * @return
   */
  private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
    String path = null;
    String[] projection = new String[]{MediaStore.Images.Media.DATA};
    Cursor cursor = null;
    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
        int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
        path = cursor.getString(columnIndex);
      }
    } catch (Exception e) {
      if (cursor != null) {
        cursor.close();
      }
    }
    return path;
  }

}
