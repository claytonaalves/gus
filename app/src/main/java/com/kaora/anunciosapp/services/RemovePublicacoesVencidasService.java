package com.kaora.anunciosapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kaora.anunciosapp.database.MyDatabaseHelper;


public class RemovePublicacoesVencidasService extends IntentService {

    public RemovePublicacoesVencidasService() {
        super("RemovePublicacoesVencidas");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i("Removendo", "Removendo publicações vencidas.");
        MyDatabaseHelper database = MyDatabaseHelper.getInstance(this);
        database.removePublicacoesVencidas();
    }

}
