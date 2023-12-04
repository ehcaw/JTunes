# JTunes:
Team Number 7, Anhadh Singh Sran, Austin Trinh, Maria Sanchez Rodriguez, Ryan Nguyen

### Problem: 
Music is too spread out, there is no way to organize and store all your music in one place. Think about how Youtube has all the music on their website, but you have to search over and over again if you want to change to a new song. 

### Functionality: 
We want to be able to listen to and organize music files in one place (mp3, etc) locally. 
The user should be able to sort their music in order to address the organization issue that music applications like Youtube struggle with. This means they can sort by artist, title, etc. 

### Previous Works:
Similar applications: Windows Media Player, Spotify, Youtube Music, iTunes

### Assumptions: 
The user has downloaded music files that they can put into the app in MP3 format. 

### Operating Environments:
The user has a personal computer that can download the application in order to use it. The user should have Java and Maven installed in order to run the application. 

### Intended Usage: 
People are meant to organize playlists and listen to them; can shuffle, loop, maybe do mixing of playlists too. 

### Plan and Approach:
Create a GUI for the user to interact with in order to add songs, create playlists, etc. The music is stored via some type of list and can be accessed when the user interacts with the GUI. The application should be able to locally store additional information about the song files, such as the title, artist, song picture, etc and display it on the interface for the user to see. We will be using Java, JavaFX to implement the audio features, and then SceneBuilder to create a GUI. 

### Link to presentation: 
https://docs.google.com/presentation/d/1fEWIZ7N7Qh_6RY-ZX1X_08F0s9IKzXIfFaaXlmTCsNk/edit?usp=sharing

### Steps to run code: 
1. Clone the repository
2. Find the controller class in the musicplayer folder and go to the initialize method
3. Find the file path inside the initialize method and replace it with the path from the song already included in the resources folder; different OS format the file path differently so you must replace it with the one that your OS recognizes.
4. Run the driver file to start the app (you can also start by adding MP3 files you already have into the resources/songfolder directory; files from spotifydown.com are recommended because the metadata in those mp3 files is very clean and formattable)

### Snapshot: 
https://drive.google.com/file/d/1BW3SR41seopNQp6qxUnGBNsTYctgKZDL/view?usp=sharing

### How we solved our problem: 
We solved our problem by creating an interface where a user can manage music they have downloaded easily and can use as an offline music player, provided they have some internet access to download the songs to begin with. It has a modern and an easily navigable interface that is user-friendly and intuitive. 

### UML Diagram: 
https://drive.google.com/file/d/1zXQAyqP0HhOgq4nMV5OhTZhNYkohxbDx/view?usp=sharing

### Description: 
JTunes is a simple computer application that allows you to listen to music completely offline. It allows you to add songs in mp3 format, make playlists and essentially have all the functionalities of a modern music application while being unreliant on the internet. 
