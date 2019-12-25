package com.kangjj.custom.glide.library.pool;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import java.util.TreeMap;

/**
 * @Description: 复用池的实现 ，使用LRU算法来保存
 * LRU算法 移除最近最少使用
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide.library.pool
 * @CreateDate: 2019/12/25 17:00
 */
public class BitmapPoolImpl extends LruCache<Integer,Bitmap> implements BitmapPool{
    private final static String TAG = BitmapPoolImpl.class.getSimpleName();

    //保存的容器，为何需要此容器，是为了get的时候，进行筛选（可以查找容器里面，和getSize一样大的，也可以比getSize大的 key）
    private TreeMap<Integer,String> treeMap = new TreeMap<>();

    /**
     * LRU 最大允许存储的容量大小
     * @param maxSize
     */
    public BitmapPoolImpl(int maxSize) {
        super(maxSize);
    }

    @Override
    public void put(Bitmap bitmap) {
        //todo 条件一:Bitmap.isMutable = true;
        if(!bitmap.isMutable()){
            Log.d(TAG, "put: 条件一：Bitmap.isMutable = true; 不能满足复用的机制，不能添加到复用池");
            return;
        }
        //todo 条件二：不能大于LRU最大允许存储的容量大小
        int bitmapSize= getBitmapSize(bitmap);
        if(bitmapSize>maxSize()){
            Log.d(TAG, "put: 条件二：大于了maxSize 不能满足复用的机制，不能添加到复用池");
            return;
        }
        //添加到复用池
        put(bitmapSize,bitmap);

        // 第二次保存，容器
        treeMap.put(bitmapSize,null);

        Log.d(TAG, "put: 添加到复用池 成功....");
    }

    /**
     * 计算Bitmap的大小
     *
     * // 早期计算的方式
     *   getRowBytes() * getHeight();
     *   3.0 12 API 版本
     *   bitmap.getByteCount();
     *      4.4 19 API 版本 19以后 都是这个  native 进行计算的
     * @param bitmap
     * @return
     */
    private int getBitmapSize(Bitmap bitmap) {

        int sdkInt = Build.VERSION.SDK_INT;
        if(sdkInt>=Build.VERSION_CODES.KITKAT){
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getByteCount();
    }

    /**
       * 位图知识 == Bitmap.Config config
       *
       * ALPHA_8 实际上Android会自动做处理的 理论上我们可以这样理解，八位 == 1个字节 透明度
       * width * height * 1  有透明度
       *
       * RGB_565 实际上Android会自动做处理的 理论上我们可以这样理解，5 + 6 + 5 == 2个字节  R红色(red), G绿色，B蓝色
       * width * height * 2  没有透明度
       *
       * ARGB_4444 实际上Android会自动做处理的 理论上我们可以这样理解，4+4+4+4 == 2个字节  A透明度，R红色(red), G绿色，B蓝色
       * width * height * 2 有透明度  但是每个原色 值下降了
       *
       * ARGB_8888 实际上Android会自动做处理的 理论上我们可以这样理解，8+8+8+8 == 4个字节  A透明度，R红色(red), G绿色，B蓝色
       * width * height * 4 有透明度  质量极高 因为每个元素 都是八位
     * @param width
     * @param height
     * @param config
     * @return
     */
    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        // Android默认推荐使用，ARGB8888，质量好，有透明度
        // 开发过程中：ARGB8888  和  RGB565 徘徊
        int getSize = width * height *(config==Bitmap.Config.ARGB_8888?4:2);

        //要去复用池里面寻找，是否匹配我计算出来的图片大小
        //可以查找容器里面，和getSize一样大的，也可以比getSize大的key
        Integer key = treeMap.ceilingKey(getSize);

        if(key == null ){
            return null;
        }
        Bitmap bitmapResult = remove(key);
        Log.d(TAG, "get: 从复用池 里面获取到了 Bitmap内存空间...");
        return bitmapResult;
    }

    // LRU 重写的方法
    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        return getBitmapSize(value);
    }

    // LRU 重写的方法
    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
    }
}
