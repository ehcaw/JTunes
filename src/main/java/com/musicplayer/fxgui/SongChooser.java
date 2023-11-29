package com.musicplayer.fxgui;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import java.io.File;

/* File chooser class for adding a song to the library
 * @author Ryan Nguyen
 */
public class SongChooser extends Application{
    Stage stage = new Stage();
    File f;
    public SongChooser(){
        start(stage);
    }
    //overriden start method because SongChooser extends Application
    public void start(Stage primaryStage){
        primaryStage.setTitle("Open File");
        FileChooser fc = new FileChooser();
        fc.setTitle("Pick an mp3 to add!");
        fc.getExtensionFilters().add(new ExtensionFilter("Audio Files", "*.mp3"));
        f = fc.showOpenDialog(primaryStage);
    }
    //Getter method
    public File getFile(){
        return f;
    }

    public static void main(String[] args){
        launch(args);
    }
}
