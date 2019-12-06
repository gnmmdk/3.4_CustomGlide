package com.kangjj.custom.glide.library;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

/**
 * @Description:
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide
 * @CreateDate: 2019/12/6 14:17
 */
public class Glide {

    RequestManagerRetriver retriver;
    public Glide(RequestManagerRetriver retriver) {
        this.retriver = retriver;
    }

    /**
     * @param fragmentActivity
     * @return
     */
    public static RequestManager with(FragmentActivity fragmentActivity){
        return getRetriver(fragmentActivity).get(fragmentActivity);
    }

    public static RequestManager with(Activity activity){
        return getRetriver(activity).get(activity);
    }

    public static RequestManager with(Context context){
        return getRetriver(context).get(context);
    }

    /**
     * RequestManager由RequestManagerRetriver去创建的
     * @param context
     * @return
     */
    public static RequestManagerRetriver  getRetriver(Context context){
        return Glide.get(context).getRetriver();
    }

    private RequestManagerRetriver getRetriver() {
        return retriver;
    }

    //Glide是new出来的--转变
    private static Glide get(Context context) {
        return new GlidBuilder().build();
    }
}
