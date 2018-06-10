package com.ortaib.memorygame;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ScoreBoard extends AppCompatActivity {
    private String name;
    private int score;
    final String TAG = "ListDataActivity";
    private TextView yourscore;
    private DatabaseHelper myDatabasehelper;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        Bundle extras = getIntent().getExtras();
        yourscore = (TextView)findViewById(R.id.yourscore);
        name = extras.getString("name","undefined");
        score = extras.getInt("score",0);
        yourscore.setText("Hello "+name+", Your score is : "+score);
        listView = (ListView)findViewById(R.id.listview);
        myDatabasehelper = new DatabaseHelper(this);

        populateListView();
    }
    public void populateListView(){
        Cursor data = myDatabasehelper.getData();
        ArrayList<String> listData = new ArrayList<>();


        while(data.moveToNext()){
            listData.add("#"+data.getInt(0)+" : " + data.getString(1)+"    ( "+data.getDouble(2)+
                    " , "+data.getDouble(3)+" ).");
        }
        ListAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listData);
        listView.setAdapter(adapter);
    }
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
    public void openMap(View view){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("name",name);
        startActivity(intent);
    }
    public void backToHomePage(View view) {
        Intent intent = new Intent(this, homePageActivity.class);
        intent.putExtra("name",name);
        startActivity(intent);
    }
}
