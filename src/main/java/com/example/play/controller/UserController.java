package com.example.play.controller;


import cn.hutool.json.JSONObject;
import com.example.play.annotation.UserLoginToken;
import com.example.play.common.RespBean;
import com.example.play.common.RespBeanEnum;
import com.example.play.enity.User;
import com.example.play.service.ServiceImpl.TokenService;
import com.example.play.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @PutMapping("/add")
    public RespBean addUser(@RequestBody User user){
        return userService.save(user) ? RespBean.success(user) : RespBean.error(RespBeanEnum.ERROR);
    }

    /**
     * 查询用户信息
     * @return
     */
    @UserLoginToken
    @GetMapping("/list")
    public RespBean list(){
        return RespBean.success(userService.list());
    }


    /**
     * 登录验证
     * @param user
     * @param response
     * @return
     */
    @PostMapping("/login")
    public RespBean login(@RequestBody User user, HttpServletResponse response){
        JSONObject jsonObject = new JSONObject();
        //获取用户账号密码
        User userForBase = new User();
        userForBase.setId(userService.findByUsername(user).getId());
        userForBase.setUsername(userService.findByUsername(user).getUsername());
        userForBase.setPassword(userService.findByUsername(user).getPassword());
        //判断账号或密码是否正确
        if (!userForBase.getPassword().equals(user.getPassword())) {
            return RespBean.error(RespBeanEnum.ERROR);
        } else {
            String token = tokenService.getToken(userForBase);
            jsonObject.put("token", token);
            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/");
            response.addCookie(cookie);
            return RespBean.success(jsonObject);
        }
    }
}
