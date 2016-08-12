package player.music.com.unique;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by VIKAS SINGH on 20-01-2016.
 */
public class FragmentPageAdapter extends FragmentPagerAdapter {
    private  Bundle bundle;
    ArrayList<Song> songsList;
    String [] songNameList;
    public FragmentPageAdapter(FragmentManager fm,ArrayList<Song> songArrayList)
    {
        super(fm);
        songsList=songArrayList;
        songNameList=new String[songsList.size()];
        for(int i=0;i<songsList.size();i++)
        songNameList[i]=songsList.get(i).getTitl();

    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0: {
                AllSongs allSongs=new AllSongs();
                try{
                    int i= MainActivity.musicSrv.getPlayingSong();
                    AllSongs.listView.setSelection(i);
                    AllSongs.listView.setItemChecked(i, true);
                }catch (Exception e)
                { System.out.print("Error at FragmentPageAdapter.getItem"); }
                return allSongs;
            }case 1: {
                Artist artist = new Artist();
                return artist;
            }case 2: {
                Album album = new Album();
                return album;
            }case 3: {
                Folders folders = new Folders();
                return folders;
            }/*case 4:{
                Playlists playlists=new Playlists();
                return playlists;
            }*/
                default:
                break;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
