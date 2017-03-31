package com.example.hp.drivetimecallshandler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences getCurrentMode = getApplicationContext().getSharedPreferences(getString(R.string.SettingsKey), MODE_PRIVATE);
        final SharedPreferences.Editor editor = getCurrentMode.edit();

        final EditText et1=(EditText) findViewById(R.id.smsToBeSent);
        final Switch enableSMS=(Switch)findViewById(R.id.switch1);
        enableSMS.setChecked(getCurrentMode.getBoolean(getString(R.string.enablerKey),true));
        enableSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(getString(R.string.enablerKey),isChecked);
                editor.commit();
                et1.setEnabled(isChecked);
            }
        });
        et1.setText(getCurrentMode.getString(getString(R.string.keySMS),getString(R.string.defaultSMS)));
        Button saveSMS=(Button) findViewById(R.id.saveSMS);
        saveSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp=et1.getText().toString();
                editor.putString(getString(R.string.keySMS),temp);
                editor.commit();
                Intent goBack=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(goBack);
            }
        });


    }
}
