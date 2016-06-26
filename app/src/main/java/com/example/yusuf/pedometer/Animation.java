package com.example.yusuf.pedometer;

/** Custom View class - for Handling the animation of the circulating line shape. */

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class Animation extends View {

    Paint rectPaint;

    DisplayMetrics displayMetrics;
    int displayWidth;
    int displayHeight;

    float x;
    float y;
    int xVelocity;
    int yVelocity;
    float x2;
    float y2;

    Handler handler;

    int statusBarHeight;
    int navBarHeight;
    int actionBarHeight;


    public Animation(Context context) {
        super(context);
    }

    public Animation(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void init() {
        Log.v("init","running");

        getStatusBarHeight();
        getNavigationBarHeight();
        getAppBarHeight();

        handler = new Handler();

        rectPaint = new Paint();
        rectPaint.setColor(Color.parseColor("#116e8a"));
        rectPaint.setAlpha(128);
        rectPaint.setStrokeCap(Paint.Cap.ROUND);
        rectPaint.setAntiAlias(true);
        rectPaint.setShadowLayer(16, 8, 8, Color.MAGENTA);

        displayMetrics = getResources().getDisplayMetrics();

        // Manage configuration changes - specifically orientation (portrait & landscape).
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            displayWidth = displayMetrics.widthPixels;
            displayHeight = displayMetrics.heightPixels + navBarHeight; // The navigation bar is accounted for natively.
        }
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            displayWidth = displayMetrics.widthPixels + navBarHeight; // The navigation bar is accounted for natively.
            displayHeight = displayMetrics.heightPixels;
        }
        Log.d("Display Width",String.valueOf(displayWidth));
        Log.d("Display Height",String.valueOf(displayHeight));

        x = 0;
        y = 0;
        xVelocity = 5;
        yVelocity = 5;

    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(x, y, x + x2, y + y2, rectPaint);

        animation();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        }, 10);

    }

    public void animation() {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //if (y < 1536 && x == 0) {
            if (y < displayHeight-statusBarHeight-actionBarHeight-navBarHeight && x == 0) {
                y = y + yVelocity;
                x = 0;
                x2 = 5;
                y2 = 100;
            }
            //else if (y >= 1536 - 5 && x < 1080 - 5) { // The reason why I subtract by 5 is because the moving shape has a thickness pf 5px.
            else if (y >= displayHeight-statusBarHeight-actionBarHeight-navBarHeight - 5 && x < displayWidth - 5) { // The reason why I subtract by 5 is because the moving shape has a thickness pf 5px.
                //y = 1536 - 5;
                y = displayHeight-statusBarHeight-actionBarHeight-navBarHeight - 5;
                x = x + xVelocity;
                x2 = 100;
                y2 = 5;
            }
            //else if (y > 0 && x == 1080 - 5) {
            else if (y > 0 && x == displayWidth - 5) {
                y = y - yVelocity;
                //x = 1080 - 5;
                x = displayWidth - 5;
                x2 = 5;
                y2 = 100;
            } else if (y <= 0 && x > 0) {
                y = 0;
                x = x - xVelocity;
                x2 = 100;
                y2 = 5;
            }
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //if (y < 864 && x <= 0) {
            if (y < displayHeight-statusBarHeight-actionBarHeight && x <= 0) {
                y = y + yVelocity;
                x = 0;
                x2 = 5;
                y2 = 100;
            }
            //else if (y >= 864-10 && x <= 1776-5) {
            else if (y >= displayHeight-statusBarHeight-actionBarHeight -5 && x <= displayWidth-navBarHeight -5) {
                //y = 864-10;
                y = displayHeight-statusBarHeight-actionBarHeight -5;
                x = x + xVelocity;
                x2 = 100;
                y2 = 5;
            }
            //else if (y > 0 && x >= 1776-5) {
            else if (y > 0 && x >= displayWidth-navBarHeight -5) {
                y = y - yVelocity;
                //x = 1776-5;
                x = displayWidth-navBarHeight -5;
                x2 = 5;
                y2 = 100;
            } else if (y <= 0 && x > 0) {
                y = 0;
                x = x - xVelocity;
                x2 = 100;
                y2 = 5;
            }
        }

    }


    // Get the size of the status bar.
    private void getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height","dimen","android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            Log.d("Status Bar Height",String.valueOf(statusBarHeight));
        }
    }
    // Get the size of the Nav bar.
    private void getNavigationBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height","dimen","android");
        if (resourceId > 0) {
            navBarHeight = getResources().getDimensionPixelSize(resourceId);
            Log.d("Nav Bar Height", String.valueOf(navBarHeight));
        }
    }
    // Get the size of the App/Action bar.
    private void getAppBarHeight() {
        final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        actionBarHeight = (int)styledAttributes.getDimension(0,0);
        Log.d("App Bar Height",String.valueOf(actionBarHeight));
    }

}

/*
*** When dealing with display width and height, take UI region metrics and margins (such as navigation bar) into account.
* Those include navigation bar (bottom UI region), app/action/title bar (app title and menu UI region), status bar (top notification region).
*
* According to the design guideline, the metrics for these system and application bars are as following:
* Navigation bar = 48dp
* App bar = 56dp (portrait) or 48dp (landscape)
* Status bar = 24dp
*
* For example, for Nexus 5 (xxhdpi_3.0 scale factor):
* Navigation bar = 48dp [144px] (portrait) --- 42dp [126px] (landscape)
* App bar = 56dp [168px] (portrait) --- 48dp [144px] (landscape)
* Status bar = 24dp [72px] (portrait & landscape)
*
* When in portrait, you'll deal with:
* Status bar + App bar + Navigation bar. <-- Vertically
* No margin <- Horizontally [Meaning you have access to the full hotizontal pixels].
* When in landscape, you'll deal with:
* Status bar + App bar <-- Vertically
* Navigation bar <-- Horizontally
*
* * Nexus 5:
*   Portrait -> 1536px vertically by 1080px horizontally
*            |--> 1536 instead of 1920 due to Nav, app and status bars (1920-(144-168-72))
*   Landscape -> 864px vertically by 1776px horizontally
*            |--> 864 instead of 1080 due to status and app bars (1080-(72-144))
*            |--> 1776 instead of 1920 due to Nav bar (1920-(144))
*
 */
