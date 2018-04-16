package com.ortaib.memorygame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;

import java.util.Calendar;

public class homePageActivity extends AppCompatActivity {
    private Bundle extras;
    private int year,month,day,age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Intent intent = getIntent();
        TextView view = (TextView)findViewById(R.id.name_age);
        extras = intent.getExtras();
        year=extras.getInt("year");
        month=extras.getInt("month");
        day=extras.getInt("day");
        age = getAge(year,month,day);
        if(isBirthday(month,day)){
            TextView birthday = (TextView)findViewById(R.id.happy_birthday);
            birthday.setVisibility(View.VISIBLE);

        }
        view.setText(extras.get("name")+","+age);
    }
    public void startMediumGame(View view) {
        Intent intent = new Intent(this,GameActivity.class);
        intent.putExtra("time",""+45);
        intent.putExtra("name",extras.get("name").toString());
        intent.putExtra("rows",""+4);
        intent.putExtra("cols",""+4);
        intent.putExtra("year",this.year);
        intent.putExtra("month",this.month);
        intent.putExtra("day",this.day);        startActivity(intent);
    }
    public void startEasyGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("time", "" + 30);
        intent.putExtra("name", extras.get("name").toString());
        intent.putExtra("rows", "" + 3);
        intent.putExtra("cols", "" + 3);
        intent.putExtra("year",this.year);
        intent.putExtra("month",this.month);
        intent.putExtra("day",this.day);

        startActivity(intent);
    }
    public void startHardGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("time", "" + 60);
        intent.putExtra("name", extras.get("name").toString());
        intent.putExtra("rows", "" + 5);
        intent.putExtra("cols", "" + 5);
        intent.putExtra("year",this.year);
        intent.putExtra("month",this.month);
        intent.putExtra("day",this.day);
        startActivity(intent);
    }
    public int getAge(int year,int month,int day){
        Calendar cal = Calendar.getInstance();
        int age = cal.get(Calendar.YEAR)-year-1;
        if( (cal.get(Calendar.MONTH) - month) >0) {
           age++;
        }
        else if(cal.get(Calendar.MONTH)==month && cal.get(Calendar.DAY_OF_MONTH)>=day)
            age++;
        return age;
    }
    public boolean isBirthday(int month,int day){
        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.MONTH)==month){
            if(cal.get(Calendar.DAY_OF_MONTH)==day)
                return true;
        }
        return false;
    }

}
