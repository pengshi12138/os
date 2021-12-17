package com.niuniukeaiyouhaochi.os.model;


import com.niuniukeaiyouhaochi.os.util.FileSystemUtil;

//    文件分配表类
public class FAT {
//    盘块号
    private int blockNum;
//    指向下一个FAT/磁盘的下标（0表示该磁盘空闲 254表示该磁盘损坏 255表示该文件已经结束）
    private int index;
//    存放的文件类型 -1表示文件空
    private int type;
//    存放的文件内容(文件夹、文件)
    private Object object;

    public FAT(int blockNum){
        this.blockNum=blockNum;
        this.index= FileSystemUtil.SPARE;
        this.type = -1;
        this.object=null;
    }


    public FAT(int index, int type, Object object,int blockNum) {
        this.blockNum=blockNum;
        this.index = index;
        this.type = type;
        this.object = object;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(int blockNum) {
        this.blockNum = blockNum;
    }
}
