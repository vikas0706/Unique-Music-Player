package player.music.com.unique;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class Playback extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,AudioManager.OnAudioFocusChangeListener {
    private final IBinder musicBind = new MusicBinder();
    //media player
    public static MediaPlayer player;
    //All song list
    private ArrayList<Song> songs;
    //current position
    public static int songPosn;
    //Current playing List
    ArrayList<String> currentPlayingList;
    //Song Name list
    private ArrayList<String> sNameList ;


    public static final String extra="PLAYED";
    private static boolean currentVersionSupportBigNotification = false;
    private static boolean currentVersionSupportLockScreenControls = false;



    public Playback() {

    }

    public void playSong(){
        //play a song
        player.reset();
        Intent intent=new Intent("play");
        intent.putExtra(extra, sNameList.get(songPosn));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Song playSong = songs.get(songPosn);
        //get id
        long currSong = playSong.getID();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
            player.setOnPreparedListener(this);
            player.prepare();

            writeSongPlayed(songPosn);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error at /Playback/playSong", e);
        }


    }


    @Override
    public void onCreate() {
        super.onCreate();
        songPosn=readsSongplayed();
        player = new MediaPlayer();
        initMusicPlayer();
        MainActivity mn=new MainActivity();
        songs=mn.returnSonglist();
        sNameList=mn.getSongNameList();


    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

    }

    public void setSong(String songName){
        songPosn=getNo(songName);
    }

    public int getNo(String name){
        int i=Collections.binarySearch(sNameList, name);
        return i; }

    public void setList(ArrayList<String> List){
        currentPlayingList=List;
    }

    public ArrayList<String> getNowPlayingList(){return currentPlayingList;}

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    public class MusicBinder extends Binder {
        Playback getService() {
            return Playback.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) { playNext();}

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public void playPrev(){
        seek(0);
        songPosn--;
        if(songPosn==-1) songPosn=songs.size()-1;
        playSong();

    }

    public void playNext(){
        seek(0);
        songPosn++;
        if(songPosn==songs.size())
            songPosn=0;
        playSong();
    }


    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public int getPlayingSong(){ return songPosn; }

    public void playPlayer(){ player.prepareAsync(); }

    public void go(){
        player.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //For Reading song played
    public int readsSongplayed()
    {  int c=0;
        String str=new String();
        try
        {
            FileInputStream fi=openFileInput("lastplayed.txt");
            int i=0;
            while((i=fi.read())!=-1)
            {
                str=str+(char)i;
            }
            c=Integer.parseInt(str);
            return c;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return c;
    }
    //For Saving song played
    public void writeSongPlayed(int c)
    {
        try
        {   String s=String.valueOf(c);
            FileOutputStream fileOutputStream =openFileOutput("lastplayed.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(s.getBytes());
            fileOutputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




}
