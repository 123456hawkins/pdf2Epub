package com.example.pdfconvertepub.convert.epub;

public class TextReader {
    private Float x;
    private Float y;
    private Float fontSize;
    private Float height;
    private Float width;
    private Integer pagenumber;
    private String font;

    public void setHeight(Float height) {
        this.height = height;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getHeight() {
        return height;
    }

    public Float getWidth() {
        return width;
    }


    public void setX(Float x) {
        this.x = x;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public void setFontSize(Float fontSize) {
        this.fontSize = fontSize;
    }

    public void setPagenumber(Integer pagenumber) {
        this.pagenumber = pagenumber;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public Float getX() {
        return x;
    }

    public Float getY() {
        return y;
    }

    public Float getFontSize() {
        return fontSize;
    }

    public Integer getPagenumber() {
        return pagenumber;
    }

    public String getFont() {
        return font;
    }
}
