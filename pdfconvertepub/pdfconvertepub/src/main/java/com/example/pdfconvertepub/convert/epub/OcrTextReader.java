package com.example.pdfconvertepub.convert.epub;

/**
 * @ClassName: OcrTextReader
 * @Description:
 * @author: 绅士的告白
 * @date: 2022/5/6 17:56
 * @Blog: 暂无
 */

public class OcrTextReader {
        /*private Float x;
        private Float y;
        private Float fontSize;
        private Float height;
        private Float width;*/
        private Integer pagenumber;
        private String line;

    public Integer getPagenumber() {
        return pagenumber;
    }

    public void setPagenumber(Integer pagenumber) {
        this.pagenumber = pagenumber;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
