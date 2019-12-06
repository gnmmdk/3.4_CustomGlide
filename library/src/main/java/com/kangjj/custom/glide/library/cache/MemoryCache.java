package com.kangjj.custom.glide.library.cache;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import com.kangjj.custom.glide.library.resource.Value;

/**
 * @Description: 内存缓存--LRU算法
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide.library.cache
 * @CreateDate: 2019/12/6 11:22
 */
public class MemoryCache extends LruCache<String, Value> {

    private boolean shoudonRemove;
    private MemoryCacheCallback memoryCacheCallback;

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    public Value shoudonRemove(String key){
        shoudonRemove = true;
        Value value = remove(key);
        shoudonRemove = false;
        return value;
    }

    @Override
    protected int sizeOf(@NonNull String key, @NonNull Value value) {
        Bitmap bitmap = value.getBitmap();
        // 最开始的时候
//        int result = bitmap.getRowBytes()*bitmap.getHeight();
        // API 12  3.0
//        result = bitmap.getByteCount();//在bitmap内存复用上有区别（所属的）
        // API 19 4.4
//        result = bitmap.getAllocationByteCount();//在bitmap内存复用上有区别（整个的）

        int sdkInt = Build.VERSION.SDK_INT;
        if( sdkInt >= Build.VERSION_CODES.KITKAT){
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getByteCount();
    }

    /**
     * 1 重复的key
     * 2 最少使用的元素会被移除
     * 上方 public Value shoudonRemove(String key)方法里面remove方法，最终要调用到entryRemoved，所以要要加shoudonRemove判断
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void entryRemoved(boolean evicted, @NonNull String key, @NonNull Value oldValue, @Nullable Value newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if(memoryCacheCallback != null && !shoudonRemove){//手动删除不回掉，被动删除 才回调
            memoryCacheCallback.entryRemovedMemoryCache(key,oldValue);
        }
    }

    public void setMemoryCacheCallback(MemoryCacheCallback memoryCacheCallback) {
        this.memoryCacheCallback = memoryCacheCallback;
    }
}
