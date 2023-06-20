package com.example.pdfconvertepub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @ClassName: FirstController
 * @Description:
 * @author: 绅士的告白
 * @date: 2022/2/13 21:29
 * @Blog: 暂无
 */
@Controller
public class FirstController {
    @RequestMapping("/epub")
    public String list(){
        return "epub";
    }

    @RequestMapping("/developer")
    public String developer(){
        return "developer";
    }

    @RequestMapping("/privacy")
    public String privacy(){
        return "privacy";
    }

    @RequestMapping("/service")
    public String service(){
        return "service";
    }

}
