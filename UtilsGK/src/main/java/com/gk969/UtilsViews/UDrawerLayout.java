package com.gk969.UtilsViews;

import android.content.Context;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by songjian on 2016/11/13.
 */

public class UDrawerLayout extends DrawerLayout {
    private static final String TAG = "MyDrawerLayout";

    public UDrawerLayout(Context context) {
        this(context, null);
    }

    public UDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.i(TAG, "MyDrawerLayout");
    }

    public boolean hasVisibleDrawer() {
        return findVisibleDrawer() != null;
    }

    boolean isDrawerView(View child) {
        final int gravity = ((LayoutParams) child.getLayoutParams()).gravity;
        final int absGravity = GravityCompat.getAbsoluteGravity(gravity,
                ViewCompat.getLayoutDirection(child));
        if((absGravity & Gravity.LEFT) != 0) {
            // This child is a left-edge drawer
            return true;
        }
        if((absGravity & Gravity.RIGHT) != 0) {
            // This child is a right-edge drawer
            return true;
        }
        return false;
    }

    private View findVisibleDrawer() {
        final int childCount = getChildCount();
        for(int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if(isDrawerView(child) && isDrawerVisible(child)) {
                return child;
            }
        }
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown " + keyCode);
        if(hasVisibleDrawer()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyUp " + keyCode);
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            final View visibleDrawer = findVisibleDrawer();
            if(visibleDrawer != null && getDrawerLockMode(visibleDrawer) == LOCK_MODE_UNLOCKED) {
                closeDrawers();
            }
            return visibleDrawer != null;
        }
        return super.onKeyUp(keyCode, event);
    }

}
