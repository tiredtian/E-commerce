package com.asiainfo.mall.controller;

import com.asiainfo.mall.common.ApiRestResponse;
import com.asiainfo.mall.common.Constant;
import com.asiainfo.mall.exception.MallException;
import com.asiainfo.mall.exception.MallExceptionEnum;
import com.asiainfo.mall.filter.UserFilter;
import com.asiainfo.mall.model.pojo.User;
import com.asiainfo.mall.service.EmailService;
import com.asiainfo.mall.service.UserService;
import com.asiainfo.mall.util.EmailUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 用户模块
 * restful统一响应对象 HttpSession登陆状态保持 统一异常处理
 */
@Controller
public class UserController {

    @Resource
    private UserService userService;

    @Autowired
    private EmailService emailService;

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
     *
     * @param userName
     * @param password
     * @return
     * @throws MallException
     */
    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("userName") String userName, @RequestParam("password") String password ,
                                    @RequestParam("emailAddress") String emailAddress,@RequestParam("verificationCode") String verificationCode) throws MallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        if (StringUtils.isEmpty(emailAddress)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_EMAIL_ADDRESS);
        }
        if (StringUtils.isEmpty(verificationCode)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_VERIFICATION_CODE);
        }
        if (password.length() < 8) {
            return ApiRestResponse.error(MallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        boolean emailRegistered = userService.checkEmailRegistered(emailAddress);
        if (!emailRegistered) {
            return ApiRestResponse.error(MallExceptionEnum.EMAIL_ALREADY_BEEN_REGISTERED);
        }
        //校验是否匹配
        boolean b = emailService.checkEmailAndCode(emailAddress, verificationCode);
        if (!b) {
            return ApiRestResponse.error(MallExceptionEnum.WRONG_VERIFICATION_CODE);
        }
        userService.register(userName, password,emailAddress);
        return ApiRestResponse.success();
    }

    /**
     * 用户登陆接口
     *
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
        session.setAttribute(Constant.MALL_USER, user);
        return ApiRestResponse.success(user);
    }

    @GetMapping("/loginWithJwt")
    @ResponseBody
    public ApiRestResponse loginWithJwt(@RequestParam("userName") String userName, @RequestParam("password") String password) throws MallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        user.setPassword(null);
        //生成用户Jwt
        Algorithm algorithm = Algorithm.HMAC256(Constant.JWT_KEY);
        String token = JWT.create()
                //携带信息
                .withClaim(Constant.USER_NAME, user.getUsername())
                .withClaim(Constant.USER_ID, user.getId())
                .withClaim(Constant.USER_ROLE, user.getRole())
                //设置过期时间
                .withExpiresAt(new Date(System.currentTimeMillis() + Constant.EXPIRE_TIME))
                //签名
                .sign(algorithm);
//        session.setAttribute(Constant.MALL_USER, user);
        return ApiRestResponse.success(token);
    }

    /**
     * 更新用户信息接口
     *
     * @param session
     * @param signature
     * @return
     * @throws MallException
     */
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session, @RequestParam String signature) throws MallException {
//        User currentUser = (User) session.getAttribute(Constant.MALL_USER);
        User currentUser = UserFilter.currentUser;
        if (currentUser == null) {
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
     *
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
     *
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
            session.setAttribute(Constant.MALL_USER, user);
            return ApiRestResponse.success(user);
        } else {
            return ApiRestResponse.error(MallExceptionEnum.NEED_ADMIN);
        }
    }

    /**
     * 发送邮件
     */
    @PostMapping("user/sendEmail")
    @ResponseBody
    public ApiRestResponse sendEmail(@RequestParam("emailAddress") String emailAddress) {
        boolean validEmailAddress = EmailUtil.isValidEmailAddress(emailAddress);
        if (validEmailAddress) {
            boolean emailRegistered = userService.checkEmailRegistered(emailAddress);
            if (!emailRegistered) {
                return ApiRestResponse.error(MallExceptionEnum.EMAIL_ALREADY_BEEN_REGISTERED);
            }else{
                String verificationCode = EmailUtil.genVerificationCode();
                //邮箱验证码存入redis缓存中，设置超时时间，防止恶意攻击
                boolean savedEmailToRedis = emailService.saveEmailToRedis(emailAddress, verificationCode);
                if (savedEmailToRedis) {
                    emailService.sendSimpleMessage(emailAddress,Constant.EMAIL_SUBJECT,"欢迎注册，您的验证码是："+verificationCode);
                    return ApiRestResponse.success();
                }else{
                    return ApiRestResponse.error(MallExceptionEnum.EMAIL_ALREADY_BEEN_SEND);
                }
            }
        }else{
            return ApiRestResponse.error(MallExceptionEnum.WRONG_EMAIL);
        }
    }

    /**
     * 管理员校验登陆接口
     */
    @PostMapping("/adminLoginWithJwt")
    @ResponseBody
    public ApiRestResponse adminLoginWithJwt(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        if (userService.checkAdminRole(user)) {
            user.setPassword(null);
            Algorithm algorithm = Algorithm.HMAC256(Constant.JWT_KEY);
            String token = JWT.create()
                    .withClaim(Constant.USER_NAME, user.getUsername())
                    .withClaim(Constant.USER_ID, user.getId())
                    .withClaim(Constant.USER_ROLE, user.getRole())
                    .withExpiresAt(new Date(System.currentTimeMillis() + Constant.EXPIRE_TIME))
                    .sign(algorithm);
            return ApiRestResponse.success(token);
        }else{
            return ApiRestResponse.error(MallExceptionEnum.NEED_ADMIN);
        }
    }
}
