package com.example.pdfconvertepub.convert.extract;

import com.example.pdfconvertepub.dao.ErrorLogMapper;
import com.example.pdfconvertepub.domain.ErrorLog;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.exporting.PdfImageInfo;

import java.awt.geom.Rectangle2D;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;

public class ExtractPdf {

    @Autowired
    private ErrorLogMapper errorLogMapper;
    //水映信息
    private static final String shuiyin = "  Evaluation Warning : The document was created with Spire.PDF for Java.";

    public void extract(File input,File output, HttpServletRequest request)  {
        try {
            List<Map<String, String>> lms = new ArrayList<Map<String,String>>();
            Map<String, String> map_re = new HashMap<String, String>();

            PdfDocument doc = new PdfDocument();
            doc.loadFromFile(input.getAbsolutePath());
            StringBuilder sb = new StringBuilder();
            PdfPageBase page;
            int index=0;
            int imgNumber=0;
            File ImgPack=new File(output.getAbsolutePath()+"/img");
            ImgPack.mkdir();//图片文件夹
            File TextPack=new File(output.getAbsolutePath()+"/text");
            TextPack.mkdir();//文子文件夹
            File ImgStylePack=new File(output.getAbsolutePath()+"/imgstyle");
            ImgStylePack.mkdir();//图片信息文件夹
            int totalpage=doc.getPages().getCount();

            for (int i = 0; i < doc.getPages().getCount(); i++) {
                //获取每一行的page对象
                page = doc.getPages().get(i);
//                System.out.println(page.extractText(true));
                sb.append(page.extractText(true));//往sb对象添加文本

                request.getSession().setAttribute("processvalue", (i+1)*99/(totalpage));
                //System.out.println(sb);

                //文字的解析输出
                final String pdfInfo=sb.toString().replace(shuiyin,"");
                try {
                    //文字内容的解析输出
                    FileWriter writer=null;
                    String textPath=TextPack.getAbsolutePath()+String.format("/pageText_%d.txt",i+1);
                    File Text=new File(textPath);//建立txt文件,每页对应一个txt
                   /* if(Text.exists())
                        Text.delete();*/
                    Text.createNewFile();

                    writer=new FileWriter(Text);//将每页的文本写入txt文件

                    writer.write(pdfInfo);
                    writer.flush();//强制全部写出缓冲区
                    writer.close();
                    sb.delete(0,sb.length());//清空sb对象内的所有内容
                }catch (IOException e){
                    e.printStackTrace();
                    ErrorLog errorLog = new ErrorLog();
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    errorLog.setCallName("extract/write");
                    errorLog.setCallFunctionFullName("com.example.pdfconvertepub.convert.extract");
                    errorLog.setExceptionMessage(sw.toString());
                    errorLogMapper.insert(errorLog);
                }
                File imgStyle=new File(ImgStylePack.getAbsolutePath()+"/imgStyle.txt");
                imgStyle.createNewFile();
                //图片的解析输出
                if (page.extractImages() != null) {
                    //输出图片文件
                    for (BufferedImage image : page.extractImages()) {
                        if (image != null) {
                            //指定输出图片名，指定图片格式,后缀自己换
                            File Img = new File(ImgPack.getAbsolutePath()+String.format("/image_%d.png",index++));
                            ImageIO.write(image, "PNG", Img);
                        }
                    }

                    //输出图片的格式信息
                    PdfImageInfo[] imageInfo=page.getImagesInfo();

                    for (int k=0;k<imageInfo.length;k++){
                        //获取指定图片的边界属性
                        Rectangle2D rect = imageInfo[k].getBounds();
                        //图片的信息内容:页数,宽,高,左上角x坐标，左上角y坐标
                        int pagenumber=i+1;
                        String ImgInfo=String.format("image_%d.png",imgNumber++)+"*"+pagenumber+"*"+rect.getWidth()+"*"+rect.getHeight()+"*"+rect.getX()+"*"+rect.getY()+"\n";
                        FileWriter imgStylewriter=null;
                        imgStylewriter=new FileWriter(imgStyle,true);//以追加的方式写入文件
                        imgStylewriter.write(ImgInfo);
                        imgStylewriter.flush();
                        imgStylewriter.close();
                    }
                }


            }
//            System.out.println(sb);

//            System.out.println(pdfInfo);
            //提取文字格式

        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog errorLog = new ErrorLog();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            errorLog.setCallName("extract");
            errorLog.setCallFunctionFullName("com.example.pdfconvertepub.convert.extract.ExtractPdf");
            errorLog.setExceptionMessage(sw.toString());
            errorLogMapper.insert(errorLog);
        }

    }

}
