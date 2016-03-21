package com.example.abhinandan.ebook;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends ActionBarActivity{

    Button bt_speak;
    TextView speech_text,tvCurrentSelected;
//    ListView lv_files;
    TextToSpeech textToSpeech;
    View view;

    int curSelection=-1;
    ArrayList< Pair<String,String> > filesInFolder; // contains name as pair.first and its path as pair.second
    public OnSwipeTouchListener mDetector;
    MyApplication mObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
        view=(View)findViewById(R.id.viewMain);
        mObject=((MyApplication)this.getApplication());
        bt_speak=(Button)findViewById(R.id.bt_speak);
        speech_text=(TextView)findViewById((R.id.speech_text));
        tvCurrentSelected=(TextView)findViewById(R.id.tvCurrentSelected);
//        lv_files=(ListView)findViewById(R.id.lv_files);

        view.setOnTouchListener(new OnSwipeTouchListener(this){
            public void onSwipeRight() {
                Log.e("check :","right");
                nextSelection();
            }

            public void onSwipeLeft() {
                Log.e("check :","left");
                prevSelection();
            }

            public void onSwipeTop() {
                Log.e("check :","top");
                exitApp();
            }

            public void onSwipeBottom() {
                Log.e("check :","down");
                curSelection();
            }

            public void onDoubleTap1(){
                Log.e("check :","double tap");

            }

            public void onLongPress1(){
                Log.e("check :","long press");
                takeSpeechInput();
            }
        });

        bt_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // takeSpeechInput();
                textToSpeech.speak("Hi Abhinandan!.", TextToSpeech.QUEUE_FLUSH, null);

                nextSelection();

            }
        });

        // listview onclick listner
//        lv_files.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(view.getContext(), player.class);
//                intent.putExtra("songName", filesInFolder.get(position).first);
//                intent.putExtra("songPath", filesInFolder.get(position).second);
//                startActivity(intent);
//            }
//        });

        fillListView();
        //nextSelection();

    }

    // open player and play the selected audio file by calling the function
    public void selectCurrentItem()
    {
        Intent intent = new Intent(this,player.class);
        intent.putExtra("songName",filesInFolder.get(curSelection).first);
        intent.putExtra("songPath",filesInFolder.get(curSelection).second);
        startActivity(intent);
    }

    // move selected counter to next item in audio file listview
    public void nextSelection(){
        curSelection++;
        if(curSelection == filesInFolder.size())
            curSelection=0;

        tvCurrentSelected.setText(filesInFolder.get(curSelection).first);

        speak(filesInFolder.get(curSelection).first);

        // highlighting selected item in list view not working
//        lv_files.requestFocusFromTouch();
//        lv_files.setSelection(curSelection);
//        lv_files.requestFocus();
    }

    public void prevSelection(){
        curSelection--;
        if(curSelection < 0)
            curSelection=filesInFolder.size()-1;
        tvCurrentSelected.setText(filesInFolder.get(curSelection).first);
        speak(filesInFolder.get(curSelection).first);
    }

    public void curSelection()    {speak(filesInFolder.get(curSelection).first);}

    public void speak(String textToSpeak){
        textToSpeech.speak(textToSpeak,TextToSpeech.QUEUE_FLUSH,null);
    }

    public void exitApp(){
        textToSpeech.stop();
        finish();
    }

    // fill listview with audio files in folder ebook in sd card
    public  void fillListView(){
        String sd_path= Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.i("","sd path"+sd_path);
        filesInFolder = FileCrawler.GetFiles(sd_path+"/ebook");

        ArrayList<String> fileName= new ArrayList<String>();

        for( Pair<String,String> p : filesInFolder)
            fileName.add(p.first);

//        lv_files.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,fileName));
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
                        case "next":
                            nextSelection();
                            mObject.playPause();
                            break;
                        case "select":
                            selectCurrentItem();
                            mObject.playPause();
                            break;
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
                        case "next":
                            nextSelection();
                            break;
                        case "select":
                            selectCurrentItem();
                            break;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
