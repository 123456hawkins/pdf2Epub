package com.example.pdfconvertepub.ocr;

import com.google.common.collect.Lists;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * ocr用于识别英文pdf
 */
public class OcrEng {
    private File imgsDir;
    private File output;
    public File TextStylePack;
    public static String tessDataPathString="C:\\Users\\Hawki\\Desktop\\tessOCRMODEL";

    public OcrEng(File imgsDir, File output) throws IOException{
        this.imgsDir=imgsDir;
        this.output=output;
        TextStylePack=new File(output.getAbsolutePath()+"\\textStyle");
        TextStylePack.mkdir();
    }

    public void create() throws IOException, TesseractException {
        Tesseract tesseract=new Tesseract();
        tesseract.setDatapath(tessDataPathString);
        tesseract.setLanguage("chi_sim");
        int count=0;
        for (File file:listFiles()){
            String pdfInfo = tesseract.doOCR(file);
            FileWriter writer=null;
            String textPath=TextStylePack.getAbsolutePath()+String.format("\\pageText_%d.txt",count);
            File Text=new File(textPath);
            writer=new FileWriter(Text);
            writer.write(pdfInfo);
            writer.flush();;
            writer.close();
            count++;
        }


    }

    private List<File> listFiles(){
        File[] files = imgsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        List<File> sorted = Lists.newArrayList(files);
        sorted.sort(Comparator.comparing(File::toString));
        return sorted;
    }
}
