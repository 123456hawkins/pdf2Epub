package com.example.pdfconvertepub.convert;

import com.example.pdfconvertepub.convert.epub.EpubCreatorText;
import com.example.pdfconvertepub.convert.extract.ExtractPdf;
import com.example.pdfconvertepub.convert.extract.PrintBookmarks;
import com.example.pdfconvertepub.convert.extract.PrintTextLocations;
import com.example.pdfconvertepub.dao.ErrorLogMapper;
import com.example.pdfconvertepub.domain.ErrorLog;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class PdfConverter {
    private static final int RESOLUTION = 300;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;
    private File pdf;
    private Integer pageNumber;

    @Autowired
    private ErrorLogMapper errorLogMapper;
    private PdfConverter(File pdf) {
        this.pdf = pdf;
    }

    public static PdfConverter convert(File pdf){
        return new PdfConverter(pdf);
    }

    public void intoEpubNoneExtract(String title, File output, HttpServletRequest request){//提取文字和图片的转化方式
        try {
            PDDocument pdDocument=PDDocument.load(pdf);
            FileInputStream fis = null;

            pageNumber=pdDocument.getNumberOfPages();
            Path alldir = new File(String.format("/tmp/%s", UUID.randomUUID().toString())).toPath();
            Files.createDirectories(alldir);

            //提取部分
            ExtractPdf extractPdf=new ExtractPdf();//文字内容,图片
            extractPdf.extract(pdf,alldir.toFile(),request);

            PrintTextLocations printTextLocations=new PrintTextLocations(pdf,alldir.toFile());//文字格式
            printTextLocations.extractTextStyle();
            try//目录
            {
                fis = new FileInputStream(pdf);
                PDFParser parser = new PDFParser(new RandomAccessBuffer(fis));
                parser.parse();
                pdDocument = parser.getPDDocument();

                PrintBookmarks the = new PrintBookmarks();
                PDDocumentOutline outline =  pdDocument.getDocumentCatalog().getDocumentOutline();
                if( outline != null )
                {
                    the.printBookmark( outline,alldir.toFile()," " );
                }
                else
                {
                    System.out.println( "This document does not contain any bookmarks" );
                }
            }
            finally
            {
                if( fis != null ) fis.close();
                if( pdDocument != null ) pdDocument.close();
            }

            //转化部分
            EpubCreatorText epubCreatorText = new EpubCreatorText();
            epubCreatorText.create(title,alldir.toFile(),output,pageNumber,pdf.getName(),request);
        }catch (IOException e){
            e.printStackTrace();
            ErrorLog errorLog = new ErrorLog();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            errorLog.setCallName("intoEpubNoneExtract");
            errorLog.setCallFunctionFullName("com.example.pdfconvertepub.convert.PdfConverter");
            errorLog.setExceptionMessage(sw.toString());
            errorLogMapper.insert(errorLog);
        }

    }
}
