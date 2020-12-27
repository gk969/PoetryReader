package com.gk969.UtilsViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by SongJian on 2017/7/30.
 */

public class NoScrollListView extends ListView{
    
    public NoScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public NoScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
