package com.niuniukeaiyouhaochi.os.model;

import javafx.scene.control.TreeItem;

import java.text.SimpleDateFormat;
import java.util.Date;

//文件夹类
public class Folder {
    //    文件夹名称
    private String folderName;
    //    文件夹类型
    private String type;
    //    文件夹结构
    private String property;
    //    文件夹子孩子的个数(最多8个)
    private int childNum;
    //    父路径
    private String location;
    //    时间
    private Date createTime;
    //    时间
    private Date reviseTime;
    //    是否只读
    private boolean isReadOnly;
    //    是否隐藏
    private boolean isHide;
    //    在盘块的位置
    private int diskNum;
    //    文件的字节大小
    private int size;
    private String space;
    //    文本文件所占磁盘的个数
    private int numOfFAT;
    //    颜色下标
    private int colorIndex;
//    对应的树节点
    private TreeItem<String> folderTreeItem;

    public Folder() {
    }

    public Folder(String folderName) {
        this.folderName = folderName;
    }

    public Folder(String folderName, String location, int diskNum,int colorIndex) {
        this.folderName = folderName;
        this.location = location;
        this.size = 0;
        this.numOfFAT=1;
        this.space = "100KB";
        this.createTime = new Date();
        this.reviseTime = new Date();
        this.diskNum = diskNum;
        this.type = "写读";
        this.isReadOnly = false;
        this.isHide = false;
        this.childNum = 0;
        this.colorIndex=colorIndex;
    }

    public TreeItem<String> getFolderTreeItem() {
        return folderTreeItem;
    }

    public void setFolderTreeItem(TreeItem<String> folderTreeItem) {
        this.folderTreeItem = folderTreeItem;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public String getFolderName() {
        return this.folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public int getChildNum() {
        return this.childNum;
    }

    public void setChildNum(int childNum) {
        this.childNum = childNum;
    }

    public String getLocation() {
        return this.location;
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

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
    }

    public boolean isHide() {
        return this.isHide;
    }

    public void setHide(boolean hide) {
        this.isHide = hide;
    }

    public int getDiskNum() {
        return this.diskNum;
    }

    public void setDiskNum(int diskNum) {
        this.diskNum = diskNum;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSpace() {
        return this.space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public int getNumOfFAT() {
        return this.numOfFAT;
    }

    public void setNumOfFAT(int numOfFAT) {
        this.numOfFAT = numOfFAT;
    }

    public boolean hasChild() {
        return this.childNum > 0;
    }

    //    所含文件数量
    public boolean addChildNum() {
        if (this.childNum < 8) {
            this.childNum++;
            return true;
        }
        return false;
    }

    public boolean decChildNum() {
        if (this.childNum > 0) {
            this.childNum--;
            return true;
        }
        return false;
    }

    public String toString() {
        return this.folderName;
    }
}
