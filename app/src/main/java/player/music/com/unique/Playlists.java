package player.music.com.unique;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Playlists extends Fragment {

    public static ArrayList<PlaylistItems> playlistses;
    ArrayList<Long> id_list;
    ArrayList<String> title_list,artist_list;
    ListView listView_playlist;
    public Playlists() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playlistses=new ArrayList<PlaylistItems>();
        id_list=new ArrayList<Long>();
        title_list=new ArrayList<String>();
        artist_list=new ArrayList<String>();
        getPlaylist();
    }

    public void getPlaylist() {
        playlistses.clear();
        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri playlistUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String PLAYLIST_ID = MediaStore.Audio.Playlists._ID;
        String PLAYLIST_NAME = MediaStore.Audio.Playlists.NAME;
        String PLAYLIST_SONG_ID = MediaStore.Audio.Playlists.Members.AUDIO_ID;
        String PLAYLIST_SONG_NAME=MediaStore.Audio.Playlists.Members.TITLE;
        String PLAYLIST_SONG_ARTIST=MediaStore.Audio.Playlists.Members.ARTIST;
        String[] playlistColumns = {
                PLAYLIST_ID,
                PLAYLIST_NAME
        };
        Cursor playlistCursor = musicResolver.query(playlistUri, playlistColumns, null, null, null);
        if (playlistCursor != null && playlistCursor.moveToFirst()) {

           do{
               id_list=new ArrayList<Long>();
               title_list=new ArrayList<String>();
               artist_list=new ArrayList<String>();
               String playListName=playlistCursor.getString(playlistCursor.getColumnIndex(PLAYLIST_NAME));
               Long playslist_ID=playlistCursor.getLong(playlistCursor.getColumnIndex(PLAYLIST_ID));
               Uri currentUri=MediaStore.Audio.Playlists.Members.getContentUri("external",playslist_ID);
               Cursor cursor2=musicResolver.query(currentUri,null,null,null,null);
               if(cursor2 != null &&cursor2.moveToFirst()) {
                   do {
                       id_list.add(cursor2.getLong(cursor2.getColumnIndex(PLAYLIST_SONG_ID)));
                       title_list.add(cursor2.getString(cursor2.getColumnIndex(PLAYLIST_SONG_NAME)));
                       artist_list.add(cursor2.getString(cursor2.getColumnIndex(PLAYLIST_SONG_ARTIST)));
                   } while (cursor2.moveToNext());
                   playlistses.add(new PlaylistItems(playListName, playslist_ID, id_list, title_list, artist_list));
               }
           }while (playlistCursor.moveToNext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ArrayList<String> playList_name=new ArrayList<String>();
        for(int i=0;i<playlistses.size();i++)
            playList_name.add(playlistses.get(i).getPlaylist_name());

        View playListView=inflater.inflate(R.layout.fragment_playlists, container, false);
        // Inflate the layout for this fragment
        listView_playlist=(ListView)playListView.findViewById(R.id.playlist);
        ArrayAdapter arrayAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,playList_name);
        listView_playlist.setAdapter(arrayAdapter);
        listView_playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Toast.makeText(getContext(),playlistses.get(position).getPlaylist_name(),Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle(playlistses.get(position).getPlaylist_name());
                ListView modeList=new ListView(getContext());
                ArrayList<String> arrayList=playlistses.get(position).getTitle_list();
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,arrayList);
                modeList.setAdapter(adapter);
                builder.setView(modeList);
                Dialog dialog=builder.create();
                dialog.show();
            }
        });
        return playListView;
    }

    public class addNewPlaylist extends AsyncTask{



        @Override
        protected Object doInBackground(Object[] params) {


            return null;
        }
    }

    public ArrayList<PlaylistItems> getPlaylistses()
    {
        return playlistses;
    }

}
