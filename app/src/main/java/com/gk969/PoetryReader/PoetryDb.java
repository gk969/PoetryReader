package com.gk969.PoetryReader;

import android.content.Context;

import com.gk969.Utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PoetryDb {
    private static final String TAG="PoetryDb";
    
    
    ArrayList<PoetrySet> poetrySetList=new ArrayList<>();
    
    class PoetrySet{
        String name;
        ArrayList<Poetry> poetryList=new ArrayList<>();
    }
    String[] POETRY_ASSETS_NAME={"tang300", "song300", "school"};
    String POETRY_DELI="----------------------------------";
    int[] POETRY_SET_NAME_ID={R.string.tang300, R.string.song300, R.string.school_poetry};
    
    class Poetry{
        String title;
        String author;
        String content;
        String audioAssetsPath;
    }
    
    private Context context;
    
    public PoetryDb(Context context){
        this.context = context;
    }
    
    void init(){
        for(int i=0; i<POETRY_SET_NAME_ID.length; i++){
            PoetrySet poetrySet=new PoetrySet();
            poetrySet.name=context.getString(POETRY_SET_NAME_ID[i]);
            try {
                String txt= Utils.readString(context.getAssets().open("poetry_txt/"+POETRY_ASSETS_NAME[i]+".txt"));
                String[] poetess=txt.split(POETRY_DELI);
                for(String poetry_str:poetess){
                    poetry_str=poetry_str.trim();
                    String[] lines=poetry_str.split("\n");
                    Poetry poetry=new Poetry();
                    poetry.title=lines[0].replace("《", "").replace("》", "");
                    poetry.author=lines[1];
                    poetry.audioAssetsPath="poetry_mp3/"+POETRY_ASSETS_NAME[i]+"/"+poetry.title+"-"+poetry.author+".mp3";
                    poetry.content="";
                    for(int h=2; h<lines.length; h++){
                        String line=lines[h].trim();
                        if(!line.isEmpty()){
                            poetry.content+=line+"\n";
                        }
                    }
                
                    poetrySet.poetryList.add(poetry);
                }
    
                Collections.sort(poetrySet.poetryList, new Comparator<Poetry>() {
                    @Override
                    public int compare(Poetry p1, Poetry p2) {
                        int lenDiff= (int) (p1.content.length()-p2.content.length());
                        if(lenDiff!=0){
                            return lenDiff;
                        }
                        
                        return p1.author.compareTo(p2.author);
                    }
                });
                poetrySetList.add(poetrySet);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        
    }
}
