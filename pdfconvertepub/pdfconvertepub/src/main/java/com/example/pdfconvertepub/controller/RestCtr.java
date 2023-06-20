package com.example.pdfconvertepub.controller;

import com.example.pdfconvertepub.Service.JDTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @ClassName: RestCtr
 * @Description:
 * @author: 绅士的告白
 * @date: 2022/2/16 21:33
 * @Blog: 暂无
 */

@RestController
public class RestCtr {

    @Autowired
    JDTService jdtService;

    @RequestMapping(value="/execute")
    public String execute(HttpServletRequest request) throws IOException {

        jdtService.exe(request);
        return "success";
    }
    /**
     * 获取session中的进度值
     *Title: getprocess
     *author:liuxuli
     *Description:
     　 * @param request
     　 * @return
     */
    @RequestMapping(value="/getprocess")
    public Object getprocess(HttpServletRequest request) {
        //从session将执行进度值取出来并返回给用户
        return request.getSession().getAttribute("processvalue");
    }
}
