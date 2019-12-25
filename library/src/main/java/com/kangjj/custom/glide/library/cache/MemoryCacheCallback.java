package com.kangjj.custom.glide.library.cache;

import com.kangjj.custom.glide.library.resource.Value;

/**
 * @Description:内存缓存中，元素被移除的接口回调
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide.library
 * @CreateDate: 2019/12/6 11:41
 */
public interface MemoryCacheCallback {

    /**
     * 内存缓存中移除key -- value
     * @param key
     * @param oldValue
     */
    void entryRemovedMemoryCache(String key, Value oldValue);
}
