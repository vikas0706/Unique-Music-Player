package player.music.com.unique;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
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
import java.security.spec.ECField;
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
    //Notification
    Notification status;


    public static final String extra="PLAYED";
    public static final String exitapp="EXITAPP";
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
            showNotification();
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
        showNotification();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public int getPlayingSong(){ return songPosn; }

    public void playPlayer(){ player.prepareAsync(); }

    public void go(){
        player.start();
        showNotification();
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

    //Return Album art of particular song
    public Bitmap getAlbumArt(int i)
    {
        Bitmap songImage=null;
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, songs.get(i).getAlbumId());

        ContentResolver res = getApplicationContext().getContentResolver();
        InputStream in = null;
        try {
            in = res.openInputStream(uri);
            songImage = BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {
            songImage=BitmapFactory.decodeResource(getResources(),R.drawable.bg_1);
            e.printStackTrace();
        }
        return songImage;
    }
    //Scale Bitmap for putting in imageview (If not it will use lot of space and app will crash)
    public static Bitmap scaleBitmap(Bitmap bitmap,int newWidth,int newHeight)
    {
        Bitmap scaledBitmap=Bitmap.createBitmap(newWidth,newHeight, Bitmap.Config.ARGB_8888);
        float scaleX=(float)newWidth/bitmap.getWidth();
        float scaleY= (float)newHeight /bitmap.getHeight();
        float scale=Math.max(scaleX,scaleY);
        float scaleWidth=scale*bitmap.getWidth();
        float scaleHieght=scale*bitmap.getHeight();
        float left=(newWidth-scaleWidth)/2;
        float top=(newHeight-scaleHieght)/2;
        RectF targetRect=new RectF(left,top,left+scaleWidth,top+scaleHieght);
        Canvas canvas=new Canvas(scaledBitmap);
        canvas.drawBitmap(bitmap,null,targetRect,null);
        return scaledBitmap;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    //    Toast.makeText(this,"started",Toast.LENGTH_SHORT).show();
      //  showNotification();
        try{

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
         //   showNotification();
          //  Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
         //   Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
            playPrev();
          //  showNotification();
            Log.i("LOg", "Clicked Previous");
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
          //  Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
          //  showNotification();
            if(player.isPlaying()){
                pausePlayer();
            }else{
                go();
            }
            Log.i("LOG_TAG", "Clicked Play");
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
           // Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
           // showNotification();
            playNext();
            Log.i("LOG_TAG", "Clicked Next");
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i("LOG_TAG", "Received Stop Foreground Intent");
          //  Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
            stopForeground(true);
            stopSelf();

        }}catch (Exception e){
            Log.e("Start Service","intents "+e.toString());
        }
        return START_STICKY;
    }

    //CREATE NOTIFIACTION AND SHOW NOTIFICATION

    private void showNotification() {
// Using RemoteViews to bind custom layouts into Notification
    //    Toast.makeText(this,"ssss",Toast.LENGTH_LONG).show();
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.small_notification);
        RemoteViews bigViews = new RemoteViews(getPackageName(),
                R.layout.big_notification);

// showing default album image
    //    views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
    //    views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        views.setImageViewBitmap(R.id.imageViewAlbumArt,scaleBitmap(getAlbumArt(readsSongplayed()),200,200));
        bigViews.setImageViewBitmap(R.id.imageViewAlbumArt,
                scaleBitmap(getAlbumArt(readsSongplayed()),200,200));

        Intent notificationIntent = new Intent(this,MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, Playback.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, Playback.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, Playback.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, Playback.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);
//        i++;
        views.setOnClickPendingIntent(R.id.btnPlay, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.btnPlay, pplayIntent);

        views.setOnClickPendingIntent(R.id.btnNext, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.btnNext, pnextIntent);

        views.setOnClickPendingIntent(R.id.btnPrevious, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.btnPrevious, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.btnDelete, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.btnDelete, pcloseIntent);
        if(player.isPlaying()) {
            views.setImageViewResource(R.id.btnPlay,
                    R.mipmap.ic_pause);
            bigViews.setImageViewResource(R.id.btnPlay,
                    R.mipmap.ic_pause);
        }else{
            views.setImageViewResource(R.id.btnPlay,
                    R.mipmap.ic_play);
            bigViews.setImageViewResource(R.id.btnPlay,
                    R.mipmap.ic_play);
        }
        views.setTextViewText(R.id.textSongName,  songs.get(songPosn).getTitl());
        bigViews.setTextViewText(R.id.textSongName, songs.get(songPosn).getTitl());

        views.setTextViewText(R.id.textArtistName, songs.get(songPosn).getArtist());
        bigViews.setTextViewText(R.id.textArtistName, songs.get(songPosn).getArtist());

        bigViews.setTextViewText(R.id.textAlbumName, songs.get(songPosn).getAlbum());

        status = new Notification.Builder(this).build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon=R.drawable.ic_play_arrow_white_48dp;

        status.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }

}
