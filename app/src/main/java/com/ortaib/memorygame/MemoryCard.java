package com.ortaib.memorygame;
import android.annotation.SuppressLint;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatDrawableManager;
import android.content.Context;
import android.widget.Button;
import android.graphics.drawable.Drawable;
import android.widget.GridLayout;

import com.ortaib.memorygame.R;

/**
 * Created by Ortaib on 01/04/2018.
 */

@SuppressLint("AppCompatCustomView")
public class MemoryCard extends Button{
    protected int row,col,frontDrawableId;
    protected Drawable front,back;
    protected boolean isFliped=false,isMatched=false;
    @SuppressLint("RestrictedApi")
    public MemoryCard(Context context, int r, int c, int frontId,int dp) {
        super(context);
        this.row=r;
        this.col=c;
        frontDrawableId=frontId;
        front = AppCompatDrawableManager.get().getDrawable(context,frontDrawableId);
        back = AppCompatDrawableManager.get().getDrawable(context,R.drawable.back);
        setBackground(back);
        GridLayout.LayoutParams tempParams = new GridLayout.LayoutParams(GridLayout.spec(row),GridLayout.spec(col));
        tempParams.width = (int) getResources().getDisplayMetrics().density *dp;
        tempParams.height = (int) getResources().getDisplayMetrics().density *dp;
        setLayoutParams(tempParams);
    }
    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    public int getFrontDrawableId() {
        return frontDrawableId;
    }
    public void flip(){
        if(isMatched)
            return;
        if(isFliped)
        {
            setBackground(back);
            isFliped=false;
        }
        else{
            setBackground(front);
            isFliped=true;
        }
    }
}
