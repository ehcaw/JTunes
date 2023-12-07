/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.musicplayer.fxgui;

import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
/**
 *
 * @author Anhadh Sran, Ryan Nguyen
 */
public class SongTile extends HBox{
    Button playButton = new Button();
    Button addButton = new Button();
    Button deleteButton = new Button();
    private Label label = new Label(); 
    private Song song; 
    //Constructor which takes a Song object; lots of formatting for the tile itself
    public SongTile(Song s) {
        song = s;
        this.setPrefSize(800, 100);
        this.setStyle("-fx-background-color:grey; -fx-text-fill:white;");
        playButton.setText("|>");
        playButton.setVisible(false);
        playButton.setPrefSize(30,30);
        addButton.setText("+");
        addButton.setVisible(false);
        addButton.setPrefSize(30,30);
        Region spacer = new Region();
        Region spacer2 = new Region();
        Region spacer3 = new Region();
        spacer2.setPrefWidth(5);
        spacer.setPrefWidth(5);
        spacer3.setPrefWidth(30);
        deleteButton.setText("T");
        deleteButton.setPrefSize(30,30);
        deleteButton.setVisible(false);
        label.setText(s.getSongTitle() + "  -  " + s.getSongArtist() + "  -  " + s.getAlbumName() + "  -  " + s.getDuration());
        label.setStyle("-fx-background-color: grey;");
        label.setPrefSize(550,100);
        this.getChildren().addAll(playButton,spacer2, addButton, spacer, label, spacer3, deleteButton);
        this.setStyle("-fx-background-color: #2F3D40;");   //#578E87
        label.setStyle("-fx-background-color: #2F3D40;");
        label.setTextFill(Color.WHITE);
        playButton.setStyle("-fx-background-color: #C3CEDA;");
        addButton.setStyle("-fx-background-color: #C3CEDA;"); 
        deleteButton.setStyle("-fx-background-color: #C3CEDA;"); 
        Insets i = new Insets(35, 5, 35, 5);
        setMargin(playButton, i);
        setMargin(addButton, i);
        setMargin(deleteButton, i);
    }
    public SongTile cloneSongTile(SongTile s){
        SongTile st = new SongTile(s.getSong());
        st.getChildren().remove(1);
        return st;
    }
    //Getter / Setter methods
    public Song getSong(){
        return song;
    }
    public Label getLabel(){
        return label;
    }
    public Button getDeleteButton(){
        return deleteButton;
    }
    public Button getPlayButton(){
        return playButton;
    }
    public Button getAddButton(){
        return addButton;
    }
}
