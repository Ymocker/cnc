package com.tv.cnc;

//import java.io.IOException;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AppController {

    Properties appProps = new Properties();
    FileChooser fileChooser = new FileChooser();
    File file;
    WorkObject workObj;

    @FXML
    public Label fxFile;


    @FXML
    public TextField fxHeight;
    @FXML
    public TextField fxAngle;
    @FXML
    public TextField fxLength;
    @FXML
    public CheckBox fxRampCheck;

@FXML
private void clickOpen() {
    Stage s = App.getPrimaryStage();

    fileChooser.setTitle("Open G-code File");
    FileChooser.ExtensionFilter extFilterO = new FileChooser.ExtensionFilter("G-codes", "*.*");
    fileChooser.getExtensionFilters().add(extFilterO);
/*
    if (new File(appProps.getProperty("dir")).isDirectory()) { // if someone sets illegal directory name in the properties file
        fileChooser.setInitialDirectory(new File(appProps.getProperty("dir")));
    } else {
        fileChooser.setInitialDirectory(new File("c:"));
    }
*/
    file = fileChooser.showOpenDialog(s); //choose file with fileOpenDialog
    if (file != null) {
        fxFile.setText(file.getName());
    }

    workObj = new WorkObject(file);
    /*
    try {
            //File file = new File("/Users/prologistic/file.txt");
            //создаем объект FileReader для объекта File
            FileReader fr = new FileReader(file);
            //создаем BufferedReader с существующего FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr);
            // считаем сначала первую строку
            String line = reader.readLine();
            while (line != null) {




                System.out.println(line);
                // считываем остальные строки в цикле
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
} // clickOpen

@FXML
private void makeFile() {
    workObj.height = Float.parseFloat(fxHeight.getText())/2;
    workObj.ramp = fxRampCheck.isSelected();
    workObj.angle = Float.parseFloat(fxAngle.getText());
    workObj.length = Float.parseFloat(fxLength.getText());

    try {
        workObj.makeOut();
    }
    catch (IOException e) {
        e.printStackTrace();
    }


    /*
    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    alert.setTitle("Information");
    alert.setHeaderText(null);
    alert.setContentText("Hello World!");
    alert.showAndWait();
*/
}

//////////////////////////////////////////////////////////////////////////////
} // End of AppController