package com.musicplayer.fxgui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Comparator;
import java.util.Collections;
import java.util.Arrays;
import java.io.FileReader;

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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/* Heart of the program; everything is controlled through here
 * 
 * @author Ryan Nguyen, Anhadh Sran
 */
public class Controller implements Initializable {
    //All components controlled through the FXML File are listed here
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
    @FXML
    private Button sortButton;

//All attributes of controller; not controlled by the FXML
    private Media media;
    private MediaPlayer player;
    private int songIndex = 0;
    private File directory;
	private File[] files;
	private ArrayList<File> songFiles;
    private ArrayList<Song> songObjects;
    private int numSongs;
    private ScrollPane scrollPane;
    private VBox vbox;
    private Pane homePage = new Pane();
    private Pane libraryPage = new Pane();
    private ArrayList<PlaylistTile> playlistArray= new ArrayList<PlaylistTile>();
    private int playlistCount = 0;
    private ArrayList<Pane> lastSeenPanes = new ArrayList<Pane>();
    private ArrayList<SongTile> songTiles = new ArrayList<SongTile>();
    private ArrayList<SongTile> copySongTiles = new ArrayList<SongTile>();
    private int seenPanesIndex = 0;
    private GridPane gp; 
    private int timesSorted = 0; 
    private PlaylistTile currentPlaylist;
    /* essentially start method for the application
     * starts by scanning through the song folder directory and initializing all SongTile Objects 
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songFiles = new ArrayList<File>();
        songObjects = new ArrayList<Song>();
        directory = new File("src/main/resources/songfolder");
        files = directory.listFiles();
        numSongs = files.length;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if(files[i].getPath().equals("src/main/resources/songfolder/TEST_One minute of silence (ID 0917)_BSB 9.47.53 AM.mp3")){
                    songFiles.add(files[i]);
                }
                if(!files[i].getName().substring(0, 1).equals(".") && !files[i].getPath().equals("src/main/resources/songfolder/TEST_One minute of silence (ID 0917)_BSB 9.47.53 AM.mp3")){
                    songFiles.add(files[i]);
                    Song s = new Song(files[i].getPath());
                    songObjects.add(s);
                }
            }
        }
        //setting up the media player
        numSongs = songFiles.size();
        if(numSongs > 0){
            media = new Media(songFiles.get(songIndex).toURI().toString());
            player = new MediaPlayer(media);
            player.setVolume(50);
        }

        //editing the style of the pane
        vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(5);
        vbox.setStyle("-fx-background-color: #04333C");
        vbox.setPrefSize(800,400);
        
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
        //setting up volume slider
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                player.setVolume(volumeSlider.getValue() * 0.01);
            }
        });

        initializeTimeSlider();     
    }
    //this method adds the SongTiles so that the page can display
    //Also sets up the various buttons that the tile has (play, add to playlist)
    public void homepageCreation(){
        for(int i = 0; i < songObjects.size(); i ++){
            SongTile st = new SongTile(songObjects.get(i));
            songTiles.add(st); 
            copySongTiles.add(st);
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
    //sets up the playback slider so you can move through the song
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
    //play function
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
    //changes one of the labels so it states the song that is currently playing
    public void setLabel(){
        try{
            String p = player.getMedia().getSource();
            Path path = Paths.get(URI.create(p));
            AudioFile af = AudioFileIO.read(new File(path.toString()));
            Tag tag = af.getTag(); 
            songLabel.setText("  " + tag.getFirst(FieldKey.TITLE));

        }
        catch(Exception e){}
    }
    //the functionality for the rewind button
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
    //the functionality for the skip button
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
    /* functionality for the + button
     * works in conjunction with the FXML file to assign this method to the corresponding button
     * dual function: on home page it allows you to add song and on library page it allows you to add a playlist
     */
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
            songTiles.add(st); 
            copySongTiles.add(st);
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
                        sortButton.setVisible(true);
                        fileButton.setVisible(false);
                        currentPlaylist = pt; 
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
    //all the songTiles have a playButton; this method just sets it up
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
    //this method brings the home page to view
    public void home() {
        middlePane.getChildren().clear();
        middlePane.getChildren().add(homePage);
        lastSeenPanes.add(homePage);
        topPaneLabel.setText("Welcome to your Home Directory! Add a song to get started!");
        seenPanesIndex += 1;
        sortButton.setVisible(true);
        fileButton.setVisible(true);
    }
    /* this method draws from the playlistFiles folder in resources to bring back past playlists that you have already made
     * creates all the existing PlaylistTiles and displays them as well as sets up their functionality
     * 
     */
    public void libraryCreation(){
        gp = new GridPane();
        gp.setPadding(new Insets(50));
        gp.setPrefSize(800,400);
        gp.setHgap(50);
        gp.setVgap(50);
        gp.setStyle("-fx-background-color: #04333C");

        File d = new File("src/main/resources/playlistFiles");
        File[] fa = d.listFiles(); 
        Arrays.sort(fa, Comparator.comparing(File::getName));
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
                PlaylistTile pt = new PlaylistTile(path, title, playlistCount);
                int row = playlistCount / 3;
                int col = playlistCount % 3;
                pt.getButton().setText(pt.getName());
                //This sets up the playlist page which displays after you click the PlaylistTile
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
                            sortButton.setVisible(true);
                            fileButton.setVisible(false);
                            currentPlaylist = pt; 
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
        //Setting up the Library Page to display multiple playlists
        ScrollPane sp = new ScrollPane(gp);
        sp.setPrefSize(800, 400);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setFitToWidth(false);
        sp.prefHeightProperty().bind(homePage.heightProperty());
        sp.setStyle("-fx-background-color: #04333C");
        libraryPage.getChildren().add(sp);
    }
    //This method is to pull the name of the playlist from the JSon file
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
    //This method brings the library page to view
    public void library() {
        middlePane.getChildren().clear();
        middlePane.getChildren().add(libraryPage);
        lastSeenPanes.add(libraryPage);
        seenPanesIndex += 1;
        topPaneLabel.setText("   Welcome to your Playlists!");
        sortButton.setVisible(false); 
        fileButton.setVisible(true);
    }
    //This one sets up the functionality for the return button, allowing you to go back to previous pages
    public void returnFunction(){
        if(seenPanesIndex > 0){
            middlePane.getChildren().clear();
            middlePane.getChildren().add(lastSeenPanes.get(seenPanesIndex - 1));
            seenPanesIndex -= 1;
        }
    }
    // helper method for setting up the playback slider
    public void updatesValues() {
        Platform.runLater(new Runnable() {
            public void run() {
                timeSlider.setValue((player.getCurrentTime().toMillis() / player.getTotalDuration().toMillis()) * 100);
            }
        });
    }
    //Custom comparator class that allows us to implement a custom sorting function
    class SongTileComparator implements Comparator<SongTile>{
        int sortingMode;
        public SongTileComparator(int num){
            sortingMode = num;
        }
        //This compare method will return a value based on what mode we want to sort by (artist, album name, title)
        @Override
        public int compare(SongTile st1, SongTile st2){
            //sorting by title; occurs when timesSorted % 4 = 0
            if(sortingMode % 4 == 0){
                sortButton.setText("Title");
                return st1.getSong().getSongTitle().compareToIgnoreCase(st2.getSong().getSongTitle());
            }
            //sorting by artist; occurs when timesSorted % 4 = 1
            else if(sortingMode % 4 == 1){
                sortButton.setText("Artist");
                return st1.getSong().getSongArtist().compareToIgnoreCase(st2.getSong().getSongArtist());
            }
            //sorting by album name; occurs when timesSorted % 4 = 2
            else{
                sortButton.setText("Album");
                return st1.getSong().getAlbumName().compareToIgnoreCase(st2.getSong().getAlbumName());
            }
        }
    }
    //This method sorts the songTiles on the homePage
    public ArrayList<SongTile> sortSongs(ArrayList<SongTile> stArr, int ts){
        //original order; occurs when timesSorted % 4 = 3
        if(timesSorted % 4 == 3){
            stArr = (ArrayList)copySongTiles.clone();
            sortButton.setText("Sort");
            return stArr;
        }
        else{
            Collections.sort(stArr, new SongTileComparator(ts));
            return stArr;
        }
        
    }
    //This method sorts the songTiles on a PlaylistPage
    public ArrayList<SongTile> sortPlaylistSongs(ArrayList<SongTile> stArr, int ts){
        if(timesSorted % 4 == 3){
            stArr = (ArrayList)currentPlaylist.getPlaylistPage().getCopySongList();
            return stArr;
        }
        else{
            Collections.sort(stArr, new SongTileComparator(ts));
            return stArr;
        }
    }
    /*This method is assigned to the Sort button through the FXML file
     * Dual functionality: it will sort the SongTiles on the homePage if the homePage is displayed
     * It will sort the songs specifically in the playlist if a playlist page is displayed
     */
    public void sort(){
        if(middlePane.getChildren().get(0).getClass() == homePage.getClass()){
            ArrayList<SongTile> al = sortSongs(songTiles, timesSorted);  
            timesSorted++; 
            songTiles = (ArrayList)al.clone();
        // Collections.shuffle(songTiles);
            middlePane.getChildren().clear();
            vbox.getChildren().clear();
            for(SongTile s: songTiles){
                vbox.getChildren().add(s);
            }
            middlePane.getChildren().add(homePage);
        }
        else{
            ArrayList<SongTile> al = currentPlaylist.getPlaylistPage().getSongList(); 
            al = currentPlaylist.getPlaylistPage().getSongList();
            al = sortPlaylistSongs(al, currentPlaylist.getPlaylistPage().getTimesSorted());
            if(currentPlaylist.getPlaylistPage().getTimesSorted() % 4 == 3){
                sortButton.setText("Sort");
            }
            currentPlaylist.getPlaylistPage().incrementTimesSorted();
            middlePane.getChildren().clear();
            currentPlaylist.getPlaylistPage().loadSongs(al);
            middlePane.getChildren().add(currentPlaylist.getPlaylistPage());
        }
    }
}
