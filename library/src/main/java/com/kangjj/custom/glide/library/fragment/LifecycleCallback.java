package com.kangjj.custom.glide.library.fragment;

/**
 * @Description:
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide.library.fragment
 * @CreateDate: 2019/12/6 14:43
 */
public interface LifecycleCallback {

    void glideInitAction();
    void glideStopAction();
    void glideRecycleAction();
}
