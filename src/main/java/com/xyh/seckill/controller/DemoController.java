package com.xyh.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author xyh
 * @date 2021/11/1 14:55
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    /**
    * @description:  
    * @param: model 
    * @return: java.lang.String
    * @author xyh
    * @date: 2021/11/1 14:58
    */ 
    @RequestMapping("/hello")
    public String hello(Model model){
        model.addAttribute("name","nnnnnn");
        return "hello";
    }
}
