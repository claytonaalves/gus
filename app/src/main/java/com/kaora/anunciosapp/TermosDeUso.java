package com.kaora.anunciosapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class TermosDeUso extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termos_de_uso);

        if (termosAceitos()) {
            iniciaTelaPrincipal();
        }

        TextView txtTermosUso = (TextView) findViewById(R.id.txtTermosUso);
        final CheckBox cbAceite = (CheckBox) findViewById(R.id.cbAceite);
        final Button btContinuar = (Button) findViewById(R.id.btContinuar);

        txtTermosUso.setText(Html.fromHtml(getString(R.string.texto_termos_uso)));

        cbAceite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btContinuar.setEnabled(cbAceite.isChecked());
            }
        });

        btContinuar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Salvar o aceite
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("termosAceitos", true);
        editor.commit();

        // ir para próximo intent
        this.iniciaTelaPrincipal();
    }

    private void iniciaTelaPrincipal() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finishActivity(0);
    }

    private boolean termosAceitos() {
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        return settings.getBoolean("termosAceitos", false);
    }
}
