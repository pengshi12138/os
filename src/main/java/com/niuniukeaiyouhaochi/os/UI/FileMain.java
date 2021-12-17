package com.niuniukeaiyouhaochi.os.UI;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FileMain extends Application {

    public static Stage stage = new Stage();
    public static Parent page;
    public static Scene scene = null;
    public static Stage LoginStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        LoginStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/view.fxml"));
        primaryStage.setTitle("File");
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);//设定窗口无边框
        primaryStage.show();
    }

    public void loginFileController() throws Exception {
        start(stage);
    }
}
