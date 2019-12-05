package com.kangjj.custom.glide.library.resource;

import android.graphics.Bitmap;
import android.util.Log;

import com.kangjj.custom.glide.library.Tool;

/**
 *  对Bitmap的封装
 */
public class Value {
    private final String TAG = Value.class.getSimpleName();

    private static Value value;

    public static Value getInstance(){
        if(value==null){
            synchronized (Value.class){
                if(value == null){
                    value = new Value();
                }
            }
        }
        return value;
    }

    private Bitmap bitmap;

    //使用计数
    private int count;

    //定义key
    private String key;

    //回调监听
    private ValueCallback callback;

    /**
     * 使用一次就加一
     */
    public void useAction(){
        Tool.checkNotEmpty(bitmap);
        if(bitmap.isRecycled()){
            Log.d(TAG,"userAction:已经被回收了");
            return;
        }
        Log.d(TAG,"userAction:加一 count:"+count);
        count++;
    }

    /**
     * 使用完成（不使用） 就 减一
     * count 小于0 不再使用
     */
    public void nonUseAction(){
        count--;
        if(count<=0 && callback!=null){
            callback.valueNonUseListener(key,this);
        }
        Log.d(TAG, "useAction: 减一 count:" + count);
    }

    /**
     * 释放
     */
    public void recycleBitmap(){
        if (count > 0) {
            Log.d(TAG, "recycleBitmap: 引用计数大于0，证明还在使用中，不能去释放...");
            return;
        }

        if (bitmap.isRecycled()) { // 被回收了
            Log.d(TAG, "recycleBitmap: bitmap.isRecycled() 已经被释放了...");
            return;
        }

        bitmap.recycle();
        value = null;
        System.gc();
    }

    public ValueCallback getCallback() {
        return callback;
    }

    public void setCallback(ValueCallback callback) {
        this.callback = callback;
    }

    public static Value getValue() {
        return value;
    }

    public static void setValue(Value value) {
        Value.value = value;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
