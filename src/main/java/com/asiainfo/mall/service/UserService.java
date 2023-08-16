package com.asiainfo.mall.service;

import com.asiainfo.mall.exception.MallException;
import com.asiainfo.mall.model.pojo.User;

public interface UserService {

    User getUser();

    void register(String userName, String password,String emailAddress) throws MallException;

    User login(String userName, String password) throws MallException;

    void updateInformation(User user) throws MallException;

    boolean checkAdminRole(User user);

    boolean checkEmailRegistered(String emailAddress);
}
