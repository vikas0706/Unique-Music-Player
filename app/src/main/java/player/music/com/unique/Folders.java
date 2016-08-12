package player.music.com.unique;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class Folders extends Fragment {

    ArrayList<ArrayList<String>> child;
    ArrayList<String> folderList;
    private ArrayList<Song> songs;
    private ArrayList<String> songNameList;
    private Playback musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private ServiceConnection musicConnection;


    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(getContext(), Playback.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
    }

    public Folders() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity mn=new MainActivity();
        songs=mn.returnSonglist();
        songNameList=mn.getSongNameList();
        musicSrv =new Playback();
        musicConnection= new ServiceConnection(){

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Playback.MusicBinder binder = (Playback.MusicBinder)service;
                //get service
                musicSrv = binder.getService();
                //pass list
                musicSrv.setList(songNameList);
                musicBound = true;
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        };
    }


    public void getFolder()
    {   folderList=new ArrayList<String>();
        for(int i=0;i<songs.size();i++)
        {
        if(!folderList.contains(songs.get(i).getPath()))
          folderList.add(songs.get(i).getPath());
        }
        Collections.sort(folderList);
        child=new ArrayList<ArrayList<String>>();
        for(int i=0;i<folderList.size();i++)
        {   ArrayList<String> childList=new ArrayList<String>();
            for (int j=0;j<songs.size();j++)
            {
                if((songs.get(j).getPath()).equals(folderList.get(i))==true)
                {
                    childList.add(songs.get(j).getTitl());
                }
            }
            Collections.sort(childList);
            child.add(childList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_folders, container, false);
        final ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.folder_list);

        try {
            int image =R.mipmap.folder_list;
            getFolder();
            final ExpandableListAdapter expandableListAdapter=new ExpandableListAdapter(getContext(),songs,folderList,child,image);
            final TextView playing_song_name=(TextView)getActivity().findViewById(R.id.playing_song_title);
            final TextView playing_song_artist=(TextView)getActivity().findViewById(R.id.playing_song_artist);
            listView.setAdapter(expandableListAdapter);
            listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int prevGroup=-1;
                @Override
                public void onGroupExpand(int groupPosition) {
                    if(groupPosition!=prevGroup)
                        listView.collapseGroup(prevGroup);
                    prevGroup=groupPosition;
                }
            });
            listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    try {
                        musicSrv.setSong(child.get(groupPosition).get(childPosition));
                        musicSrv.playSong();
                    } catch (Exception e) {
                        Log.d("SettingBg", "Error here in setting bg:M in Folder class");
                    }
                    return true;
                }
            });
        }catch (Exception e)
        {
            System.out.println("Catches at (ArrayList<Song>) getArguments().getSerializable");
        }

        return rootView;
    }

}
