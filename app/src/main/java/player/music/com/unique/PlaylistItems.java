package player.music.com.unique;

import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by VIKAS SINGH on 28-02-2016.
 */
public class PlaylistItems {
    String playlist_name;
    ArrayList<Long> id_list;
    ArrayList<String> title_list,artist_list;
    Long playlist_id;
    public PlaylistItems()
    {
        playlist_name="Default";
        playlist_id=Long.parseLong("1");
    }
    public PlaylistItems(String name,Long id,ArrayList<Long> id_list, ArrayList<String> title_list,ArrayList<String> artist_list)
    {
        playlist_name=name;
        playlist_id=id;
        this.id_list=id_list;
        this.title_list=title_list;
        this.artist_list=artist_list;
    }

    public Long getID(){return playlist_id;}
    public String getPlaylist_name(){ return playlist_name;}
    public ArrayList<Long> getIdList(){ return id_list;}
    public ArrayList<String> getTitle_list(){return title_list; }
    public ArrayList<String> getArtist_list(){ return artist_list; }
}
