package com.example.pdfconvertepub.convert.extract;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;

public class PrintBookmarks
{

    public static List<String> lines = new ArrayList<String>();
    /**
     *
     * @param bookmark The 书签 to print out.
     *
     * @throws IOException If there is an error getting the page count.
     */
    public void printBookmark( PDOutlineNode bookmark,File saveDir,String indentation) throws IOException
    {
        File bookMark=new File(saveDir.getAbsolutePath()+"/bookmark.txt");
        String bookMarkInfo=new String();
        PDOutlineItem current = bookmark.getFirstChild();

        while( current != null )
        {
            int pages =0;
            int index =0;
            if (current.getDestination() instanceof PDPageDestination)
            {

                PDPageDestination pd = (PDPageDestination) current.getDestination();
                pages = (pd.retrievePageNumber() +1);
                index = (indentation.length());
            }
            if (current.getAction() instanceof PDActionGoTo)
            {
                PDActionGoTo gta = (PDActionGoTo) current.getAction();
                if (gta.getDestination() instanceof PDPageDestination)
                {
                    PDPageDestination pd = (PDPageDestination) gta.getDestination();
                    pages = (pd.retrievePageNumber() +1);
                    index = (indentation.length());
                }
            }
            if (pages ==0){
                bookMarkInfo= index+"*"+current.getTitle()+"*"+pages+"\n";
                lines.add(bookMarkInfo);
            }
            else{
                bookMarkInfo=index+"*"+current.getTitle()+"*"+pages+"\n";
                lines.add(bookMarkInfo);

                //System.out.println( indentation +"   "+index+"     "+current.getTitle() +"  "+ pages);

            }

            printBookmark( current, saveDir, "   ");  // 递归调用
            current = current.getNextSibling();
        }
        //写文件
        BufferedWriter writer=null;
        writer=new BufferedWriter(new FileWriter(bookMark));//将每页的文本写入txt文件
        for (String textInfo: lines) {
            writer.write(textInfo);

            writer.flush();//强制全部写出缓冲区
        }
    }
}
