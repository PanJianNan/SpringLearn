package com.yabadun.mall.test.proxy;

/**
 * Created by panjiannan on 2018/7/26.
 */
public class ShiXian implements JieKou {

    public String say(String words) {
        return words;
    }

    public String saySuper(String words) {
        return null;
    }
}
