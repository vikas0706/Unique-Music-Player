package player.music.com.unique;

import android.graphics.Bitmap;

/**
 * Created by VIKAS SINGH on 20-01-2016.
 */
public class Song {
    public long id;
    public long albumId;
    public String title;
    public String artist;
    public String album;
    public String path;
    public long duration;
    public Song(long songID, String songTitle, String songArtist, String albumColumn, Long thisAlbumID, String thisFolder,long thisDuration) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        album=albumColumn;
        albumId=thisAlbumID;
        path=thisFolder;
        duration=thisDuration;
    }

    public long getDuration(){return duration;}
    public long getAlbumId(){return albumId;}
    public long getID(){ return id;}
    public String getTitl(){return title;}
    public String getArtist(){return artist;}
    public String getAlbum(){ return album; }
    public String getPath(){ return path; }

}
