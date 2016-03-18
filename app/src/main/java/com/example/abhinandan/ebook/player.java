package com.example.abhinandan.ebook;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class player extends ActionBarActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    Button btnPlay,btnPause;
    TextView tvSong;
    MediaPlayer mp;
    String songName,songPath;
    MyApplication mObject;
    public GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setIsLongpressEnabled(true);

        tvSong=(TextView)findViewById(R.id.tvSong);
        btnPlay=(Button)findViewById(R.id.btnPlay);
        mObject=((MyApplication)this.getApplication());
        mp = mObject.getMediaPlayer();


        Bundle extras=getIntent().getExtras();
        songName=extras.getString("songName");
        songPath=extras.getString("songPath");
        tvSong.setText(songName);

        playSong(songPath, songName);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mObject.playPause();
            }
        });
    }

    // starting the song initially
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

    // call this function whenever speech input is required
    public void takeSpeechInput(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something");

        try{
            if(mObject.getMediaPlayer().isPlaying())
            {
                mObject.getMediaPlayer().pause(); // pause audio during speech input
                startActivityForResult(intent,100);   // already playing
            }else{
                startActivityForResult(intent,101);   //Not playing
            }

        }catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(), "Speech support not found", Toast.LENGTH_SHORT).show();
        }
    }

    // result of startActivityforResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: { // initially playing
                if(resultCode==RESULT_OK && data!=null)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String command=result.get(0);

                    switch(command)
                    {
                        case "play":
                            mObject.playPause();
                            break;
                        case "pause":
                            // remain paused
                            break;
                        case "stop":
                            //remain paused
                            break;
                        case "back":
                            //play it and finish activity
                            mObject.playPause();
                            finish();
                            break;
                        default:
                    }
                }
            }
            break;
            case 101:{ // initially not playing
                if(resultCode==RESULT_OK && data!=null)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String command=result.get(0);

                    switch(command)
                    {
                        case "play":
                            mObject.playPause();
                            break;
                        case "pause":
                            // remain paused
                            break;
                        case "stop":
                            //remain paused
                            break;
                        case "back":
                            //remain paused and finish activity
                            finish();
                            break;
                        default:
                    }
                }
            }
            break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d("", "Here ..stc....................................................");
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d("","Here .......dtc...............................................");
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d("","Here .........dte.............................................");
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("","Here ........od..............................................");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d("","Here ...........sp...........................................");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("","Here ............stu..........................................");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    // only this gesture is active and initiate speech input
    @Override
    public void onLongPress(MotionEvent e) {
        takeSpeechInput();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
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
