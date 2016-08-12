package player.music.com.unique;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        RelativeLayout relativeLayout= (RelativeLayout)findViewById(R.id.splashLayout);
        try{
            Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_with_fade);
            ImageView im=(ImageView)findViewById(R.id.splashImage);
            im.setAnimation(animation);
            im.startAnimation(animation);
            im.postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }catch (Exception e){
        }
    }

}
