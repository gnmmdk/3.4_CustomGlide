package com.kangjj.custom.glide.library.cache;

import com.kangjj.custom.glide.library.Tool;
import com.kangjj.custom.glide.library.resource.Value;
import com.kangjj.custom.glide.library.resource.ValueCallback;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 活动缓存--真正被使用的资源
 */
public class ActivityCache {
    //容器
    private Map<String, WeakReference<Value>> mapList = new HashMap<>();
    //目的：为了监听这个弱引用 是否被回收
    private ReferenceQueue<Value> queue;
    //线程关闭的标识
    private boolean isCloseThread;
    private Thread thread;
    private boolean isShoudonRemove;
    private ValueCallback valueCallback;

    public ActivityCache(ValueCallback valueCallback){
        this.valueCallback = valueCallback;
    }

    /**
     * 添加活动缓存
     * @param key
     * @param value
     */
    public void put(String key,Value value){
        Tool.checkNotEmpty(key);

        // 绑定Value的监听 --> Value发起来的（Value没有被使用了，就会发起这个监听，给外界业务需要来使用）
        value.setCallback(valueCallback);

        mapList.put(key,new CustomWeakReference(value,getQueue(),key));
    }

    /**
     * 给外界获取Value
     * @param key
     * @return
     */
    public Value get(String key){
        WeakReference<Value> valueWeakReference = mapList.get(key);
        if(null != valueWeakReference){
            valueWeakReference.get();
        }
        return null;
    }

    /**
     * 手动移除 TODO getQueue线程 queue.remove()阻塞的问题
     * @param key
     * @return
     */
    public Value remove(String key){
        isShoudonRemove = true;
        WeakReference<Value> removeWeakReference = mapList.remove(key);
        isShoudonRemove = false;//还原 目的是为了让GC自动移除 继续工作
        if(null != removeWeakReference){
            return removeWeakReference.get();
        }
        return null;
    }

    /**
     * 监听弱引用 成为弱引用的子类
     * 目的:为了监听这个弱引用是否被回收了
     */
    public class CustomWeakReference extends WeakReference<Value>{
        private String key; //这里的key是为了mapList删除的时候使用
        public CustomWeakReference(Value referent, ReferenceQueue<? super Value> queue,String key) {
            super(referent, queue);
            this.key = key;
        }
    }

    /**
     * 为了监听弱引用被回收，被动移除
     * @return
     */
    private ReferenceQueue<Value> getQueue(){
        if(queue == null){
            queue = new ReferenceQueue<>();

            //监听这个弱引用 是否被回收了
            thread = new Thread(){
                @Override
                public void run() {
                    super.run();
                    while (!isCloseThread) {
                        try {
                            if (!isShoudonRemove) { //!isShoudonRemove：为了区分手动移除 和 被动移除
                                // TODO 既然queue.remove()被阻塞了，那么就会一直等待，是不是有可能引起!isShoudonRemove无效
                            //queue.remove 阻塞式的方法
                                Reference<? extends Value> remove = queue.remove();//如果已经被回收了，就会执行这个方法
                                CustomWeakReference weakReference = (CustomWeakReference) remove;
                                // 移除容器
                                if(mapList!=null && !mapList.isEmpty() /*&& !isShoudonRemove*/){
                                    mapList.remove(weakReference.key);
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };
            thread.start();
        }
        return queue;
    }
}
