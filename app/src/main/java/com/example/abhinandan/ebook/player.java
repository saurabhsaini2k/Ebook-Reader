package com.example.abhinandan.ebook;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;


public class player extends ActionBarActivity {

    Button btnPlay,btnPause;
    TextView tvSong;
    MediaPlayer mp;
    String songName,songPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        tvSong=(TextView)findViewById(R.id.tvSong);
        btnPlay=(Button)findViewById(R.id.btnPlay);

        mp = ((MyApplication)this.getApplication()).getMediaPlayer();


        Bundle extras=getIntent().getExtras();
        songName=extras.getString("songName");
        songPath=extras.getString("songPath");
        tvSong.setText(songName);

        playSong(songPath, songName);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check for already playing
                if(mp.isPlaying()){
                    if(mp!=null)
                        mp.pause();
                }else{
                    // Resume song
                    if(mp!=null)
                        mp.start();
                }
            }
        });
    }

    public void  playSong(String songPath,String songName){
        // Play song
        try {

            mp.reset();
            mp.setDataSource(songPath);
            mp.prepare();
            mp.start();
            // Displaying Song title
            tvSong.setText(songName);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
