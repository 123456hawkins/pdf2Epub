package com.example.pdfconvertepub.Service;

import com.example.pdfconvertepub.convert.PdfConverter;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName: JDTService
 * @Description:
 * @author: 绅士的告白
 * @date: 2022/2/16 22:19
 * @Blog: 暂无
 */
@Service
public class JDTService {
    private static final String TMP_PATH = "/tmp";

    public void exe(HttpServletRequest request) throws IOException {
        HttpSession session=request.getSession();
        String uuid= (String) session.getAttribute("dirname");
        File file = new File(TMP_PATH+"/"+uuid);
        List<String> filePath=new ArrayList<String>();

        List<String> fileName=new ArrayList<String>();
        File[] tempList = file.listFiles();
        System.out.println();
        for(int i=0;i<tempList.length;i++){
            if (tempList[i].isFile()) {
                request.getSession().setAttribute("processvalue",0);
                File finalPath=new File("/test",uuid);
                if (!finalPath.exists()){
                    finalPath.mkdirs();
                }
                PdfConverter
                        .convert(tempList[i])
                        .intoEpubNoneExtract("test",finalPath,request);
                //文件路径
                filePath.add(tempList[i].toString());

                //文件名，不包含路径
                fileName.add(tempList[i].getName());
            }

        }
//        Path path = copyToTmp();
//        //尝试初始化
//        File dest = new File(path.toFile(), "mobydick1.epub");
//        PdfConverter
//                .convert(new File(path.toFile(), "mobydick1.pdf"))
//                .intoEpubNoneExtract("mobydick1", dest,request);
    }

}
