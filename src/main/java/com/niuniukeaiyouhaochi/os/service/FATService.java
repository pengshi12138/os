package com.niuniukeaiyouhaochi.os.service;

import com.niuniukeaiyouhaochi.os.model.*;
import com.niuniukeaiyouhaochi.os.util.FileSystemUtil;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

//    文件分配表服务类
public class FATService {
    //    文件分配表列表
    private static FAT[] myFAT;
    //    已打开的文件列表对象{length,files}
    private static OpenFiles openFiles;
    //    颜色列表
    private static ArrayList<Color> colorArrayList;

    public FATService() {
        openFiles = new OpenFiles();
    }

    /**
     * 获取文件分配表
     */
    public static FAT[] getMyFAT() {
        return myFAT;
    }

    /**
     * 设置文件分配表
     *
     * @param myFAT
     */
    public static void setMyFAT(FAT[] myFAT) {
        FATService.myFAT = myFAT;
    }

    /**
     * 获取指定文件分配表
     *
     * @param index
     * @return
     */
    public static FAT getFAT(int index) {
        return myFAT[index];
    }

    /**
     * 获取已打开的文件列表对象
     *
     * @return
     */
    public static OpenFiles getOpenFiles() {
        return openFiles;
    }

    /**
     * 设置已打开的文件列表对象
     *
     * @param openFiles
     */
    public static void setOpenFiles(OpenFiles openFiles) {
        FATService.openFiles = openFiles;
    }

    /**
     * 添加已打开的文件
     */
    public void addOpenFile(FAT fat, int flag) {
        OpenFile openFile = new OpenFile();
        openFile.setFile((File) fat.getObject());
        openFile.setFlag(flag);
        openFiles.addFile(openFile);
    }

    /**
     * 移除已打开的文件
     */
    public void removeOpenFile(FAT fat) {
        for (int i = 0; i < openFiles.getFiles().size(); i++) {
            if (openFiles.getFiles().get(i).getFile() == fat.getObject()) {
                openFiles.getFiles().remove(i);
            }
        }
    }

    /**
     * 检查该文件是否打开
     */
    public boolean checkOpenFile(FAT fat) {
        for (int i = 0; i < openFiles.getFiles().size(); i++) {
            if (openFiles.getFiles().get(i).getFile() == fat.getObject()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 初始化磁盘块分配状态
     */
    public void initFAT() {
        myFAT = new FAT[128];
//      初始化文件分配表所占的磁盘块 一个文件分配表占两个盘块
        myFAT[0] = new FAT(FileSystemUtil.END, FileSystemUtil.DISK, null, 0);
        myFAT[1] = new FAT(FileSystemUtil.END, FileSystemUtil.DISK, null, 1);
//      初始化c盘所占的磁盘块
        myFAT[2] = new FAT(FileSystemUtil.END, FileSystemUtil.DISK, new Disk("D"), 2);
        for (int i = 3; i < 128; i++) {
//            磁盘默认空闲
            myFAT[i] = new FAT(i);
        }

//        初始化颜色分配表 23种
        colorArrayList = new ArrayList<>();
        int n ;
        char[] chars ={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        for (int i = 0; i < 128; i++) {
            String s ="";
            for (int j=0;j<6;j++){
                n = (int) (Math.random() * (15));
                s=s+chars[n];
            }
            Color color = new Color("#"+s, false);
            colorArrayList.add(color);
        }
        System.out.println(colorArrayList.size());
    }

    /**
     * 随机获取颜色下标
     */
    public int getColorIndex() {
        int n = (int) (Math.random() * (colorArrayList.size()-1));
        System.out.println(n);
        int i;
        for ( i= 0; i < colorArrayList.size(); i++) {
            if(!colorArrayList.get(i).getUsed()){
                break;
            }
        }
        if(i==colorArrayList.size()){
            n = (int) (Math.random() * (colorArrayList.size()-1));
            return n;
        }
        while (colorArrayList.get(n).getUsed()) {
            n = (int) (Math.random() * (colorArrayList.size()-1));
        }
        colorArrayList.get(n).setUsed(true);
        return n;
    }

    /**
     * 设置颜色为未使用
     */
    public void setColorIndex(int colorIndex) {
        colorArrayList.get(colorIndex).setUsed(false);
    }

    /**
     * 获取颜色
     */
    public String getColor(int colorIndex) {
        return colorArrayList.get(colorIndex).getColor();
    }

    /**
     * 创建文件夹
     */
    public int creatFolder(String path) {
        String folderName = null;
        boolean canName = true;
        int index = 1;
        Folder folder;
//        文件夹默认命名
        do {
            folderName = "新建文件夹";
            canName = true;
            folderName = folderName + index;

            for (int i = 0; i < myFAT.length; i++) {
                int index1 = myFAT[i].getIndex();
                if (index1 != FileSystemUtil.SPARE && index1 != FileSystemUtil.BAD && myFAT[i].getType() == FileSystemUtil.FOLDER) {
                    folder = (Folder) myFAT[i].getObject();
//                    重新遍历
                    if (path.equals(folder.getLocation()) && folderName.equals(folder.getFolderName())) {
//                        继续循环，
                        canName = false;
                    }
                }
            }
            index++;
        } while (!canName);

        index = this.searchEmptyMyFAT();
//        磁盘分配错误
        if (index == FileSystemUtil.ERROR) {
            return FileSystemUtil.ERROR;
        } else {
            int colorIndex = getColorIndex();
            folder = new Folder(folderName, path, index, colorIndex);
            myFAT[index] = new FAT(FileSystemUtil.END, FileSystemUtil.FOLDER, folder, index);
            return index;
        }
    }

    /**
     * 创建文件
     */
    public int creatFile(String path) {
        String fileName = null;
        boolean canName = true;
        int index = 1;

        File file;
//        文件默认命名
        do {
            fileName = "新建文件";
            canName = true;
            fileName = fileName + index;

            for (int i = 0; i < myFAT.length; i++) {
                int index1 = myFAT[i].getIndex();
                if (index1 != FileSystemUtil.SPARE && index1 != FileSystemUtil.BAD && myFAT[i].getType() == FileSystemUtil.FILE) {
                    file = (File) myFAT[i].getObject();
//                    重新遍历
                    if (path.equals(file.getLocation()) && fileName.equals(file.getFileName())) {
//                        继续循环，
                        canName = false;
                    }
                }
            }
            index++;
        } while (!canName);

        index = this.searchEmptyMyFAT();
//        磁盘分配错误
        if (index == FileSystemUtil.ERROR) {
            return FileSystemUtil.ERROR;
        } else {
            int colorIndex = getColorIndex();
            file = new File(fileName, path, index, colorIndex);
            myFAT[index] = new FAT(FileSystemUtil.END, FileSystemUtil.FILE, file, index);
            return index;
        }
    }

    /**
     * 查找空的磁盘块下标
     */
    public int searchEmptyMyFAT() {
        for (int i = 0; i < myFAT.length; i++) {
            if (myFAT[i].getIndex() == FileSystemUtil.SPARE) {
                return i;
            }
        }
        return FileSystemUtil.ERROR;
    }

    /**
     * 磁盘已使用的个数
     */
    public int getNumOfFAT() {
        int n = 0;
        for (int i = 0; i < myFAT.length; i++) {
            if (myFAT[i].getIndex() != FileSystemUtil.SPARE && myFAT[i].getIndex() != FileSystemUtil.BAD) {
                n++;
            }
        }
        return n;
    }

    /**
     * 磁盘未被占用的个数(不包括坏的)
     */
    public int getSpaceOfFAT() {
        int n = 0;
        for (int i = 3; i < myFAT.length; i++) {
            if (myFAT[i].getIndex() == FileSystemUtil.SPARE) {
                n++;
            }
        }
        return n;
    }

    /**
     * 保存已修改的文件
     *
     * @param fat     文件对象
     * @param content 后来输入的文件内容
     */
    public boolean saveToModifyFATS2(FAT fat, String content) {
//        获取新的文本字节长度长度
        int length = content.getBytes().length;
        System.out.println("文本长度：" + length);
//        获取新的文本所占磁盘块数
        int num = FileSystemUtil.getNumOfFAT(length);
        System.out.println("所占磁盘块数：" + num);
//        获取原来文件所占的磁盘块数
        int oldNum = ((File) fat.getObject()).getNumOfFAT();
//        获取文件的初始化盘块下标
        int begin = ((File) fat.getObject()).getDiskNum();
//        该盘块指向的下个盘块下标
        int index = begin;
        int pre = 3;
//        磁盘块数量增多
        int n;
        if (num > oldNum) {
            n = num - oldNum;
            if (this.getSpaceOfFAT() < n) {
//                提示保存的内容已经超过磁盘的容量
                return false;
            } else {
                File file = (File) fat.getObject();
                file.setContent(content);
                file.setLength(length);
                file.setNumOfFAT(num);
                while (index != FileSystemUtil.END) {
//                设置新的文本内容 长度 磁盘块数 本来已经分配的磁盘块数据更新
                    myFAT[index].setObject(file);
                    pre = index;
                    index = myFAT[index].getIndex();
                }
//                找新的磁盘块 分配新的磁盘块
                int space = this.searchEmptyMyFAT();
                index = pre;
                myFAT[index].setIndex(space);
                for (int i = 1; i <= n; i++) {
                    if (i == n) {
                        myFAT[space] = new FAT(FileSystemUtil.END, FileSystemUtil.FILE, file, space);
                    } else {
                        myFAT[space] = new FAT(FileSystemUtil.END, FileSystemUtil.FILE, file, space);
                        myFAT[space].setIndex(this.searchEmptyMyFAT());
                    }
                    space = this.searchEmptyMyFAT();
                }
            }
        } else {
            File file = (File) fat.getObject();
            file.setContent(content);
            file.setLength(length);
            file.setNumOfFAT(num);
//            磁盘块数量减少或不变
            n = oldNum - num;
            int i = 0;
            while (index != FileSystemUtil.END) {
//                设置新的文本内容 长度 磁盘块数 本来已经分配的磁盘块数据更新
                i++;
                if (i < num) {
                    myFAT[index].setObject(file);
                    pre = index;
                    index = myFAT[index].getIndex();
                } else if (i == num) {
                    myFAT[index].setObject(file);
                    pre = index;
                    index = myFAT[index].getIndex();
                    myFAT[pre].setIndex(FileSystemUtil.END);
                } else {
                    pre = index;
                    index = myFAT[index].getIndex();
                    myFAT[pre] = new FAT(pre);
                }
            }
        }
        return true;
    }

    /**
     * 获取指定路径下的文件夹
     */
    public List<Folder> getFolders(String path) {
        ArrayList<Folder> list = new ArrayList<>();
        for (int i = 0; i < myFAT.length; i++) {
            int index = myFAT[i].getIndex();
            Object object = myFAT[i].getObject();
            if (index != FileSystemUtil.SPARE && index != FileSystemUtil.BAD && object instanceof Folder && ((Folder) object).getLocation().equals(path)) {
                list.add((Folder) object);
            }
        }
        return list;
    }

    /**
     * 获取指定路径下的文件
     */
    public List<File> getFiles(String path) {
        ArrayList<File> list = new ArrayList<>();
        for (int i = 0; i < myFAT.length; i++) {
            int index = myFAT[i].getIndex();
            Object object = myFAT[i].getObject();
            if (index != FileSystemUtil.SPARE && index != FileSystemUtil.BAD && object instanceof File && ((File) object).getLocation().equals(path)) {
                list.add((File) object);
            }
        }
        return list;
    }

    /**
     * 判断新的文件名是否重复
     */
    public boolean isFileNameSame(String path, String name) {
        ArrayList<File> list = (ArrayList) getFiles(path);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFileName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断新的文件夹名是否重复
     */
    public boolean isFolderNameSame(String path, String name) {
        ArrayList<Folder> list = (ArrayList) getFolders(path);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFolderName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取具体的文件夹
     */
    public FAT getFolderFAT(String path, String name) {
        for (int i = 0; i < myFAT.length; i++) {
            int index = myFAT[i].getIndex();
            Object object = myFAT[i].getObject();
            if (index == FileSystemUtil.END && object instanceof Folder && ((Folder) object).getLocation().equals(path) && ((Folder) object).getFolderName().equals(name)) {
                return myFAT[i];
            }
        }
        return null;
    }

    /**
     * 获取具体的文件
     */
    public FAT getFileFAT(String path, String name) {
        for (int i = 0; i < myFAT.length; i++) {
            int index = myFAT[i].getIndex();
            Object object = myFAT[i].getObject();
            if (index == FileSystemUtil.END && object instanceof File && ((File) object).getLocation().equals(path) && ((File) object).getFileName().equals(name)) {
                return myFAT[i];
            }
        }
        return null;
    }

    /**
     * 获取指定路径下文件分配表
     */
    public List<FAT> getFATs(String path) {
        List<FAT> fats = new ArrayList<>();
        int i;
        int index;
        Object object;
        for (i = 0; i < myFAT.length; i++) {
            index = myFAT[i].getIndex();
            object = myFAT[i].getObject();
            if (index == FileSystemUtil.END && object instanceof File && ((File) object).getLocation().equals(path)) {
                fats.add(myFAT[i]);
            }
        }

        for (i = 0; i < myFAT.length; i++) {
            index = myFAT[i].getIndex();
            object = myFAT[i].getObject();
            if (index == FileSystemUtil.END && object instanceof Folder && ((Folder) object).getLocation().equals(path)) {
                fats.add(myFAT[i]);
            }
        }
        return fats;
    }

    /**
     * 获取某个文件夹的大小
     */
    public int getFolderSize(Folder folder) {
        int size = 0;
        String path = folder.getLocation() + folder.getFolderName() + "\\";
        for (int i = 0; i < myFAT.length; i++) {
            int type = myFAT[i].getType();
            Object object = myFAT[i].getObject();
            if (type == FileSystemUtil.FILE && ((File) object).getLocation().contains(path)) {
                size = size + ((File) object).getSize();
            } else if ((type == FileSystemUtil.FOLDER && ((Folder) object).getLocation().contains(path))) {
                size = size + ((Folder) object).getSize();
            }
        }
        return size;
    }

    /**
     * 获取某个文件夹所包含的子文件夹个数
     */
    public int getFolderChildSize(Folder folder) {
        int size = 0;
        String path = folder.getLocation()+ "\\" + folder.getFolderName();
        for (int i = 0; i < myFAT.length; i++) {
            int type = myFAT[i].getType();
            Object object = myFAT[i].getObject();
            if ((type == FileSystemUtil.FOLDER && ((Folder) object).getLocation().contains(path))) {
                size++;
            }
        }
        return size;
    }

    /**
     * 获取某个文件夹所包含的子文件夹个数
     */
    public int getFileChildSize(Folder folder) {
        int size = 0;
        String path = folder.getLocation() + "\\" + folder.getFolderName();
        for (int i = 0; i < myFAT.length; i++) {
            int type = myFAT[i].getType();
            Object object = myFAT[i].getObject();
            if (type == FileSystemUtil.FILE && ((File) object).getLocation().contains(path)) {
                size++;
            }
        }
        return size;
    }

    /**
     * 根据TreeItem来获取对应的Folder FAT
     * */
    public FAT getTreeItemFAT(TreeItem<String> treeItem){
        for (int i = 0; i < myFAT.length; i++) {
            Object object = myFAT[i].getObject();
            if(myFAT[i].getType()==FileSystemUtil.FOLDER&&((Folder)(object)).getFolderTreeItem().equals(treeItem)){
                return  myFAT[i];
            }
        }
        return null;
    }


    /***?
     * 判断当前目录下的文件或文件夹数已够8个
     */
    public boolean isEight(String path) {
        return getFATs(path).size() > 7;
    }

    /**
     * 文件夹重命名 修改文件或文件夹路径
     */
    public void modifyLocation(String oldPath, String newPath) {
        for (int i = 0; i < myFAT.length; i++) {
            int index = myFAT[i].getIndex();
            Object object = myFAT[i].getObject();
            if (index != FileSystemUtil.SPARE && index != FileSystemUtil.BAD) {
//                找到旧路径名包含需要修改的名称
                if (myFAT[i].getType() == FileSystemUtil.FILE) {
                    if (((File) object).getLocation().contains(oldPath)) {
//                        将旧的路径名替换成新的路径名
                        ((File) object).setLocation(((File) object).getLocation().replace(oldPath, newPath));
                    }
                } else if (myFAT[i].getType() == FileSystemUtil.FOLDER && ((Folder) object).getLocation().contains(oldPath)) {
//                        将旧的路径名替换成新的路径名
                    ((Folder) object).setLocation(((Folder) object).getLocation().replace(oldPath, newPath));
                }
            }
        }
    }

    /**
     * 删除文件或文件夹
     * 0 文件正在打开，不能删除
     * 1 文件夹不为空，不能删除
     */
    public int delete(FAT fat) {
        if (fat.getType() == FileSystemUtil.FILE) {
//            文件删除
            int i;
            for (i = 0; i < openFiles.getFiles().size(); i++) {
                if (openFiles.getFiles().get(i).getFile().equals(fat.getObject())) {
//                    提示文件正在打开，不能删除
                    return 0;
                }
            }

            for (i = 0; i < myFAT.length; i++) {
                int index = myFAT[i].getIndex();
                if (index != FileSystemUtil.SPARE && index != FileSystemUtil.BAD && myFAT[i].getType() == FileSystemUtil.FILE && myFAT[i].getObject().equals(fat.getObject())) {
//                    删除文件，将该文件分配表 磁盘块号设为空闲 内容清空
                    myFAT[i].setIndex(FileSystemUtil.SPARE);
                    myFAT[i].setType(-1);
                    myFAT[i].setObject(null);
                    System.out.println("----------------------------》删除文件");
                }
            }
        } else {
//            文件夹删除 path父目录路径
            String path = ((Folder) fat.getObject()).getLocation();
            String folderPath = path + "\\" + ((Folder) fat.getObject()).getFolderName();
            System.out.println("路径：" + folderPath);
            int index = 0;

            for (int i = 3; i < myFAT.length; i++) {
                int next = myFAT[i].getIndex();
//                该磁盘不为空 不坏
                if (next != FileSystemUtil.SPARE && next != FileSystemUtil.BAD) {
                    Object object = myFAT[i].getObject();
                    if (myFAT[i].getType() == FileSystemUtil.FOLDER) {
//                        folderPath路径下是否有文件夹
                        if (((Folder) object).getLocation().equals(folderPath)) {
                            return 1;
                        }
                    } else if (myFAT[i].getType() == FileSystemUtil.FILE) {
                        if (((File) object).getLocation().equals(folderPath)) {
                            return 1;
                        }
                    }

                    if (myFAT[i].getType() == FileSystemUtil.FOLDER && myFAT[i].getObject().equals(fat.getObject())) {
                        index = i;
                    }
                }
            }
//            删除文件夹 将该文件夹分配表 磁盘块号设为空闲 内容清空
            myFAT[index].setIndex(FileSystemUtil.SPARE);
            myFAT[index].setType(-1);
            myFAT[index].setObject(null);
            System.out.println("----------------------------》删除文件夹");
        }
        return -1;
    }

}
