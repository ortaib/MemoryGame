package com.ortaib.memorygame;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import java.util.Random;
import java.util.Stack;


public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    final private int EASY = 110;
    final private int MEDIUM = 100;
    final private int HARD = 80;
    final private int MAX_NUM_OF_ELEMENTS = 13;
    int year, month, day;
    DatabaseHelper myDatabaseHelper;
    final private String TAG = "GameActivity";

    private final static int LOCATION_PERMISSION_REQUEST_CODE = 1234,ERROR_DIALOG_REQUEST = 9001;
    private double latitude,longitude;
    private boolean locationPermissionGranted=false;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private CountDownTimer timer;
    private TextView timerText, name, gameResult, scoreTextView;
    private Button gameResButton,scorebtn;
    private int timeleft, numOfElements,score=0, dp;
    private String user_age, user_name;
    private Bundle extra;
    private Context context=this;
    private MemoryCard[] cards;
    private int[] memoryCardGraphicsLocations;
    private int[] memoryCardGraphics;
    private GridLayout grid;
    private MemoryCard card1, card2;
    private boolean isBusy, isTimesUp = false;

    private AccelService aService;
    boolean isBound=false;

    private Stack<PairCard> stCards=new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Binding Service via localbinder
        Intent aI=new Intent(this,AccelService.class);
        bindService(aI,aConnection,Context.BIND_AUTO_CREATE);

        Intent intent = getIntent();
        extra = intent.getExtras();
        name = findViewById(R.id.name);
        user_name = extra.get("name").toString();
        name.setText(user_name);
        myDatabaseHelper = new DatabaseHelper(this);
        gameResButton = (Button) findViewById(R.id.res_btn);
        gameResult = (TextView) findViewById(R.id.res_text);
        scoreTextView = (TextView) findViewById(R.id.score);
        int numRows = Integer.parseInt(extra.get("rows").toString());
        int numColumn = Integer.parseInt(extra.get("cols").toString());
        scorebtn = (Button)findViewById(R.id.scoreboard);
        year = extra.getInt("year");
        month = extra.getInt("month");
        day = extra.getInt("day");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {
            }
            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        getLocationPermission();
        getDeviceLocation();
        grid = (GridLayout) findViewById(R.id.board);
        grid.setColumnCount(numColumn);
        grid.setRowCount(numRows);
        numOfElements = numColumn * numRows;
        setDp(numColumn);
        cards = new MemoryCard[numOfElements];
        memoryCardGraphics = new int[MAX_NUM_OF_ELEMENTS];
        initMemoryCardGraphics();
        memoryCardGraphicsLocations = new int[numOfElements];
        shuffleMemoryCards();

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numColumn; c++) {
                MemoryCard tempCard = new MemoryCard(this, r, c, memoryCardGraphics[memoryCardGraphicsLocations[r * numColumn + c]], dp);
                tempCard.setId(View.generateViewId());
                tempCard.setOnClickListener(this);
                cards[r * numColumn + c] = tempCard;
                grid.addView(tempCard);
            }
        }
        initTimer();
    }

    protected void initMemoryCardGraphics() {
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

    protected void setDp(int cols) {
        if (cols == 4) {
            dp = MEDIUM;
            return;
        }
        if (cols == 3) {
            dp = EASY;
            return;
        }
        if (cols == 5) {
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
                timeleft--;
                timerText.setText("" + (int) l / 1000);
                if(isBound&&l/1000%3==0)
                {
                    if(!aService.isOnPosition())
                    {
                        Toast.makeText(context,"Please return your phone angle to start position",Toast.LENGTH_SHORT).show();
                        unFlip();
                    }
                }


            }

            @Override
            public void onFinish() {
                isTimesUp = true;
                //getview by id linear layout

                final Rect viewRect = new Rect(400,600,200,300);
                                Explode explode = new Explode();
                                explode.setEpicenterCallback(new Transition.EpicenterCallback() {
                    @Override
                   public Rect onGetEpicenter(Transition transition) {
                                                return viewRect;
                                           }
                });
                                explode.setDuration(5000);
                                LinearLayout l=(LinearLayout) findViewById(R.id.rootLayout);
                                TransitionManager.go(Scene.getSceneForLayout(grid,R.layout.win_game,context),explode);
                                gameResult = (TextView)findViewById(R.id.res_text);

                gameResult.setText("Time's up! Game Over!");
               /* gameResButton.setText("Finish");
                gameResButton.setVisibility(View.VISIBLE);
                scorebtn.setText("Score Board");*/
            }
        };
        timer.start();
    }

    protected void shuffleMemoryCards() {
        Random rand = new Random();

        for (int i = 0; i < numOfElements; i++)
            memoryCardGraphicsLocations[i] = i % (numOfElements / 2);

        for (int i = 0; i < numOfElements; i++) {
            int temp = memoryCardGraphicsLocations[i];
            int swapLocation = rand.nextInt(numOfElements
            );
            memoryCardGraphicsLocations[i] = memoryCardGraphicsLocations[swapLocation];
            memoryCardGraphicsLocations[swapLocation] = temp;
        }
    }

    @Override
    public void onClick(View view) {
        if (!isTimesUp) {
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
                stCards.push(new PairCard(card1,card));

                card1.setMatched(true);
                card.setMatched(true);

                card1.setEnabled(false);
                card.setEnabled(false);
                card1 = null;
                score += 1;
                if (score >= numOfElements / 2) {

                    Slide slide=new Slide(Gravity.RIGHT);
                    slide.setDuration(3000);
                    /*Fade fade=new Fade();
                    fade.setDuration(3000);*/
                    LinearLayout l=(LinearLayout) findViewById(R.id.rootLayout);

                    TransitionManager.go(Scene.getSceneForLayout(grid,R.layout.win_game,this), slide);
                    gameResButton=(Button)findViewById(R.id.res_btn);
                    gameResult = (TextView)findViewById(R.id.res_text);
                    gameResult.setText("Congratulations! You won!");
                    timer.cancel();
                    isBusy=true;
                    finalScore();
                }
                scoreTextView.setText("" + score);
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
        if(isBusy==false) {
            Intent intent = new Intent(this, homePageActivity.class);
            intent.putExtra("name", user_name);
            intent.putExtra("year", this.year);
            intent.putExtra("month", this.month);
            intent.putExtra("day", this.day);
            unbindService(aConnection);
            startActivity(intent);
        }

    }

    public void finalScore() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeleft--;
                score++;
                timerText.setText("" + timeleft);
                scoreTextView.setText("" + score);
                if (timeleft > 0) {
                    finalScore();
                }
                else{
                    isBusy=false;
                    addData(user_name,score,latitude,longitude);

                }
            }
        }, 100);


    }
    public void addData(String name,int score, double latitude,double longitude){
        boolean insertData =  myDatabaseHelper.addData(name,score,latitude,longitude);
        if(insertData){
            toastMessage("Data successfully inserted, Score "+score);
        }else{
            toastMessage("Something went wrong");
        }
    }
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device current location");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationPermissionGranted){
            try{
                @SuppressLint("MissingPermission") Location currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                latitude = currentLocation.getLatitude();
                longitude = currentLocation.getLongitude();
            }catch(SecurityException e){
                Log.e(TAG, "getDeviceLocation: SecurityException: "+e.getMessage());
            }
        }
    }
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: requesting permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: called");

        locationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission granted ");
                    locationPermissionGranted = true;
                }
        }
    }
    public void moveToScoreboard(View view){
        Intent intent = new Intent(this,ScoreBoard.class);
        intent.putExtra("latitude",latitude);
        intent.putExtra("longitude",longitude);
        startActivity(intent);
    }
    private ServiceConnection aConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AccelService.LocalAccelBinder binder=(AccelService.LocalAccelBinder) iBinder;
            aService = binder.getService();
            isBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound=false;
        }
    };
    private class PairCard{
        private MemoryCard c1,c2;
        PairCard(MemoryCard card1,MemoryCard card2){
            c1=card1;
            c2=card2;
        }

        public MemoryCard getC1() {
            return c1;
        }

        public MemoryCard getC2() {
            return c2;
        }
    }
    private void unFlip(){
        if(!stCards.empty())
        {
           stCards.peek().getC1().setMatched(false);
           stCards.peek().getC2().setMatched(false);
           stCards.peek().getC1().setEnabled(true);
           stCards.peek().getC2().setEnabled(true);
           stCards.peek().getC1().flip();
           stCards.peek().getC2().flip();
           stCards.pop();
           score--;
           scoreTextView.setText("" + score);
        }
    }
}


