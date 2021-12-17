package com.niuniukeaiyouhaochi.os.model;

import java.text.SimpleDateFormat;
import java.util.Date;

//文件类
public class File {
//    文件名称
    private String fileName;
//    文件类型 写读、读
    private String type;
//    文件结构
    private String property;
//    文件内容
    private String content;
//    父目录
    private Folder parent;
//    父路径
    private String location;
//    时间
    private Date createTime;
//    时间
    private Date reviseTime;
//    是否隐藏
    private boolean isHide;
//    在盘块的位置
    private int diskNum;
//    文件文本长度 getText().length()  单位字符
    private int length;
//    文本文件所占磁盘的个数
    private int numOfFAT;
//    文件的字节大小
    private int size;

//    颜色下标
    private int colorIndex;

    private String space;

    public File(){}

    public File(String fileName) {
        this.fileName = fileName;
    }

    public File(String fileName,String location,int diskNum,int colorIndex) {
        this.fileName = fileName;
        this.location = location;
        this.size = 0;
        this.space = "100KB";
        this.createTime = new Date();
        this.reviseTime = new Date();
        this.diskNum = diskNum;
        this.type= "写读";
        this.isHide = false;
        this.length = 0;
        this.numOfFAT=1;
        this.content = "";
        this.colorIndex=colorIndex;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReviseTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
        return format.format(this.reviseTime);
    }


    public void setReviseTime() {
        this.reviseTime = new Date();
    }

    public String getCreateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
        return format.format(this.createTime);
    }

    public void setCreateTime() {
        this.createTime = new Date();
    }


    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public int getDiskNum() {
        return diskNum;
    }

    public void setDiskNum(int diskNum) {
        this.diskNum = diskNum;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getNumOfFAT() {
        return numOfFAT;
    }

    public void setNumOfFAT(int numOfFAT) {
        this.numOfFAT = numOfFAT;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    @Override
    public String toString() {
        return this.fileName;
    }

}
