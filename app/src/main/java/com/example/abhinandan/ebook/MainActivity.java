package com.example.abhinandan.ebook;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Environment;
import android.speech.RecognizerIntent;
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


public class MainActivity extends ActionBarActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    Button bt_speak;
    TextView speech_text,tvCurrentSelected;
    ListView lv_files;
    int curSelection=-1;
    ArrayList< Pair<String,String> > filesInFolder; // contains name as pair.first and its path as pair.second
    public GestureDetectorCompat mDetector;
    MyApplication mObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mObject=((MyApplication)this.getApplication());
        bt_speak=(Button)findViewById(R.id.bt_speak);
        speech_text=(TextView)findViewById((R.id.speech_text));
        tvCurrentSelected=(TextView)findViewById(R.id.tvCurrentSelected);
        lv_files=(ListView)findViewById(R.id.lv_files);

        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setIsLongpressEnabled(true); // gesture input for voice input

        bt_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeSpeechInput();
            }
        });

        // listview onclick listner
        lv_files.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(),player.class);
                intent.putExtra("songName",filesInFolder.get(position).first);
                intent.putExtra("songPath",filesInFolder.get(position).second);
                startActivity(intent);
            }
        });

        fillListView();
        nextSelection();
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
        // highlighting selected item in list view not working
//        lv_files.requestFocusFromTouch();
//        lv_files.setSelection(curSelection);
//        lv_files.requestFocus();
    }

    // fill listview with audio files in folder ebook in sd card
    public  void fillListView(){
        String sd_path= Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.e("","sd path" + sd_path);
        filesInFolder = FileCrawler.GetFiles(sd_path+"/ebook");

        ArrayList<String> fileName= new ArrayList<String>();

        for( Pair<String,String> p : filesInFolder)
            fileName.add(p.first);

        lv_files.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fileName));
    }

    // call this function whenever speech input is required
    public void takeSpeechInput(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something");

        if(mObject.getMediaPlayer().isPlaying())
            mObject.getMediaPlayer().pause();

        try{
            startActivityForResult(intent,100);

        }catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(),"Speech support not found",Toast.LENGTH_SHORT).show();
        }
    }

    // result of startActivityforResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if(resultCode==RESULT_OK && data!=null)
                {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speech_text.setText(result.get(0));

                    String command=result.get(0);

                    switch(command)
                    {
                        case "next":
                            nextSelection();
                            break;
                        case "select":
                            selectCurrentItem();
                            break;
                        default:
                    }
                }
            }
            mObject.playPause();
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
        Log.d("","Here ..stc....................................................");
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
