package com.example.hp.drivetimecallshandler;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.hp.drivetimecallshandler.R.layout.row;

/**
 * Created by varunrao on 29/03/17.
 */

public class CallerAdapter extends ArrayAdapter<Caller> {
    Context mContext;
    int mResource;
    Caller mData[];
    public CallerAdapter(Context context, int resource, Caller[] data) {
        super(context,resource,data);
        mContext=context;
        mResource=resource;
        mData=data;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row=convertView;

        LayoutInflater inflater= LayoutInflater.from(mContext);
        row=inflater.inflate(mResource,parent,false);

        TextView callerNameView=(TextView)row.findViewById(R.id.callerName);
        TextView numberView=(TextView)row.findViewById(R.id.number);
        Button callThemUp=(Button)row.findViewById(R.id.CallThem);

        callThemUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callPerson=new Intent(Intent.ACTION_CALL);
                String toBeCalled="tel:"+mData[position].number;
                callPerson.setData(Uri.parse(toBeCalled));
                if (ActivityCompat.checkSelfPermission(mContext,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext,"Permission disabled to call. Hence, cannot call",Toast.LENGTH_LONG);
                    return;
                }
                else{
                    mContext.startActivity(callPerson);
                }
            }
        });


        Caller tempCaller=mData[position];

        callerNameView.setText(tempCaller.name);
        numberView.setText(tempCaller.number);

        return row;
    }

    @Nullable
    @Override
    public Caller getItem(int position) {
        return super.getItem(position);
    }
}
