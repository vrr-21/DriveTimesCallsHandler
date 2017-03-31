package com.example.hp.drivetimecallshandler;

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
        List temp1= (List) bundle.getSerializable("callerDataContactList");
        List temp2=(List)bundle.getSerializable("callerDataNonContactList");
        Caller callerData[]=new Caller[temp1.size()+temp2.size()];
        //Toast.makeText(getApplicationContext(),BackgroundCallsRejecting.callersFromContactList.elementAt(0),Toast.LENGTH_SHORT).show();
        int i=0;
        Iterator itr=temp1.iterator();

        while (itr.hasNext())
        {
            callerData[i]=new Caller("","","");;
            callerData[i]= (Caller) itr.next();
            i++;
        }
        itr=temp2.iterator();
        while (itr.hasNext())
        {
            callerData[i]=new Caller("","","");
            callerData[i]=(Caller)itr.next();
            i++;
        }

        ListView showAllCallers=(ListView)findViewById(R.id.callerList);
        CallerAdapter callerAdapter=new CallerAdapter(getApplicationContext(),R.layout.row,callerData);
        showAllCallers.setAdapter(callerAdapter);
    }
}
