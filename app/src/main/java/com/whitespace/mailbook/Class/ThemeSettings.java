package com.whitespace.mailbook.Class;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeSettings {
    SharedPreferences mySharedPref;

    public ThemeSettings(Context context){
        mySharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    public void setNightModeState(Boolean state){
        SharedPreferences.Editor editor =  mySharedPref.edit();
        editor.putBoolean("NightMode",state);
        editor.commit();
    }

    public Boolean loadNightModeState(){
        Boolean state = mySharedPref.getBoolean("NightMode",false);
        return state;
    }
}
