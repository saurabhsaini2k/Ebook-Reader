package com.example.abhinandan.ebook;

import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ABHINANDAN on 3/15/2016.
 */
public class FileCrawler {

    public static ArrayList< Pair<String,String> > GetFiles(String DirectoryPath) {
        ArrayList< Pair<String,String> > MyFiles = new ArrayList< Pair<String,String> >();
        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++)
                MyFiles.add( new Pair<String, String>(files[i].getName(),files[i].getAbsolutePath()));
        }

        return MyFiles;
    }
}
