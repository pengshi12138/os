package com.niuniukeaiyouhaochi.os.UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class ProcessMain extends Application {

    public static Stage stage = new Stage();
    public static Parent page;
    public static Scene scene = null;
    public static Stage LoginStage;
    @Override
    public void start(Stage primaryStage) throws Exception {

        LoginStage = primaryStage;
        page = FXMLLoader.load(getClass().getResource("/fxml/sample.fxml"));
        scene = new Scene(page);
        scene.getStylesheets().add(getClass().getResource("/css/sample.css").toExternalForm());
        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
        primaryStage.initStyle(StageStyle.TRANSPARENT);//设定窗口无边框
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void loginProcessController() throws Exception {
        start(stage);
    }
}
