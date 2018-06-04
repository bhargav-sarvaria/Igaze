package com.igaze.Utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by ascent-3 on 17/5/18.
 */
public class CustomBoldTextView extends android.support.v7.widget.AppCompatTextView{


    public CustomBoldTextView(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "NunitoSans_Bold.ttf");
        this.setTypeface(face);
    }

    public CustomBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "NunitoSans_Bold.ttf");
        this.setTypeface(face);
    }

    public CustomBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "NunitoSans_Bold.ttf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }
}