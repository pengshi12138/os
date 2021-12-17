package com.niuniukeaiyouhaochi.os.UI;

import com.niuniukeaiyouhaochi.os.model.FAT;
import com.niuniukeaiyouhaochi.os.model.File;
import com.niuniukeaiyouhaochi.os.model.Folder;
import com.niuniukeaiyouhaochi.os.service.FATService;
import com.niuniukeaiyouhaochi.os.util.FileSystemUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AttributeXMLControl implements Initializable {

    private static final ArrayList<FAT> fatArrayList = new ArrayList<>();
    @FXML
    private Label containFile;
    @FXML
    private Label fileName;
    @FXML
    private RadioButton readOnly;
    @FXML
    private RadioButton readWrite;
    @FXML
    private Button buttonEnsure;
    @FXML
    private Button buttonCancel;
    @FXML
    private Label size;
    @FXML
    private Label reviseTime;
    @FXML
    private Label createTime;
    @FXML
    private Button buttonUse;
    @FXML
    private Label fileStyle;
    @FXML
    private Label fileLocation;
    @FXML
    private ImageView fileImg;
    @FXML
    private Label startBlock;
    @FXML
    private Label blockNum;

    private FAT fat;
    private FATService fatService;
    private int fileNum;
    private int folderNum;
    private String path;

    //    添加fat
    public static void addFatArrayList(FAT fat) {
        fatArrayList.add(fat);
    }

    //    删除fat
    public static void removeFatArrayList(FAT fat) {
        fatArrayList.remove(fat);
    }

    //    初始化
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fatService = new FATService();
//        不为零，记录是哪个fat
        int length = fatArrayList.size();
        if (length != 0) {
            fat = fatArrayList.get(length - 1);
        }
        if (fat.getType() == FileSystemUtil.FOLDER) {
//            文件夹
            Folder folder = (Folder) fat.getObject();
            fileName.setText(folder.getFolderName());
            fileImg.setImage(new Image(FileSystemUtil.folderPath));
            fileStyle.setText("文件夹");
            fileLocation.setText(folder.getLocation());
            size.setText(fatService.getFolderSize(folder) + "B");
            folderNum = fatService.getFolderChildSize(folder);
            fileNum = fatService.getFileChildSize(folder);
            containFile.setText(fileNum + "个文件 " + folderNum + "个文件夹");
            createTime.setText(folder.getCreateTime());
            reviseTime.setText(folder.getReviseTime());
            readWrite.selectedProperty().setValue(true);
            readOnly.selectedProperty().setValue(false);
            readOnly.setDisable(true);
            readWrite.setDisable(true);
            startBlock.setText(folder.getDiskNum()+"");
            blockNum.setText("1");
        } else {
//            文件
            File file = (File) fat.getObject();
            fileName.setText(file.getFileName());
            fileImg.setImage(new Image(FileSystemUtil.filePath));
            fileStyle.setText("文件夹");
            fileLocation.setText(file.getLocation());
            size.setText(file.getSize() + "B");
            path = file.getLocation() + "\\" + file.getFileName();
            folderNum = fatService.getFolders(path).size();
            fileNum = fatService.getFiles(path).size();
            containFile.setText(fileNum + "个文件 " + folderNum + "个文件夹");
            createTime.setText(file.getCreateTime());
            reviseTime.setText(file.getReviseTime());
            startBlock.setText(file.getDiskNum()+"");
            blockNum.setText(file.getNumOfFAT()+"");
            String type = file.getType();
            if (type.equals("写读")) {
                readWrite.selectedProperty().setValue(true);
            } else {
                readOnly.selectedProperty().setValue(true);
            }

//            修改属性按钮
            readOnly.selectedProperty().addListener(new ChangeListener<Boolean>() {   //readOnly监听单独
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    readWrite.selectedProperty().setValue(!newValue);
                }
            });

            readWrite.selectedProperty().addListener(new ChangeListener<Boolean>() {   //readOnly监听单独
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    readOnly.selectedProperty().setValue(!newValue);
                }
            });
//            确定
            buttonEnsure.setOnAction(actionEvent -> {
                if (readOnly.selectedProperty().getValue()) {
                    file.setType("读");
                } else {
                    file.setType("写读");
                }
//              当前窗体
                Stage stage = (Stage) buttonCancel.getScene().getWindow();
                stage.close();
            });
//            取消
            buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
//             当前窗体
                    Stage stage = (Stage) buttonCancel.getScene().getWindow();
                    stage.close();
                }
            });
//            应用
            buttonUse.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (readOnly.selectedProperty().getValue()) {
                        file.setType("读");
                    } else {
                        file.setType("写读");
                    }
                    //             当前窗体
                    Stage stage = (Stage) buttonCancel.getScene().getWindow();
                    stage.close();
                }
            });
        }
    }
}
