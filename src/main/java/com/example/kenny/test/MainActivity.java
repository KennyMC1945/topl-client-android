package com.example.kenny.test;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.app.Notification;
import android.app.NotificationManager;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int NOTIFY_ID = 101;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Connect connect;
    // Catching gesture
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Акселлерометр
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this,senAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        // Кнопка
        final ToggleButton on = (ToggleButton) findViewById(R.id.toogleWork);
        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                String msg = "";
                if (on.isChecked()) {
                    msg = "Включено";
                    connect = new Connect();
                    connect.run();
                }
                else {
                    msg = "Выключено";
                    //connect.stop();
                }
                Toast toast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onSensorChanged(SensorEvent sensorEvent){
        Sensor mySensor = sensorEvent.sensor;

        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER ){
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100){
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/diffTime * 10000;
                ToggleButton toggle = (ToggleButton) findViewById(R.id.toogleWork);
                if (speed < SHAKE_THRESHOLD && speed > 900 && toggle.isChecked()) {
                    //Toast.makeText(getApplicationContext(),speed+"",Toast.LENGTH_SHORT).show();
                    try {
                        connect.send("Shaking");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"No connection",Toast.LENGTH_SHORT).show();
                    }
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){}
}
