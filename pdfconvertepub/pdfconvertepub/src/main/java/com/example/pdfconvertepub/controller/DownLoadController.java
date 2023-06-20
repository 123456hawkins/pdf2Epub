package com.example.pdfconvertepub.controller;

import com.example.pdfconvertepub.Service.DownloadService;
import com.example.pdfconvertepub.dao.ErrorLogMapper;
import com.example.pdfconvertepub.domain.ErrorLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * @ClassName: DownController
 * @Description:
 * @author: 绅士的告白
 * @date: 2022/2/17 20:18
 * @Blog: 暂无
 */
@RestController
public class DownLoadController {
    @Autowired
    DownloadService downloadService;
    public static String fileDictory="";

    @Autowired
    private ErrorLogMapper errorLogMapper;


    @GetMapping("/downloadZip")
    //@ApiOperation("下载zip")
    public void downloadZip(HttpServletResponse response) {
        String zipFileName="epubs.zip";
        List<File> fileList=downloadService.getAllFile(fileDictory,false);
              if (fileList.size()==1){
            File filepath= fileList.get(0);
            //System.out.println(filepath);
            String fileName = filepath.getName();
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            byte[] buff = new byte[1024];
            BufferedInputStream bis = null;
            OutputStream os = null;
            try {
                os = response.getOutputStream();
                bis = new BufferedInputStream(new FileInputStream(filepath));
                int i = bis.read(buff);
                while (i != -1) {
                    os.write(buff, 0, buff.length);
                    os.flush();
                    i = bis.read(buff);
                }

            } catch (IOException e) {
                ErrorLog errorLog = new ErrorLog();
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                errorLog.setCallName("downloadZip");
                errorLog.setCallFunctionFullName("com.example.pdfconvertepub.controller.DownLoadController");
                errorLog.setExceptionMessage(sw.toString());
                errorLogMapper.insert(errorLog);
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("success");
        }else if(fileList.size()>1){
            downloadService.zip(fileList,zipFileName,fileDictory);
            downloadService.downloadZip(response,zipFileName,fileDictory);
        }

    }


}




