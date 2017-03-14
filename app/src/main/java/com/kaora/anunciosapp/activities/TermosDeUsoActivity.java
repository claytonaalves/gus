package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Categoria;

import java.util.List;

public class TermosDeUsoActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtTermosUso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termos_de_uso);

        setTitle("Termos de uso");

        txtTermosUso = (TextView) findViewById(R.id.txtTermosUso);
        final CheckBox cbAceite = (CheckBox) findViewById(R.id.cbAceite);
        final Button btContinuar = (Button) findViewById(R.id.btContinuar);

        cbAceite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btContinuar.setEnabled(cbAceite.isChecked());
            }
        });

        btContinuar.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (termosAceitos()) {
            if (preferenciasDefinidas()) {
                iniciaActivityCategorias();
                finish();
            } else {
                iniciaActivityPreferencias();
            }
        } else {
            exibeTermosDeUso();
        }
    }

    private boolean preferenciasDefinidas() {
        MyDatabaseHelper database = new MyDatabaseHelper(this);
        List<Categoria> preferencias = database.categoriasPreferidas();
        return preferencias.size()>0;
    }

    private void iniciaActivityPreferencias() {
        Intent intent = new Intent(this, PreferenciasActivity.class);
        this.startActivity(intent);
//        this.finish();
    }

    private void iniciaActivityCategorias() {
        Intent intent = new Intent(this, CategoriasActivity.class);
        this.startActivity(intent);
    }

    private void exibeTermosDeUso() {
        txtTermosUso.setText(Html.fromHtml(getString(R.string.texto_termos_uso)));
    }

    @Override
    public void onClick(View v) {
        // Salvar o aceite
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("termosAceitos", true);
        editor.commit();

        // ir para próximo intent
        this.iniciaActivityPreferencias();
    }

    private boolean termosAceitos() {
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        return settings.getBoolean("termosAceitos", false);
    }
}
