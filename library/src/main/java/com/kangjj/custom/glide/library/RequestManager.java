package com.kangjj.custom.glide.library;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.kangjj.custom.glide.library.fragment.ActivityFragmentManageer;
import com.kangjj.custom.glide.library.fragment.FragmentActivityFragmentManageer;

/**
 * @Description: todo E 生命周期 可以管理的生命周期：FragmentActivity Activity。不可管理的生命周期： Context
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide.library
 * @CreateDate: 2019/12/6 14:19
 */
public class RequestManager {
    private static final String TAG = RequestManager.class.getSimpleName();
    private static final String FRAGMENT_ACTIVITY_NAME = "fragment_activity_name";
    private final String ACTIVITY_NAME = "activity_name";
    private static final int NEXT_HANDLER_MSG = 995465;

    private Context requestManagerContext;
    private static RequestTargetEngine requestTargetEngine;

    // 构造代码块，不用再所有的构造方法里面去实例化了，统一的去写
    {
        if(requestTargetEngine == null){
            requestTargetEngine = new RequestTargetEngine();
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
//            Fragment fragment = fragmentActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
//            Log.d(TAG, "Handler: fragment2" + fragment); // 有值 ： 不在排队中，所以有值
            return false;
        }
    });

    /**
     * todo E.1 可以管理生命周期 - FragmentActivity是有生命周期方法的（fragment)
     * @param fragmentActivity
     */
    public RequestManager(FragmentActivity fragmentActivity) {
        this.requestManagerContext = fragmentActivity;
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if(fragment == null){
            //todo E.1.1 该类继承对应的Fragment（V4包 Fragment）实现 LifecycleCallback
            fragment = new FragmentActivityFragmentManageer(requestTargetEngine); // Fragment的生命周期与requestTargetEngine关联起来了
            supportFragmentManager.beginTransaction().add(fragment,FRAGMENT_ACTIVITY_NAME).commitAllowingStateLoss();
        }

        //todo E.1.2 Android基于Handler消息的，为了让我们的fragment，不要再排队中，为了下次可以取出来
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);

    }

    /**
     * todo E.2 可以管理生命周期 -- Activity是有生命周期方法的(Fragment)
     * @param activity
     */
    public RequestManager(Activity activity) {
        this.requestManagerContext = activity;
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(ACTIVITY_NAME);
        if(fragment == null){
            //todo E.2.1 该类继承对应的Fragment（app Fragment）实现 LifecycleCallback
            fragment = new ActivityFragmentManageer(requestTargetEngine);// Fragment的生命周期与requestTargetEngine关联起来了
            // 添加到管理器 -- fragmentManager.beginTransaction().add.. Handler
            fragmentManager.beginTransaction().add(fragment,ACTIVITY_NAME).commitAllowingStateLoss();
        }
//        android.app.Fragment fragment2 = fragmentManager.findFragmentByTag(ACTIVITY_NAME);
//        Log.d(TAG, "RequestManager: fragment2" + fragment2); // null ： @3 还在排队中，还没有消费
        //todo E.2.2 Android基于Handler消息的，为了让我们的fragment，不要再排队中，为了下次可以取出来
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);
    }

    /**
     * 代表无法去管理生命周期，因为Application无法管理
     * @param context
     */
    public RequestManager(Context context) {
        this.requestManagerContext = context;
    }

    /**
     * load 拿到要显示的图片路径
     * @param path
     * @return
     */
    public RequestTargetEngine load(String path) {
        mHandler.removeMessages(NEXT_HANDLER_MSG);

        requestTargetEngine.loadValueInitAction(path,requestManagerContext);
        return requestTargetEngine;
    }
}
