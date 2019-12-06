package com.kangjj.custom.glide.library;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

/**
 * @Description:
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide.library
 * @CreateDate: 2019/12/6 14:23
 */
public class RequestManagerRetriver {
    public RequestManager get(FragmentActivity fragmentActivity){
        return new RequestManager(fragmentActivity);
    }

    public RequestManager get(Activity activity){
        return new RequestManager(activity);
    }

    public RequestManager get(Context context){
        return new RequestManager(context);
    }
}
