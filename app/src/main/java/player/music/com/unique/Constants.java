package player.music.com.unique;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by VIKAS SINGH on 19-01-2017.
 */

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.unique.customnotification.action.main";
        public static String INIT_ACTION = "com.unique.customnotification.action.init";
        public static String PREV_ACTION = "com.unique.customnotification.action.prev";
        public static String PLAY_ACTION = "com.unique.customnotification.action.play";
        public static String NEXT_ACTION = "com.unique.customnotification.action.next";
        public static String STARTFOREGROUND_ACTION = "com.unique.customnotification.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.unique.customnotification.action.stopforeground";

    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_launcher, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }

}
