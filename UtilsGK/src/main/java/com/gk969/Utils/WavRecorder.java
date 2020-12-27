package com.gk969.Utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.SystemClock;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WavRecorder {
    private volatile boolean isRecording;
    private volatile boolean isValid;

    private long startTime;
    private long refreshTime;
    private Handler handler = new Handler();

    public interface Listener {
        public void onRecordTime(long timeInMills);

        public void onStopped(boolean isValid);
    }
    
    public void start(final File file, final Listener listener) {
        start(file, 44100, listener);
    }
    
    public void start(final File file, final int sampleRate, final Listener listener) {
        isRecording = true;
        isValid = true;
        new Thread() {
            @Override
            public void run() {
                startTime = SystemClock.uptimeMillis();
                refreshTime = startTime;

                int audioSource = MediaRecorder.AudioSource.MIC;
                int channelConfig = AudioFormat.CHANNEL_IN_MONO;//AudioFormat.CHANNEL_IN_STEREO;
                int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                AudioRecord audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, minBufferSize);
                try {
                    file.delete();
                    RandomAccessFile writer = new RandomAccessFile(file, "rw");
                    writer.seek(44);
                    byte[] buffer = new byte[minBufferSize];

                    audioRecord.startRecording();
                    while (isRecording) {
                        int readSize = audioRecord.read(buffer, 0, minBufferSize);
                        writer.write(buffer, 0, readSize);

                        long curTime = SystemClock.uptimeMillis();
                        if (curTime - refreshTime > 500) {
                            refreshTime = curTime;
                            final long time = curTime - startTime;
                            if (listener != null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onRecordTime(time);
                                    }
                                });
                            }
                        }
                    }

                    audioRecord.stop();
                    audioRecord.release();

                    if (isValid) {
                        writeWaveFileHeader(writer, writer.length(), sampleRate, 1);
                        writer.close();
                    } else {
                        writer.close();
                        file.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (listener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onStopped(isValid);
                        }
                    });
                }
            }
        }.start();
    }

    private void writeWaveFileHeader(RandomAccessFile raf, long fileLength, int sampleRate, int channel) throws IOException {
        long totalDataLen = fileLength + 36;
        int byteRate = sampleRate * 16 * channel / 8;
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channel;
        header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (fileLength & 0xff);
        header[41] = (byte) ((fileLength >> 8) & 0xff);
        header[42] = (byte) ((fileLength >> 16) & 0xff);
        header[43] = (byte) ((fileLength >> 24) & 0xff);
        raf.seek(0);
        raf.write(header, 0, 44);
    }

    public void stop() {
        isRecording = false;
    }

    public void cancel() {
        isValid = false;
        isRecording = false;
    }

}
