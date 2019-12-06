package com.kangjj.custom.glide.library.doload;

import android.content.Context;

import com.kangjj.custom.glide.library.resource.Value;

/**
 * @Description: 加载外部资源 标准
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide.library.doload
 * @CreateDate: 2019/12/6 16:02
 */
public interface ILoadData {
    //加载外部资源的行为
    Value loadResource(Context context, String path,ResponseCallback callback);
}
