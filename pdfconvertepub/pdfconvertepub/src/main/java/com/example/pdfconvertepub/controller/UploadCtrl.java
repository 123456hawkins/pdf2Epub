package com.example.pdfconvertepub.controller;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.pdfconvertepub.bean.InfoMsg;
import com.example.pdfconvertepub.dao.ErrorLogMapper;
import com.example.pdfconvertepub.domain.ErrorLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/upload")
public class UploadCtrl {
    private static final String TMP_PATH = "/tmp";
    @Autowired
    private ErrorLogMapper errorLogMapper;
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String fileUploadInit() {
        // InfoMsg infoMsg = new InfoMsg();

        return "upload";
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public InfoMsg fileUpload(@RequestParam("uploadFile") MultipartFile[] files, HttpServletRequest request) {
        InfoMsg infoMsg = new InfoMsg();
        if (files==null) {
            infoMsg.setCode("error");
            infoMsg.setMsg("Please select a file to upload");
            return infoMsg;
        }
        //tmp的下一级目录，为了区分来自不同的用户的转换文件
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        HttpSession session=request.getSession();
        session.setAttribute("dirname",uuid);
        try {
            for (int i=0;i<files.length;i++){
                File tmp = new File(TMP_PATH+"/"+uuid, files[i].getOriginalFilename());
                if(!tmp.getParentFile().exists()){
                    tmp.getParentFile().mkdirs();
                }

                files[i].transferTo(tmp);

                infoMsg.setCode("success");
                infoMsg.setMsg("You successfully uploaded '" + files[i].getOriginalFilename() + "'");
            }


        } catch (IOException e) {
            infoMsg.setCode("error");
            infoMsg.setMsg("Uploaded file failed");
            ErrorLog errorLog = new ErrorLog();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            errorLog.setCallName("fileUpload");
            errorLog.setCallFunctionFullName("com.example.pdfconvertepub.controller.UploadCtrl");
            errorLog.setExceptionMessage(sw.toString());
            errorLogMapper.insert(errorLog);
        }

        return infoMsg;
    }


}
