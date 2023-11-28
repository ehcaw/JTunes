package com.musicplayer.fxgui;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ContextMenuEvent;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Region;

public class PlaylistMenu extends Application{
    private SongTile songTile;
    ScrollPane scrollPane;
    VBox vbox = new VBox(); 
    Stage stage = new Stage();
    public PlaylistMenu(SongTile s, ArrayList<PlaylistTile> pa){
        songTile = s;
        for(PlaylistTile x: pa){
            PlaylistButton pb = new PlaylistButton(x);
            pb.setPrefSize(180,100);

            pb.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent e){
                    pb.getPlaylistTile().addPathToJson(songTile.getSong().getFilePath());
                    stage.close();
                }

            });

            vbox.getChildren().addAll(pb); 
        }
        start(stage);
    }
    public void start(Stage primaryStage){
        vbox.setPadding(new Insets(20));
        scrollPane = new ScrollPane(vbox);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(false);
        scrollPane.prefHeightProperty().bind(vbox.heightProperty());
        Scene scene = new Scene(scrollPane ,200, 400);
        if(vbox.getChildren().size() > 1){
            primaryStage.setTitle("Choose a playlist to add to!");
        }
        else{
            primaryStage.setTitle("Create a playlist first!");
        }
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    public VBox getVBox(){
        return vbox;
    }
    public static void main(String[] args){
        launch(args);
    }

    class PlaylistButton extends Button{
        PlaylistTile playlistTile;
        public PlaylistButton(PlaylistTile pt){
            playlistTile = pt;
            setText(pt.getName());
        }
        public PlaylistTile getPlaylistTile(){
            return playlistTile;
        }
    }
}
