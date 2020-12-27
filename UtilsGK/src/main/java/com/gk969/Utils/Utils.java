package com.gk969.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.TrafficStats;
import android.os.Build;
import android.os.SystemClock;
import android.view.Display;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import androidx.annotation.NonNull;

public class Utils {
    static String TAG = "Utils";
    
    
    public static boolean isArrayEquals(int[] array, int value) {
        if (array.length == 0) {
            return false;
        }
        
        for (int valInArray : array) {
            if (valInArray != value) {
                return false;
            }
        }
        
        return true;
    }
    
    public static boolean isArrayEquals(byte[] array1, byte[] array2) {
        if(array1.length == array2.length) {
            int len = array2.length;
            
            for(int i = 0; i < len; i++) {
                if(array1[i] != array2[i]) {
                    return false;
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    public static class ThreadFactory implements java.util.concurrent.ThreadFactory {
        java.util.concurrent.ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        private String name;
        
        public ThreadFactory(String name) {
            this.name = name;
        }
        
        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            thread.setName(name);
            return thread;
        }
        
    }
    
    public static File newFile(String filePath) {
        File file = new File(filePath);
        
        File dir = new File(file.getParent());
        if(!dir.exists()) {
            dir.mkdirs();
        }
        
        return file;
    }
    
    public static String msToTimeStr(long ms, String dayStr) {
        long second = ms / 1000;
        long minute = second / 60;
        second %= 60;
        long hour = minute / 60;
        minute %= 60;
        long day = hour / 24;
        hour %= 24;
        
        return String.format("%d%s %02d:%02d:%02d", day, dayStr, hour, minute, second);
    }
    
    public static String stringsToString(String[] strings) {
        return stringsToString(strings, " ");
    }
    
    public static String stringsToString(String[] strings, String separator) {
        StringBuilder sb = new StringBuilder();
        for(String string : strings) {
            sb.append(string).append(separator);
        }
        return sb.toString();
    }
    
    public static boolean stringArrayContains(String[] strings, String targetStr) {
        for(String string : strings) {
            if(string.equals(targetStr)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static boolean stringContainsStrings(String string, String[] strings) {
        for(String str : strings) {
            if(string.contains(str)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Binary search target in a sorted list.
     *
     * @param list       sorted list contain object
     * @param target     search target
     * @param comparator the comparator to determine the order of the list
     * @param mustEqual  <tt>true</tt> if must find the equally object in list.
     *                   <tt>false</tt> if just find a range to locate target
     * @return <tt>negative</tt> if search fail.<tt> zero, positive</tt> if search success
     */
    public static <T> int binaryFindIndexInList(List<T> list, T target, Comparator<? super T> comparator, boolean mustEqual) {
        int size = list.size();
        if(size == 0) {
            return -1;
        }
        
        int startComp = comparator.compare(list.get(0), target);
        if(size == 1) {
            return (!mustEqual || startComp == 0) ? 0 : -1;
        }
        if(startComp > 0) {
            return -1;
        }
        if(startComp == 0) {
            return 0;
        }
        
        int endComp = comparator.compare(list.get(size - 1), target);
        if(endComp < 0) {
            return -1;
        }
        if(endComp == 0) {
            return size - 1;
        }
        
        int start = 0;
        int end = size - 1;
        
        while((start + 1) < end) {
            int middle = start + (end - start) / 2;
            int middleComp = comparator.compare(list.get(middle), target);
            // ULog.i(TAG, "binarySearch start %d middle %d end %d comp %d", start, middle, end, middleComp);
            if(middleComp < 0) {
                start = middle;
            } else if(middleComp == 0) {
                return middle;
            } else {
                end = middle;
            }
        }
        
        return (!mustEqual || comparator.compare(list.get(start), target) == 0) ? start : -1;
    }
    
    /**
     * Binary search target in a sorted array.
     *
     * @param array      sorted array contain object
     * @param target     search target
     * @param comparator the comparator to determine the order of the array
     * @param mustEqual  <tt>true</tt> if must find the equally object in array.
     *                   <tt>false</tt> if just find a range to locate target
     * @return <tt>negative</tt> if search fail.<tt> zero, positive</tt> if search success
     */
    public static <T> int binaryFindIndexInArray(T[] array, T target, Comparator<? super T> comparator, boolean mustEqual) {
        int size = array.length;
        if(size == 0) {
            return -1;
        }
        
        int startComp = comparator.compare(array[0], target);
        if(size == 1) {
            return (!mustEqual || startComp == 0) ? 0 : -1;
        }
        if(startComp > 0) {
            return -1;
        }
        if(startComp == 0) {
            return 0;
        }
        
        int endComp = comparator.compare(array[size - 1], target);
        if(endComp < 0) {
            return -1;
        }
        if(endComp == 0) {
            return size - 1;
        }
        
        int start = 0;
        int end = size - 1;
        
        while((start + 1) < end) {
            int middle = start + (end - start) / 2;
            int middleComp = comparator.compare(array[middle], target);
            // ULog.i(TAG, "binarySearch start %d middle %d end %d comp %d", start, middle, end, middleComp);
            if(middleComp < 0) {
                start = middle;
            } else if(middleComp == 0) {
                return middle;
            } else {
                end = middle;
            }
        }
        
        return (!mustEqual || comparator.compare(array[start], target) == 0) ? start : -1;
    }
    
    public static void deleteDir(String dirFilePath) {
        deleteDir(new File(dirFilePath));
    }
    
    public static void deleteDir(File dirFile) {
        //ULog.i(TAG, "deleteDir "+dirFile.getPath());
        if(dirFile == null) {
            return;
        }
        
        if(!dirFile.exists() || !dirFile.isDirectory()) {
            return;
        }
        
        File[] files = dirFile.listFiles();
        if(files != null) {
            for(File file : files) {
                if(file.isFile()) {
                    //ULog.i(TAG, "deleteFile "+file.getPath());
                    file.delete();
                } else {
                    deleteDir(file);
                }
            }
        }
        
        dirFile.delete();
    }
    
    public static void copyFile(File source, File dest) {
        try {
            FileInputStream ins = new FileInputStream(source);
            FileOutputStream outs = new FileOutputStream(dest);
            byte[] buffer = new byte[8192];
            int length;
            while((length = ins.read(buffer)) > 0) {
                outs.write(buffer, 0, length);
            }
            outs.flush();
            ins.close();
            outs.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void copyFile(String srcFilePath, String dstFilePath) {
        copyFile(new File(srcFilePath), new File(dstFilePath));
    }
    
    public static interface ReadInputStreamDataCallback {
        public void onData(byte[] data);
    }
    
    public static void readAllInputStream(InputStream ins, ReadInputStreamDataCallback dataCallback) {
        try {
            byte[] buf = new byte[128];
            int len;
            while((len = ins.read(buf)) != -1) {
                byte[] data;
                if(len < buf.length) {
                    data = new byte[len];
                    System.arraycopy(buf, 0, data, 0, len);
                } else {
                    data = buf;
                }
                
                dataCallback.onData(data);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void inputStreamToFile(InputStream ins, File dest) {
        try {
            final OutputStream outputs = new FileOutputStream(dest);
            readAllInputStream(ins, new ReadInputStreamDataCallback() {
                @Override
                public void onData(byte[] data) {
                    try {
                        outputs.write(data);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            outputs.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String getCurTimeStr() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
    }
    
    public static File findUniqueFileNameInDir(String dirPath, String fileName, String fileExt) {
        if(!dirPath.endsWith("/")) {
            dirPath += "/";
        }
        
        File newFile = new File(dirPath + fileName + fileExt);
        if(newFile.exists()) {
            int i = 1;
            while(true) {
                newFile = new File(dirPath + fileName + "(" + i + ")" + fileExt);
                if(!newFile.exists()) {
                    break;
                }
                i++;
            }
        }
        
        return newFile;
    }
    
    public static File findUniqueDirNameInDir(String parentDir, String dirName) {
        ULog.i(TAG, "findUniqueDirNameInDir " + parentDir + " " + dirName);
        if(!parentDir.endsWith("/")) {
            parentDir += "/";
        }
        
        String newDirPath = parentDir + dirName;
        File newDir = new File(newDirPath);
        if(newDir.exists()) {
            int i = 1;
            while(true) {
                newDir = new File(newDirPath + "(" + i + ")");
                if(!newDir.exists()) {
                    newDir.mkdirs();
                    break;
                }
                i++;
            }
        }
        
        return newDir;
    }
    
    public static String byteArrayToHexString(byte[] arrayIn) {
        if(arrayIn == null) {
            return null;
        }
        
        StringBuilder builder = new StringBuilder(arrayIn.length * 2);
        
        for(byte oneByte : arrayIn) {
            builder.append(String.format("%02X", oneByte));
        }
        
        return builder.toString();
    }
    
    public static String getFileHashString(String filePath, String algorithm) {
        return byteArrayToHexString(getFileHash(filePath, algorithm));
    }
    
    public static String getStringHash(String str, String algorithm) {
        return getByteArrayHash(str.getBytes(), algorithm);
    }
    
    public static byte[] getFileHash(String filePath, String algorithm) {
        FileInputStream fileInputStream = null;
        
        try {
            fileInputStream = new FileInputStream(filePath);
            MessageDigest digester = MessageDigest.getInstance(algorithm);
            byte[] bytes = new byte[8192];
            int byteCount;
            while((byteCount = fileInputStream.read(bytes)) > 0) {
                digester.update(bytes, 0, byteCount);
            }
            
            return digester.digest();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fileInputStream != null) fileInputStream.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    public static String getByteArrayHash(byte[] data, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(data);
            return byteArrayToHexString(messageDigest.digest());
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static class NetTrafficCalc {
        private AtomicLong netTrafficPerSec = new AtomicLong(0);
        private long lastTraffic = 0;
        private long refreshTime = 0;
        private Context context;
        
        private final static int AVERAGE_BUF_SIZE = 5;
        private int avrgBufIndex;
        private long[] averageBuf = new long[AVERAGE_BUF_SIZE];
        private long[] intervalBuf = new long[AVERAGE_BUF_SIZE];
        
        public NetTrafficCalc(Context ctx) {
            context = ctx;
        }
        
        public void refreshNetTraffic() {
            long curTraffic = getTotalNetTraffic();
            if(curTraffic != 0) {
                long curTime = SystemClock.uptimeMillis();
                if(lastTraffic != 0) {
                    averageBuf[avrgBufIndex] = (curTraffic - lastTraffic);
                    intervalBuf[avrgBufIndex] = (curTime - refreshTime);
                    
                    avrgBufIndex++;
                    avrgBufIndex %= AVERAGE_BUF_SIZE;
                    
                    long traffic = 0;
                    long time = 0;
                    for(int i = 0; i < AVERAGE_BUF_SIZE; i++) {
                        traffic += averageBuf[i];
                        time += intervalBuf[i];
                    }
                    
                    netTrafficPerSec.set(traffic * 1000 / time);
                }
                refreshTime = curTime;
                lastTraffic = curTraffic;
            }
        }
        
        private long getTotalNetTraffic() {
            try {
                ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                        context.getPackageName(), 0);
                return TrafficStats.getUidRxBytes(appInfo.uid) + TrafficStats.getUidTxBytes(appInfo.uid);
            } catch(PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            
            return 0;
        }
        
        public long getNetTrafficPerSecond() {
            return netTrafficPerSec.get();
        }
    }
    
    public static class SynchronizedCounter {
        private int value;
        
        public SynchronizedCounter(int startValue) {
            value = startValue;
        }
        
        public SynchronizedCounter() {
            value = 0;
        }
        
        public synchronized void setValue(int value) {
            this.value = value;
        }
        
        public synchronized boolean isDecreasedToZero() {
            if(value > 0) {
                value--;
                return value == 0;
            }
            
            return false;
        }
        
        public synchronized void downCount() {
            if(value > 0) {
                value--;
            }
        }
        
        public synchronized boolean isZero() {
            return value == 0;
        }
        
        public synchronized int getValue() {
            return value;
        }
    }
    
    public static class ReadWaitLock {
        private boolean isLocked;
        
        public synchronized void waitIfLocked() {
            while(isLocked) {
                try {
                    wait();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        public synchronized void lock() {
            while(isLocked) {
                try {
                    wait();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isLocked = true;
        }
        
        public synchronized boolean lockIfUnlocked() {
            if(!isLocked) {
                lock();
                return true;
            }
            
            return false;
        }
        
        public synchronized boolean unlockIfLocked() {
            if(isLocked) {
                unlock();
                return true;
            }
            
            return false;
        }
        
        public synchronized void lockWait() {
            isLocked = true;
            while(isLocked) {
                try {
                    wait();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        public synchronized void unlock() {
            isLocked = false;
            notifyAll();
        }
    }
    
    public static class CubicBezier {
        private static final String TAG = "CubicBezier";
        private static final int POINT_NUM = 128;
        
        private class Point {
            float x;
            float y;
            
            Point(float pointX, float pointY) {
                x = pointX;
                y = pointY;
            }
        }
        
        private Point pointA;
        private Point pointB;
        
        private Point pointsX2Y[];
        private Point pointsY2X[];
        
        public CubicBezier(float pointAx, float pointAy, float pointBx, float pointBy) {
            ULog.i(TAG, pointAx + " " + pointAy + " " + pointBx + " " + pointBy);
            
            pointAx = valueInRange(pointAx, 1, 0);
            pointBx = valueInRange(pointBx, 1, 0);
            
            pointA = new Point(pointAx, pointAy);
            pointB = new Point(pointBx, pointBy);
            
            pointsX2Y = new Point[POINT_NUM];
            pointsY2X = new Point[POINT_NUM];
            for(int i = 0; i < POINT_NUM; i++) {
                pointsX2Y[i] = getPoint((float) i / (POINT_NUM - 1));
                pointsY2X[i] = new Point(pointsX2Y[i].y, pointsX2Y[i].x);
            }
        }
        
        private Point getPoint(float t) {
            float tSquared, tCubed, oneDecT, oneDecTSquared;
            float px, py;
            
            tSquared = t * t;
            tCubed = tSquared * t;
            
            oneDecT = 1 - t;
            oneDecTSquared = oneDecT * oneDecT;
            
            px = 3 * pointA.x * t * oneDecTSquared + 3 * pointB.x * tSquared * oneDecT + tCubed;
            py = 3 * pointA.y * t * oneDecTSquared + 3 * pointB.y * tSquared * oneDecT + tCubed;
            
            //ULog.i(TAG, px+" "+py);
            
            return new Point(px, py);
        }
        
        private float getValue(boolean isGetY, float knownValue) {
            float x = knownValue;
            if(x <= 0) {
                return 0;
            }
            
            if(x >= 1) {
                return 1;
            }
            
            Point targetFunc[] = isGetY ? pointsX2Y : pointsY2X;
            
            int start = 0;
            int end = POINT_NUM - 1;
            
            while(end - start > 1) {
                //ULog.i(TAG, String.format("start:%d:%f end:%d:%f", start, points[start].x,
                //        end, points[end].x));
                int mid = start + (end - start) / 2;
                Point midPoint = targetFunc[mid];
                if(x == midPoint.x) {
                    return midPoint.y;
                } else if(x > midPoint.x) {
                    start = mid;
                } else {
                    end = mid;
                }
            }
            
            //ULog.i(TAG, "points[start].y:"+points[start].y);
            return targetFunc[start].y + (targetFunc[end].y - targetFunc[start].y) * (x - targetFunc[start].x) /
                    (targetFunc[end].x - targetFunc[start].x);
        }
        
        public float getY(float x) {
            //ULog.i(TAG, "getY x:"+x);
            return getValue(true, x);
        }
        
        public float getX(float y) {
            return getValue(false, y);
        }
        
        public static void test(float x1, float y1, float x2, float y2) {
            String str = "";
            Utils.CubicBezier cubicBezier = new Utils.CubicBezier(x1, y1, x2, y2);
            
            for(int i = 0; i < 100; i++) {
                str += String.format("%.4f,", cubicBezier.getY((float) i / 99));
            }
            ULog.i(TAG, str);
        }
    }
    
    private static final String[] SIZE_UNIT_NAME = new String[]{"TB", "GB", "MB", "KB"};
    private static final long[] SIZE_UNIT = new long[]{(long) 1 << 40, 1 << 30, 1 << 20, 1 << 10, 1};
    
    public static String byteSizeToString(long size) {
        for(int i = 0; i < SIZE_UNIT_NAME.length; i++) {
            if(size >= SIZE_UNIT[i]) {
                size /= SIZE_UNIT[i + 1];
                double sizeInFloat = (double) size / 1024;
                String sizeStr = String.format("%.2f", sizeInFloat);
                if(sizeStr.length() > 4) {
                    sizeStr = sizeStr.substring(0, sizeStr.length() - 1);
                }
                return sizeStr + SIZE_UNIT_NAME[i];
            }
        }
        
        return size + "B";
    }
    
    public static void saveStringToFile(String str, String filePath) {
        ULog.i(TAG, "saveStringToFile " + str + " " + filePath);
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            fileOut.write(str.getBytes());
            fileOut.flush();
            fileOut.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public static byte[] bitmapToJpgByteArray(Bitmap bitmap) {
        ByteArrayOutputStream jpgByteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, jpgByteStream);
        return jpgByteStream.toByteArray();
    }
    
    public static void saveBitmapToFile(Bitmap bmp, File file) {
        FileOutputStream fileOut;
        try {
            fileOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fileOut);
            fileOut.flush();
            fileOut.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveBitmapToFile(Bitmap bmp, String filePath) {
        saveBitmapToFile(bmp, new File(filePath));
    }
    
    public static Bitmap bitmapScale(Bitmap srcBmp, float maxSize) {
        float scale = maxSize / Math.max(srcBmp.getWidth(), srcBmp.getHeight());
        return Bitmap.createScaledBitmap(srcBmp, (int) (srcBmp.getWidth() * scale), (int) (srcBmp.getHeight() * scale), true);
    }
    
    public static String trimAll(String string) {
        return string.replaceAll("\\s", " ").trim();
    }
    
    public static byte[] readBytesFromStream(InputStream inputStream) {
        final ArrayList<Byte> bytesList = new ArrayList<>();
        try {
            readAllInputStream(inputStream, new ReadInputStreamDataCallback() {
                @Override
                public void onData(byte[] data) {
                    for(byte b : data) {
                        bytesList.add(b);
                    }
                }
            });
            inputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        byte[] bytes = new byte[bytesList.size()];
        for(int i = 0; i < bytes.length; i++) {
            bytes[i] = bytesList.get(i);
        }
        return bytes;
    }
    
    public static void readBytesFromStream(InputStream inputStream, byte[] bytes) {
        ULog.i(TAG, "readBytesFromStream %d", bytes.length);
        try {
            int bufPtr = 0;
            int len;
            while((len = inputStream.read(bytes, bufPtr, bytes.length - bufPtr)) >= 0 && bufPtr < len) {
                bufPtr += len;
            }
            inputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public static byte[] readBytesFromFile(File file) {
        ULog.i(TAG, "readBytesFromFile %s", file.getPath());
        if(file.isFile()) {
            try {
                byte[] bytes = new byte[(int) file.length()];
                readBytesFromStream(new FileInputStream(file), bytes);
                return bytes;
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    public static String readString(InputStream inputStream) {
        return new String(readBytesFromStream(inputStream));
    }
    
    public static String readStringFromFile(File file) {
        byte[] bytes = readBytesFromFile(file);
        if(bytes == null) {
            return null;
        }
        return new String(bytes);
    }
    
    public static String readStringFromFile(String filePath) {
        return readStringFromFile(new File(filePath));
    }
    
    public static String readStringFromHttp(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            if(conn.getResponseCode() == 200) {
                return readString(conn.getInputStream());
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
        
        return null;
    }
    
    
    static public String getFullUrl(String pageUrl, String url) {
        if(url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        
        if(url.startsWith("//")) {
            return (pageUrl.startsWith("http:") ? "http:" : "https:") + url;
        }
        
        String pageUrlPath;
        String pageUrlHost;
        int pathEnd = pageUrl.lastIndexOf("/");
        if(pathEnd > 7) {
            int queryIndex = pageUrl.indexOf("?");
            if(queryIndex > 7) {
                pageUrlPath = pageUrl.substring(0, queryIndex);
                int parent = pageUrlPath.lastIndexOf("/");
                if(pageUrlPath.indexOf(".", parent) >= 2) {
                    pageUrlPath = pageUrl.substring(0, parent);
                }
            } else {
                pageUrlPath = pageUrl.substring(0, pathEnd);
            }
            pageUrlHost = pageUrl.substring(0, pageUrl.indexOf("/", 8));
        } else {
            pageUrlPath = pageUrl;
            pageUrlHost = pageUrl;
        }

//        ULog.i(TAG, Log.getStackTraceString(new Throwable()));
//        ULog.i(TAG, "getFullUrl pageUrl %s url %s", pageUrl, url);
//        ULog.i(TAG, "getFullUrl pageUrlPath %s pageUrlHost %s", pageUrlPath, pageUrlHost);
        
        while(true) {
            if(url.startsWith("/")) {
                url = url.substring(1);
                pageUrlPath = pageUrlHost;
            } else if(url.startsWith("./")) {
                url = url.substring(2);
            } else if(url.startsWith("../")) {
                url = url.substring(3);
                int lastLevelPathPos = pageUrlPath.lastIndexOf("/");
                if(lastLevelPathPos > 7) {
                    pageUrlPath = pageUrlPath.substring(0, lastLevelPathPos);
                }
            } else {
//                ULog.i(TAG, "getFullUrl end %s", pageUrlPath + "/" + url);
                return pageUrlPath + "/" + url;
            }
        }
    }
    
    public static String getFinalLocation(String url) {
        return getFinalLocation(url, null);
    }
    
    public static String getFinalLocation(String url, String userAgent) {
        ULog.d(TAG, "getReDirUrl url %s", url);
        
        int REDIRECT_MAX = 5;
        for(int redirectCnt = 0; redirectCnt < REDIRECT_MAX; redirectCnt++) {
            HttpURLConnection conn = null;
            try {
                URL uUrl = new URL(url);
                conn = (HttpURLConnection) uUrl.openConnection();
                conn.setInstanceFollowRedirects(false);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                if(userAgent != null) {
                    conn.setRequestProperty("User-Agent", userAgent);
                }
                conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml");
                conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
                conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
                conn.setRequestProperty("Host", uUrl.getHost());
                int res = conn.getResponseCode();
                if((res / 100) == 3) {
                    String reDirUrl = conn.getHeaderField("Location");
                    if(reDirUrl != null) {
                        url = getFullUrl(url, reDirUrl.replaceAll(" ", "%20"));
                        continue;
                    }
                } else if(res == 200) {
                    url = conn.getURL().toString().replaceAll(" ", "%20");
                    String type=conn.getContentType();
                    if(type!=null && type.contains("image")){
                        url="image:"+url;
                    }
                }
                break;
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                if(conn != null) {
                    conn.disconnect();
                }
            }
        }
        
        ULog.d(TAG, "reDirUrl result %s", url);
        return url;
    }
    
    public static int byteArrayToInt(byte[] b) {
        return b[0] & 0xFF |
                (b[1] & 0xFF) << 8 |
                (b[2] & 0xFF) << 16 |
                (b[3] & 0xFF) << 24;
    }
    
    public static byte[] intToByteArray(int i) {
        return new byte[]{
                (byte) (i & 0xFF),
                (byte) ((i >> 8) & 0xFF),
                (byte) ((i >> 16) & 0xFF),
                (byte) ((i >> 24) & 0xFF)
        };
    }
    
    public static class ByteArrayBuf {
        public byte[] buf;
        public int len;
    }
    
    public static boolean urlsEqualWithoutProtocol(String srcUrl, String tarUrl) {
        if(srcUrl == null || tarUrl == null) {
            return false;
        }
        
        try {
            return srcUrl.substring(srcUrl.indexOf("://")).equals(tarUrl.substring(tarUrl.indexOf("://")));
        } catch(IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static int parseInt(String string) {
        int value = 0;
        
        try {
            value = Integer.parseInt(string);
        } catch(NumberFormatException e) {
            e.printStackTrace();
        }
        
        return value;
    }
    
    public static float valueInRange(float target, float max, float min) {
        if(target > max) {
            return max;
        } else if(target < min) {
            return min;
        }
        
        return target;
    }
    
    public static LinkedList<String> exeShell(String cmd) {
        LinkedList<String> ret = new LinkedList<String>();
        
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            String line;
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while((line = inputStream.readLine()) != null) {
                ret.add(line);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        
        return ret;
    }
    
    public static boolean mayBeUrl(String str) {
        int posOdDot = str.lastIndexOf(".");
        return posOdDot > 0 && posOdDot < (str.length() - 2);
    }
    
    public static class LogRecorder extends Thread {
        private AtomicBoolean isRunning = new AtomicBoolean(true);
        private File recordFile;
        
        public void stopThread() {
            isRunning.set(false);
        }
        
        public LogRecorder(String appName) {
            setDaemon(true);
            File logDir = StorageUtils.getDirInSto(appName + "/log");
            if(logDir != null) {
                recordFile = new File(logDir.getPath() + "/log.txt");
                start();
            }
        }
        
        public void run() {
            Process logcatProcess;
            
            try {
                Runtime.getRuntime().exec("logcat -c").waitFor();
                logcatProcess = Runtime.getRuntime().exec("logcat");
                
                BufferedReader logStream = null;
                FileOutputStream logFileOut = null;
                try {
                    logStream = new BufferedReader(
                            new InputStreamReader(logcatProcess.getInputStream()));
                    
                    logFileOut = new FileOutputStream(recordFile.getPath());
                    
                    String logLine;
                    
                    while((logLine = logStream.readLine()) != null) {
                        logLine += "\r\n";
                        logFileOut.write(logLine.getBytes());
                        
                        if(!isRunning.get()) {
                            break;
                        }
                        
                        yield();
                    }
                } finally {
                    if(logStream != null) {
                        logStream.close();
                    }
                    
                    if(logFileOut != null) {
                        logFileOut.flush();
                        logFileOut.close();
                    }
                }
            } catch(InterruptedException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static int getCpuCoresNum() {
        return new File("/sys/devices/system/cpu/").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String path = pathname.getName();
                //regex is slow, so checking char by char.
                if(path.startsWith("cpu")) {
                    for(int i = 3; i < path.length(); i++) {
                        if(path.charAt(i) < '0' || path.charAt(i) > '9') {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        }).length;
    }
    
    public static String getSDKVersion() {
        return android.os.Build.VERSION.RELEASE;
    }
    
    public static String getPhoneType() {
        return android.os.Build.MODEL;
    }
    
    public static boolean isNetworkEffective() {
        String stableWebUrl[] = {"https://www.baidu.com", "https://www.qq.com", "https://www.google.com"};
        
        for(String webUrl : stableWebUrl) {
            HttpURLConnection urlConn = null;
            try {
                URL url = new URL(webUrl);
                
                urlConn = (HttpURLConnection) url
                        .openConnection();
                urlConn.setConnectTimeout(30000);
                urlConn.setReadTimeout(10000);
                if(urlConn.getResponseCode() < 400) {
                    urlConn.disconnect();
                    ULog.i(TAG, "isNetworkEffective " + webUrl);
                    return true;
                }
            } catch(MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                if(urlConn != null)
                    urlConn.disconnect();
            }
        }
        return false;
    }
    
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
    
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
    
    public static boolean hasNavigationBar(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        }
        return false;
    }
    
    /**
     * dp、sp 转换为 px 的工具类
     */
    public static class DisplayUtil {
        /**
         * 将px值转换为dip或dp值，保证尺寸大小不变
         */
        public static int pxToDip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }
        
        /**
         * 将dip或dp值转换为px值，保证尺寸大小不变
         */
        public static int dipToPx(Context context, float dipValue) {
            return (int) (context.getResources().getDisplayMetrics().density * dipValue);
        }
        
        /**
         * 将px值转换为sp值，保证文字大小不变
         */
        public static int pxToSp(Context context, float pxValue) {
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (pxValue / fontScale + 0.5f);
        }
        
        /**
         * 将sp值转换为px值，保证文字大小不变
         */
        public static int spToPx(Context context, float spValue) {
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (spValue * fontScale + 0.5f);
        }
        
        public static int attrToPx(Context context, String attr) {
            int px = 0;
            try {
                int attrVal = Integer.parseInt(attr.substring(0,
                        attr.length() - 2));
                if(attr.endsWith("px")) {
                    px = attrVal;
                } else if(attr.endsWith("dp")) {
                    px = dipToPx(context, attrVal);
                } else if(attr.endsWith("sp")) {
                    px = spToPx(context, attrVal);
                }
            } catch(NumberFormatException e) {
                e.printStackTrace();
            }
            return px;
        }
    }
}
