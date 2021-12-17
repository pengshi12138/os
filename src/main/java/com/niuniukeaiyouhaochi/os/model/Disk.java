package com.niuniukeaiyouhaochi.os.model;

//    磁盘类 c盘
public class Disk {
//    磁盘名称
    private String diskName;

    public Disk(){}

    public Disk(String diskName) {
        this.diskName = diskName;
    }

    public String getDiskName() {
        return this.diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    @Override
    public String toString() {
        return this.diskName;
    }
}
