package com.gk969.PoetryReader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gk969.Utils.AudioPlayer;
import com.gk969.Utils.StorageUtils;
import com.gk969.Utils.ULog;
import com.gk969.Utils.WavRecorder;

import java.io.File;

import androidx.fragment.app.Fragment;

public class PoetryFragment extends Fragment {
    private final static String TAG = "PoetryFragment";
    
    private PoetryDb.Poetry poetry;
    private MainActivity activity;
    private Handler handler=new Handler();
    
    private AudioPlayer audioPlayer;
    private WavRecorder wavRecorder = new WavRecorder();
    
    private File recordWavFile;
    private ImageView buttonManSpeak;
    private ImageView buttonSpeak;
    private ImageView buttonLoop;
    private ImageView buttonStop;
    
    private boolean needLoopPlay;
    
    PoetryFragment(MainActivity activity, PoetryDb.Poetry poetry){
        this.activity=activity;
        this.poetry=poetry;
        audioPlayer=new AudioPlayer(activity);
        File recordWavDir=StorageUtils.getDirInSto(activity.getString(R.string.app_name) +
                "/record");
        recordWavFile=new File(recordWavDir.getPath()+"/"+poetry.title+"-"+poetry.author+".wav");
    }
    
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    
        handler.removeMessages(0);
        stopPlay();
    }
    
    private void stopPlay(){
        needLoopPlay=false;
        refreshLoopButton();
        audioPlayer.stop();
    }
    
    private void refreshLoopButton(){
        int background=needLoopPlay?R.drawable.round_focus_background:R.drawable.round_blur_background;
        buttonLoop.setBackgroundResource(background);
    }
    
    private AudioPlayer.ProgressListener listener=new AudioPlayer.ProgressListener() {
        @Override
        public void onStarted(int totalTimeMs) {
            buttonStop.setVisibility(View.VISIBLE);
        }
        
        @Override
        public void onProgress(int curPositionMs) {
        
        }
        
        @Override
        public void onStopped() {
            if(needLoopPlay){
                startPlay(playDataType);
            }else {
                buttonStop.setVisibility(View.GONE);
            }
        }
    };
    
    private static final int DISK_FILE=0;
    private static final int ASSETS_FILE=1;
    
    private int playDataType;
    private void startPlay(int dataType){
        playDataType=dataType;
        if(dataType==DISK_FILE){
            audioPlayer.play(recordWavFile.getPath(), listener);
        }else{
            audioPlayer.playAssets(poetry.audioAssetsPath, listener);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.poetry_view, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            
            }
        });
        
        String poetryString="《"+poetry.title+"》\n"+poetry.author+"\n\n"+poetry.content;
        ((TextView)view.findViewById(R.id.textPoetry)).setText(poetryString);
    
        buttonLoop=view.findViewById(R.id.buttonLoop);
        buttonLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needLoopPlay=!needLoopPlay;
                refreshLoopButton();
            }
        });
        
        buttonStop=view.findViewById(R.id.buttonStop);
        buttonStop.setVisibility(View.GONE);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlay();
            }
        });
        
        buttonSpeak=view.findViewById(R.id.buttonSpeak);
        buttonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlay(ASSETS_FILE);
            }
        });
        
        buttonManSpeak=view.findViewById(R.id.buttonManSpeak);
        buttonManSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlay(DISK_FILE);
            }
        });
        if(!recordWavFile.isFile()){
            buttonManSpeak.setVisibility(View.GONE);
        }
        
        view.findViewById(R.id.buttonRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlay();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recordToFile(recordWavFile);
                    }
                }, 300);
            }
        });
        return view;
    }
    
    private void recordToFile(final File file) {
        ULog.i(TAG, "recordToFile %s", file.getPath());
        
        LinearLayout layout = (LinearLayout) activity.getLayoutInflater()
                .inflate(R.layout.recorder_dialog, null);
        final TextView textTime = layout.findViewById(R.id.textTime);
        
        wavRecorder.start(file, new WavRecorder.Listener() {
            @Override
            public void onRecordTime(long timeInMills) {
                textTime.setText((timeInMills / 1000) + "s");
            }
            
            @Override
            public void onStopped(boolean isValid) {
                if(isValid){
                    buttonManSpeak.setVisibility(View.VISIBLE);
                }
            }
        });
        
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(getString(R.string.record) + " : " + file.getName())
                .setView(layout)
                .setPositiveButton(R.string.stop, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wavRecorder.stop();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wavRecorder.cancel();
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                wavRecorder.stop();
            }
        });
        dialog.show();
    }
}
