package com.example.pdfconvertepub.convert.epub;


import com.example.pdfconvertepub.controller.DownLoadController;
import com.example.pdfconvertepub.dao.ErrorLogMapper;
import com.example.pdfconvertepub.domain.ErrorLog;
import com.google.common.collect.Lists;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EpubCreatorText {//带图片和文字的pdf转化

    @Autowired
    private ErrorLogMapper errorLogMapper;

    private String timestamp;
    private String uuid;
    private File basedir;
    private ClassLoader classLoader;
    private String title;
    private File imgsDir;
    private File textStyleDir;
    private File imgsStyleDir;
    private Integer pageNumber;
    private File bookmark;
    private File outputDir;
    private String pdfName;
    public void create(String title, File input, File output, Integer pageNumber,String Name,HttpServletRequest request) throws IOException {
        this.pageNumber=pageNumber;
        timestamp = DateTimeFormat.forPattern("yyyy-MM-dd'T'hh:mm:ssSZZ").print(DateTime.now());
        uuid = UUID.randomUUID().toString();
        this.title = title;
        this.imgsDir = new File(input.getAbsolutePath()+"/img");//图片
        this.textStyleDir =new File(input.getAbsolutePath()+"/textStyle");//文字样式
        this.imgsStyleDir =new File(input.getAbsolutePath()+"/imgstyle");//图片样式
        this.bookmark=new File(input.getAbsolutePath()+"/bookmark.txt");
        this.pdfName=Name;
        this.outputDir=output;
        try {
            basedir = File.createTempFile(uuid,"");
            basedir.delete();
            basedir.mkdirs();

        } catch (IOException e) {
            e.printStackTrace();
            ErrorLog errorLog = new ErrorLog();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            errorLog.setCallName("create");
            errorLog.setCallFunctionFullName("com.example.pdfconvertepub.convert.epub.EpubCreatorText");
            errorLog.setExceptionMessage(sw.toString());
            errorLogMapper.insert(errorLog);
        }
        classLoader = getClass().getClassLoader();

        writeMimetype();
        copyImages();//复制图片
        copyStandardFilez();
        createIndex();
        createOPFFile();
        createTOC();

        new_pack(basedir.getAbsolutePath(), request);//epub的打包

        copyEpubToOutput(request,outputDir);
//        FileUtils.deleteDirectory(basedir);
    }
    private void copyEpubToOutput(HttpServletRequest request,File outputDir) throws IOException{
        HttpSession session=request.getSession();
        String newname=session.getAttribute(pdfName).toString();
        FileInputStream Fin=new FileInputStream(new File(basedir.getAbsolutePath()+"/done.epub"));
        FileOutputStream Fout=new FileOutputStream(new File(outputDir.getAbsolutePath()+"/"+newname+".epub"));

        DownLoadController.fileDictory=outputDir.getAbsolutePath()+"/";
        IOUtils.copy(Fin,Fout);
        Fin.close();
        Fout.close();
    }
    private void copyImages() throws IOException {
        File ImgPack=new File(basedir+"/img");
        ImgPack.mkdir();
        for(File file : listFiles()){
            FileInputStream Fin=new FileInputStream(file);
            FileOutputStream Fout=new FileOutputStream(new File(ImgPack, file.getName()));
            IOUtils.copy(Fin, Fout);
            Fin.close();
            Fout.close();
        }
    }

    private void copyStandardFilez() throws IOException {
        File metainf = new File(basedir, "META-INF");
        metainf.mkdirs();
        writeFile(new File(metainf, "container.xml"), readFileFromSrc("static/epub/META-INF/container.xml"));

        writeFile(new File(basedir, "page_styles.css"), readFileFromSrc("static/epub/page_styles.css"));
        writeFile(new File(basedir, "stylesheet.css"), readFileFromSrc("static/epub/stylesheet.css"));

    }
    private void writeMimetype() throws IOException{
        writeFile(new File(basedir, "mimetype"), readFileFromSrc("static/epub/mimetype"));
    }
    private void createOPFFile() throws IOException {
        StringBuilder content = new StringBuilder();
        StringBuilder content1 = new StringBuilder();
        for(File file : listFiles()){
            content.append(String.format("<item href=\"img/%s\" id=\"%s\" media-type=\"image/png\"/>\n", file.getName(), idForImage(file.getName())));
        }
        for (File file:listFileIndex())
        {
            content.append(String.format("<item href=\"%s\" id=\"%s\" media-type=\"application/xhtml+xml\"/>\n", file.getName(), idForIndex(file.getName())));
            content1.append(String.format("<itemref idref=\"%s\"/>\n",idForIndex(file.getName())));
        }
        String opf = readFileFromSrc("static/epub/content.opf");
        opf = opf
                .replace("$AUTHOR", "Unknown")
                .replace("$TIMESTAMP", timestamp)
                .replace("$TITLE", title)
                .replace("$UUID", uuid)
                .replace("$CONTENT", content.toString())
                .replace("$NCX", content1.toString());
        writeFile(new File(basedir, "content.opf"), opf);
    }

    private void createIndex() throws IOException {
        List<TextReader> textReaders= new ArrayList<TextReader>();
        List<ImgReader> imgReaders=new ArrayList<ImgReader>();
        try {//讲文字样式读出放入List中并按页数分割list
            //读文字专用
            String[] readArray;
            String str;
            File file = new File(textStyleDir.getAbsolutePath()+"/pageTextStyle.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            //读图片格式专用
            String[] readArray1;
            String str1;
            File file1=new File(imgsStyleDir.getAbsolutePath()+"/imgStyle.txt");
            BufferedReader br1=new BufferedReader(new FileReader(file1));
            while((str1=br1.readLine())!=null){
                readArray1=str1.split("[*]");

                ImgReader imgReader=new ImgReader();
                imgReader.setName(readArray1[0]);
                imgReader.setPage(Integer.valueOf(readArray1[1]));
                imgReader.setWidth(Float.valueOf(readArray1[2]));
                imgReader.setHeight(Float.valueOf(readArray1[3]));
                imgReader.setX(Float.valueOf(readArray1[4]));
                imgReader.setY(Float.valueOf(readArray1[5]));

                imgReaders.add(imgReader);
            }
            while ((str=br.readLine())!=null){

                readArray=str.split("[*]");

                TextReader textReader=new TextReader();
                textReader.setX(Float.valueOf(readArray[0]));
                textReader.setY(Float.valueOf(readArray[1]));
                textReader.setFontSize(Float.valueOf(readArray[2]));
                textReader.setHeight(Float.valueOf(readArray[3]));
                textReader.setWidth(Float.valueOf(readArray[4]));
                textReader.setPagenumber(Integer.valueOf(readArray[5]));
                textReader.setFont(readArray[6]);

                textReaders.add(textReader);
            }


        }catch (Exception e){
            e.printStackTrace();
            ErrorLog errorLog = new ErrorLog();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            errorLog.setCallName("createIndex");
            errorLog.setCallFunctionFullName("com.example.pdfconvertepub.convert.epub.EpubCreatorText");
            errorLog.setExceptionMessage(sw.toString());
            errorLogMapper.insert(errorLog);
        }

        int temp1=1;
        int temp2=1;
        for (int j = 1;; j++) {
            StringBuilder content = new StringBuilder();
            content.append(String.format("<div style=\"position:relative;\">\n"));
            for (int i = temp1; i <=textReaders.size(); i++) {
                int pageNumber=textReaders.get(i-1).getPagenumber();
                if (pageNumber==j){
                    content.append(String.format("<span style=\"-webkit-transform:scale(0.833);left:%spx;top:%spx;font-size:%spx;margin-right:2px;height:%spx;width:%spx\">%s</span>\n"
                            , textReaders.get(i-1).getX(), textReaders.get(i-1).getY(), textReaders.get(i-1).getFontSize(), textReaders.get(i-1).getHeight(),
                            textReaders.get(i-1).getWidth()+3,
                            textReaders.get(i-1).getFont()));

                }else if(pageNumber!=j) {
                    temp1=i;
                    break;
                }
                if(i==textReaders.size()){
                    temp1=i+1;
                    break;
                }

            }

            for (int i = temp2; i <=imgReaders.size();i++) {

                int pageNumber=imgReaders.get(i-1).getPage();
                if (pageNumber==j){
                    content.append(String.format("<img src=\"img/%s\" style=\"left:%spx;top:%spx; height:%spx;width:%spx\"></img>\n"
                            ,imgReaders.get(i-1).getName(), imgReaders.get(i-1).getX(),
                            imgReaders.get(i-1).getY(), imgReaders.get(i-1).getHeight(), imgReaders.get(i-1).getWidth()));
                }else if(pageNumber!=j) {
                    temp2=i;
                    break;
                }
                if(i==imgReaders.size()){
                    temp2=i+1;
                    break;
                }

            }

            content.append(String.format("</div>\n"));
            String index = readFileFromSrc("static/epub/index.html");
            index = index.replace("$CONTENT", content.toString());

            writeFile(new File(basedir, String.format("index_%d.html", j-1)), index);
            if(temp1 <=textReaders.size()||temp2<=imgReaders.size()){
                continue;
            }else {
                break;
            }

        }
    }
    private void createOcrIndex() throws IOException {
        List<OcrTextReader> ocrTextReaders= new ArrayList<OcrTextReader>();
        try {//讲文字样式读出放入List中并按页数分割list
            //读文字专用
            //读ocr文字专用
            String str2;
            int page=1;
            for(File listFile:listOcrImage()){
                File file2 = listFile;
                BufferedReader br2 = new BufferedReader(new FileReader(file2));//构造一个BufferedReader类来读取文件

                while((str2=br2.readLine())!=null){

                    OcrTextReader ocrTextReader1=new OcrTextReader();
                    ocrTextReader1.setLine(str2);
                    ocrTextReader1.setPagenumber(page);
                    ocrTextReaders.add(ocrTextReader1);

                }
                page++;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        int temp2=1;
        for (int j = 1;; j++) {
            StringBuilder content = new StringBuilder();
            int i=temp2;
            content.append(String.format("<div style=\"position:relative;font-size:0.7em\">\n"));
            for (; i <= ocrTextReaders.size(); i++) {
                int pageNumber=ocrTextReaders.get(i-1).getPagenumber();
                temp2=i;
                if (pageNumber==j){
                    content.append(String.format("<p style=\"line-height:0.1\">%s</p>\n"
                            , ocrTextReaders.get(i-1).getLine()));
                }else {
                    break;
                }

            }

            content.append(String.format("</div>\n"));
            String index = readFileFromSrc("static/epub/index.html");
            index = index.replace("$CONTENT", content.toString());

            writeFile(new File(basedir, String.format("index_%d.html", j-1)), index);
            if(i>ocrTextReaders.size()){
                break;
            }

        }
    }
    private void createTOC() throws IOException {
        //读目录信息专用
        String[] readArray;
        String str;
        if (bookmark.exists()){
            BufferedReader br = new BufferedReader(new FileReader(bookmark));//构造一个BufferedReader类来读取文件
            BufferedReader br1 = new BufferedReader(new FileReader(bookmark));//构造一个BufferedReader类来读取文件
            StringBuilder content = new StringBuilder();
            int index=1;
            String toc = readFileFromSrc("static/epub/toc.ncx");
            String str1 = br1.readLine();
            String[] readArray1=str1.split("[*]");
            int top1 = Integer.parseInt(readArray1[0]);
            while ((str=br.readLine())!=null) {
                readArray = str.split("[*]");
                if(index==1)
                {
                    content.append(String.format("<navPoint id=\"navpiont-%d\" playOrder=\"%d\">\n" +
                            "      <navLabel>\n" +
                            "        <text>%s</text>\n" +
                            "      </navLabel>\n" +
                            "      <content src=\"index_%s.html\"/>\n",index,index,readArray[1],readArray[2]));
                    index++;
                }
                else if(Integer.parseInt(readArray[0])== top1)
                {
                    content.append(String.format("</navPoint>\n"));
                    content.append(String.format("<navPoint id=\"navpiont-%d\" playOrder=\"%d\">\n" +
                            "      <navLabel>\n" +
                            "        <text>%s</text>\n" +
                            "      </navLabel>\n" +
                            "      <content src=\"index_%s.html\"/>\n",index,index,readArray[1],readArray[2]));
                    index++;
                }
                else{
                    content.append(String.format("<navPoint id=\"navpiont-%d\" playOrder=\"%d\">\n" +
                            "      <navLabel>\n" +
                            "        <text>%s</text>\n" +
                            "      </navLabel>\n" +
                            "      <content src=\"index_%s.html\"/>\n" +
                            "    </navPoint>\n",index,index,readArray[1],readArray[2]));
                    index++;
                }
            }
            content.append(String.format("</navPoint>\n"));
            toc = toc
                    .replace("$TITLE", title)
                    .replace("$UUID", uuid)
                    .replace("$BOOKMARK",content);
            writeFile(new File(basedir, "toc.ncx"), toc);
        }
        else{
            String toc = readFileFromSrc("static/epub/toc.ncx");
            toc = toc
                    .replace("$TITLE", title)
                    .replace("$UUID", uuid);
            writeFile(new File(basedir, "toc.ncx"), toc);
        }
    }

    private void writeFile(File dest, String content) throws IOException {
        FileWriter writer = new FileWriter(dest);
        writer.write(content);
        writer.flush();
        writer.close();
    }

    private String readFileFromSrc(String path) throws IOException {
        return IOUtils.toString(classLoader.getResourceAsStream(path));
    }

    private String idForImage(String name){
        return String.format("id%s", name.replace(".png", ""));
    }
    private String idForIndex(String name){
        return String.format("id%s", name.replace(".html", ""));
    }
    private String idForTxt(String name){
        return String.format("id%s", name.replace(".txt", ""));
    }
    public static byte[] getFileBytes(File file) throws FileNotFoundException,
            IOException {
        byte[] buffer;
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        byte[] b = new byte[1000];
        int n;
        while ((n = fis.read(b)) != -1) {
            bos.write(b, 0, n);
        }
        fis.close();
        bos.close();
        buffer = bos.toByteArray();
        return buffer;
    }

    private void new_pack(String sourceDirPath, HttpServletRequest request){
        try {

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(CompressionMethod.STORE);
        new ZipFile(sourceDirPath+"/done.epub").addFile(sourceDirPath+"/mimetype", zipParameters);
        new ZipFile(sourceDirPath+"/done.epub").addFile(new File(sourceDirPath+"/page_styles.css"));
        new ZipFile(sourceDirPath+"/done.epub").addFile(new File(sourceDirPath+"/stylesheet.css"));
        new ZipFile(sourceDirPath+"/done.epub").addFile(new File(sourceDirPath+"/content.opf"));
        new ZipFile(sourceDirPath+"/done.epub").addFolder(new File(sourceDirPath+"/META-INF"));
        new ZipFile(sourceDirPath+"/done.epub").addFile(new File(sourceDirPath+"/toc.ncx"));
        new ZipFile(sourceDirPath+"/done.epub").addFolder(new File(sourceDirPath+"/img"));
        for (File file:listFileIndex()){
            new ZipFile(sourceDirPath+"/done.epub").addFile(file.getAbsolutePath());
        }

        request.getSession().setAttribute("processvalue", 100);


        }catch (IOException e){
            e.printStackTrace();
            ErrorLog errorLog = new ErrorLog();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            errorLog.setCallName("new_pack");
            errorLog.setCallFunctionFullName("com.example.pdfconvertepub.convert.epub.EpubCreatorText");
            errorLog.setExceptionMessage(sw.toString());
            errorLogMapper.insert(errorLog);
        }
    }



    private List<File> listFiles(){
        File[] files = imgsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        List<File> sorted = Lists.newArrayList(files);
        sorted.sort(Comparator.comparing(File::toString));
        return sorted;
    }
    private List<File> listFileIndex(){
        File[] files = basedir.listFiles((dir, name) -> name.toLowerCase().endsWith(".html"));
        List<File> sorted = Lists.newArrayList(files);

        sorted.sort(Comparator.comparing(File::lastModified));
        return sorted;
    }
    private List<File> listFilesTxt(){
        File[] files = imgsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        List<File> sorted = Lists.newArrayList(files);
        sorted.sort(Comparator.comparing(File::toString));
        return sorted;
    }
    private List<File> listOcrImage(){
        File[] files = textStyleDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        List<File> sorted = Lists.newArrayList(files);
        sorted.sort(Comparator.comparing(File::lastModified));
        return sorted;
    }
}

