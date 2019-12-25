package com.kangjj.custom.glide.library.doload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kangjj.custom.glide.library.pool.BitmapPool;
import com.kangjj.custom.glide.library.pool.BitmapPoolImpl;
import com.kangjj.custom.glide.library.resource.Value;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide.library.doload
 * @CreateDate: 2019/12/6 16:05
 */
public class LoadDataManager implements ILoadData,Runnable {
    private final String TAG = LoadDataManager.class.getSimpleName();
    private String path;
    private ResponseCallback responseCallback;
    private Context context;

    private BitmapPool bitmapPool = new BitmapPoolImpl(1024 * 1024 * 6);
    /**
     * 加载 网络图片/SD本地图片/..
     * @param context
     * @param path
     * @param callback
     * @return
     */
    @Override
    public Value loadResource(Context context, String path, ResponseCallback callback) {
        this.context = context;
        this.path = path;
        this.responseCallback = callback;

        Uri uri = Uri.parse(path);

        if("HTTP".equalsIgnoreCase(uri.getScheme())||"HTTPS".equalsIgnoreCase(uri.getScheme())){
            new ThreadPoolExecutor(0,Integer.MAX_VALUE,60, //TODO 每次都要实例化？
                    TimeUnit.SECONDS,new SynchronousQueue<Runnable>()).execute(this);
            return null;
        }

        // SD本地图片 返回Value
        // ....

        return null;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(path);
            URLConnection urlConnection = url.openConnection();

            // 修改成了 OKHttp 实在太高效了
            // 应用层 HttpURLConnection http https
            // 传输层 OKHttp进行了封装Socket

            httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setConnectTimeout(5000);
            final int responseCode = httpURLConnection.getResponseCode();
            if(HttpURLConnection.HTTP_OK == responseCode){
                inputStream = httpURLConnection.getInputStream();

//                BitmapFactory.Options options = new BitmapFactory.Options();
//                //需要拿到图片的宽和高，才能到复用池里面去虚招，是否可用的内存给我复用
//                options.inJustDecodeBounds = true;//只要拿到图片的周边信息 为了获取宽高
//                // 只有执行此代码后，outWidth  outHeight 才会有值
//                BitmapFactory.decodeStream(inputStream,null,options);
//                int width = options.outWidth;
//                int height = options.outHeight;
//
//                Bitmap bitmapPoolResult = bitmapPool.get(width,height, Bitmap.Config.ARGB_8888);
//                //不关心此bitmapPoolResult本身的内容（风景、美女）是什么，只关心内存是否可以为我所用。
//                //复用...
//                BitmapFactory.Options options2= new BitmapFactory.Options();
//                //只接受可以被复用的Bitmap内存
//                // 证明可以复用（不开辟内存空间，不会内存抖动，内存碎片问题）
//                //options2.inBitmap = bitmapPoolResult;
//
//                // 无法复用，直接开辟内存空间
//                // options.inBitmap = bitmapPoolResult有问题 或者 为null;
//
//                options2.inBitmap = bitmapPoolResult;
//                options2.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                options2.inJustDecodeBounds = false;    //默认就是false,拿到完整的信息
//                options2.inMutable = true;              // 必须为true，才有复用的资格
//
//                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream,null,options2);
//                //添加到复用池里面去。
//                bitmapPool.put(bitmap);

                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Value value = Value.getInstance();//TODO? 单例
                        value.setBitmap(bitmap);
                        //回调成功
                        responseCallback.responseSuccess(value);
                    }
                });
            }else{
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        responseCallback.responseException(new IllegalStateException("请求失败 请求码:" + responseCode));
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: 关闭 inputStream.close(); e:" + e.getMessage());
                }
            }
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }

    }
}
