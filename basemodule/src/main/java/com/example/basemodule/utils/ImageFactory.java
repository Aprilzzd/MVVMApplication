package com.example.basemodule.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.basemodule.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Apink on 2018/1/10.
 *
 */

public class ImageFactory {

    public static void loadLocalImg(Fragment context, String url, ImageView imageView){
        RequestOptions options=new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
        Glide.with(context).load(url).apply(options).into(imageView);
    }

    public static void loadImg(Fragment context, String url, ImageView imageView){
        Glide.with(context).asBitmap().load(url).apply(getPlaceholder()).into(imageView);
    }

    public static void loadTopImg(Fragment context, String url, ImageView imageView){
        Glide.with(context).load(url).apply(new RequestOptions().placeholder(R.color.white))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    public static void loadRoundImg(Fragment context, String url, ImageView imageView){
        RequestOptions options=new RequestOptions().transform(new GlideRoundTransform(5)).placeholder(R.color.white);
        Glide.with(context).load(url).apply(options)
                .transition(DrawableTransitionOptions.withCrossFade()).into(imageView);
    }

    public static RequestOptions getPlaceholder(){
        return new RequestOptions().placeholder(R.color.gray_light);
    }

    public static void loadImgHead(Fragment context, String url, ImageView imageView){
        if (TextUtils.isEmpty(url)){
            Glide.with(context).load(R.color.white).into(imageView);
        }else {
            Glide.with(context).load(url).into(imageView);//不能设占位图
        }
    }

    public static String startPhotoZoom(Fragment fragment, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        String headPath = fragment.getActivity().getExternalCacheDir().getPath()
                + File.separator + "head.jpg";
        File headFile = new File(headPath);
        intent.putExtra("output", Uri.fromFile(headFile));
        fragment.startActivityForResult(intent, 3);
        return headPath;
    }

    /**
     * file转Bitmap
     *
     * @param imgPath
     * @return
     */
    public static Bitmap getBitmap(String imgPath) {
        // Get bitmap through image path
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        // Do not compress
        newOpts.inSampleSize = 1;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(imgPath, newOpts);
    }

    /**
     * 保存Bitmap
     *
     * @param bitmap
     * @param outPath
     */
    public static void storeImage(Bitmap bitmap, String outPath) {
        try {
            FileOutputStream os = new FileOutputStream(outPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        }catch(IllegalArgumentException e) {
            e.printStackTrace();
        }catch (RuntimeException e) {
            e.printStackTrace();
        }finally {
            try {
                retriever.release();
            }catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image, String outPath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        image.recycle();
        return bitmap;
    }

    public static File compressImageToFile(Bitmap image, String outPath){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        try {
            FileOutputStream os = new FileOutputStream(outPath);
            os.write(baos.toByteArray());
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        image.recycle();
        return new File(outPath);
    }

    /**
     * 图片按比例大小压缩方法
     *
     * @param srcPath （根据路径获取图片并压缩）
     * @return
     */
    public static File getImage(String srcPath, String outPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap=BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 1000f;//
        float ww = 1000f;//
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

        return compressImageToFile(bitmap,outPath);// 压缩好比例大小后再进行质量压缩
//        storeImage(bitmap,outPath);
//        bitmap.recycle();
//        return new File(outPath);
    }
}
