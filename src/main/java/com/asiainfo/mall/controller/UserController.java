package com.asiainfo.mall.controller;

import com.asiainfo.mall.common.ApiRestResponse;
import com.asiainfo.mall.common.Constant;
import com.asiainfo.mall.exception.MallException;
import com.asiainfo.mall.exception.MallExceptionEnum;
import com.asiainfo.mall.model.pojo.User;
import com.asiainfo.mall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 用户模块
 * restful统一响应对象 HttpSession登陆状态保持 统一异常处理
 */
@Controller
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 测试接口
     *
     * @return
     */
    @GetMapping("/test")
    @ResponseBody
    public User personalPage() {
        return userService.getUser();
    }

    /**
     * user注册接口
     * @param userName
     * @param password
     * @return
     * @throws MallException
     */
    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("userName") String userName, @RequestParam("password") String password) throws MallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        if (password.length() < 8) {
            return ApiRestResponse.error(MallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(userName,password);
        return ApiRestResponse.success();
    }

    /**
     * 用户登陆接口
     * @param userName
     * @param password
     * @param session
     * @return
     * @throws MallException
     */
    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws MallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        user.setPassword(null);
        session.setAttribute(Constant.MALL_USER,user);
        return ApiRestResponse.success(user);
    }

    /**
     * 更新用户信息接口
     * @param session
     * @param signature
     * @return
     * @throws MallException
     */
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session, @RequestParam String signature) throws MallException {
        User currentUser = (User) session.getAttribute(Constant.MALL_USER);
        if(currentUser==null){
            return ApiRestResponse.error(MallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }

    /**
     * 用户登出接口
     * @param session
     * @return
     */
    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session) {
        session.removeAttribute(Constant.MALL_USER);
        return ApiRestResponse.success();
    }

    /**
     * 管理员登陆接口
     * @param userName
     * @param password
     * @param session
     * @return
     * @throws MallException
     */
    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws MallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        if (userService.checkAdminRole(user)) {
            user.setPassword(null);
            session.setAttribute(Constant.MALL_USER,user);
            return ApiRestResponse.success(user);
        }else{
            return ApiRestResponse.error(MallExceptionEnum.NEED_ADMIN);
        }
    }
}
