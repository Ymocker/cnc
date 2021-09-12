package com.tv.cnc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import javafx.scene.image.Image;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Stage primaryStage;
    
    private void setPrimaryStage(Stage stage) {
        App.primaryStage = stage;
    }
    
    static public Stage getPrimaryStage() {
        return App.primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        
        setPrimaryStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("cnc.fxml"));
        
        primaryStage.setTitle("CNC");
        primaryStage.setScene(new Scene(root));
        
        /*InputStream iconStream = getClass().getResourceAsStream("prnlogo.png");
        Image image = new Image(iconStream);
        primaryStage.getIcons().add(image);*/

        primaryStage.show();
        
    }
/*
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
*/
    public static void main(String[] args) {
        launch();
    }

}