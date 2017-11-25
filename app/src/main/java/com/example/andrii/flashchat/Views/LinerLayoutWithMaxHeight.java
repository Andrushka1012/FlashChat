package com.example.andrii.flashchat.Views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class LinerLayoutWithMaxHeight extends LinearLayout {

    private int maxHeight = 0;

    public LinerLayoutWithMaxHeight(Context context) {
        super(context);
    }

    public LinerLayoutWithMaxHeight(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinerLayoutWithMaxHeight(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LinerLayoutWithMaxHeight(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            if (maxHeight != 0 && heightSize > maxHeight) {
                heightSize = maxHeight;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
            getLayoutParams().height = heightSize;
        }catch (Exception e){
            Log.e("qwe","Error forcing height",e);
        }finally {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }
}
