package com.example.hp.drivetimecallshandler;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

public class AllCallers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_callers);
        final Bundle bundle=getIntent().getExtras();
        final SQLiteDatabase sqlDB=openOrCreateDatabase("db123#5",MODE_PRIVATE,null);
        int totalCount=BackgroundCallsRejecting.numberOfCalled(sqlDB);
        Caller callerData[]=new Caller[totalCount];
        if(totalCount!=0)
        {
            Cursor getCallers=sqlDB.rawQuery("SELECT * FROM Callers",null);
            int i=0;
            getCallers.moveToFirst();
            if(getCallers!=null)
            {
                do {
                    callerData[i]=new Caller(getCallers.getString(0),getCallers.getString(1),getCallers.getString(2));
                    i++;
                }
                while (getCallers.moveToNext());
            }
        }
        ListView showAllCallers=(ListView)findViewById(R.id.callerList);
        CallerAdapter callerAdapter=new CallerAdapter(getApplicationContext(),R.layout.row,callerData);
        showAllCallers.setAdapter(callerAdapter);
    }
}
