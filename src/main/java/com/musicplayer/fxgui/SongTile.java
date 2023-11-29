/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.musicplayer.fxgui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.nio.file.Paths;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.Border;
import java.util.ArrayList;

import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ContextMenu;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
    private Label label = new Label(); 
    private Song song;
    private int age; 
    //Constructor which takes a Song object; lots of formatting for the tile itself
    public SongTile(Song s) {
        song = s;
        this.setPrefSize(800, 100);
        this.setStyle("-fx-background-color:grey; -fx-text-fill:white;");
        playButton.setText("|>");
        playButton.setPrefSize(30,30);
        addButton.setText("+");
        addButton.setPrefSize(30,30);
        Region spacer = new Region();
        Region spacer2 = new Region();
        spacer2.setPrefWidth(5);
        spacer.setPrefWidth(5);
        label.setText(s.getSongTitle() + "  -  " + s.getSongArtist() + "  -  " + s.getAlbumName() + "  -  " + s.getDuration());
        label.setStyle("-fx-background-color: grey;");
        label.setPrefSize(550,100);
        this.getChildren().addAll(playButton,spacer2, addButton, spacer, label);
        this.setStyle("-fx-background-color: #0c151b;");
        label.setStyle("-fx-background-color: #0c151b;");
        label.setTextFill(Color.WHITE);
        playButton.setStyle("-fx-background-color: #C3CEDA;");
        addButton.setStyle("-fx-background-color: #C3CEDA;"); 
        Insets i = new Insets(35, 5, 35, 5);
        setMargin(playButton, i);
        setMargin(addButton, i);
    }
    //Getter / Setter methods
    public Song getSong(){
        return song;
    }
    public Label getLabel(){
        return label;
    }
    public void setAge(int num){
        age = num;
    }
}
