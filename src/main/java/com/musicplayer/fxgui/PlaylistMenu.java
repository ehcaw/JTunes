package com.musicplayer.fxgui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/* Class for displaying the available playlists to add a song to
 * 
 * @author Ryan Nguyen
 */
public class PlaylistMenu extends Application{
    private SongTile songTile;
    ScrollPane scrollPane;
    VBox vbox = new VBox(); 
    public Stage stage = new Stage();
    //The menu on creation adds all existing playlists to a form
    //You can select one of the playlists to add the corresponding song to
    public PlaylistMenu(SongTile s, ArrayList<PlaylistTile> pa){
        for(PlaylistTile x: pa){
            System.out.println(x.getName());
            PlaylistButton pb = new PlaylistButton(x);
            pb.setPrefSize(180,100);
            pb.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent e){
                    pb.getPlaylistTile().addPathToJson(s.getSong().getFilePath());
                    songTile = s.cloneSongTile(s);
                    songTile.getChildren().remove(1);
                    x.getPlaylistPage().getVBox().getChildren().add(songTile);
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
        Scene scene = new Scene(scrollPane ,300, 400);
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
    public SongTile getSongTile(){
        return songTile;
    }
    public static void main(String[] args){
        launch(args);
    }
    //custom button class for the menu
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
