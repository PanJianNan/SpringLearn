package com.yabadun.mall.service.impl;

import com.yabadun.mall.bean.User;
import com.yabadun.mall.service.SecurityService;

/**
 * SpringLearn
 *
 * @author panjn
 * @date 2016/2/24
 */
public class SercurityServiceImpl implements SecurityService {
    public boolean userAuth(User user) {
        if (user != null) {
            if ("panjn".equals(user.getAccount()) && "123456".equals(user.getPassword())) {
                user.setName("潘建南");
                return true;
            }
        }
        return false;
    }
}
