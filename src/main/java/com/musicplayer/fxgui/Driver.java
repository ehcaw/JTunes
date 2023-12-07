package com.musicplayer.fxgui;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;

/* Driver class
 * Everything runs through here
*/
public class Driver extends Application {
	@Override
	public void start(Stage stage) throws IOException {
		try{
		Parent root = FXMLLoader.load(getClass().getResource("Scene.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		stage.setResizable(false);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				Platform.exit();
				System.exit(0);	
			}		
		});
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}	
	public static void main(String[] args) {
		launch(args);
	}
}
