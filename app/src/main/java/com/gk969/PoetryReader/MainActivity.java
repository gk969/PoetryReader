package com.gk969.PoetryReader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gk969.Utils.ULog;
import com.gk969.Utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private Handler handler=new Handler();
    
    private BaseAdapter poetrySetAdapter;
    
    PoetryDb poetryDb=new PoetryDb(this);
    private TextView textTitle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        init();
    }
    
    private static final int PERMISSION_REQUEST_CODE = 0;
    private void checkPermission() {
        Log.i(TAG, "checkPermission");
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            };
            ArrayList<String> requestPermissionList = new ArrayList();
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionList.add(permission);
                    ULog.i(TAG, "%s NOT GRANTED", permission);
                }
            }
            if (!requestPermissionList.isEmpty()) {
                String[] requestPermissions = requestPermissionList.toArray(new String[requestPermissionList.size()]);
                requestPermissions(requestPermissions, PERMISSION_REQUEST_CODE);
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!Utils.isArrayEquals(grantResults, PackageManager.PERMISSION_GRANTED)) {
                ULog.i(TAG, "PERMISSION NOT GRANTED finish");
                finish();
            }
        }
    }
    
    private void init(){
        textTitle=findViewById(R.id.textTitle);
        setTitle(getString(R.string.app_name));
        
        findViewById(R.id.imageBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        poetrySetAdapter=new BaseAdapter() {
            @Override
            public int getCount() {
                return poetryDb.poetrySetList.size();
            }
    
            @Override
            public Object getItem(int i) {
                return null;
            }
    
            @Override
            public long getItemId(int i) {
                return i;
            }
    
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if(view == null) {
                    view = getLayoutInflater().inflate(R.layout.poetry_set_list_item, null);
                }
                
                ((TextView)view.findViewById(R.id.listText)).setText(poetryDb.poetrySetList.get(i).name);
                
                return view;
            }
        };
        ListView listPoetrySet=findViewById(R.id.listPoetrySet);
        listPoetrySet.setAdapter(poetrySetAdapter);
        listPoetrySet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment fragment = new PoetrySetFragment(MainActivity.this, poetryDb.poetrySetList.get(i));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layoutList, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
            }
        });
        
        executeAsyncTask(new Runnable() {
            @Override
            public void run() {
                poetryDb.init();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        poetrySetAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
    
    void setTitle(String title){
        textTitle.setText(title);
    }
    
    public void executeAsyncTask(Runnable runnable) {
        executor.execute(runnable);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    
        executor.shutdown();
        handler.removeMessages(0);
        
    }
}