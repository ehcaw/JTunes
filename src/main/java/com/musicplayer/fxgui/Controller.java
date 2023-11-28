package com.musicplayer.fxgui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.scene.control.MenuItem;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;




public class Controller implements Initializable {
    @FXML
    private Button playButton;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider timeSlider;
    @FXML
    private Pane middlePane;
    @FXML
    private Pane bottomPane;
    @FXML
    private Label songLabel;
    @FXML
    private Button fileButton;
    @FXML
    private Button returnButton;
    @FXML
    private Label topPaneLabel;
    @FXML 
    private Pane topPane;

    private Media media;
    private MediaPlayer player;
    private int songIndex = 0;
    private File directory;
	private File[] files;
	private ArrayList<File> songFiles;
    private ArrayList<Song> songObjects;
    private int numSongs;
    private ScrollPane scrollPane;
    private BorderPane borderPane;
    private VBox vbox;
    private Pane homePage = new Pane();
    private Pane libraryPage = new Pane();
    private ArrayList<PlaylistTile> playlistArray= new ArrayList<PlaylistTile>();
    private int playlistCount = 0;
    private ArrayList<Pane> lastSeenPanes = new ArrayList<Pane>();
    private int seenPanesIndex = 0;
    private GridPane gp; 
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songFiles = new ArrayList<File>();
        songObjects = new ArrayList<Song>();
        directory = new File("src/main/resources/songfolder");
        files = directory.listFiles();
        numSongs = files.length;
        System.out.println(files.toString());
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if(!files[i].getName().substring(0, 1).equals(".") && !files[i].getPath().equals("src/main/resources/songfolder/TEST_One minute of silence (ID 0917)_BSB 9.47.53 AM.mp3")){
                    songFiles.add(files[i]);
                    Song s = new Song(files[i].getPath());
                    songObjects.add(s);
                    System.out.println(files[i]);
                }
            }
        }
        numSongs = songFiles.size();
        if(numSongs > 0){
            media = new Media(songFiles.get(songIndex).toURI().toString());
            player = new MediaPlayer(media);
            player.setVolume(50);
        }
        vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(5);
        vbox.setStyle("-fx-background-color: #04333C");
        
        scrollPane = new ScrollPane(vbox);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(false);
        scrollPane.prefHeightProperty().bind(homePage.heightProperty());
        scrollPane.setPrefWidth(800);
        scrollPane.setStyle("-fx-background-color:  #04333C");
        


        homePage.getChildren().add(scrollPane);
        homePage.setPrefSize(800,400);
        homePage.setStyle("-fx-background-color: #04333C;");

        homepageCreation();
        
        middlePane.getChildren().add(homePage);
        lastSeenPanes.add(homePage);
        this.libraryCreation();

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                player.setVolume(volumeSlider.getValue() * 0.01);
            }
        });

        initializeTimeSlider();     
    }

    public void homepageCreation(){
        
        for(int i = 0; i < songObjects.size(); i ++){
            System.out.println(songObjects.get(i).getSongTitle());
            SongTile st = new SongTile(songObjects.get(i));
            int j = i;
            setupPlayButton(st);
            st.addButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent e){
                    PlaylistMenu pm = new PlaylistMenu(st, playlistArray);
                }
            });
            vbox.getChildren().add(st);
        }
    }
    
    public void initializeTimeSlider() {
        player.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable observable) {
                updatesValues();
            }
        });

        timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (timeSlider.isPressed()) {
                    player.seek(player.getMedia().getDuration().multiply(timeSlider.getValue() / 100)); 
                }
            }
            
        });    
    }

    public void playMedia() {
        if (player.getStatus() == Status.PLAYING) {
            player.pause();
            playButton.setText("||");
        }
        else {
            setLabel();
            playButton.setText("|>");
            player.play();


        }
    }
    public void setLabel(){
        try{
            String p = player.getMedia().getSource();
            System.out.println(p);
            Path path = Paths.get(URI.create(p));
            AudioFile af = AudioFileIO.read(new File(path.toString()));
            Tag tag = af.getTag(); 
            songLabel.setText("  " + tag.getFirst(FieldKey.TITLE));

        }
        catch(Exception e){}
    }
    public void prevMedia() {
        if (songIndex > 0) {
            songIndex--;
            player.stop();
            media = new Media(songFiles.get(songIndex).toURI().toString());
            player = new MediaPlayer(media);
            player.play();
            initializeTimeSlider();
            setLabel();
        }
        else {
            songIndex = songFiles.size() - 1;
            player.stop();
            media = new Media(songFiles.get(songIndex).toURI().toString());
            player = new MediaPlayer(media);
            setLabel();
            player.play();
            initializeTimeSlider();
            
        }
    }
    public void nextMedia() {
        if (songIndex < songFiles.size() - 1) {
            songIndex++;
            player.stop();
            media = new Media(songFiles.get(songIndex).toURI().toString());
            player = new MediaPlayer(media);
            setLabel();
            player.play();
            initializeTimeSlider();
        }
        else {
            songIndex = 0;
            player.stop();
            media = new Media(songFiles.get(songIndex).toURI().toString());
            player = new MediaPlayer(media);
            setLabel();
            player.play();
            initializeTimeSlider();
        }
    }
    public void addSong(){
        if(middlePane.getChildren().get(0) == homePage){
            SongChooser sc = new SongChooser();
            File file = sc.getFile();
            String type = "";
            String fileName = file.toString();
            int index = fileName.lastIndexOf(".");
            if(index > 0){
                type = fileName.substring(index + 1);
        }
        if(type.equals("mp3")){
            songFiles.add(file);
            Song s = new Song(file.getPath());
            songObjects.add(s);
            SongTile st = new SongTile(s);
            int j = numSongs;
            setupPlayButton(st);
            st.addButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent e){
                    PlaylistMenu pm = new PlaylistMenu(st, playlistArray);
                    File d = new File("src/main/resources/playlistFiles");
                    File[] f = d.listFiles(); 
                    String sourcePath = s.getFilePath(); 
                    String destinationPath = "src/main/resources/songfolder/";
                    try{
                        Path source = Paths.get(sourcePath);
                        Path destination = Paths.get(destinationPath);
                        Files.copy(source, destination);
                    }
                    catch(IOException excep){
                        excep.printStackTrace();
                    }
                }
            });
            vbox.getChildren().add(st);
            numSongs += 1;
            String directoryPath = directory.getPath();
            Path d = Paths.get(directoryPath);
            Path f = Paths.get(file.getPath());
            try{
                Path targetPath = d.resolve(f.getFileName());
                Files.copy(f, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            catch(Exception e){}
            }
        }
        else{
            PlaylistTile pt = new PlaylistTile(playlistCount);
            PlaylistForm pf = new PlaylistForm(pt); 
            int row = playlistCount / 3;
            int col = playlistCount % 3;
            pt.getButton().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e){
                        middlePane.getChildren().clear();
                        middlePane.getChildren().add(pt.createPlaylistPage());
                        for(SongTile s: pt.getPlaylistPage().getSongList()){
                            setupPlayButton(s);
            }
                        lastSeenPanes.add(pt.createPlaylistPage());
                        seenPanesIndex += 1;
                        if(pt.getName().charAt(0) == '\"'){
                            topPaneLabel.setText(pt.getName().substring(1, pt.getName().length() - 1));
                        }
                        else{
                            topPaneLabel.setText("  " + pt.getName().replace('"', ' '));
                        }
                    }
            });
            pt.createJSON(playlistCount);
            playlistCount++;
            playlistArray.add(pt);
            gp.add(pt.getButton(), col, row);
    }
}
    public void setupPlayButton(SongTile s){
        s.playButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent e){
                    if(player.getStatus() != Status.PLAYING){
                        String convertedFilePath = Paths.get(s.getSong().getFilePath()).toUri().toString();
                        media = new Media(convertedFilePath);
                        player = new MediaPlayer(media);
                        setLabel();
                        playButton.setText("|>");
                        player.play();
                    }
                   else{
                        player.pause(); 
                        String convertedFilePath = Paths.get(s.getSong().getFilePath()).toUri().toString();
                        media = new Media(convertedFilePath);
                        player = new MediaPlayer(media);
                        setLabel();
                        playButton.setText("|>");
                        player.play();
                   }
                }
            });
    }

    public void home() {
        middlePane.getChildren().clear();
        middlePane.getChildren().add(homePage);
        lastSeenPanes.add(homePage);
        topPaneLabel.setText("Welcome to your Home Directory! Add a song to get started!");
        seenPanesIndex += 1;
    }
    public void libraryCreation(){
        gp = new GridPane();
        gp.setPadding(new Insets(50));
        gp.setPrefSize(800,400);
        gp.setHgap(50);
        gp.setVgap(50);

        File d = new File("src/main/resources/playlistFiles");
        File[] fa = d.listFiles(); 
        System.out.println(fa.length + " is the size of the playlistfiels");
        for(File f: fa){
            String type = "";
            String fileName = f.toString();
            int index = fileName.lastIndexOf(".");
            if(index > 0){
                type = fileName.substring(index + 1);
            }
            if(type.equals("json")){
                String path = f.getPath(); 
                String title = retrieveJsonObject(path, "name").toString();
                System.out.println(title);
                PlaylistTile pt = new PlaylistTile(path, title, playlistCount);
                int row = playlistCount / 3;
                int col = playlistCount % 3;
                pt.getButton().setText(pt.getName());
                pt.getButton().setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e){
                            middlePane.getChildren().clear();
                            middlePane.getChildren().add(pt.createPlaylistPage());
                            for(SongTile s: pt.getPlaylistPage().getSongList()){
                                setupPlayButton(s);
                            }
                            lastSeenPanes.add(pt.createPlaylistPage());
                            seenPanesIndex += 1;
                            if(pt.getName().charAt(0) == '\"'){
                            topPaneLabel.setText(pt.getName().substring(1, pt.getName().length() - 1));
                        }
                        else{
                            topPaneLabel.setText("  " + pt.getName().replace('"', ' '));
                        }
                        }
                });
                playlistCount++; 
                playlistArray.add(pt);
                gp.add(pt.getButton(), col, row);
            }
        }
        ScrollPane sp = new ScrollPane(gp);
        sp.setPrefSize(800, 400);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setFitToWidth(false);
        sp.prefHeightProperty().bind(homePage.heightProperty());
        libraryPage.getChildren().add(sp);
    }
    public JsonElement retrieveJsonObject(String path, String key){
        try(FileReader fr = new FileReader(path)){
            JsonReader jr = new JsonReader(fr);
            JsonObject jo = JsonParser.parseReader(jr).getAsJsonObject(); 
            JsonElement jp = jo.get("name");
            return jp;
        }
        catch(IOException | JsonParseException e){
            e.printStackTrace();
            return null;
        }
}

    public void library() {
        middlePane.getChildren().clear();
        middlePane.getChildren().add(libraryPage);
        lastSeenPanes.add(libraryPage);
        seenPanesIndex += 1;
        topPaneLabel.setText("   Welcome to your Playlists!");
    }
    public void returnFunction(){
        if(seenPanesIndex > 0){
            middlePane.getChildren().clear();
            middlePane.getChildren().add(lastSeenPanes.get(seenPanesIndex - 1));
            seenPanesIndex -= 1;
        }
    }
    public void updatesValues() {
        Platform.runLater(new Runnable() {
            public void run() {
                timeSlider.setValue((player.getCurrentTime().toMillis() / player.getTotalDuration().toMillis()) * 100);
            }
        });
    }
}
