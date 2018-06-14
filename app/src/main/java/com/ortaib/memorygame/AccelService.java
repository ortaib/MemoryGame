package com.ortaib.memorygame;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Binder;
import android.hardware.SensorEventListener;
import android.widget.Toast;

public class AccelService extends Service implements SensorEventListener{
    private final IBinder accelBinder= new LocalAccelBinder();
    private float x,y,z;
    private float sX,sY,sZ;
    private double startAngle;
    private double angle;
    private boolean isFirstSet=false;

    private final int ALLOWEDOFFSET=5;
    public AccelService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //registering Sensor
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        return accelBinder;
    }

    public boolean isOnPosition()
    {
        if(Math.abs(startAngle-angle)<ALLOWEDOFFSET)
        {
            //Toast.makeText(this, "on pos", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(!isFirstSet){
            sX=sensorEvent.values[0];
            sY=sensorEvent.values[1];
            //sZ=sensorEvent.values[2];
            startAngle = Math.atan2(sX, sY)/(Math.PI/180);
            isFirstSet=true;
            Toast.makeText(this, "Service start angle "+startAngle, Toast.LENGTH_LONG).show();
        }
        else
            {
            x=sensorEvent.values[0];
            y=sensorEvent.values[1];
            angle = Math.atan2(x, y)/(Math.PI/180);
            //z=sensorEvent.values[2];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

      private SensorManager mSensorManager;

      @Override
      public void onCreate() {
          mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
      }
    public class LocalAccelBinder extends Binder{
        AccelService getService(){
            return AccelService.this;
        }
    }
}

