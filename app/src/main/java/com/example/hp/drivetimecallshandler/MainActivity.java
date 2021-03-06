package com.example.hp.drivetimecallshandler;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static List<Caller> callersFromContactList=new ArrayList<Caller>();
    static List<Caller> callersNotFromContactList=new ArrayList<Caller>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] perms = {Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.MODIFY_PHONE_STATE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};

        int permsRequestCode = 200;

        for (int i = 0; i < perms.length; i++) {
            if (ContextCompat.checkSelfPermission(this, perms[i]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, perms, permsRequestCode);
                break;
            }
        }

        SharedPreferences getCurrentMode = getApplicationContext().getSharedPreferences("currentState", MODE_PRIVATE);
        final SharedPreferences.Editor editor = getCurrentMode.edit();

        final String checkFirstRunKey = "isFirstRun";
        final String isCurrentDrivingKey = "isCurrentDriving";
        if (getCurrentMode.getBoolean(checkFirstRunKey, true)) {
            //Toast.makeText(getApplicationContext(),"Welcome for the first time!",Toast.LENGTH_SHORT).show();
            editor.putBoolean(isCurrentDrivingKey, false).commit();
            editor.putBoolean(checkFirstRunKey, false).commit();
        }

        final Button startDrive = (Button) findViewById(R.id.drivingStart);
        final Button endDrive = (Button) findViewById(R.id.drivingOver);
        final Button goToSettings = (Button) findViewById(R.id.goToSettings);
        final TextView someText = (TextView) findViewById(R.id.someText);

        if (getCurrentMode.getBoolean(isCurrentDrivingKey, Boolean.parseBoolean(null))) {
            startDrive.setVisibility(View.INVISIBLE);
            endDrive.setVisibility(View.VISIBLE);
            goToSettings.setVisibility(View.INVISIBLE);
            someText.setText(R.string.msg2);
        } else {
            endDrive.setVisibility(View.INVISIBLE);
            goToSettings.setVisibility(View.VISIBLE);
            startDrive.setVisibility(View.VISIBLE);
            someText.setText(R.string.msg1);
        }

        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        goToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent headSettings=new Intent(getApplicationContext(),Settings.class);
                startActivity(headSettings);
            }
        });

        startDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Database Creation
                final SQLiteDatabase sqlDB=openOrCreateDatabase("db123#5",MODE_PRIVATE,null);
                sqlDB.execSQL("CREATE TABLE IF NOT EXISTS Callers(name VARCHAR,number VARCHAR,timeCalled VARCHAR)");
                sqlDB.execSQL("DELETE FROM Callers");

                startDrive.setVisibility(View.INVISIBLE);
                endDrive.setVisibility(View.VISIBLE);
                someText.setText(R.string.msg2);
                goToSettings.setVisibility(View.INVISIBLE);
                editor.putBoolean(isCurrentDrivingKey, true).commit();
                //speedDisplayer.setVisibility(View.VISIBLE);

                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        //location.getLatitude();
                        //Toast.makeText(getApplicationContext(), "Current speed:" + location.getSpeed(), Toast.LENGTH_SHORT).show();
                        String toBeDisplayed="Current Speed:"+location.getSpeed();
                        //speedDisplayer.setText(toBeDisplayed);
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onProviderDisabled(String provider) {
                    }
                };
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


                startService(new Intent(getBaseContext(), BackgroundCallsRejecting.class));

            }
        });

        endDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDrive.setVisibility(View.INVISIBLE);
                startDrive.setVisibility(View.VISIBLE);
                someText.setText(R.string.msg1);
                goToSettings.setVisibility(View.VISIBLE);
                editor.putBoolean(isCurrentDrivingKey,false).commit();
                stopService(new Intent(getBaseContext(),BackgroundCallsRejecting.class));
            }
        });
    }

    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        //Toast.makeText(getApplicationContext(),grantResults.length,Toast.LENGTH_LONG).show();

        switch(permsRequestCode){
            case 200:{

                if (grantResults.length>0&&checkIfAllGranted(grantResults,permissions.length-1))//grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED&&grantResults[2]==PackageManager.PERMISSION_GRANTED&&grantResults[3]==PackageManager.PERMISSION_GRANTED) {
                {
                    Toast.makeText(getApplicationContext(),"Thank You!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"WHY!",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private boolean checkIfAllGranted(int[] grantResults,int length) {
        for (int i=0;i<length;i++)
        {
            if(grantResults[i]!=PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }
}