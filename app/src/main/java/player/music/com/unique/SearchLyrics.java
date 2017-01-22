package player.music.com.unique;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SearchLyrics extends AppCompatActivity {
    TextView tv;
    HttpURLConnection connection;
    BufferedReader reader;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lyrics);
        tv=(TextView)findViewById(R.id.viewCode);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void click(View v)
    {   if(!isNetworkAvailable())
    {
        Toast.makeText(getApplicationContext(), "CHECK NETWORK CONNECTION", Toast.LENGTH_LONG).show();
    }else {
        progressBar.setVisibility(View.VISIBLE);
        EditText ed = (EditText) findViewById(R.id.editText);
        String s = "http://search.letssingit.com/cgi-exe/am.cgi?a=search&artist_id=&l=archive&s=";
        String d = "";
        s = s + ed.getText().toString();
        for (int k = 0; k < s.length(); k++) {
            if (s.charAt(k) == ' ') {
                d = d + '+';
            } else {
                d = d + s.charAt(k);
            }
        }
        Toast.makeText(getApplicationContext(), "Searching...", Toast.LENGTH_SHORT).show();
        new loadWebPage().execute(d);
    }
    }



    public void getWebsite(String webpage)
    {
        String str = "";
        int i;
        if(webpage.contains(" 0 results"))
        {
            tv.setText("Unable to Find your Song");
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
        new loadLyricsPage().execute(str);
    }


    public void getLyrics(String result)
    {
        String str = "";

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
        tv.setText(str);
    }


    public class loadLyricsPage extends AsyncTask<String,String,String> {

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
                    if(c<5||c>200&&c<420)
                        buffer.append(line);
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


}
