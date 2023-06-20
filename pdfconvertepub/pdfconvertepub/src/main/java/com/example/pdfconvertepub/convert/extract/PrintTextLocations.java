package com.example.pdfconvertepub.convert.extract;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.example.pdfconvertepub.dao.ErrorLogMapper;
import com.example.pdfconvertepub.domain.ErrorLog;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This is an example on how to get some x/y coordinates of text.
 *
 * @author Ben Litchfield
 */
public class PrintTextLocations extends PDFTextStripper {


    public File input;
    public File output;
    public File TextStylePack;
    public String textPath;
    public File Text;//建立txt文件,每页对应一个txt
    public static List<String> lines = new ArrayList<String>();

    @Autowired
    private ErrorLogMapper errorLogMapper;
    public PrintTextLocations(File input,File output) throws IOException {
        this.input=input;
        this.output=output;
        TextStylePack=new File(output.getAbsolutePath()+"/textStyle");
    }

    public void extractTextStyle() throws IOException {
//        PDDocument document = null;
        try {
            TextStylePack.mkdir();
            PDDocument document = PDDocument.load(input);
            textPath=TextStylePack.getAbsolutePath()+String.format("/pageTextStyle.txt");
            Text=new File(textPath);

            Text.createNewFile();
            PDFTextStripper stripper = new PrintTextLocations(input,output);
            stripper.setSortByPosition(true);
            stripper.setStartPage(0);
            stripper.setEndPage(document.getNumberOfPages());
            Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
            stripper.writeText(document, dummy);

            BufferedWriter writer=null;
            FileWriter fileWriter=new FileWriter(Text);
            writer=new BufferedWriter(fileWriter);//将每页的文本写入txt文件


            for (String textInfo: lines) {
                writer.write(textInfo);
                writer.flush();//强制全部写出缓冲区
            }
            lines.clear();

        }catch (IOException e){
            e.printStackTrace();
            ErrorLog errorLog = new ErrorLog();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            errorLog.setCallName("extractTextStyle");
            errorLog.setCallFunctionFullName("com.example.pdfconvertepub.convert.extract.PrintTextLocations");
            errorLog.setExceptionMessage(sw.toString());
            errorLogMapper.insert(errorLog);
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    //getX()x坐标,getY()y坐标,getFontSize()获取文字尺寸,pagenumber出现页号,getUnicode()获取文字
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        for (TextPosition text : textPositions) {

            String textInfo=text.getX() + "*" + text.getY()+ "*" + text.getFontSize()+ "*" +  text.getHeightDir() + "*" + text.getWidthDirAdj()
                    +"*"+getCurrentPageNo()+ "*" + text.getUnicode()+"\n";
            lines.add(textInfo);
        }

    }

}
