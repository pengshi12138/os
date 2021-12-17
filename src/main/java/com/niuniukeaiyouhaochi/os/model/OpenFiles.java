package com.niuniukeaiyouhaochi.os.model;

import com.niuniukeaiyouhaochi.os.util.FileSystemUtil;

import java.util.ArrayList;
import java.util.List;

// 打开的文件列表类
public class OpenFiles {
    //    打开的文件列表
    private List<OpenFile> files;
    //    列表中文件的数量
    private int length;

    public OpenFiles() {
//        默认新建一个长度为5的列表
        this.files = new ArrayList(FileSystemUtil.num);
//        列表中文件的个数
        this.length = 0;
    }

    //    添加已打开文件
    public void addFile(OpenFile openFile) {
        this.files.add(openFile);
    }

    //    删除已打开文件
    public void removeFile(OpenFile openFile) {
        this.files.remove(openFile);
    }

    //    获取已打开文件列表
    public List<OpenFile> getFiles() {
        return this.files;
    }

    //    赋值
    public void setFiles(List<OpenFile> files) {
        this.files = files;
    }


    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
