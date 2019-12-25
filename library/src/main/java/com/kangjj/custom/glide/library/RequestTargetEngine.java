package com.kangjj.custom.glide.library;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.kangjj.custom.glide.library.cache.ActivityCache;
import com.kangjj.custom.glide.library.cache.MemoryCache;
import com.kangjj.custom.glide.library.cache.MemoryCacheCallback;
import com.kangjj.custom.glide.library.cache.disk.DiskLruCacheImpl;
import com.kangjj.custom.glide.library.doload.LoadDataManager;
import com.kangjj.custom.glide.library.doload.ResponseCallback;
import com.kangjj.custom.glide.library.fragment.LifecycleCallback;
import com.kangjj.custom.glide.library.resource.Key;
import com.kangjj.custom.glide.library.resource.Value;
import com.kangjj.custom.glide.library.resource.ValueCallback;

/**
 * @Description: todo F 加载图片资源
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide.library
 * @CreateDate: 2019/12/6 14:41
 */
public class RequestTargetEngine implements LifecycleCallback, ValueCallback, MemoryCacheCallback, ResponseCallback {
    private final String TAG = RequestTargetEngine.class.getSimpleName();
    private String path;
    private Context glideContext;
    private String key;
    private ImageView imageView;//显示的目标;

    private ActivityCache activityCache;            //活动缓存缓存
    private MemoryCache memoryCache;                //内存缓存
    private DiskLruCacheImpl diskLruCache;          //磁盘缓存
                                                    //复用池 TODO
    private final int MEMORY_MAX_SIZE = 1024 * 1024 * 60;

    public RequestTargetEngine() {
        if(activityCache == null){
            activityCache = new ActivityCache(this);//ValueCallback 回调告诉外界，Value资源不再使用了
        }
        if(memoryCache == null){
            memoryCache = new MemoryCache(MEMORY_MAX_SIZE);
            memoryCache.setMemoryCacheCallback(this);           //LRU 最少使用的元素会被移除 设置监听
        }
        //初始化磁盘缓存
        diskLruCache = new DiskLruCacheImpl();
    }

    @Override
    public void glideInitAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之 已经开启了 初始化了....");
    }

    @Override
    public void glideStopAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之 已经停止中 ....");
    }

    @Override
    public void glideRecycleAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之 进行释放操作 缓存策略释放操作等 >>>>>> ....");
        if(activityCache != null){
            activityCache.closeThread();        //todo E.3 把活动缓存给释放掉
        }
        //是否需要加入到内存缓存中？ 把内存缓存移除 这点没必要
    }

    public void loadValueInitAction(String path, Context glideContext) {
        this.path = path;
        this.glideContext = glideContext;
        key = new Key(path).getKey();
    }

    public void into(ImageView imageView) {
        Tool.checkNotEmpty(imageView);
        Tool.assertMainThread();
        this.imageView = imageView;

        //todo F 加载资源 --> 缓存 --> 网络/SD/加载资源 成功后-->资源保存在缓存中 >>>
        Value value = cancheAction();
        if(value != null){
            imageView.setImageBitmap(value.getBitmap());
            //使用完成了 减一  当为0 的时候调用到下方的valueNonUseListener
            value.nonUseAction();
        }
    }

    /**
     * 加载资源 --> 缓存 --> 网络/SD/加载资源 成功后-->资源保存在缓存中 >>>
     * todo 改成责任链？
     * @return
     */
    private Value cancheAction() {
        //todo F.1 第一步，判断活动缓存是否有资源，如果有资源 就返回， 否则就继续往下找
        Value value = activityCache.get(key);
        if(value != null){
            Log.d(TAG, "cacheAction: 本次加载是在(活动缓存)中获取的资源>>>");

            value.useAction();//使用了一次加一
            return value;  // 返回 代表 使用了一次 Value
        }
        //todo F.2 第二步，从内存缓存中去找，如果找到了，内存缓存中的元素 “移动” 到 活动缓存， 然后再返回
        value = memoryCache.get(key);
        if(value != null){
            memoryCache.shoudonRemove(key);//手动移除，不会调用到entryRemovedMemoryCache
            activityCache.put(key,value);// 把内存缓存中的元素 加入到活动缓存中
            Log.d(TAG, "cacheAction: 本次加载是在(内存缓存)中获取的资源>>>");

            value.useAction();// 使用了一次 加一
            return value;  // 返回 代表 使用了一次 Value
        }

        //todo F.3 第三步，从磁盘缓存中去找，如果找到了，把磁盘缓存中的元素 加入到 活动缓存中
        value = diskLruCache.get(key);
        if(value != null){
            // 把磁盘缓存中的元素 --> 加入到活动缓存中
            activityCache.put(key,value);
            // 把磁盘缓存中的元素 --> 加入到内存缓存中 TODO glideRecycleAction
            // memoryCache.put(key, value);
            Log.d(TAG, "cacheAction: 本次加载是在(磁盘缓存)中获取的资源>>>");
            value.useAction();
            return value;
        }
        //todo F.4 第四步，真正的去加载外部资源了， 去网络上加载/去SD本地上加载
        value = new LoadDataManager().loadResource(glideContext,path,this);

        return value;//加载网络的话 这里会返回空
    }

    /**
     * todo F.5 通过接口的回到 保存到缓存中
     * @param key
     * @param value
     */
    private void saveCache(String key, Value value) {

        Log.d(TAG, "saveCahce: >>>>>>>>>>>>>>>>>>>>>> 加载外部资源成功后，保存到缓存中 key:" + key + " value:" + value);
        value.setKey(key);
        if(diskLruCache!=null){
            diskLruCache.put(key,value);
        }
        if(activityCache!=null){    //
            value.useAction();
            activityCache.put(key,value);
        }
    }

    /**
     * 活动缓存间接的调用Value所发出的
     * 回调告诉外界，Value资源不再使用了
     * 监听的方法（Value不再使用）
     * @param key
     * @param value
     */
    @Override
    public void valueNonUseListener(String key, Value value) {
        // 把活动缓存操作的Value资源 加入到 内存缓存
        if(key !=null && value!=null){
            memoryCache.put(key,value);
        }
    }

    /**
     * 内存缓存发出的 LRU最少使用的元素会被移除
     * @param key
     * @param oldValue
     */
    @Override
    public void entryRemovedMemoryCache(String key, Value oldValue) {
        // TODO 添加到复用池 ...... ，空留的功能点
    }


    @Override
    public void responseSuccess(Value value) {
        if(value!=null){
            saveCache(key,value);
            imageView.setImageBitmap(value.getBitmap());
        }
    }


    // 加载外部资源失败
    @Override
    public void responseException(Exception e) {
        Log.d(TAG, "responseException: 加载外部资源失败 e:" + e.getMessage());
    }
}
