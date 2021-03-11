package com.xiaojinzi.base.service.inter.user;

/**
 * time   : 2018/12/03
 *
 * @author : xiaojinzi
 */
public interface UserService {

    /**
     * 是否登录
     *
     * @return
     */
    boolean isLogin();

    /**
     * 退出登录
     */
    void loginOut();

}
