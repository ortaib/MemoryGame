package com.ortaib.memorygame;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView date;
    private TextView name;
    private DatePickerDialog.OnDateSetListener date_listener;
    private Calendar cal;
    private boolean dateIsSet=false;
    private int year,month,day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (TextView)findViewById(R.id.name);
        date = (TextView)findViewById(R.id.dateofbirth);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog,date_listener,year,month,day);
                dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dpd.show();
            }
        });
        date_listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i1, int i2, int i3) {
                int tempMonth=i2+1;
                date.setText(i3 + "/" +tempMonth+"/"+i1);
                year=i1;
                month=i2;
                day=i3;
                dateIsSet=true;
            }
        };

    }

    public void SendMessage(View view) {
        if(dateIsSet==true && name.length() != 0 ){
            Intent intent = new Intent(this,homePageActivity.class);
            intent.putExtra("name",name.getText().toString());
            intent.putExtra("year",this.year);
            intent.putExtra("month",this.month);
            intent.putExtra("day",this.day);
            startActivity(intent);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            if(dateIsSet == false) {
                builder.setTitle("Date not set");
                builder.setMessage("Date wasn't selected");
            }
            else{
                builder.setTitle("name not set");
                builder.setMessage("name wasn't selected");
            }
             builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
                 builder.setIcon(android.R.drawable.ic_dialog_alert);
                 builder.show();

        }
    }
}
