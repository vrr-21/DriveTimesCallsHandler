package com.example.hp.drivetimecallshandler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Vector;

/**
 * Created by varunrao on 09/02/17.
 */

public class BackgroundCallsRejecting extends Service {
    private final int NOTIFICATION_ID=1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    NotificationManager notificationManager;


    //Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
    //PendingIntent resultPendingIntent =PendingIntent.getActivity(getApplicationContext(),0,resultIntent,0);
    ArrayList<Caller> callersFromContactList=new ArrayList<Caller>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final SQLiteDatabase sqlDB=openOrCreateDatabase("db123#5",MODE_PRIVATE,null);
        //Used to redirect user to app on clicking the notification.
        Intent traceBackToApp=new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent resultPendingIntent =PendingIntent.getActivity(getApplicationContext(),0,traceBackToApp,0);

        Toast.makeText(getApplicationContext(),"All Calls to be rejected now.", Toast.LENGTH_LONG).show();
        //Notification building
        final NotificationCompat.Builder notiBuilder=new NotificationCompat.Builder(this)
                .setContentTitle("Driving Started")
                .setContentText(numberOfCalled(sqlDB)+" calls rejected")
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.driver_icon)
                .setOngoing(true);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,notiBuilder.build());

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        // register PhoneStateListener
        PhoneStateListener callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber)
            {
                //  React to incoming call.
                String number=incomingNumber;
                // If phone ringing
                if(state==TelephonyManager.CALL_STATE_RINGING)
                {
                    try
                    {
                        //Following Code gets time:
                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                        Date currentLocalTime = cal.getTime();
                        SimpleDateFormat date = new SimpleDateFormat("HH:mm a");
                        // you can get seconds by adding  "...:ss" to it
                        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                        String localTime = date.format(currentLocalTime);

                        String name=findNameByNumber(incomingNumber);
                        TelephonyManager telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                        Class clazz = Class.forName(telephonyManager.getClass().getName());
                        Method method = clazz.getDeclaredMethod("getITelephony");
                        method.setAccessible(true);
                        ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
                        telephonyService.endCall();
                        if(name.equals("Not Found"))
                        {
                            name="Unknown";
                            Toast.makeText(getApplicationContext(),"Call From "+number+".Rejected.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            callersFromContactList.add(new Caller(name,incomingNumber,localTime));
                            Toast.makeText(getApplicationContext(),"Call From "+name+".Rejected.", Toast.LENGTH_SHORT).show();
                            //Sending the message to one who has called
                            String phoneNo=number;
                            String message="Varun is currently driving. He has been notified, and will call when free.\nSent via Drive time Calls Handler";
                            SmsManager smsManager=SmsManager.getDefault();
                            //smsManager.sendTextMessage(phoneNo,null,message,null,null);
                            Toast.makeText(getApplicationContext(), "SMS sent.",Toast.LENGTH_SHORT).show();
                        }
                        //Update database
                        sqlDB.execSQL("INSERT INTO Callers(name,number,timeCalled) VALUES('"+name+"','"+incomingNumber+"','"+localTime+"')");
                        //Update notif
                        notiBuilder.setContentText(numberOfCalled(sqlDB)+" calls rejected");
                        notificationManager.notify(NOTIFICATION_ID,notiBuilder.build());
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Some Error:"+e, Toast.LENGTH_LONG).show();
                        Log.i("1",e.toString());
                    }
                }
            }
        };
        telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);
        return START_STICKY;
    }

    private String findNameByNumber(String incomingNumber) {
        String res = "Not Found";
        try {
            ContentResolver resolver = getApplicationContext().getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));
            Cursor c = resolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

            if (c != null) {
                if (c.moveToFirst()) {
                    res = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                }
                c.close();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.toString(), Toast.LENGTH_SHORT).show();
        }
        return res;
    }

    @Override
    public void onDestroy() {
        final SQLiteDatabase sqlDB=openOrCreateDatabase("db123#5",MODE_PRIVATE,null);
        Toast.makeText(getApplicationContext(),"Calls now available", Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),"Callers who had called:", Toast.LENGTH_LONG).show();
        Cursor getCallers=sqlDB.rawQuery("SELECT name FROM Callers",null);
        String a;
        getCallers.moveToFirst();
        if(getCallers!=null)
        {
            do {
                a=getCallers.getString(0);
                Toast.makeText(getApplicationContext(),a,Toast.LENGTH_SHORT).show();
            }
            while (getCallers.moveToNext());
        }
        sqlDB.execSQL("DELETE FROM Callers");
        notificationManager.cancel(NOTIFICATION_ID);
        super.onDestroy();
    }

    protected static int numberOfCalled(SQLiteDatabase sqlDB)
    {
        Cursor getNumCallers=sqlDB.rawQuery("SELECT COUNT(*) FROM Callers",null);
        int a=0;
        getNumCallers.moveToFirst();
        if(getNumCallers!=null)
        {
            do {
                a=getNumCallers.getInt(0);
            }
            while (getNumCallers.moveToNext());
        }
        return a;
    }
}