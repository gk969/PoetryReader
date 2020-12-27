package com.gk969.PoetryReader;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class PoetrySetFragment extends Fragment {
    private final static String TAG = "PoetrySetFragment";
    
    private PoetryDb.PoetrySet poetrySet;
    private MainActivity activity;
    
    PoetrySetFragment(MainActivity activity, PoetryDb.PoetrySet poetrySet){
        this.activity=activity;
        this.poetrySet=poetrySet;
        activity.setTitle(poetrySet.name);
    }
    
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    
        activity.setTitle(getString(R.string.app_name));
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.poetry_list, container, false);
        ListView listPoetry=view.findViewById(R.id.listPoetry);
        listPoetry.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return poetrySet.poetryList.size();
            }
    
            @Override
            public Object getItem(int position) {
                return null;
            }
    
            @Override
            public long getItemId(int i) {
                return i;
            }
    
            @Override
            public View getView(int i, View view, ViewGroup parent) {
                if(view == null) {
                    view = getLayoutInflater().inflate(R.layout.poetry_list_item, null);
                }
        
                PoetryDb.Poetry poetry = poetrySet.poetryList.get(i);
                ((TextView) view.findViewById(R.id.textTitle)).setText(poetry.title);
                ((TextView) view.findViewById(R.id.textAuthor)).setText(poetry.author);
        
                return view;
            }
        });
        listPoetry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new PoetryFragment(activity, poetrySet.poetryList.get(position));
                getFragmentManager().beginTransaction()
                        .replace(R.id.layoutPoetry, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
            }
        });
    
        return view;
    }
}
