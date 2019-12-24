package com.kangjj.custom.glide.library.doload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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
            httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setConnectTimeout(5000);
            final int responseCode = httpURLConnection.getResponseCode();
            if(HttpURLConnection.HTTP_OK == responseCode){
                inputStream = httpURLConnection.getInputStream();
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
