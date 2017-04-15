package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.kaora.anunciosapp.R;

public class AvisoPerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso_perfil);
    }

    public void fechaActivity(View view) {
        finish();
    }

    public void abreActivityCriarNovoPerfil(View view) {
        Intent intent = new Intent(this, NovoPerfilActivity.class);
        startActivity(intent);
        finish();
    }
}
