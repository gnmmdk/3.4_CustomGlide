package com.kangjj.custom.glide.library.pool;

import android.graphics.Bitmap;

/**
 * @Description: 复用池的标准
 *  任何的池子，都会有添加 获取 的行为特点 线程池例外
 * @Author: jj.kang
 * @Email: jj.kang@zkteco.com
 * @ProjectName: 3.4_CustomGlide
 * @Package: com.kangjj.custom.glide.library.pool
 * @CreateDate: 2019/12/25 16:58
 */
public interface BitmapPool {
    /**
     * 存入到复用池
     * @param bitmap
     */
    void put(Bitmap bitmap);

    /**
     * 获取匹配可以复用的Bitmap
     * 计算公式：宽度 * 高度 * 每个像素点的大小字节（分块）
     * @param width
     * @param height
     * @param config
     * @return
     */
    Bitmap get(int width,int height,Bitmap.Config config);
}
