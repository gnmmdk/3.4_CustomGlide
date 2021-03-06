package com.kangjj.custom.glide.library.resource;

import com.kangjj.custom.glide.library.Tool;

/**
 * TODO A 资源封装   A.1 key是由路径加密成sha256的字符串
 * 唯一描述
 */
public class Key {
    private String key;     //例如：ac037ea49e34257dc5577d1796bb137dbaddc0e42a9dff051beee8ea457a4668

    /**
     * sha256（https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg）之前
     * ac037ea49e34257dc5577d1796bb137dbaddc0e42a9dff051beee8ea457a4668 处理后
     * @param key
     */
    public Key(String key){
        this.key = Tool.getSHA256StrJava(key);
    }

    public String getKey() {
        return key;
    }
}
