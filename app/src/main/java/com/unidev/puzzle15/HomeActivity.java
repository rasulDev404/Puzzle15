package com.unidev.puzzle15;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class HomeActivity extends AppCompatActivity {
    private Button play;
    private Button option;
    private Button about;
    private Button exit;
    private Button ok;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loadView();
        preferences = getSharedPreferences("app_data",Context.MODE_PRIVATE);
        editor = preferences.edit();
        play.setOnClickListener(view -> start(MainActivity.class));
        about.setOnClickListener(view -> start(AboutActivity.class));
        exit.setOnClickListener( view -> finish());
        option.setOnClickListener(view->showSetting());
    }
    private void loadView(){
        play = findViewById(R.id.btn_play);
        option = findViewById(R.id.btn_options);
        about = findViewById(R.id.btn_about);
        exit = findViewById(R.id.btn_exit);
    }
    private void showSetting(){
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_settings,null);
        Switch soundOff =view.findViewById(R.id.soundOnOff);
        soundOff.setChecked(preferences.getBoolean("music_game",true));
        ok = view.findViewById(R.id.btnOk);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        soundOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("music_game",b);
                editor.apply();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //    private void startMainActivity(){
//        Intent intent = new Intent(HomeActivity.this , MainActivity.class);
//        startActivity(intent);
//    }
//    private void startAboutActivity(){
//        Intent intent = new Intent(HomeActivity.this,AboutActivity.class);
//        startActivity(intent);
//    }
//    private void startOptionsActivity(){
//        Intent intent = new Intent(HomeActivity.this,OptionsActivity.class);
//        startActivity(intent);
//    }
    private void start(Class activity){
        Intent intent = new Intent(this,activity);
        startActivity(intent);
    }

}