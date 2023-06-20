package com.example.pdfconvertepub.convert.epub;

public class ImgReader {
    private String name;
    private Integer page;
    private Float width;
    private Float height;
    private Float x;
    private Float y;

    public String getName() {
        return name;
    }

    public Integer getPage() {
        return page;
    }

    public Float getWidth() {
        return width;
    }

    public Float getHeight() {
        return height;
    }

    public Float getX() {
        return x;
    }

    public Float getY() {
        return y;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public void setY(Float y) {
        this.y = y;
    }
}
