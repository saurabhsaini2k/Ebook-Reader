package com.example.abhinandan.ebook;

import android.app.Application;
import android.media.MediaPlayer;

/**
 * Created by ABHINANDAN on 3/17/2016.
 */
public class MyApplication extends Application {

    MediaPlayer mp=new MediaPlayer();

    public MediaPlayer getMediaPlayer()
    {
        return mp;
    }

    public void playPause(){
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
}
