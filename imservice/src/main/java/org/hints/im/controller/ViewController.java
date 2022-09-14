package org.hints.im.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/8/4 12:32
 */
@RestController
public class ViewController {

    /** 聊天页面
     *
     * @return
     */
    @GetMapping("/index")
    public ModelAndView toIndex() {
        ModelAndView model = new ModelAndView("chat");
        return model;
    }

    /** 登录页面
     *
     * @return
     */
    @GetMapping("/login")
    public ModelAndView toLogin() {
        ModelAndView model = new ModelAndView("login");
        return model;
    }
}
