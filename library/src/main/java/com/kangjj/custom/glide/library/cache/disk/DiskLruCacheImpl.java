package com.kangjj.custom.glide.library.cache.disk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.kangjj.custom.glide.library.Tool;
import com.kangjj.custom.glide.library.resource.Value;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Description: 磁盘缓存的封装
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide.library.cache.disk
 * @CreateDate: 2019/12/6 11:55
 */
public class DiskLruCacheImpl {

    private static final String TAG = DiskLruCacheImpl.class.getSimpleName();

    // SD/kangjj_disklru_cache_dir/ac037ea49e34257dc5577d1796bb137dbaddc0e42a9dff051beee8ea457a4668
    private final String DISKLRU_CACHE_DIR = "kangjj_disklru_cache_dir";//磁盘缓存的目录

    private final int APP_VERSION = 1; // 我们的版本号，一旦修改这个版本号，之前的缓存失效
    private final int VALUE_COUNT = 1;  // 通常情况下都是1
    private final long MAX_SIZE = 1024 * 1024 * 100; // 以后修改成 使用者可以设置的  注意：可以自己去自动配置

    private DiskLruCache diskLruCache;

    public DiskLruCacheImpl() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + DISKLRU_CACHE_DIR);

        try {
            diskLruCache = DiskLruCache.open(file,APP_VERSION, VALUE_COUNT,MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void put(String key,Value value){
        Tool.checkNotEmpty(key);
        DiskLruCache.Editor editor = null;
        OutputStream outputStream = null;
        try {
            editor = diskLruCache.edit(key);
            outputStream = editor.newOutputStream(0);// index 不能大于 VALUE_COUNT

            Bitmap bitmap = value.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);// 把bitmap写入到outputStream
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.e(TAG, "put: editor.abort() e:" + e.getMessage());
            }
        }finally {
            try {
                editor.commit();
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "put: editor.commit(); e:" + e.getMessage());
            }
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "put: outputStream.close(); e:" + e.getMessage());
                }
            }
        }
    }

    public Value get(String key){
        Tool.checkNotEmpty(key);
        InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            if( null != snapshot){
                Value value = Value.getInstance();//TODO 为什么是单例？
                inputStream = snapshot.getInputStream(0);// index 不能大于 VALUE_COUNT
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                value.setBitmap(bitmap);
                value.setKey(key);
                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "get: inputStream.close(); e:" + e.getMessage());
                }
            }
        }
        return null;
    }
}
