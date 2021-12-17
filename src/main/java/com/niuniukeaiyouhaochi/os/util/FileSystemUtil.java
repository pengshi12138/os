package com.niuniukeaiyouhaochi.os.util;

public class FileSystemUtil {
//    D:\大三上学期\操作系统实验\源码\test03\DPYos\src\main\resources\img\folder.png D:\大三上学期\操作系统实验\源码\test03\DPYos\src\main\resources\img\filePic.png
    public static int num = 5;
    public static String folderPath = "/img/folder.png";
    public static String folderTreePath = "/img/folderPic.png";
    public static String filePath = "/img/filePic.png";
    public static String diskPath = "/img/diskPic.png";
    public static String delPath = "/img/del.png";
    public static String warnPath ="/img/warn.png";
    //    表示该磁盘块已结束
    public static int END = 255;
//    表示该磁盘块空闲
    public static int SPARE = 0;
//    表示该磁盘块损坏
    public static int BAD = 254;
    //    表示该磁盘块存放c盘
    public static int DISK = 0;
    //    表示该磁盘块存放文件夹
    public static int FOLDER = 1;
    //    表示该磁盘块存放文件
    public static int FILE = 2;
    //     错误返回值
    public static int ERROR = -1;
    public static int flagRead = 0;
    public static int flagWrite = 1;

    public FileSystemUtil() {
    }

    //    根据文本内容的长度获取所占磁盘的块数 64-》一磁盘
    public static int getNumOfFAT(int length) {
        if (length <= 64) {
            return 1;
        } else {
            int n = 0;
            if (length % 64 == 0) {
                n = length / 64;
            } else {
                n = length / 64;
                n++;
            }
            return n;
        }
    }

}
