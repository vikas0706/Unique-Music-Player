package player.music.com.unique;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by VIKAS SINGH on 23-01-2016.
 */
public class ExpandableListAdapter  extends BaseExpandableListAdapter {
    public ArrayList<Song> songs;
    public LayoutInflater parentInf;
    public LayoutInflater childInf;
    public ArrayList<String> parentList;
    public Context context;
    ArrayList<ArrayList<String>> child;
    int groupimage;
    public ExpandableListAdapter(Context context, ArrayList<Song> songs, ArrayList<String> parentList,
                                 ArrayList<ArrayList<String>> child,int image) {
        this.songs =songs;
        parentInf= LayoutInflater.from(context);
        childInf= LayoutInflater.from(context);
        this.context=context;
        this.parentList=parentList;
        this.child=child;
        this.groupimage=image;
    }


    @Override
    public int getGroupCount() {
        return parentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        LinearLayout songLay = (LinearLayout)parentInf.inflate
                (R.layout.parent_list_layout, parent, false);
        TextView artistView = (TextView)songLay.findViewById(R.id.parent_textView);
        TextView childCount=(TextView)songLay.findViewById(R.id.child_count);
        ImageView imageView=(ImageView)songLay.findViewById(R.id.parent_image);
        imageView.setImageResource(groupimage);
        artistView.setText(parentList.get(groupPosition));
        childCount.setText(getChildrenCount(groupPosition) + " Tracks");
        songLay.setTag(groupPosition);

        /*ScaleAnimation animation=new ScaleAnimation((float)0.5,(float)1.0,(float)0.5,(float)1.0,(float)1.0,(float)1.0);
        animation.setDuration(500);
        songLay.setAnimation(animation);
        animation=null;*/
        return songLay;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LinearLayout songLay = (LinearLayout)parentInf.inflate
                (R.layout.child_list_layout, parent, false);
        TextView childList=(TextView)songLay.findViewById(R.id.child_TextView);
        childList.setText(child.get(groupPosition).get(childPosition));
        return songLay;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
