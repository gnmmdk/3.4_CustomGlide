package com.kangjj.custom.glide.library.fragment;

import androidx.fragment.app.Fragment;

/**
 * @Description: FragmentActivity关联管理
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide.library
 * @CreateDate: 2019/12/6 15:10
 */
public class FragmentActivityFragmentManageer extends Fragment {
    private LifecycleCallback lifecycleCallback;
    public FragmentActivityFragmentManageer(LifecycleCallback lifecycleCallback) {
        this.lifecycleCallback = lifecycleCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(lifecycleCallback!=null){
            lifecycleCallback.glideInitAction();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(lifecycleCallback!=null){
            lifecycleCallback.glideStopAction();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(lifecycleCallback != null){
            lifecycleCallback.glideRecycleAction();
        }
    }
}
