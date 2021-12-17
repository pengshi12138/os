package com.niuniukeaiyouhaochi.os.model;

//   打开的文件类
public class OpenFile {
    //
    private int flag;
    //    文件对象
    private File file;

    public OpenFile() {
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    /**
     * 获取打开的文件对象
     *
     * @return file
     */
    public File getFile() {
        return file;
    }

    /**
     * 设置打开的文件对象
     *
     * @param file
     */
    public void setFile(File file) {
        this.file = file;
    }
}
