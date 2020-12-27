package com.gk969.Utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;

import java.io.IOException;

public class AudioPlayer {
    private final static String TAG = "AudioPlayer";
    
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private ProgressListener listener;
    private boolean isPlaying;
    private Context context;
    
    public interface ProgressListener {
        public void onStarted(int totalTimeMs);

        public void onProgress(int curPositionMs);

        public void onStopped();
    }

    public AudioPlayer(Context context){
        this.context=context;
    }
    
    private void refreshProgress() {
        if (mediaPlayer == null) {
            return;
        }

        if (isPlaying && !mediaPlayer.isPlaying()) {
            stop();
            return;
        }

        if (listener != null) {
            listener.onProgress(mediaPlayer.getCurrentPosition());
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshProgress();
            }
        }, 200);
    }
    
    private static final int DISK_FILE=0;
    private static final int ASSETS_FILE=1;
    
    public void play(String filePath) {
        play(filePath, null);
    }
    public void play(String filePath, ProgressListener listener){
        play(filePath, DISK_FILE, listener);
    }
    
    public void playAssets(String filePath) {
        play(filePath, ASSETS_FILE, null);
    }
    public void playAssets(String filePath, ProgressListener listener){
        play(filePath, ASSETS_FILE, listener);
    }
    
    public void play(String filePath, int dataType, ProgressListener listener) {
        stop();

        this.listener = listener;
        mediaPlayer = new MediaPlayer();
        try {
            if(dataType==DISK_FILE) {
                mediaPlayer.setDataSource(filePath);
            }else if(dataType==ASSETS_FILE){
                AssetFileDescriptor afd = context.getAssets().openFd(filePath);
                mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                
            }
            mediaPlayer.prepare();
            if (listener != null) {
                listener.onStarted(mediaPlayer.getDuration());
            }
            mediaPlayer.start();
            isPlaying = true;

            refreshProgress();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        isPlaying = false;
        handler.removeMessages(0);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            
            if (listener != null) {
                listener.onStopped();
            }
        }
    }

}
