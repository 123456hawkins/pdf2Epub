package com.example.pdfconvertepub.webapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/webapi")
public class findRestController {
    private static final String TMP_PATH = "/tmp";

    @GetMapping("/getPdfName")
    public List<String> getPdfName(HttpServletRequest request) {
        List<String> fileName = new ArrayList<String>();
        HttpSession session = request.getSession();
        String uuid = (String) session.getAttribute("dirname");
        File file = new File(TMP_PATH + "/" + uuid);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            fileName.add(tempList[i].getName());
        }

        return fileName;
    }
    @GetMapping("/newName")//获取文件新老名字
    public int newName(String on1,String on2,String on3,String nn1,String nn2,String nn3,HttpServletRequest request){
        HttpSession session=request.getSession();
        if (on1 ==null && on2== null && on3==null){
            return -1;
        }
        else{

            if(nn1.equals(nn2) || nn1.equals(nn3) ){
                return -2;
            }
            else{
                if (on1!=null){
                    session.setAttribute(on1,nn1);
                    if(nn1==null){
                        nn1=on1;
                    }
                }
                if (on2!=null){
                    session.setAttribute(on2,nn2);
                    if(nn2==null){
                        nn2=on2;
                    }
                }
                if (on3!=null){
                    session.setAttribute(on3,nn3);
                    if(nn3==null){
                        nn3=on3;
                    }
                }
                return 1;
            }
        }

    }
}
