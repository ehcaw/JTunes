package com.musicplayer.fxgui;

//import statements for jaudiotagger
import java.io.File;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;


import javax.swing.JLabel;



public class Song{
    String songTitle; 
    String songArtist;
    String albumName; 
    int length;
    String duration;
    String filePath;
    public JLabel jl;
    public Song(String fp){
        try{
            AudioFile af = AudioFileIO.read(new File(fp));
            Tag tag = af.getTag(); 
            songTitle = tag.getFirst(FieldKey.TITLE);
            songArtist = tag.getFirst(FieldKey.ARTIST);
            albumName = tag.getFirst(FieldKey.ALBUM);
            length = af.getAudioHeader().getTrackLength();
            duration = convertToMinutes(length);
            filePath = fp;
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void test(){
        System.out.println("hello");
    }
    public static String convertToMinutes(int d){
        int min = d / 60;
        int sec = d % 60;
        String seconds = String.valueOf(sec);
        String minutes = String.valueOf(min);
        if(seconds.length() < 2){
            seconds = "0" + seconds;
        }
        String mAndS = minutes + ":" + seconds;
        return mAndS;
    }
    public String getFilePath(){
        return filePath;
    }
    public String getSongTitle(){
        return songTitle;
    }
    public String getSongArtist(){
        return songArtist;
    }
    public String getAlbumName(){
        return albumName;
    }
    public String getDuration(){
        return duration;
    }
}