package com.kangjj.custom.glide.library.doload;

import com.kangjj.custom.glide.library.resource.Value;

/**
 * @Description: 加载外部资源 成功与失败的回调
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide.library.doload
 * @CreateDate: 2019/12/6 16:03
 */
public interface ResponseCallback {
    void responseSuccess(Value value);
    void responseException(Exception e);
}
