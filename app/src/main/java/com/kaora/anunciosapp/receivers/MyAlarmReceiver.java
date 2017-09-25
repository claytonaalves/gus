package com.kaora.anunciosapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kaora.anunciosapp.services.RemovePublicacoesVencidasService;


public class MyAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 1;
//    public static final String ACTION = "com.kaora.anunciosapp.alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, RemovePublicacoesVencidasService.class);
        context.startService(i);
    }

}
