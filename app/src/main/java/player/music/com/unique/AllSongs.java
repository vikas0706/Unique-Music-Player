package player.music.com.unique;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import player.music.com.unique.Playback.MusicBinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllSongs extends Fragment  {
    ArrayList<Song> songs;
    private Playback musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private ServiceConnection musicConnection;
    private DisplayMetrics metrics;
    ArrayList<String> snamelist;
    public static ListView listView;
    public static SongAdapter songAdt;


    public AllSongs() {

          // Required empty public constructor
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Toast.makeText(getContext(), "Running Low", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(getContext(), Playback.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try{
            int i= MainActivity.musicSrv.getPlayingSong();
            listView.setSelection(i);
            listView.setItemChecked(i, true);
            listView.smoothScrollToPosition(i);
        }catch (Exception e)
        { System.out.print("Error at FragmentPageAdapter.getItem"); }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity mn=new MainActivity();
        songs=mn.returnSonglist();
        snamelist=mn.getSongNameList();
        metrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        musicSrv =new Playback();
        musicConnection= new ServiceConnection(){

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicBinder binder = (MusicBinder)service;
                //get service
                musicSrv = binder.getService();
                //pass list
                musicSrv.setList(snamelist);
                musicBound = true;
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView= inflater.inflate(R.layout.fragment_all_songs, container, false);
         listView = (ListView) rootView.findViewById(R.id.songList);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        try {
            songAdt = new SongAdapter(getContext(), songs,metrics);
            listView.setAdapter(songAdt);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        musicSrv.setList(snamelist);
                        musicSrv.setSong(songs.get(position).getTitl());
                        musicSrv.playSong();
                        listView.setItemChecked(position, true);
                    } catch (Exception e) {
                        Log.d("SettingBg", "Error here in setting bg-M in ALLSongs class");
                    }
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    return true;
                }
            });
        }catch (Exception e)
        {
            System.out.println("Dont know the error . . . I catched it . . Handle youself");
        }

        return rootView;
    }
}
