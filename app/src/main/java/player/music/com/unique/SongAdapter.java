package player.music.com.unique;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by VIKAS SINGH on 20-01-2016.
 */
public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> songs;
    private LayoutInflater songInf;
    private DisplayMetrics metric;
    private Context context;
    public SongAdapter(Context c, ArrayList<Song> theSongs, DisplayMetrics metrics){
        songs=theSongs;
        songInf=LayoutInflater.from(c);
        this.metric=metrics;
        context=c;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LinearLayout songLay = (LinearLayout)songInf.inflate
                (R.layout.songs, viewGroup, false);
        //get title and artist views

        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        TextView songNo=(TextView)songLay.findViewById(R.id.song_no);
        songView.setEnabled(true);
        //get song using position
        Song currSong = songs.get(i);
        //get title and artist strings
        int j=i+1;
        if(i!=MainActivity.musicSrv.getPlayingSong()) {
            songNo.setText("" + j);
        }else { songNo.setText(" ");
         if(MainActivity.musicSrv.isPng())
         { songNo.setBackground(context.getDrawable(R.drawable.ic_equalizer));
             AnimationDrawable animationDrawable=(AnimationDrawable)songNo.getBackground();
             animationDrawable.start();
         }
         else {songNo.setBackground(context.getDrawable(R.mipmap.ic_equalizer1_white_36dp));}
        }
        songView.setText(currSong.getTitl());
        artistView.setText(currSong.getArtist());
        //set position as tag
        songLay.setTag(i);
        /*AnimationSet animationSet = new AnimationSet(true);
        Animation animation = new TranslateAnimation(metric.widthPixels / 2, 0,
                0, 0);
        animation.setDuration(500);
        animationSet.addAnimation(animation);
        animation=new ScaleAnimation((float)0.5,(float)1.0,(float)0.5,(float)1.0,(float)1.0,(float)1.0);
        animation.setDuration(500);
        animationSet.addAnimation(animation);
        songLay.setAnimation(animationSet);
        animation=null;
        animationSet=null;
        */
        return songLay;
    }
}
