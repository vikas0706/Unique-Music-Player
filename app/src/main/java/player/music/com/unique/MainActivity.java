package player.music.com.unique;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.search.material.library.MaterialSearchView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

/**
 * Created by VIKAS SINGH on 19-01-2016.
 */

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks ,ActionBar.TabListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    //So many Variable declaration

    public NavigationDrawerFragment mNavigationDrawerFragment;
    public Toolbar mToolbar;
    public FragmentManager fragmentManager;
    public FragmentTransaction fragmentTransaction;
    ViewPager viewPager;
    FragmentPageAdapter ft;
    TabLayout tabLayout;
    public static ArrayList<Song> songList;
    public static ListView songView;
    public static long count;
    public static SlidingUpPanelLayout slideLayout;
    static LinearLayout playerLayout;
    static Drawable drawable;
    public static boolean panel_flag;
    public static ArrayList<String> sNameList;
    public static MaterialSearchView searchView;
    static Drawable linearDrawable ;
    static FrameLayout frameLayout;
    public static Playback musicSrv;
    public static Intent playIntent;
    public static boolean musicBound=false;
    public static ServiceConnection musicConnection;
    public static int screen_Hieght;
    public static int screen_width;
    public static DrawerLayout drawerLayout;
    public static TextView playing_song_name;
    public static TextView playing_song_artist;
    static ImageView back;
    static BroadcastReceiver broadcastReceiver;
    static LinearLayout ll;
    Button play;
    ImageView play_pause;
    static final String STATE_PLAY="kuch_v";
    TextView lyricsTV;
    HttpURLConnection connection;
    BufferedReader reader;
    ProgressBar progressBar;
    public static SeekBar seekbar;
    public static Thread updateSeekbar;
    public static TextView totalDur,curDur;
    public static Handler mHandler;
    public static ScrollView scrollView;
    static int scroll;
    public static String CurrentPlayingSongName;


    //ONSTART() --------------------------------------------------->

    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(getApplicationContext(), Playback.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);

        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("play"));
    }

    //ONSTOP()---------------------------------------------------->
    @Override
    protected void onStop() {
       /* if(musicConnection!=null) {
            unbindService(musicConnection);
            musicBound=false;
        }*/super.onStop();
    }

    //ONCREATE()--------------------------------------------------->

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        Display display=getWindowManager().getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);
        screen_width=size.x;
        screen_Hieght=size.y;
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);
        frameLayout=(FrameLayout)findViewById(R.id.container);
        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        scrollView=(ScrollView)findViewById(R.id.scroll_view);
        viewPager=(ViewPager)findViewById(R.id.pager);
        songView=(ListView)findViewById(R.id.songList);
        songList = new ArrayList<Song>();
        sNameList=new ArrayList<String>();
        getSongList();
        seekbar=(SeekBar)findViewById(R.id.seek_music);
        Collections.sort(sNameList);
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitl().compareTo(b.getTitl());
            }
        });
        play=(Button)findViewById(R.id.play_in_titlebar);

        totalDur=(TextView)findViewById(R.id.duration_total);
        curDur=(TextView)findViewById(R.id.duration_playing);
        musicSrv =new Playback();
        musicConnection= new ServiceConnection(){

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Playback.MusicBinder binder = (Playback.MusicBinder)service;
                //get service
                musicSrv = binder.getService();
                //pass list
                musicSrv.setList(sNameList);
                musicBound = true;
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        };
        
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        final SearchAdapter adapter = new SearchAdapter(songList);
        searchView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                mToolbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSearchViewClosed() {
                mToolbar.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int i = Collections.binarySearch(sNameList, adapter.getItem(position));
                musicSrv.seek(0);
                seekbar.setProgress(0);
                musicSrv.setSong(adapter.getItem(position));
                musicSrv.playSong();
                AllSongs.listView.smoothScrollToPosition(i);
                Toast.makeText(getApplicationContext(), "Playing " + adapter.getItem(position), Toast.LENGTH_SHORT).show();
            }
        });


        ft = new FragmentPageAdapter(getSupportFragmentManager(),songList);
        viewPager.setAdapter(ft);
        tabLayout=(TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("All Songs"));
        tabLayout.addTab(tabLayout.newTab().setText("Artist"));
        tabLayout.addTab(tabLayout.newTab().setText("Album"));
        tabLayout.addTab(tabLayout.newTab().setText("Folders"));
       // tabLayout.addTab(tabLayout.newTab().setText("Playlists"));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mNavigationDrawerFragment.selectItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                mNavigationDrawerFragment.selectItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        panel_flag=false;
        drawable=getDrawable(R.color.myDrawerBackground);
        slideLayout=(SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        playerLayout=(LinearLayout)findViewById(R.id.playerScreen);
        ll = (LinearLayout) findViewById(R.id.ll);
        back = (ImageView) findViewById(R.id.backButton);
        ll.setLayoutParams(new LinearLayout.LayoutParams(screen_width, screen_width));

        slideLayout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                if (v == 1.0)
                    play.setVisibility(View.INVISIBLE);
                if (v > 0 && v != 1.0) {
                    linearDrawable = back.getBackground();
                    play.setVisibility(View.VISIBLE);
                    play.setAlpha(1 - v);
                }
            }

            @Override
            public void onPanelCollapsed(View view) {
                if (panel_flag == true) {
                    back.setImageResource(R.color.myPrimaryColor);
                    back.setBackground(drawable);

                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            slideLayout.setPanelState(PanelState.EXPANDED);
                        }
                    });
                    linearDrawable = drawable;
                    ll.setBackground(getResources().getDrawable(R.color.full_transparent));
                    panel_flag = false;
                }
                frameLayout.setPadding(0, 0, 0, dpToPx(25));
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPanelExpanded(View view) {
                if (panel_flag == false) {

                    drawable = back.getBackground();
                    back.setBackground(getDrawable(R.mipmap.back_button));
                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            slideLayout.setPanelState(PanelState.COLLAPSED);
                        }
                    });
                    ll.setBackground(linearDrawable);
                    panel_flag = true;
                }
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });
        play_pause=(ImageView)findViewById(R.id.play_pause);
        mHandler=new Handler();
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            String str=intent.getStringExtra(Playback.extra);
        //  Toast.makeText(getApplicationContext(),"Broadcasting: "+str,Toast.LENGTH_SHORT).show();
            int i=Collections.binarySearch(sNameList, str);
            currentSong(i);
            play_pause.setBackgroundResource(R.mipmap.ic_paused);
            play.setBackgroundResource(R.mipmap.ic_pause);
            fstSongPlayed=true;
            int dur=musicSrv.getDur();
                seekbar.setMax(dur);
                seekbar.setProgress(0);
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser)
                        musicSrv.seek(seekBar.getProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            totalDur.setText(((dur / 1000) / 60) + ":" + ((dur / 1000) % 60));
            scroll=0;

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicSrv.isPng()) {
                        int currentPosition = musicSrv.getPosn();
                        seekbar.setProgress(currentPosition);
                        int maxScroll = lyricsTV.getHeight()-(lyricsTV.getHeight()/7);
                        curDur.setText(((currentPosition / 1000) / 60) + ":" + ((currentPosition / 1000) % 60));
                        currentPosition-=10000;
                        if(currentPosition>10000) {
                            int newScroll = (currentPosition * maxScroll) / musicSrv.getDur();
                            //  Toast.makeText(getApplicationContext(),String.valueOf(newScroll)+" Max "+String.valueOf(maxScroll),Toast.LENGTH_SHORT).show();
                            scrollView.smoothScrollTo(scroll, newScroll);
                            scroll = newScroll;
                        }else {
                            scrollView.scrollTo(scroll,0);
                        }
                    }
                    mHandler.postDelayed(this,1000);
                }
            });

            /* REMEMBER there is always a better method

            updateSeekbar= new Thread(){
                    @Override
                    public void run() {
                        int totalDuration= (int) songList.get(musicSrv.getPlayingSong()).getDuration();
                        int currentPosition = 0;
                        while(currentPosition < totalDuration){
                            try {
                                sleep(500);
                                currentPosition= musicSrv.getPosn();
                                seekbar.setProgress(currentPosition);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int dur= musicSrv.getPosn();
                                        curDur.setText(((dur / 1000) / 60) + ":" + ((dur / 1000) % 60));
                                    }
                                });
                            }catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                updateSeekbar.start(); */
             CurrentPlayingSongName=str;
             CurrentPlayingSongName= CurrentPlayingSongName.replaceAll(" ","_");
             searchLyrics(str + " " + songList.get(i).getArtist());
             AllSongs.songAdt.notifyDataSetChanged();
            }
        };
        lyricsTV=(TextView)findViewById(R.id.lyrics_textview);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


    }

    //REGISTERING BROADCAST RECIEVER---------------------------------------->
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("play"));
        try {
          if(sNameList.size()!=0) {
            currentSong(readsSongplayed());
          }
        }catch(Exception e){
            Log.e("IN onResume","size()"+e.toString());
        }
    }

    @Override
    protected void onPause(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        if(musicConnection!=null) {
            unbindService(musicConnection);
            musicBound=false;
        }
        musicSrv.stopSelf();
        musicSrv.onDestroy();
        super.onDestroy();
    }

    // DP to Pixels conversion--------------------------------------->
    public int dpToPx(int dp)
    {
        DisplayMetrics displayMetrics=getApplicationContext().getResources().getDisplayMetrics();
        int px=Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    //Scan Songs Here------------------------------------------------->

    public void scanSongs(View v)
    {  /* count=0;
        songList.clear();
        sNameList.clear();

        getSongList();
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitl().compareTo(b.getTitl());
            }
        }); */
        TextView t1=(TextView)findViewById(R.id.scan_songs);
        count=songList.size();
        t1.setText("Song Scanned: " + count);
    }

    //Song List Creates Here--------------------------------------------->

    public void getSongList() {
        //retrieve song info
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumID = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int albumColumn=musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int size=musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.SIZE);
            int filepath=musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int disp=musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int dur=musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            //add songs to list
            do {
                Long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum=musicCursor.getString(albumColumn);
                Long thisAlbumID=musicCursor.getLong(albumID);
                String thisfolder=musicCursor.getString(filepath);
                long thisDuration=musicCursor.getLong(dur);
                if(musicCursor.getLong(size)>500000) {
                    while(sNameList.contains(thisTitle))
                    { thisTitle=thisTitle+"_"; }
                    songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisAlbumID, folderName(thisfolder),thisDuration));
                    sNameList.add(thisTitle);
                }
            }while (musicCursor.moveToNext());
        }
    }

    public String folderName(String folder) {
        int i=folder.lastIndexOf("/");
        folder= folder.substring(0,i+1);
        return folder;
    }


    //Return List Here------------------------------------------------------>

    public ArrayList<Song> returnSonglist()
    {
        return songList;
    }

    public ArrayList<String> getSongNameList(){ return sNameList; }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                try{
                    viewPager.setCurrentItem(position);
                    blank_main blank_main=new blank_main();
                    fragmentManager =getSupportFragmentManager();
                    fragmentTransaction=fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, blank_main);
                    fragmentTransaction.commit();
                }catch (Exception e)
                {
                    System.out.println("Error at MainActivity.onNavigationDrawerItemSelected");
                }
                break;
            case 1: {

                viewPager.setCurrentItem(position);
                blank_main blank_main=new blank_main();
                fragmentManager =getSupportFragmentManager();
                fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, blank_main);
                fragmentTransaction.commit();
                break;
            }
            case 2: {
                viewPager.setCurrentItem(position);
                blank_main blank_main=new blank_main();
                fragmentManager =getSupportFragmentManager();
                fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, blank_main);
                fragmentTransaction.commit();
                    break;
                }
            case 3: {
                viewPager.setCurrentItem(position);
                blank_main blank_main=new blank_main();
                fragmentManager =getSupportFragmentManager();
                fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, blank_main);
                fragmentTransaction.commit();
                break;
            }
            /*case 4: {
                viewPager.setCurrentItem(position);
                blank_main blank_main=new blank_main();
                fragmentManager =getSupportFragmentManager();
                fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, blank_main);
                fragmentTransaction.commit();
                break;
            }*/
            case 4:
            {
              Intent in=new Intent(this,SearchLyrics.class);
              startActivity(in);
              break;
            }
            case 5:
                ScanSongs scanSongs=new ScanSongs();
                fragmentManager =getSupportFragmentManager();
                fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, scanSongs);
                fragmentTransaction.commit();
                break;
            case 6:
                Intent in=new Intent(this,Settings.class);
                startActivity(in);
                break;

        }
    }

    boolean doubleBackPressed=false;
    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
        if(slideLayout.getPanelState()==PanelState.EXPANDED)
            slideLayout.setPanelState(PanelState.COLLAPSED);
        else
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }else{
            if (doubleBackPressed) {
                moveTaskToBack(true);
                return;
            }
            this.doubleBackPressed = true;
            Toast.makeText(this, "Press again to go background", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackPressed = false;
                }
            }, 2000);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            MenuItem item = menu.findItem(R.id.action_search);
            searchView.setMenuItem(item);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

                return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    //Search Adapter goes here-------------------------------------->
    private class SearchAdapter extends BaseAdapter implements Filterable {

        private ArrayList<String> data;

        private String[] typeAheadData;

        private String[] typeAheadData2;
        LayoutInflater inflater;

        public SearchAdapter(ArrayList<Song> ar) {
            inflater = LayoutInflater.from(MainActivity.this);
            data = new ArrayList<String>();
            typeAheadData =new String[ar.size()] ;
            typeAheadData2 =new String[ar.size()] ;
            for(int i=0;i<ar.size();i++) {
                typeAheadData[i] = ar.get(i).getTitl();
                typeAheadData2[i]= ar.get(i).getArtist();
            }
        }


        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (!TextUtils.isEmpty(constraint)) {
                        // Retrieve the autocomplete results.
                        List<String> searchData = new ArrayList<>();

                        for (int i=0;i<typeAheadData.length;i++) {
                            String str=typeAheadData[i];
                            String str2=typeAheadData2[i];
                            if (str.toLowerCase().contains(constraint.toString().toLowerCase())||
                                    str2.toLowerCase().contains(constraint.toString().toLowerCase())) {
                                searchData.add(str);
                            }
                        }

                        // Assign the data to the FilterResults
                        filterResults.values = searchData;
                        filterResults.count = searchData.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                    if (results.values != null) {
                        data = (ArrayList<String>) results.values;
                        notifyDataSetChanged();
                    }
                }
            };
            return filter;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder mViewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                mViewHolder = new MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (MyViewHolder) convertView.getTag();
            }

            String currentListData = (String) getItem(position);
            mViewHolder.textView.setTextColor(getResources().getColor(android.R.color.white));
            mViewHolder.textView.setText(currentListData);

            return convertView;
        }


        private class MyViewHolder {
            TextView textView;

            public MyViewHolder(View convertView) {
                textView = (TextView) convertView.findViewById(android.R.id.text1);
            }
        }
    }

    //BLUR BITMAP--------------------------------------------------------->
    private Bitmap createBitmapBlur(Bitmap src, float r) {

        //Radius range (0 < r <= 25)
        if(r <= 0){
            r = 0.1f;
        }else if(r > 25){
            r = 25.0f;
        }
        Bitmap bitmap = Bitmap.createBitmap(
                src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(getApplicationContext());

        Allocation blurInput = Allocation.createFromBitmap(renderScript, src);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(r);
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();
        return bitmap;
    }

    //GET ALBUM ART------------------------------------------------------->
    public Bitmap getAlbumArt(int i)
    {
        Bitmap songImage=null;
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, songList.get(i).getAlbumId());

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

    //Set Current playing
    public void currentSong(int i)
    {
        playing_song_name=(TextView)findViewById(R.id.playing_song_title);
        playing_song_artist=(TextView)findViewById(R.id.playing_song_artist);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        Bitmap bit=getAlbumArt(i);
        try {
            int ii=bit.getWidth();
        }catch (Exception e)
        {
          bit=BitmapFactory.decodeResource(getResources(),R.drawable.bg_1);
        }
        BitmapDrawable bg = new BitmapDrawable(createBitmapBlur
                (scaleBitmap(bit, screen_width, screen_Hieght), 25.0f));
        drawerLayout.setBackground(bg);
        Bitmap b=Bitmap.createScaledBitmap(((BitmapDrawable)bg).getBitmap(),screen_width/7,screen_Hieght/7,true);
        Bitmap ee=createBitmapBlur(b, 25.0f);
        playerLayout.setBackground(new BitmapDrawable(ee));
        Bitmap k=createBitmapBlur(Bitmap.createScaledBitmap(ee,screen_width/25,screen_Hieght/25,true),25.0f);
        mNavigationDrawerFragment.setDrawerBackground(new BitmapDrawable(k));
        back=(ImageView)findViewById(R.id.backButton);
        BitmapDrawable bitmap=new BitmapDrawable(bit);
        if(slideLayout.getPanelState()==PanelState.EXPANDED)
        {ll.setBackground(bitmap);
         drawable=bitmap;
        }
        else
        back.setBackground(bitmap);
        playing_song_name.setText(songList.get(i).getTitl());
        playing_song_artist.setText(songList.get(i).getArtist());
        playing_song_name.setSelected(true);
        playing_song_artist.setSelected(true);
        try {
            AllSongs.listView.setSelection(i);
            AllSongs.listView.setItemChecked(i, true);
            seekbar.setMax((int) songList.get(i).getDuration());
        }catch(Exception e)
        { Log.d("Allsongs.listview","catched at setItemChecked"); }
        try {
            AllSongs.songAdt.notifyDataSetChanged();
        }catch(Exception e){
            Log.e("Mainactivity","At notify Data set");
        }
    }

    //PlayNext
    public void playnext(View v)
    {
        musicSrv.playNext();
    }
    //Play Prev
    public void playprev(View v)
    {
        musicSrv.playPrev();
    }

    boolean fstSongPlayed=false;
    //Pause Play
    public void play_pauseClick(View v) {

        if(musicSrv.isPng())
        {   play_pause.setBackgroundResource(R.mipmap.ic_playing);
            play.setBackgroundResource(R.mipmap.ic_play);
            musicSrv.pausePlayer();
        }else
        { if(fstSongPlayed) {
            play_pause.setBackgroundResource(R.mipmap.ic_paused);
            play.setBackgroundResource(R.mipmap.ic_pause);
            musicSrv.go();
         }else {
            musicSrv.playSong();
            fstSongPlayed=true;
          }
        }
        AllSongs.songAdt.notifyDataSetChanged();
    }

    //Song Last played Reading
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

    //Check NETWORK CONNECTION
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //LYRICS SEARCH FUNCTION
    public static loadWebPage lwp;

    public void searchLyrics(String ed)
    {   try{
        lwp.cancel(true);
        }catch(Exception e){
         }
        String lyrs= getSavedLyrics();
        if(lyrs.compareTo("")!=0){
            lyricsTV.setText(lyrs);
            return ;
        }
        if(!isNetworkAvailable())
    {
        lyricsTV.setText("CHECK NETWORK CONNECTION !");
        return;
    }

        {
        progressBar.setVisibility(View.VISIBLE);

        String s = "http://search.letssingit.com/cgi-exe/am.cgi?a=search&artist_id=&l=archive&s=";
        String d = "";
        s = s + ed;
        for (int k = 0; k < s.length(); k++) {
            if (s.charAt(k) == ' ') {
                d = d + '+';
            } else {
                d = d + s.charAt(k);
            }
        }
        lyricsTV.setText("        Searching lyrics. . .");
            lwp=new loadWebPage();
        lwp.execute(d);
    }
    }


    //EXTRACT LYRICS PAGE URL FROM SEARCH PAGE
    public static loadLyricsPage llp;

    public void getWebsite(String webpage)
    {   try{
        llp.cancel(true);
        }catch(Exception e){

        }
        String str = "";
        int i;
        try{
        if(webpage==null||webpage.contains(" 0 results"))
        {
            lyricsTV.setText("Unable to Find your Song");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        for(i=0;i<webpage.length();i++)
        {
            if(webpage.charAt(i)=='a'&&webpage.charAt(i+1)=='-'&&webpage.charAt(i+2)=='p'&&webpage.charAt(i+3)=='o')
            {
                i=i+140;
                break;
            }
        }
        for(int j=i;j<webpage.length();j++)
        {
            if(webpage.charAt(j)=='h'&&webpage.charAt(j+1)=='r'&&webpage.charAt(j+2)=='e'&&webpage.charAt(j+3)=='f'
                    &&webpage.charAt(j+4)=='='&&webpage.charAt(j+5)=='"')
            {
                i=j+6;
                break;
            }
        }
        while(webpage.charAt(i)!='"')
        {
            str=str+webpage.charAt(i);
            i++;
        }
          llp= new loadLyricsPage();
          llp.execute(str);
        }catch(Exception e){
            lyricsTV.setText("Unable to Find your Song");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
    }
    //EXTRACT LYRICS FROM PAGE
    public void getLyrics(String result)
    {
        String str = "";
        if(result==null){
            lyricsTV.setText("Unable to find your song");
            return;
        }
        int i=34;
        while(result.charAt(i)!='<')
        {
            str=str+result.charAt(i);
            i++;
        }
        str=str+"\n\n";
        for(i=0;i<result.length();i++)
        {
            if(result.charAt(i)=='d'&&result.charAt(i+1)=='='&&result.charAt(i+2)=='l'&&result.charAt(i+3)=='y'
                    &&result.charAt(i+4)=='r'&&result.charAt(i+5)=='i')
            {
                i=i+10;
                break;
            }
        }
        int count=1;
        for(int j=i;j<result.length();j++) {
            if(count==0){
                break;
            }

            if(result.charAt(j)=='<'&&result.charAt(j+1)=='D'&&result.charAt(j+2)=='I')
            {  count++;
               // break;
            }else
            if(result.charAt(j)=='<'&&result.charAt(j+1)=='/'&&result.charAt(j+2)=='D')
            {  count--;
                if(count==0){
                    break;
                }
                j+=5;
            }else
            if(result.charAt(j)=='<'&&result.charAt(j+1)=='B'&&result.charAt(j+2)=='R') {
                j = j + 3;
                str=str+"\n";
            }else
            if(count>1){
                continue;
            }else
            {
                str=str+result.charAt(j);
            }
        }
        progressBar.setVisibility(View.INVISIBLE);
        lyricsTV.setText(str);
        saveLyrics(str,CurrentPlayingSongName);
    }

    //LOAD LYRICS PAGE
    public class loadLyricsPage extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            connection=null;
            try {
                URL url=new URL(params[0]);
                connection=(HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream stream=connection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer=new StringBuffer();
                String line="";
                int c=0;
                while((line=reader.readLine())!=null) {
                    c++;
                    if(c<5||c>200&&c<450)
                        buffer.append(line);
                }
                if(isCancelled()){
                    return null;
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                System.out.println("MalformedURLException hai");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("IOException hai");
                e.printStackTrace();
            }finally {
                if(connection!=null)
                    connection.disconnect();
                try {
                    if(reader!=null)
                        reader.close();
                } catch (IOException e) {
                    System.out.println("Fir se IOException hai");
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            getLyrics(result);
        }
    }


    //LOAD SEARCH PAGE
    public class loadWebPage extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            connection=null;
            try {
                URL url=new URL(params[0]);
                connection=(HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream stream=connection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer=new StringBuffer();
                String line="";
                while((line=reader.readLine())!=null) {
                    buffer.append(line);
                }
                if(isCancelled()){
                    return null;
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                System.out.println("MalformedURLException hai");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("IOException hai");
                e.printStackTrace();
            }finally {
                if(connection!=null)
                    connection.disconnect();
                try {
                    if(reader!=null)
                        reader.close();
                } catch (IOException e) {
                    System.out.println("Fir se IOException hai");
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            getWebsite(result);
        }
    }

    //SAVE LYRICS OFFLINE
    public void saveLyrics(String lyrics,String Name){
        BufferedWriter out;
        try {
            File path=new File(Environment.getExternalStorageDirectory().toString()+"/Unique/");
            String filename=Name;
            if(!path.exists()){
                path.mkdir();
            }
            filename=filename.replaceAll(" ","_");
            File mypath=new File(path,filename);
            if(mypath.exists()){
                mypath.delete();
            }
            if (!mypath.exists()) {
                mypath.createNewFile();
                out = new BufferedWriter(new FileWriter(mypath,true));
                out.write(lyrics);
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //GET SAVED LYRICS
    public String getSavedLyrics(){
        StringBuilder Lyrics = new StringBuilder();
        File path=new File(Environment.getExternalStorageDirectory().toString()+"/Unique/");
        String name=CurrentPlayingSongName;
        File mypath=new File(path,name);

        if(mypath.exists()){

            try {
                BufferedReader br = new BufferedReader(new FileReader(mypath));
                String line;
                while ((line = br.readLine()) != null) {
                    Lyrics.append(line);
                    Lyrics.append('\n');
                }
                br.close();

                return Lyrics.toString();
             //   textView.setText(text.toString());
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }
        }else{
            return "";
        }
        return "";
    }
}
