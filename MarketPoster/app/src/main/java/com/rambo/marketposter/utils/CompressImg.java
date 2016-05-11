package com.rambo.marketposter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;

import com.rambo.marketposter.application.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class CompressImg {
    // 图片按比例大小压缩
    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 1200f;// 这里设置高度为800f
        float ww = 1200f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    public static Bitmap ShowImg(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 600f;// 这里设置高度为800f
        float ww = 360f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        try {
            return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
        } catch (Exception e) {
            return null;
        }
    }

    // 质量压缩
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 300) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        Mylog.i("baos", baos.toByteArray().length / 1024 + "");
        byte[] bitmapArray;
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        Mylog.i("bitmap.w", bitmap.getWidth() + "");
        return bitmap;
    }


    public static Bitmap compressImage(Bitmap image, float yasuo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, (int) (100 * yasuo), baos);

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        Mylog.i("bitmap.w", bitmap.getWidth() + "");
        return bitmap;
    }

    public static String bitmapToBase64(String picPath) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (picPath != null && !picPath.equals("")) {
                baos = new ByteArrayOutputStream();
                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
                newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                newOpts.inPurgeable = true;
                newOpts.inInputShareable = true;
                newOpts.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile(picPath, newOpts);// 此时返回bm为空

                newOpts.inJustDecodeBounds = false;
                int w = newOpts.outWidth;
                int h = newOpts.outHeight;
                // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
                float hh = 1200f;// 这里设置高度为800f
                float ww = 1200f;// 这里设置宽度为480f
                // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                int be = 1;// be=1表示不缩放
                if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
                    be = (int) (newOpts.outWidth / ww);
                } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
                    be = (int) (newOpts.outHeight / hh);
                }
                if (be <= 0)
                    be = 1;
                newOpts.inSampleSize = be;// 设置缩放比例
                Mylog.i("be", be + "");
                newOpts.inJustDecodeBounds = false;
                // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
                bitmap = BitmapFactory.decodeFile(picPath, newOpts);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                int options = 100;
                while (baos.toByteArray().length / 1024 > 400) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                    baos.reset();// 重置baos即清空baos
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                    options -= 10;// 每次都减少10
                }
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                int options = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                while (baos.toByteArray().length / 1024 > 300) { // 循环判断如果压缩后图片是否大于300kb,大于继续压缩
                    baos.reset();// 重置baos即清空baos
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                    options -= 10;// 每次都减少10
                }
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 缩略图
    public static Bitmap getminimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 120f;// 这里设置高度为800f
        float ww = 160f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;// 压缩好比例大小后再进行质量压缩
    }

    // 最省内存的加载图片方法
    public static Bitmap readBitMap(int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = MyApplication.getAppContext().getResources()
                .openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    // 设置背景图片
    public static void setBg(Context context, int resId, View view) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
        Drawable drawable = new BitmapDrawable(bitmap);
        view.setBackgroundDrawable(drawable);
    }
}
