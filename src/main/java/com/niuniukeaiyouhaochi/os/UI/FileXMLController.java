package com.niuniukeaiyouhaochi.os.UI;

import com.niuniukeaiyouhaochi.os.Listener.DragUtil;
import com.niuniukeaiyouhaochi.os.model.FAT;
import com.niuniukeaiyouhaochi.os.model.File;
import com.niuniukeaiyouhaochi.os.model.Folder;
import com.niuniukeaiyouhaochi.os.model.OpenFiles;
import com.niuniukeaiyouhaochi.os.service.FATService;
import com.niuniukeaiyouhaochi.os.util.FileSystemUtil;
import com.niuniukeaiyouhaochi.os.util.GUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FileXMLController implements Initializable {
    //    label集合
    private static final List<Label> labelList = new ArrayList<Label>();
    public static ObservableList<FAT> FATsModels = FXCollections.observableArrayList();
    public static ObservableList<File> FilesModels = FXCollections.observableArrayList();
    public static String path = "D:";
    public static FATService fatService = null;
    public static FAT[] myFAT = null;
    public static OpenFiles openFiles = null;
    public static Stage stage;//新建窗口
    //    右击面板菜单
    private static ContextMenu PaneContextMenu;
   //    右击书目录菜单
   private static ContextMenu TreeContextMenu;
   //   单击标志
    private static Button flagButton;
    @FXML
    Button filePaneFileBtn;
    @FXML
    Button folderPaneFileBtn;
    //        设置文件树目录
    boolean isFirst = true;
    //    当前的树节点
    private TreeItem<String> currentTreeItem;
//    右击书目录的树节点
    private TreeItem<String> rightClickTreeItem;
//    右击书目录的路径
    private String rightClickTreePath;
    //    磁盘树节点
    private TreeItem<String> treeDiskItem;
    @FXML
    private AnchorPane rootPane;
    //    圆
    @FXML
    private PieChart circleView;

    //盘块详情面板
    @FXML
    private AnchorPane DiskBlockPane;

    //    文件图标面板
    @FXML
    private FlowPane filePane;
    //    文件树目录
    @FXML
    private TreeView<String> treeView;
    //    文件打开目录表格
    @FXML
    private TableView<File> fileTable;
    @FXML
    private TableColumn<File, String> fileName;
    @FXML
    private TableColumn<File, String> fileStyle;
    @FXML
    private TableColumn<File, Integer> startNum;
    @FXML
    private TableColumn<File, String> fileLocation;
    //    文件分配表格
    @FXML
    private TableView<FAT> fatTable;
    @FXML
    private TableColumn<FAT, Integer> num;
    //    文件右击事件
    @FXML
    private TableColumn<FAT, Integer> index;
    //饼图
    @FXML
    private PieChart pieChart;
    //用于路径搜索的输入框
    @FXML
    private TextField searchPath;
    //饼图文字
    @FXML
    private Label pieChartTextBusy;
    @FXML
    private Label pieChartTextSpace;

    @FXML
    private ImageView backIcon;

    @FXML
    private ImageView outIcon;

    @FXML
    private AnchorPane top;

    // back
    @FXML
    void back() {
        FileMain.stage.hide();
        LoginMain.LoginStage.show();
    }

    // out
    @FXML
    void out() {
        Stage stage = (Stage) top.getScene().getWindow();
        stage.close();
    }

    void addListener(){

        // 添加窗体拖动
        DragUtil.addDragListener(FileMain.LoginStage, top);

        // 添加退出程序按钮
        outIcon.setOnMouseEntered(e -> outIcon.setCursor(Cursor.HAND));

        // 添加返回按钮
        backIcon.setOnMouseEntered(e -> backIcon.setCursor(Cursor.HAND));
    }

    @FXML
    void searchFile(ActionEvent event) {
        String inputPath;
        inputPath = searchPath.getText();
        if (fatService.getFATs(inputPath).size() == 0) {
            GUI.contentOutGUI("搜索的内容为空，请输入正确的路径！");
            return;
        }
        setFilePane(inputPath);
        path = inputPath;
//        获取当前目录的文件夹对象
        String folderPath = "";
        String folderName = "";
        if (!path.equals("D:")) {
            String[] pathArray = path.split("\\\\|:");
            System.out.println(pathArray.length);
            folderName = pathArray[pathArray.length - 1];
            for (int i = 0; i < pathArray.length - 1; i++) {
                if (i == 0) {
                    folderPath = folderPath + pathArray[i] + ":";
                } else if(!pathArray[i].equals("")){
                    folderPath = folderPath + "\\"+ pathArray[i];
                }
            }
            System.out.println(folderPath+"  "+folderName);
            FAT fat = fatService.getFolderFAT(folderPath, folderName);
            currentTreeItem = ((Folder) (fat.getObject())).getFolderTreeItem();
        } else {
            currentTreeItem=treeDiskItem;
        }
//        把对应的书目录展开
        TreeItem<String> item = currentTreeItem;
        while (!item.equals(treeDiskItem)){
            item.setExpanded(true);
            item=item.getParent();
        }
        treeDiskItem.setExpanded(true);
//        设置查询的文件夹被选中
        treeView.getSelectionModel().select(currentTreeItem);
        System.out.println(currentTreeItem.getValue());
    }


    //    初始化
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        初始化
        init();
//        菜单栏初始化
        initMenu();
//        设置文件树目录
        setTree();
//        设置面板的文件图标
        setFilePane("D:");
//        设置盘块号状态
        setFATStatue();
//        设置打开文件状态
        setFileStatue();
//        设置面板右击事件
        setPaneRightClick();
//        饼状图改变事件
        changPie();

        initDiskBlockPane();
        // 添加监听函数
        addListener();
    }

    //      菜单栏初始化
    private void initMenu() {
//          右击面板菜单初始化
        PaneContextMenu = new ContextMenu();
        MenuItem M1 = new MenuItem("新建文件");
        MenuItem M2 = new MenuItem("新建文件夹");
//         MenuItem点击
        PaneContextMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index;
                String name;
                if (event.getTarget().equals(M1)) {
                    if (fatService.isEight(path)) {
                        GUI.contentOutGUI("已达到最大容量！");
                        return;
                    }
                    index = fatService.creatFile(path);
                    if(index == FileSystemUtil.ERROR){
                        GUI.contentOutGUI("已达到磁盘最大容量！");
                    }
                    System.out.println("新建文件成功！ 文件路径：" + ((File) myFAT[index].getObject()).getFileName());
//                    设置文件面板图标
                    setFilePane(path);
                    changPie();
                } else if (event.getTarget().equals(M2)) {
                    if (fatService.isEight(path)) {
                        GUI.contentOutGUI("已达到最大容量！");
                        return;
                    }
                    index = fatService.creatFolder(path);
                    if(index==FileSystemUtil.ERROR){
                        GUI.contentOutGUI("已达到磁盘最大容量！");
                    }
                    Folder folder = ((Folder) myFAT[index].getObject());
                    name = folder.getFolderName();
                    System.out.println("新建文件夹成功！ 文件夹名：" + name);
//                    设置文件树结构
                    Node folderPicIcon = new ImageView(new Image(getClass().getResourceAsStream(FileSystemUtil.folderTreePath), 18, 18, false, false));
                    TreeItem<String> treeFolderItem = new TreeItem<String>(name, folderPicIcon);
                    currentTreeItem.getChildren().add(treeFolderItem);
//                    设置树结构
                    folder.setFolderTreeItem(treeFolderItem);
//                    设置文件面板图标
                    setFilePane(path);
                    changPie();
                }
//                初始化盘块号状态
                setFATStatue();
//                设置颜色变化
                setDiskBlockPane();
            }
        });
        PaneContextMenu.getItems().addAll(M1, M2);

//        右击树菜单初始化
        TreeContextMenu =new ContextMenu();
        MenuItem M3 = new MenuItem("新建文件");
        MenuItem M4 = new MenuItem("新建文件夹");
        MenuItem M5 = new MenuItem("删除");
        MenuItem M6 = new MenuItem("重命名");
        //         MenuItem点击
        TreeContextMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index;
                String name;
                if (event.getTarget().equals(M3)) {
//                    新建文件
                    if (fatService.isEight(rightClickTreePath)) {
                        GUI.contentOutGUI("已达到最大容量！");
                        return;
                    }
                    index = fatService.creatFile(rightClickTreePath);
                    System.out.println("新建文件成功！ 文件路径：" + ((File) myFAT[index].getObject()).getFileName());
//                    设置文件面板图标
                    if(rightClickTreePath.equals(path)){
                        setFilePane(path);
                    }
                    changPie();
                } else if (event.getTarget().equals(M4)) {
//                    新建文件夹
                    if (fatService.isEight(rightClickTreePath)) {
                        GUI.contentOutGUI("已达到最大容量！");
                        return;
                    }
                    index = fatService.creatFolder(rightClickTreePath);
                    Folder folder = ((Folder) myFAT[index].getObject());
                    name = folder.getFolderName();
                    System.out.println("新建文件夹成功！ 文件夹名：" + name);
//                    设置文件树结构
                    Node folderPicIcon = new ImageView(new Image(getClass().getResourceAsStream(FileSystemUtil.folderTreePath), 18, 18, false, false));
                    TreeItem<String> treeFolderItem = new TreeItem<String>(name, folderPicIcon);
                    rightClickTreeItem.getChildren().add(treeFolderItem);
//                    设置树结构
                    folder.setFolderTreeItem(treeFolderItem);
//                    设置文件面板图标
                    if(rightClickTreePath.equals(path)){
                        setFilePane(path);
                        changPie();
                    }
                }else if (event.getTarget().equals(M5)) {
//                    删除
                    if(rightClickTreeItem.getValue().equals("D:")){
                        GUI.contentOutGUI("D盘不能删除！");
                        return;
                    }
                    FAT fat =fatService.getTreeItemFAT(rightClickTreeItem);
                    Folder folder =(Folder) fat.getObject();
                    index = fatService.delete(fat);
                    if (index == 1) {
                        GUI.contentOutGUI("文件夹内容不为空，不能删除！");
                        return;
                    }
                    TreeItem<String> parentItem = rightClickTreeItem.getParent();
                    parentItem.getChildren().remove(rightClickTreeItem);
//                    把对应的颜色置为未使用
                    int colorIndex = folder.getColorIndex();
                    fatService.setColorIndex(colorIndex);
//                    刷新图标面板
                    String location =getPath(parentItem);
                    if(location.equals(path)){
                        setFilePane(path);
                    }
//                  设置颜色变化
                    setDiskBlockPane();
                    setFATStatue();
                    changPie();
                }else if (event.getTarget().equals(M6)) {
//                    重命名
                    if(rightClickTreeItem.getValue().equals("D:")){
                        GUI.contentOutGUI("D盘不能重命名！");
                        return;
                    }
                    FAT fat =fatService.getTreeItemFAT(rightClickTreeItem);
                    Folder folder =(Folder) fat.getObject();
                    //                    文件夹重命名
                    TextArea textArea = new TextArea();
                    textArea.setText(folder.getFolderName());
                    textArea.setWrapText(true);
                    textArea.setPrefSize(500,270);
                    textArea.setLayoutY(30);
                    Button writeBtn = new Button("保存");
                    Button cancelBtn = new Button("取消");
                    AnchorPane anchorPane = new AnchorPane();
                    AnchorPane anchorPane1 = new AnchorPane();
                    anchorPane1.setPrefSize(496,50);
                    anchorPane1.setLayoutY(298);
                    anchorPane1.setLayoutX(2);
                    anchorPane1.setStyle("-fx-background-color:white;");
                    anchorPane1.getChildren().add(writeBtn);
                    anchorPane1.getChildren().add(cancelBtn);
                    writeBtn.setStyle("-fx-background-color: #D5D5D5;"+
                            "-fx-border-color: #C4C4C4;"+
                            "-fx-border-radius:0;"+
                            "-fx-border-style:solid;"+
                            "-fx-border-width:1px;");
                    writeBtn.setPrefSize(90,15);
                    writeBtn.setLayoutX(100);
                    writeBtn.setLayoutY(12);
                    cancelBtn.setStyle("-fx-background-color: #D5D5D5;"+
                            "-fx-border-color: #C4C4C4;"+
                            "-fx-border-radius:0;"+
                            "-fx-border-style:solid;"+
                            "-fx-border-width:1px;");
                    cancelBtn.setPrefSize(90,15);
                    cancelBtn.setLayoutX(300);
                    cancelBtn.setLayoutY(12);
                    ImageView imageView = new ImageView();
                    imageView.setImage(new Image(getClass().getResourceAsStream(FileSystemUtil.delPath), 17, 17, false, false));
                    Label label1=new Label("",imageView);
                    label1.setLayoutX(470);
                    label1.setLayoutY(7);
                    anchorPane.getChildren().add(textArea);
                    anchorPane.getChildren().add(label1);
                    anchorPane.getChildren().add(anchorPane1);
                    anchorPane.setStyle("-fx-background-color:#666666;" +         //设置背景颜色
                            "-fx-text-fill:#FF0000;" +        //设置字体颜色
                            "-fx-border-radius:0;" +         //设置边框圆角
                            "-fx-border-style:solid;"+      //设置边框样式
                            "-fx-border-width:2;"
                    );
                    textArea.setStyle("-fx-background-color:white;" +         //设置背景颜色
                            "-fx-text-fill:black;" +//设置字体颜色
                            "-fx-font-size:20;"+
                            "-fx-border-radius:0;" +         //设置边框圆角
                            "-fx-border-color:black;"+     //设置边框颜色
                            "-fx-border-style:solid;"+      //设置边框样式
                            "-fx-border-width:2;"         //设置边框宽度
                    );
                    Stage dialogStage = new Stage();
                    Scene dialogScene = new Scene(anchorPane, 500, 350);
                    dialogStage.setScene(dialogScene);
                    dialogStage.initStyle(StageStyle.UNDECORATED);
                    dialogStage.show();
//                    窗体拖动
                    DragUtil.addDragListener(dialogStage,anchorPane);
                    label1.setOnMouseClicked(mouseEvent -> {
                        if(mouseEvent.getClickCount()==1)
                            dialogStage.close();
                    });
                    dialogStage.show();

                    cancelBtn.setOnAction(e -> {
                        dialogStage.close();
                    });

                    writeBtn.setOnAction(e -> {
//                        重命名
                        String folderName = textArea.getText();
                        System.out.println(rightClickTreePath+" "+folderName);
                        FAT fat1 = fatService.getTreeItemFAT(rightClickTreeItem.getParent());
                        String location ="D:";
                        if(fat1!=null){
                            location=((Folder)fat1.getObject()).getLocation();
                        }
                        if (fatService.isFolderNameSame(location, folderName)) {
                            GUI.contentOutGUI("名字重复！");
                            dialogStage.close();
                            return;
                        }
//                        刷新书目录上的文件夹名称
                        rightClickTreeItem.setValue(folderName);
//                        修改路径目录名称
                        String newName = getPath(rightClickTreeItem);
                        fatService.modifyLocation(rightClickTreePath, newName);
                        folder.setFolderName(folderName);
//                       刷新面板图标内容
                        TreeItem<String> parentItem = rightClickTreeItem.getParent();
                        String folderLocation = folder.getLocation();
                        if(folderLocation.equals(path)){
                            setFilePane(path);
                        }
                        dialogStage.close();
                    });
                }

//                初始化盘块号状态
                setFATStatue();
//                设置颜色变化
                setDiskBlockPane();
            }
        });
        TreeContextMenu.getItems().addAll(M3,M4,M5,M6);
    }

    //      饼状图实时更新
    private void changPie() {
        int busy = fatService.getNumOfFAT();
        int space = fatService.getSpaceOfFAT();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("占用", busy),
                new PieChart.Data("空闲", space));
        pieChart.setData(pieChartData);
        pieChart.setLegendVisible(false);
        pieChart.setTitle("磁盘利用情况");
        pieChart.getStylesheets().add("/css/chart.css");
        pieChartTextSpace.setText("空闲:" + space);
        pieChartTextBusy.setText("占用:" + busy);
    }

    //      初始化盘块颜色使用详情
    private void initDiskBlockPane() {
        for (int j = 0; j < 16; j++) {
            for (int i = 0; i < 8; i++) {
                Label label = new Label();
                label.setStyle("-fx-background-color:grey;" + "-fx-border:none");
                label.setPrefSize(20, 10);
                label.setMinSize(20, 10);
                label.setMaxSize(20, 10);
                label.setLayoutX(25 * i + 4);
                label.setLayoutY(12 * j + 32);
                DiskBlockPane.getChildren().add(label);
                labelList.add(label);
//               点击事件
                label.setOnMouseClicked(event -> {
                    MouseButton button = event.getButton();
//               右击事件
                    if (event.getClickCount() == 1) {
                        setLabelStyle(labelList.indexOf(label));
                    }
                });
            }
        }
        labelList.get(0).setStyle("-fx-background-color:yellow;" +
                "-fx-border-style: solid;" +
                "-fx-border-color: black");
        labelList.get(1).setStyle("-fx-background-color:yellow;" +
                "-fx-border-style: solid;" +
                "-fx-border-color: black");
        labelList.get(2).setStyle("-fx-background-color:yellowgreen;" +
                "-fx-border-style: solid;" +
                "-fx-border-color: black");
    }
//       点击label弹出属性
    private void setLabelStyle(int index) {
        FAT fat = myFAT[index];
        if(myFAT[index].getType()==FileSystemUtil.FOLDER){
            //                    属性
            Parent root = null;
//                    传值到令外一个窗体
            AttributeXMLControl.addFatArrayList(fat);
            try {
                root = FXMLLoader.load(getClass().getResource("/fxml/attribute.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage dialogStage = new Stage();
            Scene dialogScene = new Scene(root, 387, 504);
            dialogStage.getIcons().add(new Image(getClass().getResourceAsStream(FileSystemUtil.folderPath), 10, 10, false, false));
            dialogStage.setScene(dialogScene);
            dialogStage.show();
//                    监听窗体关闭
            dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
//                            移除已打开的文件夹属性
                    AttributeXMLControl.removeFatArrayList(fat);
                }
            });
        }else if(myFAT[index].getType()==FileSystemUtil.FILE){
//            属性
            Parent root = null;
//                    传值到令外一个窗体
            AttributeXMLControl.addFatArrayList(fat);
            try {
                root = FXMLLoader.load(getClass().getResource("/fxml/attribute.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage dialogStage = new Stage();
            Scene dialogScene = new Scene(root, 387, 504);
            dialogStage.getIcons().add(new Image(getClass().getResourceAsStream(FileSystemUtil.filePath), 10, 10, false, false));
            dialogStage.setScene(dialogScene);
            dialogStage.show();
//                    监听窗体关闭
            dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
//                            移除已打开的文件夹属性
                    AttributeXMLControl.removeFatArrayList(fat);
                }
            });
        }
    }

    //      盘块颜色使用详情实时更新
    private void setDiskBlockPane() {
        for (int i = 3; i < myFAT.length; i++) {
            Object object = myFAT[i].getObject();
            if (myFAT[i].getType() == FileSystemUtil.FILE) {
                File file = (File) object;
                int colorIndex = file.getColorIndex();
                labelList.get(i).setStyle("-fx-background-color:" + fatService.getColor(colorIndex) + " ;" +
                        "-fx-border-style: solid;" +
                        "-fx-border-color: black");
            } else if (myFAT[i].getType() == FileSystemUtil.FOLDER) {
                Folder folder = (Folder) object;
                int colorIndex = folder.getColorIndex();
                labelList.get(i).setStyle("-fx-background-color:" + fatService.getColor(colorIndex) + " ;" +
                        "-fx-border-style: solid;" +
                        "-fx-border-color: black");
            } else {
                labelList.get(i).setStyle("-fx-background-color:grey;");
            }
        }
    }

    //      设置搜索路径
    private void setSearchPath() {
        searchPath.setText(path);
    }

    //      设置面板右击事件
    private void setPaneRightClick() {
        PaneContextMenu.hide();
        filePane.setOnMouseClicked(event -> {
            MouseButton button = event.getButton();
            //右键点击
            if (button == MouseButton.SECONDARY) {
                double x = event.getScreenX();
                double y = event.getScreenY();
                PaneContextMenu.show(filePane, x, y);
            } else{
                if(!(flagButton==null)){
                    flagButton.setStyle("-fx-background-color:white");
                }
                PaneContextMenu.hide();
            }
        });
    }

    //      设置面板的文件图标
    private void setFilePane(String location) {
//          清空面板
        filePane.getChildren().clear();
        List<FAT> fatList = fatService.getFATs(location);
        String name;
        for (int i = 0; i < fatList.size(); i++) {
//             文件夹
            if (fatList.get(i).getType() == FileSystemUtil.FOLDER) {
                name = ((Folder) fatList.get(i).getObject()).getFolderName();

                ImageView folderIcon = new ImageView(new Image(getClass().getResourceAsStream(FileSystemUtil.folderPath), 80, 80, false, false));
                Label labelItem = new Label(name, folderIcon);
                labelItem.setWrapText(true);
                labelItem.setPrefWidth(120);
                labelItem.setPrefHeight(150);
                labelItem.setAlignment(Pos.CENTER);
                labelItem.setContentDisplay(ContentDisplay.TOP);
                folderPaneFileBtn = new Button();
                folderPaneFileBtn.setStyle("-fx-background-color:white;" +         //设置背景颜色
                                "-fx-background-radius:20;" +     //设置背景圆角
                                "-fx-text-fill:#FF0000;" +        //设置字体颜色
                                "-fx-border-radius:20;" +         //设置边框圆角
                                "-fx-border-color:white;"     //设置边框颜色
                );
                folderPaneFileBtn.setGraphic(labelItem);
//                获取该节点对应的FAT表
                FAT fat = fatService.getFolderFAT(location, name);
                setRightMenuFolder(folderPaneFileBtn, fat);
                filePane.getChildren().add(folderPaneFileBtn);
            } else {
//                文件
                name = ((File) fatList.get(i).getObject()).getFileName();

                ImageView fileIcon = new ImageView(new Image(getClass().getResourceAsStream(FileSystemUtil.filePath), 80, 80, false, false));
                Label labelItem = new Label(name, fileIcon);
                labelItem.setWrapText(true);
                labelItem.setPrefWidth(120);
                labelItem.setPrefHeight(150);
                labelItem.setAlignment(Pos.CENTER);
                labelItem.setContentDisplay(ContentDisplay.TOP);
                filePaneFileBtn = new Button();
                filePaneFileBtn.setStyle("-fx-background-color:white;" +         //设置背景颜色
                                "-fx-background-radius:20;" +     //设置背景圆角
                                "-fx-text-fill:#FF0000;" +        //设置字体颜色
                                "-fx-border-radius:20;" +         //设置边框圆角
                                "-fx-border-color:white;"     //设置边框颜色
                        //"-fx-border-style:dashed;"+      //设置边框样式
                        //"-fx-border-width:5;"+           //设置边框宽度
                        //"-fx-border-insets:-5"           //设置边框插入值
                );
                filePaneFileBtn.setGraphic(labelItem);
//                获取该节点对应的FAT表
                FAT fat = fatService.getFileFAT(location, name);
                setRightMenuFile(filePaneFileBtn, fat);
                filePane.getChildren().add(filePaneFileBtn);
            }
        }
    }

    //      设置文件夹右击事件
    private void setRightMenuFolder(Button folderPaneFileBtn, FAT fat) {
        Folder folder = ((Folder) fat.getObject());
        ContextMenu folderPaneContextMenu = new ContextMenu();
        //        右击文件菜单初始化
        MenuItem M1 = new MenuItem("打开");
        MenuItem M2 = new MenuItem("重命名");
        MenuItem M3 = new MenuItem("删除");
        MenuItem M4 = new MenuItem("属性");
        folderPaneContextMenu.getItems().addAll(M1, M2, M3, M4);
        folderPaneContextMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (event.getTarget().equals(M1)) {
//                    修改该文件夹的修改时间
                    folder.setReviseTime();
//                    打开文件夹
//                    获取文件夹点击的路径值，展示该路径下的文件、文件夹内容
                    path = path + "\\"+ folder.getFolderName() ;
                    setFilePane(path);
//                    设置当前路径
                    setSearchPath();
//                    设置当前树结构对象
                    currentTreeItem = folder.getFolderTreeItem();
                } else if (event.getTarget().equals(M2)) {
//                    文件夹重命名
                    TextArea textArea = new TextArea();
                    textArea.setText(folder.getFolderName());
                    textArea.setWrapText(true);
                    textArea.setPrefSize(500,270);
                    textArea.setLayoutY(30);
                    Button writeBtn = new Button("保存");
                    Button cancelBtn = new Button("取消");
                    AnchorPane anchorPane = new AnchorPane();
                    AnchorPane anchorPane1 = new AnchorPane();
                    anchorPane1.setPrefSize(496,50);
                    anchorPane1.setLayoutY(298);
                    anchorPane1.setLayoutX(2);
                    anchorPane1.setStyle("-fx-background-color:white;");
                    anchorPane1.getChildren().add(writeBtn);
                    anchorPane1.getChildren().add(cancelBtn);
                    writeBtn.setStyle("-fx-background-color: #D5D5D5;"+
                            "-fx-border-color: #C4C4C4;"+
                            "-fx-border-radius:0;"+
                            "-fx-border-style:solid;"+
                            "-fx-border-width:1px;");
                    writeBtn.setPrefSize(90,15);
                    writeBtn.setLayoutX(100);
                    writeBtn.setLayoutY(12);
                    cancelBtn.setStyle("-fx-background-color: #D5D5D5;"+
                            "-fx-border-color: #C4C4C4;"+
                            "-fx-border-radius:0;"+
                            "-fx-border-style:solid;"+
                            "-fx-border-width:1px;");
                    cancelBtn.setPrefSize(90,15);
                    cancelBtn.setLayoutX(300);
                    cancelBtn.setLayoutY(12);
                    ImageView imageView = new ImageView();
                    imageView.setImage(new Image(getClass().getResourceAsStream(FileSystemUtil.delPath), 17, 17, false, false));
                    Label label1=new Label("",imageView);
                    label1.setLayoutX(470);
                    label1.setLayoutY(7);
                    anchorPane.getChildren().add(textArea);
                    anchorPane.getChildren().add(label1);
                    anchorPane.getChildren().add(anchorPane1);
                    anchorPane.setStyle("-fx-background-color:#666666;" +         //设置背景颜色
                            "-fx-text-fill:#FF0000;" +        //设置字体颜色
                            "-fx-border-radius:0;" +         //设置边框圆角
                            "-fx-border-style:solid;"+      //设置边框样式
                            "-fx-border-width:2;"
                    );
                    textArea.setStyle("-fx-background-color:white;" +         //设置背景颜色
                            "-fx-text-fill:black;" +//设置字体颜色
                            "-fx-font-size:20;"+
                            "-fx-border-radius:0;" +         //设置边框圆角
                            "-fx-border-color:black;"+     //设置边框颜色
                            "-fx-border-style:solid;"+      //设置边框样式
                            "-fx-border-width:2;"         //设置边框宽度
                    );
                    Stage dialogStage = new Stage();
                    Scene dialogScene = new Scene(anchorPane, 500, 350);
                    dialogStage.setScene(dialogScene);
                    dialogStage.initStyle(StageStyle.UNDECORATED);
                    dialogStage.show();
//                    窗体拖动
                    DragUtil.addDragListener(dialogStage,anchorPane);
                    label1.setOnMouseClicked(mouseEvent -> {
                        if(mouseEvent.getClickCount()==1)
                            dialogStage.close();
                    });
                    dialogStage.show();

                    cancelBtn.setOnAction(e -> {
                        dialogStage.close();
                    });
                    writeBtn.setOnAction(e -> {
//                        重命名
                        String name = textArea.getText();
                        if (fatService.isFolderNameSame(path, name)) {
                            GUI.contentOutGUI("名字重复！");
                            dialogStage.close();
                            return;
                        }
//                        刷新书目录上的文件夹名称
                        TreeItem<String> treeItem = folder.getFolderTreeItem();
                        treeItem.setValue(name);
//                        修改路径目录名称
                        fatService.modifyLocation((path+"\\"+folder.getFolderName()), (path+ "\\" +name));
                        folder.setFolderName(name);
//                       刷新面板图标内容
                        setFilePane(path);
                        dialogStage.close();
                    });
                } else if (event.getTarget().equals(M3)) {
//                    删除
                    int index = fatService.delete(fat);
                    if (index == 1) {
                        GUI.contentOutGUI("文件夹内容不为空，不能删除！");
                        return;
                    }
//                    删除书目录上对应的树节点
                    TreeItem<String> treeItem = folder.getFolderTreeItem();
                    currentTreeItem.getChildren().remove(treeItem);
//                    把对应的颜色置为未使用
                    int colorIndex = folder.getColorIndex();
                    fatService.setColorIndex(colorIndex);
//                    刷新图标面板
                    setFilePane(path);
//                    设置颜色变化
                    setDiskBlockPane();
                    setFATStatue();
                    changPie();
                } else if (event.getTarget().equals(M4)) {
//                    属性
                    Parent root = null;
//                    传值到令外一个窗体
                    AttributeXMLControl.addFatArrayList(fat);
                    try {
                        root = FXMLLoader.load(getClass().getResource("/fxml/attribute.fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Stage dialogStage = new Stage();
                    Scene dialogScene = new Scene(root, 387, 504);
                    dialogStage.getIcons().add(new Image(getClass().getResourceAsStream(FileSystemUtil.folderPath), 10, 10, false, false));
                    dialogStage.setScene(dialogScene);
                    dialogStage.show();
//                    监听窗体关闭
                    dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
//                            移除已打开的文件夹属性
                            AttributeXMLControl.removeFatArrayList(fat);
                        }
                    });
                }
            }
        });
        folderPaneFileBtn.setContextMenu(folderPaneContextMenu);

        //        设置文件夹双击事件
        folderPaneFileBtn.setOnMouseClicked(event -> {
            MouseButton button = event.getButton();
            //  双击事件
            if (event.getClickCount() == 2) {
//                    打开文件夹
//                    获取文件夹点击的路径值，展示该路径下的文件、文件夹内容
                path = path+ "\\" + folder.getFolderName() ;
                setFilePane(path);
//                设置当前路径
                setSearchPath();
//                设置当前树结构对象
                currentTreeItem = folder.getFolderTreeItem();
            } else if(event.getClickCount() == 1){
                if(!(flagButton==null)){
                    flagButton.setStyle("-fx-background-color:white");
                }
                folderPaneFileBtn.setStyle("-fx-background-color:rgba(51,204,255,0.2);");
                flagButton=folderPaneFileBtn;
            }
        });
    }

    //      设置文件右击事件 双击事件
    private void setRightMenuFile(Button filePaneFileBtn, FAT fat) {
        File file = ((File) fat.getObject());
        ContextMenu filePaneContextMenu = new ContextMenu();
        //        右击文件菜单初始化
        MenuItem M1 = new MenuItem("读文件");
        MenuItem M2 = new MenuItem("写文件");
        MenuItem M3 = new MenuItem("重命名");
        MenuItem M4 = new MenuItem("删除");
        MenuItem M5 = new MenuItem("属性");
        filePaneContextMenu.getItems().addAll(M1, M2, M3, M4, M5);
        filePaneContextMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (event.getTarget().equals(M1)) {
//                    判断该文件是否已经打开
                    if (fatService.checkOpenFile(fat)) {
                        GUI.contentOutGUI("该文件已经打开了，不能重复打开！");
                        return;
                    }
                    System.out.println("文件是否已经打开");
//                    显示文件内容
                    String contentText = file.getContent();
                    AnchorPane anchorPane = new AnchorPane();
                    ImageView imageView = new ImageView();
                    imageView.setImage(new Image(getClass().getResourceAsStream(FileSystemUtil.delPath), 17, 17, false, false));
                    Label label = new Label();
                    label.setText(contentText);
                    label.setPrefSize(500,270);
                    label.setLayoutY(30);
                    label.setAlignment(Pos.TOP_LEFT);
                    Label label1=new Label("",imageView);
                    label1.setLayoutX(470);
                    label1.setLayoutY(7);
                    anchorPane.getChildren().add(label);
                    anchorPane.getChildren().add(label1);
                    anchorPane.setStyle("-fx-background-color:#666666;" +         //设置背景颜色
                            "-fx-text-fill:#FF0000;" +        //设置字体颜色
                            "-fx-border-radius:0;" +         //设置边框圆角
                            "-fx-border-style:solid;"+      //设置边框样式
                            "-fx-border-width:2;"
                    );
                    label.setStyle("-fx-background-color:white;" +         //设置背景颜色
                            "-fx-text-fill:black;" +//设置字体颜色
                            "-fx-font-size:20;"+
                            "-fx-font-weight: bold;"+
                            "-fx-border-radius:0;" +         //设置边框圆角
                            "-fx-border-color:black;"+     //设置边框颜色
                            "-fx-border-style:solid;"+      //设置边框样式
                            "-fx-border-width:2;"         //设置边框宽度
                    );
                    Stage dialogStage = new Stage();
                    Scene dialogScene = new Scene(anchorPane, 500, 300);
                    dialogStage.setScene(dialogScene);
                    dialogStage.setTitle(file.getFileName());
                    dialogStage.initStyle(StageStyle.UNDECORATED);
                    label1.setOnMouseClicked(mouseEvent -> {
                        if(mouseEvent.getClickCount()==1)
                            dialogStage.close();
                        //             移除打开文件序列
                        fatService.removeOpenFile(fat);
//                    刷新文件打开状态表
                        setFileStatue();
                    });
                    dialogStage.show();
//                    窗体拖动
                    DragUtil.addDragListener(dialogStage,anchorPane);
//                    添加到打开文件序列中
                    fatService.addOpenFile(fat, 0);
//                    刷新文件打开状态表
                    setFileStatue();
                }   else if (event.getTarget().equals(M2)) {
//                    判断该文件是否可写
                    if (file.getType().equals("读")) {
                        GUI.contentOutGUI("该文件只能读，不能写！");
                        return;
                    }
//                    判断该文件是否已经打开
                    if (fatService.checkOpenFile(fat)) {
                        GUI.contentOutGUI("该文件已经打开了，不能重复打开！");
                        return;
                    }
//                   写文件
                    TextArea textArea = new TextArea();
                    textArea.setText(file.getContent());
                    textArea.setWrapText(true);
                    textArea.setPrefSize(500,270);
                    textArea.setLayoutY(30);
                    Button writeBtn = new Button("保存");
                    Button cancelBtn = new Button("取消");
                    AnchorPane anchorPane = new AnchorPane();
                    AnchorPane anchorPane1 = new AnchorPane();
                    anchorPane1.setPrefSize(496,50);
                    anchorPane1.setLayoutY(298);
                    anchorPane1.setLayoutX(2);
                    anchorPane1.setStyle("-fx-background-color:white;");
                    anchorPane1.getChildren().add(writeBtn);
                    anchorPane1.getChildren().add(cancelBtn);
                    writeBtn.setStyle("-fx-background-color: #D5D5D5;"+
                            "-fx-border-color: #C4C4C4;"+
                            "-fx-border-radius:0;"+
                            "-fx-border-style:solid;"+
                            "-fx-border-width:1px;");
                    writeBtn.setPrefSize(90,15);
                    writeBtn.setLayoutX(100);
                    writeBtn.setLayoutY(12);
                    cancelBtn.setStyle("-fx-background-color: #D5D5D5;"+
                            "-fx-border-color: #C4C4C4;"+
                            "-fx-border-radius:0;"+
                            "-fx-border-style:solid;"+
                            "-fx-border-width:1px;");
                    cancelBtn.setPrefSize(90,15);
                    cancelBtn.setLayoutX(300);
                    cancelBtn.setLayoutY(12);
                    ImageView imageView = new ImageView();
                    imageView.setImage(new Image(getClass().getResourceAsStream(FileSystemUtil.delPath), 17, 17, false, false));
                    Label label1=new Label("",imageView);
                    label1.setLayoutX(470);
                    label1.setLayoutY(7);
                    anchorPane.getChildren().add(textArea);
                    anchorPane.getChildren().add(label1);
                    anchorPane.getChildren().add(anchorPane1);
                    anchorPane.setStyle("-fx-background-color:#666666;" +         //设置背景颜色
                            "-fx-text-fill:#FF0000;" +        //设置字体颜色
                            "-fx-border-radius:0;" +         //设置边框圆角
                            "-fx-border-style:solid;"+      //设置边框样式
                            "-fx-border-width:2;"
                    );
                    textArea.setStyle("-fx-background-color:white;" +         //设置背景颜色
                            "-fx-text-fill:black;" +//设置字体颜色
                            "-fx-font-size:20;"+
                            "-fx-border-radius:0;" +         //设置边框圆角
                            "-fx-border-color:black;"+     //设置边框颜色
                            "-fx-border-style:solid;"+      //设置边框样式
                            "-fx-border-width:2;"         //设置边框宽度
                    );
                    Stage dialogStage = new Stage();
                    Scene dialogScene = new Scene(anchorPane, 500, 350);
                    dialogStage.setScene(dialogScene);
                    dialogStage.setTitle(file.getFileName());
                    dialogStage.initStyle(StageStyle.UNDECORATED);
                    dialogStage.show();
//                    窗体拖动
                    DragUtil.addDragListener(dialogStage,anchorPane);
                    label1.setOnMouseClicked(mouseEvent -> {
                        if(mouseEvent.getClickCount()==1)
                            dialogStage.close();
                        //             移除打开文件序列
                        fatService.removeOpenFile(fat);
//                    刷新文件打开状态表
                        setFileStatue();
                    });
//                    窗体拖动
                    DragUtil.addDragListener(dialogStage,anchorPane);
                    dialogStage.show();
//                    添加到打开文件序列中
                    fatService.addOpenFile(fat, 0);
//                    刷新文件打开状态表
                    setFileStatue();

                    writeBtn.setOnAction(e -> {
                        String text = textArea.getText();
                        boolean isOut = fatService.saveToModifyFATS2(fat, text);
                        if (!isOut) {
                            GUI.contentOutGUI("磁盘容量已满，保存失败！");
                        } else {
                            file.setReviseTime();
                            file.setSize(text.getBytes().length);
                        }
//                    移除打开文件序列
                        fatService.removeOpenFile(fat);
//                    刷新文件打开状态表
                        setFileStatue();
//                    刷新文件分配表状态
                        setFATStatue();
//                    设置颜色变化
                        setDiskBlockPane();
                        dialogStage.close();
                    });
                    cancelBtn.setOnAction(e -> {
                        dialogStage.close();
                        fatService.removeOpenFile(fat);
//                    刷新文件打开状态表
                        setFileStatue();
                    });

//                    监听窗体关闭
                    dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            System.out.println("移除打开文件序列");
//                    移除打开文件序列
                            fatService.removeOpenFile(fat);
//                    刷新文件打开状态表
                            setFileStatue();
                        }
                    });
                } else if (event.getTarget().equals(M3)) {
//                    文件重命名
                    TextArea textArea = new TextArea();
                    textArea.setText(file.getFileName());
                    textArea.setWrapText(true);
                    textArea.setPrefSize(500,270);
                    textArea.setLayoutY(30);
                    Button writeBtn = new Button("保存");
                    Button cancelBtn = new Button("取消");
                    AnchorPane anchorPane = new AnchorPane();
                    AnchorPane anchorPane1 = new AnchorPane();
                    anchorPane1.setPrefSize(496,50);
                    anchorPane1.setLayoutY(298);
                    anchorPane1.setLayoutX(2);
                    anchorPane1.setStyle("-fx-background-color:white;");
                    anchorPane1.getChildren().add(writeBtn);
                    anchorPane1.getChildren().add(cancelBtn);
                    writeBtn.setStyle("-fx-background-color: #D5D5D5;"+
                            "-fx-border-color: #C4C4C4;"+
                            "-fx-border-radius:0;"+
                            "-fx-border-style:solid;"+
                            "-fx-border-width:1px;");
                    writeBtn.setPrefSize(90,15);
                    writeBtn.setLayoutX(100);
                    writeBtn.setLayoutY(12);
                    cancelBtn.setStyle("-fx-background-color: #D5D5D5;"+
                            "-fx-border-color: #C4C4C4;"+
                            "-fx-border-radius:0;"+
                            "-fx-border-style:solid;"+
                            "-fx-border-width:1px;");
                    cancelBtn.setPrefSize(90,15);
                    cancelBtn.setLayoutX(300);
                    cancelBtn.setLayoutY(12);
                    ImageView imageView = new ImageView();
                    imageView.setImage(new Image(getClass().getResourceAsStream(FileSystemUtil.delPath), 17, 17, false, false));
                    Label label1=new Label("",imageView);
                    label1.setLayoutX(470);
                    label1.setLayoutY(7);
                    anchorPane.getChildren().add(textArea);
                    anchorPane.getChildren().add(label1);
                    anchorPane.getChildren().add(anchorPane1);
                    anchorPane.setStyle("-fx-background-color:#666666;" +         //设置背景颜色
                            "-fx-text-fill:#FF0000;" +        //设置字体颜色
                            "-fx-border-radius:0;" +         //设置边框圆角
                            "-fx-border-style:solid;"+      //设置边框样式
                            "-fx-border-width:2;"
                    );
                    textArea.setStyle("-fx-background-color:white;" +         //设置背景颜色
                            "-fx-text-fill:black;" +//设置字体颜色
                            "-fx-font-size:20;"+
                            "-fx-border-radius:0;" +         //设置边框圆角
                            "-fx-border-color:black;"+     //设置边框颜色
                            "-fx-border-style:solid;"+      //设置边框样式
                            "-fx-border-width:2;"         //设置边框宽度
                    );
                    Stage dialogStage = new Stage();
                    Scene dialogScene = new Scene(anchorPane, 500, 350);
                    dialogStage.setScene(dialogScene);
                    dialogStage.setTitle(file.getFileName());
                    dialogStage.initStyle(StageStyle.UNDECORATED);
                    dialogStage.show();
//                    窗体拖动
                    DragUtil.addDragListener(dialogStage,anchorPane);
                    label1.setOnMouseClicked(mouseEvent -> {
                        if(mouseEvent.getClickCount()==1)
                            dialogStage.close();
                    });
                    dialogStage.show();
                    writeBtn.setOnAction(e -> {
//                        重命名
                        String name = textArea.getText();
                        if (fatService.isFileNameSame(path, name)) {
                            GUI.contentOutGUI("名字重复！");
                            dialogStage.close();
                            return;
                        }
                        file.setFileName(name);
//                       刷新面板图标内容
                        setFilePane(path);
                        dialogStage.close();
                    });
                    cancelBtn.setOnAction(e -> {
                        dialogStage.close();
                    });

                } else if (event.getTarget().equals(M4)) {
//                    删除文件
                    int index = fatService.delete(fat);
                    if (index == 0) {
                        GUI.contentOutGUI("文件正在打开，不能删除！");
                    }
//                    把对应的颜色置为未使用
                    int colorIndex = file.getColorIndex();
                    fatService.setColorIndex(colorIndex);
//                    刷新文件分配表
                    setFATStatue();
//                    刷新面板图标内容
                    setFilePane(path);
//                    设置颜色变化
                    setDiskBlockPane();
                    changPie();
                } else if (event.getTarget().equals(M5)) {
//                    属性
                    Parent root = null;
//                    传值到令外一个窗体
                    AttributeXMLControl.addFatArrayList(fat);
                    try {
                        root = FXMLLoader.load(getClass().getResource("/fxml/attribute.fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Stage dialogStage = new Stage();
                    Scene dialogScene = new Scene(root, 387, 504);
                    Image i =new Image("/img/华为.png");
                    dialogStage.getIcons().add(i);
                    dialogStage.setScene(dialogScene);
                    dialogStage.show();
//                    监听窗体关闭
                    dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
//                            移除已打开的文件夹属性
                            AttributeXMLControl.removeFatArrayList(fat);
                        }
                    });
                }
            }
        });
        filePaneFileBtn.setContextMenu(filePaneContextMenu);

//        设置文件双击事件
        filePaneFileBtn.setOnMouseClicked(event -> {
            MouseButton button = event.getButton();
            //  双击事件
            if (event.getClickCount() == 2) {
                //                    判断该文件是否已经打开
                if (fatService.checkOpenFile(fat)) {
                    GUI.contentOutGUI("该文件已经打开了，不能重复打开！");
                    return;
                }
                System.out.println("文件是否已经打开");
//                    显示文件内容
                String contentText = file.getContent();
                AnchorPane anchorPane = new AnchorPane();
                ImageView imageView = new ImageView();
                imageView.setImage(new Image(getClass().getResourceAsStream(FileSystemUtil.delPath), 17, 17, false, false));
                Label label = new Label();
                label.setText(contentText);
                label.setPrefSize(500,270);
                label.setLayoutY(30);
                label.setAlignment(Pos.TOP_LEFT);
                Label label1=new Label("",imageView);
                label1.setLayoutX(470);
                label1.setLayoutY(7);
                anchorPane.getChildren().add(label);
                anchorPane.getChildren().add(label1);
                anchorPane.setStyle("-fx-background-color:#666666;" +         //设置背景颜色
                        "-fx-text-fill:#FF0000;" +        //设置字体颜色
                        "-fx-border-radius:0;" +         //设置边框圆角
                        "-fx-border-style:solid;"+      //设置边框样式
                        "-fx-border-width:2;"
                );
                label.setStyle("-fx-background-color:white;" +         //设置背景颜色
                        "-fx-text-fill:black;" +//设置字体颜色
                        "-fx-font-size:20;"+
                        "-fx-font-weight: bold;"+
                        "-fx-border-radius:0;" +         //设置边框圆角
                        "-fx-border-color:black;"+     //设置边框颜色
                        "-fx-border-style:solid;"+      //设置边框样式
                        "-fx-border-width:2;"         //设置边框宽度
                );
                Stage dialogStage = new Stage();
                Scene dialogScene = new Scene(anchorPane, 500, 300);
                dialogStage.setScene(dialogScene);
                dialogStage.setTitle(file.getFileName());
                dialogStage.initStyle(StageStyle.UNDECORATED);
                label1.setOnMouseClicked(mouseEvent -> {
                    if(mouseEvent.getClickCount()==1)
                        dialogStage.close();
                    //             移除打开文件序列
                    fatService.removeOpenFile(fat);
//                    刷新文件打开状态表
                    setFileStatue();
                });
                dialogStage.show();
//                    窗体拖动
                DragUtil.addDragListener(dialogStage,anchorPane);
//                    添加到打开文件序列中
                fatService.addOpenFile(fat, 0);
//                    刷新文件打开状态表
                setFileStatue();
            } else if(event.getClickCount() == 1){
                if(!(flagButton==null)){
                    flagButton.setStyle("-fx-background-color:white");
                }
                filePaneFileBtn.setStyle("-fx-background-color:rgba(51,204,255,0.2);");
                flagButton=filePaneFileBtn;
            }
        });
    }

    //      设置树结构
    private void setTree() {
        Node fileDiskIcon = new ImageView(new Image(getClass().getResourceAsStream(FileSystemUtil.diskPath), 20, 20, false, false));
        treeDiskItem = new TreeItem<String>("D:", fileDiskIcon);
        currentTreeItem = treeDiskItem;
//        是否默认打开文件夹
        treeDiskItem.setExpanded(false);
        treeView.setRoot(currentTreeItem);
//        遍历该磁盘下的文件夹列表
        String location = "D:";
//        递归调用遍历文件夹
        setFolderTree(treeDiskItem, location);
        if (isFirst) {
            isFirst = false;
//            点击事件
            treeView.setOnMouseClicked(event -> {
                MouseButton button = event.getButton();
//                右击事件
                if(button == MouseButton.SECONDARY){
                    Node node = event.getPickResult().getIntersectedNode();
                    if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
                       rightClickTreeItem = treeView.getSelectionModel().getSelectedItem();
//                        设置当前的路径
                        rightClickTreePath = getPath(rightClickTreeItem);
                        System.out.println("右击的树路径" + rightClickTreePath);
                        double x = event.getScreenX();
                        double y = event.getScreenY();
                        TreeContextMenu.show(treeView, x, y);
                    }
                }else if (event.getClickCount() == 1) {
//                    单击事件
                    TreeContextMenu.hide();
                    Node node = event.getPickResult().getIntersectedNode();
                    if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
                        currentTreeItem = treeView.getSelectionModel().getSelectedItem();
//                        设置当前的路径
                        path = getPath(currentTreeItem);
                        System.out.println("单击的路径" + path);
//                         设置当前路径
                        setSearchPath();
//                        获取文件树目录点击的路径值，展示该路径下的文件、文件夹内容
                        setFilePane(path);
                    }
                }
            });
        }
    }

    //      获取当前树节点的路径,进入该文件夹
    private String getPath(TreeItem<String> treeItem) {
        String name = "";
        String path = treeItem.getValue();
        if(!treeItem.equals(treeDiskItem)){
            while (!treeItem.equals(treeDiskItem) || name == null) {
                treeItem = treeItem.getParent();
                path =  "\\"+name + path;
                name = treeItem.getValue();
            }
            path = "D:" + path;
        }
        System.out.println(path);
        return path;
    }

    //      递归调用遍历文件夹
    private void setFolderTree(TreeItem<String> treeItem, String location) {
        List<Folder> folderList = fatService.getFolders(location);
        for (int i = 0; i < folderList.size(); i++) {
            Folder folder = folderList.get(i);
            String folderName = folder.getFolderName();
            String location1 = folderList.get(i).getLocation() + "\\"+ folderName;
            Node folderPicIcon = new ImageView(new Image(getClass().getResourceAsStream(FileSystemUtil.folderPath), 18, 18, false, false));
            TreeItem<String> treeFolderItem = new TreeItem<String>(folderName, folderPicIcon);
            treeItem.getChildren().add(treeFolderItem);
//            递归调用遍历
            setFolderTree(treeFolderItem, location1);
        }
    }

    //      设置打开文件状态
    private void setFileStatue() {
        fileTable.getItems().clear();
        openFiles = FATService.getOpenFiles();
        fileName.setCellValueFactory(new PropertyValueFactory<File, String>("fileName"));
        fileStyle.setCellValueFactory(new PropertyValueFactory<File, String>("type"));
        startNum.setCellValueFactory(new PropertyValueFactory<File, Integer>("diskNum"));
        fileLocation.setCellValueFactory(new PropertyValueFactory<File, String>("location"));
        for (int i = 0; i < openFiles.getFiles().size(); i++) {
            FilesModels.add(openFiles.getFiles().get(i).getFile());
        }
        fileTable.setItems(FilesModels);
        fileTable.refresh();
    }

    //      设置盘块号状态
    private void setFATStatue() {
        fatTable.getItems().clear();
        num.setCellValueFactory(new PropertyValueFactory<FAT, Integer>("blockNum"));
        index.setCellValueFactory(new PropertyValueFactory<FAT, Integer>("index"));
        for (int i = 0; i < myFAT.length; i++) {
            FATsModels.add(myFAT[i]);
        }
        fatTable.setItems(FATsModels);
        fatTable.refresh();
    }

    //      初始化
    private void init() {
        //       文件分配表对象
        fatService = new FATService();
        fatService.initFAT();
        myFAT = FATService.getMyFAT();
    }
}
