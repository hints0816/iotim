package org.hints.im.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/4 12:32
 */
@RestController
public class ViewController {

    @GetMapping("/index")
    public ModelAndView toIndex() {
        ModelAndView model = new ModelAndView("chat");
        return model;
    }

    @GetMapping("/login")
    public ModelAndView toLogin() {
        ModelAndView model = new ModelAndView("login");
        return model;
    }
}
