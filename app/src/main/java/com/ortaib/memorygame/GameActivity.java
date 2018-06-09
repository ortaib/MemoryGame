package com.ortaib.memorygame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;

import java.util.Random;


public class GameActivity extends AppCompatActivity implements View.OnClickListener{
    final private int EASY=110;
    final private int MEDIUM=100;
    final private int HARD=80;
    final private int MAX_NUM_OF_ELEMENTS=13;
    int year,month,day;
    private CountDownTimer timer;
    private TextView timerText,name,gameResult;
    private Button gameResButton;
    private int timeleft,numOfElements,score=0,dp;
    private String user_age,user_name;
    private Bundle extra;
    private Context c=this;
    private MemoryCard[] cards;
    private int[] memoryCardGraphicsLocations;
    private int[] memoryCardGraphics;

    private MemoryCard card1,card2;
    private boolean isBusy,isTimesUp=false;
    private GridLayout grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        extra = intent.getExtras();
        name = findViewById(R.id.name);
        user_name = extra.get("name").toString();
        name.setText(user_name);
        gameResButton=(Button)findViewById(R.id.res_btn_loss);
        gameResult = (TextView)findViewById(R.id.res_text_loss);
        int numRows = Integer.parseInt(extra.get("rows").toString());
        int numColumn = Integer.parseInt(extra.get("cols").toString());
        year=extra.getInt("year");
        month=extra.getInt("month");
        day=extra.getInt("day");
        grid =(GridLayout) findViewById(R.id.board);
        grid.setColumnCount(numColumn);
        grid.setRowCount(numRows);
        numOfElements =  numColumn*numRows;
        setDp(numColumn);
        cards = new MemoryCard[numOfElements];
        memoryCardGraphics = new int[MAX_NUM_OF_ELEMENTS];
        initMemoryCardGraphics();
        memoryCardGraphicsLocations = new int[numOfElements];
        shuffleMemoryCards();

        for(int r=0;r<numRows;r++){
            for(int c=0;c<numColumn;c++){
                MemoryCard tempCard = new MemoryCard(this,r,c,memoryCardGraphics[memoryCardGraphicsLocations[r*numColumn +c]],dp);
                tempCard.setId(View.generateViewId());
                tempCard.setOnClickListener(this);
                cards[r*numColumn+c]=tempCard;
                grid.addView(tempCard);
            }
        }
        initTimer();
    }
    protected void initMemoryCardGraphics(){
        memoryCardGraphics[0] = R.drawable.button_1;
        memoryCardGraphics[1] = R.drawable.button_2;
        memoryCardGraphics[2] = R.drawable.button_3;
        memoryCardGraphics[3] = R.drawable.button_4;
        memoryCardGraphics[4] = R.drawable.button_5;
        memoryCardGraphics[5] = R.drawable.button_6;
        memoryCardGraphics[6] = R.drawable.button_7;
        memoryCardGraphics[7] = R.drawable.button_8;
        memoryCardGraphics[8] = R.drawable.button_9;
        memoryCardGraphics[9] = R.drawable.button_10;
    }
    protected void setDp(int cols){
        if(cols==4) {
            dp = MEDIUM;
            return;
        }
        if(cols==3) {
            dp = EASY;
            return;
        }
        if(cols==5) {
            dp = HARD;
            return;
        }
    }
    protected void initTimer() {
        timeleft = Integer.valueOf(extra.getString("time"));
        timerText = findViewById(R.id.timer);
        timerText.setText("" + timeleft);
        timer = new CountDownTimer(timeleft * 1000, 1000) {
            @Override
            public void onTick(long l) {
                timerText.setText("" + (int) l / 1000);
            }
            @Override
            public void onFinish() {
                isTimesUp=true;
                final Rect viewRect = new Rect();
                Explode explode = new Explode();
                explode.setEpicenterCallback(new Transition.EpicenterCallback() {
                    @Override
                    public Rect onGetEpicenter(Transition transition) {
                        return viewRect;
                    }
                });
                explode.setDuration(5000);
                LinearLayout l=(LinearLayout) findViewById(R.id.rootLayout);
                TransitionManager.go(Scene.getSceneForLayout(grid,R.layout.win_game,c),explode);
                gameResButton=(Button)findViewById(R.id.res_btn);
                gameResult = (TextView)findViewById(R.id.res_text);
                gameResult.setText("Time's up! Game Over!");
                gameResButton.setText("Finish");
                gameResButton.setVisibility(View.VISIBLE);
            }
        };
        timer.start();
    }
    protected void shuffleMemoryCards(){
        Random rand = new Random();

        for(int i=0;i<numOfElements;i++)
            memoryCardGraphicsLocations[i] = i%(numOfElements/2);

        for(int i=0;i<numOfElements;i++){
            int temp = memoryCardGraphicsLocations[i];
            int swapLocation = rand.nextInt(numOfElements
            );
            memoryCardGraphicsLocations[i] = memoryCardGraphicsLocations[swapLocation];
            memoryCardGraphicsLocations[swapLocation]=temp;
        }
    }
    @Override
    public void onClick(View view) {
        final Rect viewRect = new Rect();
        view.getGlobalVisibleRect(viewRect);
        if(!isTimesUp) {
            if (isBusy)
                return;
            MemoryCard card = (MemoryCard) view;

            if (card.isMatched())
                return;

            if (card1 == null) {
                card1 = card;
                card1.flip();
                return;
            }
            if (card1.getId() == card.getId())
                return;

            if (card1.getFrontDrawableId() == card.getFrontDrawableId()) {
                card.flip();

                card1.setMatched(true);
                card.setMatched(true);

                card1.setEnabled(false);
                card.setEnabled(false);
                card1 = null;
                score += 1;
                if (score >= numOfElements / 2||true) {


                    Slide slide=new Slide(Gravity.RIGHT);
                    slide.setDuration(3000);

                    /*Fade fade=new Fade();
                    fade.setDuration(3000);*/
                    LinearLayout l=(LinearLayout) findViewById(R.id.rootLayout);

                    //setContentView(R.layout.win_game);
                    //grid.setVisibility(View.INVISIBLE);
                    //grid.removeAllViews();
                    TransitionManager.go(Scene.getSceneForLayout(grid,R.layout.win_game,this), slide);
                    gameResButton=(Button)findViewById(R.id.res_btn);
                    gameResult = (TextView)findViewById(R.id.res_text);
                    gameResult.setText("Congratulations! You won!");
                    gameResButton.setText("Finish");
                    gameResButton.setVisibility(View.VISIBLE);


                    timer.cancel();

                }
                return;
            } else {
                card2 = card;
                card2.flip();

                isBusy = true;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        card1.flip();
                        card2.flip();
                        card1 = null;
                        card2 = null;
                        isBusy = false;
                    }
                }, 1000);
            }
        }
    }
    public void backToHomePage(View view) {
        Intent intent = new Intent(this,homePageActivity.class);
        intent.putExtra("name",user_name);
        intent.putExtra("year",this.year);
        intent.putExtra("month",this.month);
        intent.putExtra("day",this.day);
        startActivity(intent);

    }
}


