package com.example.pdfconvertepub.Service;

import com.example.pdfconvertepub.dao.ErrorLogMapper;
import com.example.pdfconvertepub.domain.ErrorLog;
import com.spire.pdf.exporting.xps.schema.EdgeMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName: DownloadService
 * @Description:
 * @author: 绅士的告白
 * @date: 2022/3/5 22:21
 * @Blog: 暂无
 */
@Service
public class DownloadService {
    /**
     * 获取路径下的所有文件/文件夹
     * @param directoryPath 需要遍历的文件夹路径
     * @param isAddDirectory 是否将子文件夹的路径也添加到list集合中
     * @return
     */
    @Autowired
    private ErrorLogMapper errorLogMapper;
    public List<File> getAllFile(String directoryPath, boolean isAddDirectory) {
        List<File> list = new ArrayList<File>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if(isAddDirectory){
                    list.add(new File(file.getAbsolutePath()));
                }
                list.addAll(getAllFile(file.getAbsolutePath(),isAddDirectory));
            } else {
                list.add(new File(file.getAbsolutePath()));
            }
        }
        return list;
    }


    public void zip(List<File> fileList,String zipFileName,String fileDictory) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        BufferedInputStream bufferInputStream = null;
        try {
            // zipFileName为压缩文件的名称（xx.zip），首先在某个目录下（C:/temp/路径可以根据自己的需求进行修改）创建一个.zip结尾的文件
            fileOutputStream = new FileOutputStream(new File(fileDictory + zipFileName));
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
            // 创建读写缓冲区
            byte[] bufs = new byte[1024 * 10];

            for (File file : fileList) {
                // 创建ZIP实体，并添加进压缩包
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOutputStream.putNextEntry(zipEntry);

                // 读取待压缩的文件并写进压缩包里
                fileInputStream = new FileInputStream(file);
                bufferInputStream = new BufferedInputStream(fileInputStream, 1024 * 10);
                int read = 0;
                while ((read = bufferInputStream.read(bufs, 0, 1024 * 10)) != -1) {
                    zipOutputStream.write(bufs, 0, read);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            ErrorLog errorLog = new ErrorLog();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            errorLog.setCallName("zip");
            errorLog.setCallFunctionFullName("com.example.pdfconvertepub.Service.DownloadService");
            errorLog.setExceptionMessage(sw.toString());
            errorLogMapper.insert(errorLog);
        } finally {
            try {
                if (bufferInputStream != null) {
                    bufferInputStream.close();
                }
                if (zipOutputStream != null) {
                    zipOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                ErrorLog errorLog = new ErrorLog();
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                errorLog.setCallName("zip/finally");
                errorLog.setCallFunctionFullName("com.example.pdfconvertepub.Service.DownloadService");
                errorLog.setExceptionMessage(sw.toString());
                errorLogMapper.insert(errorLog);
            }

        }
    }
    public void downloadZip(HttpServletResponse response, String zipFileName, String fileDictory) {
        // zipName为上一步文件打包zip时传入的zipName
        File zipFile = new File(fileDictory + zipFileName);
        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);

        FileInputStream fileInputStream = null;
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            fileInputStream = new FileInputStream(zipFile);
            byte[] bufs = new byte[1024 * 10];
            int read = 0;
            while ((read = fileInputStream.read(bufs, 0, 1024 * 10)) != -1) {
                outputStream.write(bufs, 0, read);
            }
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog errorLog = new ErrorLog();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            errorLog.setCallName("downloadZip");
            errorLog.setCallFunctionFullName("com.example.pdfconvertepub.Service.DownloadService");
            errorLog.setExceptionMessage(sw.toString());
            errorLogMapper.insert(errorLog);
        }finally {
            try {
                // 删除压缩包
                File file = new File(fileDictory + zipFileName);
                file.delete();

                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                ErrorLog errorLog = new ErrorLog();
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                errorLog.setCallName("downloadZip/finally");
                errorLog.setCallFunctionFullName("com.example.pdfconvertepub.Service.DownloadService");
                errorLog.setExceptionMessage(sw.toString());
                errorLogMapper.insert(errorLog);
            }
        }
    }




}
