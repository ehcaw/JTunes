package com.musicplayer.fxgui;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import java.util.ArrayList;
import javafx.scene.control.Label;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;




public class PlaylistTile extends Button{
    private Button button;
    private String name;
    private String jsonName;
    private String jsonPath;
    private PlaylistPage playlistPage;
    private int playlistNumber;
    private ArrayList<Song> songList = new ArrayList<Song>();  // could make it an array list of songs or song tiles; probably song objects
    public PlaylistTile(){
        button = new Button();
        button.setPrefSize(200,200);
    }
    public PlaylistTile(String n, int num){
        playlistNumber = num;
        button = new Button();
        button.setPrefSize(200,200);
        name = n;
    }
    public PlaylistTile(int num){
        playlistNumber = num;
        button = new Button();
        button.setPrefSize(200, 200);
    }
    //just creates tiles that already exist
    public PlaylistTile(String p, String n, int num){
        button = new Button();
        button.setPrefSize(200, 200);
        name = n;
        playlistNumber = num;
        jsonPath = p;
    }
    
    public Button getButton(){
        return button;
    }
    public PlaylistPage createPlaylistPage(){
        PlaylistPage pp = new PlaylistPage(jsonPath);
        playlistPage = pp;
        return pp;
    }
    public PlaylistPage getPlaylistPage(){
        return playlistPage;
    }
    public String getName(){
        if(name.charAt(0) == '\"'){
            name = name.substring(1, name.length() -1); 
        }
        return name;
    }
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
        playlistPage = createPlaylistPage();
        try(FileWriter writer = new FileWriter(filePath.toString())){
            writer.write(jsonString);
            System.out.println("json written");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public void addPathToJson(String path){
        String jsonPath = "src/main/resources/playlistFiles/playlist" + playlistNumber + ".json";
        JsonObject existingData = readJsonFile(jsonPath);
        JsonArray itemsArray = existingData.getAsJsonArray("song_paths");
        itemsArray.add(path);
        String updatedString = existingData.toString();
        writeJsonFile(jsonPath, updatedString);
    }
    public void addNameToJson(){
        String jsonPath = "src/main/resources/playlistFiles/playlist" + playlistNumber + ".json";
        JsonObject existingData = readJsonFile(jsonPath);
        existingData.addProperty("name", name);
        String updatedString = existingData.toString();
        writeJsonFile(jsonPath, updatedString);
    }
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

    class PlaylistPage extends Pane{
        VBox vbox;
        ScrollPane scrollPane;
        Pane pane;
        ArrayList<Song> songAL;
        ArrayList<SongTile> songTileAL = new ArrayList<SongTile>();
        String path;
        public PlaylistPage(String jp){
            path = jp;
            vbox = new VBox();
            vbox.setPrefSize(800,400);
            scrollPane = new ScrollPane(vbox);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setFitToWidth(false);
            scrollPane.setPrefSize(800, 400);

            JsonArray ja = retrieveJsonArray(jp);
            if(ja == null){
                vbox.getChildren().add(new Label("add a song!"));
            }
            else{
                for(int i = 0; i < ja.size(); i++){
                    String p = ja.get(i).getAsString();
                    SongTile st = new SongTile(new Song(p));
                    st.getChildren().remove(1);
                    st.getLabel().setPrefSize(650,100);
                    songTileAL.add(st);
                    vbox.getChildren().add(st); 
                }  
                this.getChildren().add(scrollPane);
            }
        }
        public VBox getVBox(){
            return vbox;
        }
        public ArrayList<SongTile> getSongList(){
            return songTileAL;
        }
    }
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
    }