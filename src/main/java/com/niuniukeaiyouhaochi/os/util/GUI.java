package com.niuniukeaiyouhaochi.os.util;

import com.niuniukeaiyouhaochi.os.Listener.DragUtil;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GUI {
    //    弹窗警告
    public static void contentOutGUI(String context){
        //                    显示文件内容
        AnchorPane anchorPane =  new AnchorPane();
        anchorPane.setPrefSize(300,40);
        anchorPane.setStyle("-fx-background-color: #eeeeee;");
        ImageView imageView = new ImageView();
        ImageView imageView1 = new ImageView();
        imageView.setImage(new Image(FileSystemUtil.warnPath, 25, 25, false, false));
        imageView1.setImage(new Image(FileSystemUtil.delPath, 10, 10, false, false));
        Label label1=new Label("",imageView1);
        Label label = new Label();
        label.setText(context);
        label.setPrefSize(250,30);
        label.setLayoutX(44);
        label.setLayoutY(8);
        label.setStyle("-fx-font-weight: bold;"+"-fx-text-fill: red;");
        label1.setLayoutX(235);
        label1.setLayoutY(3);
        anchorPane.getChildren().add(label);
        anchorPane.getChildren().add(imageView);
        anchorPane.getChildren().add(label1);
        Stage dialogStage = new Stage();
        Scene dialogScene = new Scene(anchorPane,250,40);
        imageView.setLayoutX(10);
        imageView.setLayoutY(10);
        dialogStage.setScene(dialogScene);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        //                    窗体拖动
        DragUtil.addDragListener(dialogStage,anchorPane);
        dialogStage.show();
        label1.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount()==1)
                dialogStage.close();
        });
    }

}
