package com.kangjj.custom.glide.library.resource;

/**
 * 专门给Value,不再使用的回调监听
 */
public interface ValueCallback {

    /**
     * 监听的方法（Value不再使用)
     * @param key
     * @param value
     */
    void valueNonUseListener(String key,Value value);
}
