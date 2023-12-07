package com.musicplayer.fxgui;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import java.util.ArrayList;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/*  PlaylistTile class which is displayed in the library
 * 
 * @author Ryan Nguyen
 */
public class PlaylistTile extends Button{
    private Button button;
    private String name;
    private String jsonName;
    private String jsonPath;
    private PlaylistPage playlistPage;
    private int playlistNumber;
    private ArrayList<SongTile> songList = new ArrayList<SongTile>(); 
    //Various constructors depending on the use case
    public PlaylistTile(){
        button = new Button();
        button.setPrefSize(200,200);
        button.setStyle("-fx-background-color: #578E87;");
    }
    public PlaylistTile(String n, int num){
        playlistNumber = num;
        button = new Button();
        button.setPrefSize(200,200);
        button.setStyle("-fx-background-color: #578E87;");
        button.setTextFill(Color.WHITE);
        name = n;
    }
    public PlaylistTile(int num){
        playlistNumber = num;
        button = new Button();
        button.setPrefSize(200, 200);
        button.setStyle("-fx-background-color: #578E87;");
        button.setTextFill(Color.WHITE);
    }
    //Constructor for making playlists that already exist in the playlistFiles directory
    public PlaylistTile(String p, String n, int num){
        button = new Button();
        button.setPrefSize(200, 200);
        name = n;
        playlistNumber = num;
        jsonPath = p;
        button.setStyle("-fx-background-color: #578E87;");
        button.setTextFill(Color.WHITE);
        createPlaylistPage(jsonPath);
    }
    //Getter method
    public Button getButton(){
        return button;
    }
    //Creates the playlist page to be displayed when you click on the PlaylistTile
    public PlaylistPage createPlaylistPage(String jsonpath){
        PlaylistPage pp = new PlaylistPage(jsonpath);
        return pp;
    }
    public PlaylistPage getPlaylistPage(){
        return playlistPage;
    }
    public void setPlaylistPage(PlaylistPage p){
        playlistPage = p;
    }
    public String getName(){
        if(name.charAt(0) == '\"'){
            name = name.substring(1, name.length() -1); 
        }
        return name;
    }
    public String getJsonPath(){
        return jsonPath;
    }

    //This method creates the JSON file where the playlist name and the songs within the playlist are stored
    public void createJSON(int num){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject mainObject = new JsonObject();
        JsonArray pathList = new JsonArray();
        mainObject.add("song_paths", pathList);
        String jsonString = gson.toJson(mainObject); 

        String path = "src/main/resources/playlistFiles/";
        String filename = "playlist" + num + ".json";
        jsonName = "playlist" + num;
        Path filePath = Paths.get(path, filename);
        jsonPath = path + jsonName + ".json";
        try(FileWriter writer = new FileWriter(filePath.toString())){
            writer.write(jsonString);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            playlistPage = createPlaylistPage(jsonPath);
        }
    }
    //method to add a song path to the Json file so that it is stored
    public void addPathToJson(String path){
        String jsonPath = "src/main/resources/playlistFiles/playlist" + playlistNumber + ".json";
        JsonObject existingData = readJsonFile(jsonPath);
        JsonArray itemsArray = existingData.getAsJsonArray("song_paths");
        itemsArray.add(path);
        String updatedString = existingData.toString();
        writeJsonFile(jsonPath, updatedString);
    }
    //adds a name property to the Json after the playlist form is completed
    public void addNameToJson(){
        String jsonPath = "src/main/resources/playlistFiles/playlist" + playlistNumber + ".json";
        JsonObject existingData = readJsonFile(jsonPath);
        existingData.addProperty("name", name);
        String updatedString = existingData.toString();
        writeJsonFile(jsonPath, updatedString);
    }
    //grabbing the existing Json from the playlistFiles directory
    public JsonObject readJsonFile(String path){
        try(FileReader reader = new FileReader(path)){
            Gson gson = new Gson();
            return gson.fromJson(reader, JsonObject.class);
        }
        catch(IOException | JsonParseException e){
            e.printStackTrace();
        }
        return new JsonObject();
    }
    //Helper method for writing information to the Json file
    public void writeJsonFile(String p, String jsonString){
        try(FileWriter writer = new FileWriter(p)){
            writer.write(jsonString);            
        }
        catch(IOException | JsonParseException e){
            e.printStackTrace();
        }
    }
    public void setName(String n){
        name = n;
    }
    // method for removing a song from the playlist, changing the corresponding json and the songList attribute
    public void removeSongFromPlaylist(String path, String jsonpath, SongTile st){
        try{
            JsonObject existingData = readJsonFile(jsonpath);
            JsonArray itemsArray = existingData.getAsJsonArray("song_paths");
            for(int i = 0; i < itemsArray.size(); i++){
                JsonElement je = itemsArray.get(i);
                System.out.println(je.toString());
                System.out.println(st.getSong().getFilePath());
                if(je.toString().substring(1,je.toString().length() - 1).equals(st.getSong().getFilePath())){
                    itemsArray.remove(i);
                    break;
                }
            }
            String updatedString = existingData.toString();
            writeJsonFile(jsonpath, updatedString);
            songList.remove(st);
        }
        catch(JsonParseException e){
            e.printStackTrace();
        }
    }
    public ArrayList<SongTile> getSongList(){
        return songList;
    }
    /* Playlist Page Class where songs in a playlist are displayed on the GUI
     * 
     * 
     */
    class PlaylistPage extends Pane{
        VBox vbox;
        ScrollPane scrollPane;
        Pane pane;
        ArrayList<Song> songAL;
        ArrayList<SongTile> copySongList = new ArrayList<SongTile>();
        String path;
        int timesSorted = 0;
        public PlaylistPage(String jp){
            path = jp;
            vbox = new VBox();
            vbox.setPrefSize(800,400);
            vbox.setStyle("-fx-background-color: #04333C");
            scrollPane = new ScrollPane(vbox);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setFitToWidth(false);
            scrollPane.setPrefSize(800, 400);
            scrollPane.setStyle("-fx-background-color: #04333C");
            JsonArray ja = retrieveJsonArray(jp);
            if(ja == null){
                vbox.getChildren().add(new Label("add a song!"));
            }
            else{
                songList = new ArrayList<SongTile>();
                for(int i = 0; i < ja.size(); i++){
                    String p = ja.get(i).getAsString();
                    SongTile st = new SongTile(new Song(p));
                    st.getChildren().remove(1);
                    st.getLabel().setPrefSize(650,100);
                    songList.add(st);
                    copySongList.add(st); 
                    vbox.getChildren().add(st); 
                }  
                this.getChildren().add(scrollPane);
            }
        }
        //Method for loading SongTiles onto the page after an action
        public void loadSongs(ArrayList<SongTile> al){
            vbox.getChildren().clear();
            vbox.getChildren().addAll(al);
        }
        public VBox getVBox(){
            return vbox;
        }
        public ArrayList<SongTile> getCopySongList(){
            return copySongList;
        }
        //Method for pulling the song path array from the Json file
        public JsonArray retrieveJsonArray(String jp){
            try(FileReader fr = new FileReader(jp)){
                JsonElement jsonElement = JsonParser.parseReader(fr); 
                JsonObject jsonObject = jsonElement.getAsJsonObject(); 
                return jsonObject.getAsJsonArray("song_paths");
            }
            catch(IOException | JsonParseException e){
                e.printStackTrace();
                return null;
            }
        }
        //Times sorted is an attribute to help switch between the different sorting modes
        public int getTimesSorted(){
            return timesSorted;
        }
        public void incrementTimesSorted(){
            timesSorted ++;
        }
    }
}