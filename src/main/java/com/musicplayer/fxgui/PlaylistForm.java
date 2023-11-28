package com.musicplayer.fxgui;

import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;


public class PlaylistForm extends Application{
    Stage stage = new Stage();
    GridPane gridPane = new GridPane();
    TextField playlistName;
    Button button;
    String text;
    PlaylistTile playlistTile;
    public PlaylistForm(PlaylistTile pt){
        playlistTile = pt;
        start(stage);
    }

    public void start(Stage primaryStage){
        primaryStage.setTitle("Make a playlist!");
        Label playlistNameLabel = new Label("Enter Playlist Name");
        playlistNameLabel.setPrefSize(150,100);
        playlistName = new TextField();
        playlistName.setPrefSize(200, 100);
        gridPane.setPrefSize(400, 400);
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(20));
        gridPane.add(playlistNameLabel, 0, 1);
        gridPane.add(playlistName, 1, 1);
        button = new Button("Create Playlist");
        button.setPrefSize(100,50);
        

        gridPane.add(button, 0,2);
        gridPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(gridPane, 400, 150);
        primaryStage.setScene(scene);
        primaryStage.show();
        button.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e){
                String s = playlistName.getText(); 
                playlistTile.getButton().setText(s);
                playlistTile.setName(s); 
                playlistTile.addNameToJson();
                stage.close();
            }
        });
    
    }
    public String getText(){
        return playlistName.getText();
    }
    public String getTextString(){
        return text;
    }
    public Button getButton(){
        return button;
    }
    public Stage getStage(){
        return stage;
    }
    public void setText(String s){
        text = s;
    }
    public static void main(String[] args){
        launch(args);
    }
}
