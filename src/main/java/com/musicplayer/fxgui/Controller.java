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
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    @FXML
    private TextField searchbar;
    @FXML
    private Label label1;

//All attributes of controller; not controlled by the FXML
    private Media media;
    private MediaPlayer player;
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
    private ArrayList<SongTile> songQueue = new ArrayList<SongTile>();
    private int currSongIndex = 0;
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
        // song folder includes this sentinel song by default in order to "start" the app; its ignored in the GUI and when actually using the app
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if(files[i].getPath().equals("src/main/resources/songfolder/TEST_One minute of silence (ID 0917)_BSB 9.47.53 AM.mp3")){
                    songFiles.add(files[i]);
                }
                //adds the user songs into their respective arrays;
                if(!files[i].getName().substring(0, 1).equals(".") && !files[i].getPath().equals("src/main/resources/songfolder/TEST_One minute of silence (ID 0917)_BSB 9.47.53 AM.mp3")){
                    songFiles.add(files[i]);
                    Song s = new Song(files[i].getPath());
                    songObjects.add(s);
                }
            }
        }
        //setting up searchbar functionalities; searchbar that works for the library vs when you're on a playlist page
        searchbar.textProperty().addListener((observable, oldValue, newValue) -> { filterData(newValue, songTiles, vbox);});
        //editing the style of the vbox
        vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(5);
        vbox.setPrefSize(800,400);
        vbox.setStyle("-fx-background-color: #04333C");
        vbox.setPrefSize(800,400);
        //scrollpane customization; this allows the home page to be scrollable(we create the scrollpane using the vbox 
        //so it can scale as we add more songs
        scrollPane = new ScrollPane(vbox);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(false);
        scrollPane.prefHeightProperty().bind(homePage.heightProperty());
        scrollPane.setPrefWidth(800);
        scrollPane.setStyle("-fx-background-color:#04333C");
        //calls the homepage creation method which actually makes the songtile objects + customizes the homepage
        homepageCreation();
        homePage.getChildren().add(scrollPane);
        homePage.setPrefSize(800,400);
        homePage.setStyle("-fx-background-color: #04333C;");
        label1.setAlignment(Pos.CENTER);
        topPaneLabel.setAlignment(Pos.CENTER);
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
        //setting up the initial song queue; should be the songs on the homepage
        songQueue = songTiles;
        //setting up the media player
        numSongs = songFiles.size();
        media = new Media(Paths.get(files[1].getPath()).toUri().toString());
        player = new MediaPlayer(media);
        player.setVolume(50);
        searchbar.setVisible(true);
        topPaneLabel.setVisible(false);
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
            setupDeleteButton(st);
            setupAddButton(st);
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
    //play function; it modifies the audio output and the label for the play button
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
    //changes the label on the bottom so it states the song that is currently playing
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
        //this is when the current song is not the first song in the queue
        if (currSongIndex > 0) {
            currSongIndex--;
            player.stop();
            media = new Media(Paths.get(songQueue.get(currSongIndex).getSong().getFilePath()).toUri().toString());
            player = new MediaPlayer(media);
            player.play();
            initializeTimeSlider();
            setLabel();
            playButton.setText("|>");
        }
        //if the current song is the first song in the list, it will go back to the end
        else {
            currSongIndex = songFiles.size() - 1;
            player.stop();
            media = new Media(Paths.get(songQueue.get(currSongIndex).getSong().getFilePath()).toUri().toString());
            player = new MediaPlayer(media);
            setLabel();
            player.play();
            initializeTimeSlider();
            playButton.setText("|>");
            
        }
    }
    //the functionality for the skip button
    public void nextMedia() {
        //this is for when the song is not the last song in the queue
        if (currSongIndex < songQueue.size() - 1) {
            currSongIndex++;
            player.stop();
            media = new Media(Paths.get(songQueue.get(currSongIndex).getSong().getFilePath()).toUri().toString());
            player = new MediaPlayer(media);
            setLabel();
            player.play();
            initializeTimeSlider();
            playButton.setText("|>");
        }
        // it goes to the beginning of the song queue
        else {
            currSongIndex = 0;
            player.stop();
            media = new Media(Paths.get(songQueue.get(currSongIndex).getSong().getFilePath()).toUri().toString());
            player = new MediaPlayer(media);
            setLabel();
            player.play();
            initializeTimeSlider();
            playButton.setText("|>");
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
        //checks if the file type is mp3; currently there is no check to see if the file type is not MP3
        if(type.equals("mp3")){
            String directoryPath = directory.getPath();
            Path d = Paths.get(directoryPath);
            Path f = Paths.get(file.getPath());
            try{
                Path targetPath = d.resolve(f.getFileName());
                Files.copy(f, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            catch(Exception e){}
            }
            //setting up the songTile and displaying it on the GUI
            songFiles.add(file);
            String fp = file.getPath().substring(file.getPath().lastIndexOf("/"));
            String directoryPath = directory.getPath();
            Song s = new Song(directory + fp);
            songObjects.add(s);
            SongTile st = new SongTile(s);
            songTiles.add(st); 
            copySongTiles.add(st);
            int j = numSongs;
            setupPlayButton(st);
            setupDeleteButton(st);
            setupAddButton(st);
            vbox.getChildren().add(st);
            numSongs += 1;
        }
        //if not the adding a song, then making a playlist and adding it to the library page as well as setting JSON for song paths
        else{
            PlaylistTile pt = new PlaylistTile(playlistCount);
            PlaylistForm pf = new PlaylistForm(pt); 
            int row = playlistCount / 3;
            int col = playlistCount % 3;
            pt.createJSON(playlistCount);
            pt.getButton().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e){
                        middlePane.getChildren().clear();
                        middlePane.getChildren().add(pt.createPlaylistPage(pt.getJsonPath()));
                        for(SongTile s: pt.getSongList()){
                            setupPlayButton(s);
                            setupDeleteButton(s);
                            s.getChildren().remove(1);
                        }
                        lastSeenPanes.add(pt.getPlaylistPage());
                        seenPanesIndex += 1;
                        sortButton.setVisible(true);
                        fileButton.setVisible(false);
                        searchbar.setVisible(false);
                        label1.setText("JTunes");
                        label1.setVisible(true);
                        currentPlaylist = pt;
                        songQueue = pt.getSongList(); 
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
    //all the songTiles have a playButton; this method just sets it up to play the audio when its clicked
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
                        currSongIndex = songQueue.indexOf(s);
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
    //this method sets up the button for the song tiles on the library page in order to add them to a playlist
    public void setupAddButton(SongTile s){
        s.addButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e){
                PlaylistMenu pm = new PlaylistMenu(s, playlistArray);
            }
        });
    }
    //This sets up the delete button; there are two cases of the delete button functionality
    public void setupDeleteButton(SongTile s){
        //if the delete butotn is from a song tile in the playlist, it will remove the song from the playlist but not the library
        if(s.getChildren().get(2) instanceof Button){
            s.getDeleteButton().setOnAction(event -> deleteSongFromLibrary(s));
        }
        //if the delete button is from a song tile in the playlist, it will remove the song from the playlist but not the library
        else{
            s.getDeleteButton().setOnAction(e -> deleteSongFromPlaylist(s));
        }
        //sets up hovering functionality so the UI is cleaner
        s.setOnMouseEntered(e -> {
            s.getDeleteButton().setVisible(true);
            s.getPlayButton().setVisible(true);
            s.getAddButton().setVisible(true);
        });
        s.setOnMouseExited(e -> {
            s.getDeleteButton().setVisible(false);
            s.getPlayButton().setVisible(false);
            s.getAddButton().setVisible(false);
        });
    }
    //this calls a playlistTile method to remove a song from a playlist, removing the visual tile and the path from the JSON
    public void deleteSongFromPlaylist(SongTile st){
        try{
            currentPlaylist.removeSongFromPlaylist(st.getSong().getFilePath(), currentPlaylist.getJsonPath(), st);
            for(SongTile s: currentPlaylist.getSongList()){
                System.out.println(s.getSong().getSongTitle());
            }
            middlePane.getChildren().clear();
            middlePane.getChildren().add(currentPlaylist.createPlaylistPage(currentPlaylist.getJsonPath()));
            for(SongTile s: currentPlaylist.getSongList()){
                setupPlayButton(s);
                setupDeleteButton(s);
                s.getChildren().remove(1);
            }
        }
        catch(JsonParseException e){
            e.printStackTrace();
        }
    }
    /*this removes the file from the songfolder directory as well as removes all instances of the song in all 
    //playlists using the playlistTile removeSongFromPlaylist method 
    */
    public void deleteSongFromLibrary(SongTile st){
        Path path = Paths.get(st.getSong().getFilePath());
        try{
            for(PlaylistTile p: playlistArray){
                ArrayList<SongTile> arr = p.getSongList();
                for(int i = 0; i < arr.size(); i++){
                    SongTile songtile = arr.get(i);
                    if(songtile.getSong().getFilePath().equals(st.getSong().getFilePath())){
                        p.getSongList().remove(songtile);
                        p.removeSongFromPlaylist(songtile.getSong().getFilePath(), p.getJsonPath(), songtile);
                        p.createPlaylistPage(p.getJsonPath());
                        for(SongTile s: p.getSongList()){
                            setupPlayButton(s);
                            setupDeleteButton(s);
                            s.getChildren().remove(1);
                        }
                        i--;
                        break;
                    }
                }
            }
            //this removes the song from the folder and then stops the song 
            Files.delete(path);
            songTiles.remove(st);
            vbox.getChildren().remove(st); 
            System.out.println(player.getMedia().getSource());
            System.out.println(Paths.get(st.getSong().getFilePath()).toUri().toString());
            if(player.getStatus() == Status.PLAYING && player.getMedia().getSource().equals(Paths.get(st.getSong().getFilePath()).toUri().toString())){
                player.pause();
                songLabel.setText("   No Song Currently Playing!");
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    //this method brings the home page to view
    public void home() {
        middlePane.getChildren().clear();
        middlePane.getChildren().add(homePage);
        lastSeenPanes.add(homePage);
        topPaneLabel.setText("Welcome to your Home Directory! Add a song to get started!");
        label1.setText("JTunes");
        seenPanesIndex += 1;
        sortButton.setVisible(true);
        fileButton.setVisible(true);
        searchbar.setVisible(true);
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
                pt.setPlaylistPage(pt.createPlaylistPage(pt.getJsonPath()));
                //This sets up the playlist page which displays after you click the PlaylistTile
                pt.getButton().setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e){
                            middlePane.getChildren().clear();
                            middlePane.getChildren().add(pt.createPlaylistPage(pt.getJsonPath()));
                            for(SongTile s: pt.getSongList()){
                                setupPlayButton(s);
                                setupDeleteButton(s);
                                s.getChildren().remove(1);
                            }
                            lastSeenPanes.add(pt.getPlaylistPage());
                            seenPanesIndex += 1;
                            sortButton.setVisible(true);
                            fileButton.setVisible(false);
                            searchbar.setVisible(false);
                            currentPlaylist = pt; 
                            songQueue = pt.getSongList();
                            label1.setText("JTunes");
                            if(pt.getName().charAt(0) == '\"'){
                                topPaneLabel.setText(pt.getName().substring(1, pt.getName().length() - 1));
                            }
                            else{  
                                topPaneLabel.setText(pt.getName().replace('"', ' '));
                            }
                        }
                });
                playlistCount++; 
                playlistArray.add(pt);
                gp.add(pt.getButton(), col, row);
                searchbar.setVisible(false);
                label1.setVisible(true);
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
        topPaneLabel.setText("Welcome to your Library! Here You Can Find Your Playlists");  
        label1.setText("Library");
        sortButton.setVisible(false); 
        fileButton.setVisible(true);
        searchbar.setVisible(false);
        topPaneLabel.setVisible(true);
    }
    //This one sets up the functionality for the return button, allowing you to go back to previous pages
    public void returnFunction(){
        if(seenPanesIndex > 0){
            middlePane.getChildren().clear();
            middlePane.getChildren().add(lastSeenPanes.get(seenPanesIndex - 1));
            if(lastSeenPanes.get(seenPanesIndex - 1) == homePage){
                sortButton.setVisible(true);
                fileButton.setVisible(true);
                searchbar.setVisible(true);
                label1.setText("JTunes");
                
            }
            else if(lastSeenPanes.get(seenPanesIndex - 1) == libraryPage){
                sortButton.setVisible(false);
                fileButton.setVisible(true);
                searchbar.setVisible(false);
                label1.setText("Library");
            }
            else{
                sortButton.setVisible(true);
                fileButton.setVisible(false);
                searchbar.setVisible(false);
                label1.setText("JTunes");
            }
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
            stArr.clear();
            stArr = (ArrayList)currentPlaylist.getPlaylistPage().getCopySongList().clone();
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
            ArrayList<SongTile> al = currentPlaylist.getSongList(); 
            al = currentPlaylist.getSongList();
            al = sortPlaylistSongs(al, currentPlaylist.getPlaylistPage().getTimesSorted());
            if(currentPlaylist.getPlaylistPage().getTimesSorted() % 4 == 3){
                sortButton.setText("Sort");
            }
            currentPlaylist.getPlaylistPage().incrementTimesSorted();
            middlePane.getChildren().clear();
            currentPlaylist.getPlaylistPage().getVBox().getChildren().clear();
            currentPlaylist.getPlaylistPage().loadSongs(al);
            middlePane.getChildren().add(currentPlaylist.getPlaylistPage());
        }
    }
    /*This is the searching function; it just checks if the song attributes 
      contain the string being searched for and displays them
    */
    public void filterData(String searchString, ArrayList<SongTile> arr, VBox v){
        ObservableList<SongTile> filteredSongs = FXCollections.observableArrayList();
        if(searchString.length() == 0){
            for(SongTile st: arr){
                filteredSongs.add(st); 
            }
        }
        else{
            for(SongTile t: arr){
                if(t.getSong().getSongTitle().toLowerCase().contains(searchString.toLowerCase())){
                    filteredSongs.add(t);
                }
                else if(t.getSong().getSongArtist().toLowerCase().contains(searchString.toLowerCase())){
                    filteredSongs.add(t);
                }
                else if(t.getSong().getAlbumName().toLowerCase().contains(searchString.toLowerCase())){
                    filteredSongs.add(t);
                }
            }
        }
        v.getChildren().clear();
        for(SongTile f: filteredSongs){
            v.getChildren().add(f);
        }
    }
}
