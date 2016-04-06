package com.yabadun.mall.service;

import com.yabadun.mall.bean.User;

/**
 * SpringLearn
 *
 * @author panjn
 * @date 2016/2/24
 */
public interface SecurityService {
    boolean userAuth(User user);
}
