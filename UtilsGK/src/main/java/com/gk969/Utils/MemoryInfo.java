/*
 * Copyright (c) 2012-2013 NetEase, Inc. and other contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.gk969.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MemoryInfo {
    
    private static final String TAG = MemoryInfo.class.getSimpleName();
    
    public static int getTotalMemInMb() {
        String memInfoPath = "/proc/meminfo";
        String readTemp = "";
        String memTotal = "";
        long memory = 0;
        try {
            FileReader fr = new FileReader(memInfoPath);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while((readTemp = localBufferedReader.readLine()) != null) {
                if(readTemp.contains("MemTotal")) {
                    String[] total = readTemp.split(":");
                    memTotal = total[1].trim();
                }
            }
            localBufferedReader.close();
            String[] memKb = memTotal.split(" ");
            memTotal = memKb[0].trim();
            //Log.d(TAG, "memTotal: " + memTotal);
            memory = Long.parseLong(memTotal);
        } catch(IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
        return (int) (memory >> 10);
    }
    
    public static long getFreeMemInMb(Context context) {
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.getMemoryInfo(outInfo);
        long availMem = outInfo.availMem;
        return availMem >> 20;
    }
    
    public static int getPidMemorySize(Context context) {
        return getPidMemoryInfo(context).getTotalPss();
    }
    
    public static Debug.MemoryInfo getPidMemoryInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int[] memPid = new int[]{android.os.Process.myPid()};
        Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(memPid);
        return memoryInfo[0];
    }
}
