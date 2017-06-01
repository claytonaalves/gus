package com.kaora.anunciosapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Anuncio;
import com.kaora.anunciosapp.models.PerfilAnunciante;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NovoAnuncioActivity extends AppCompatActivity {

    private TextView tvNomeAnunciante;
    private EditText etTitulo;
    private EditText etDescricao;
    private EditText etValidoAte;
    private DatePickerDialog datePicker;
    private SimpleDateFormat dateFormat;

    MyDatabaseHelper database = MyDatabaseHelper.getInstance(this);

    PerfilAnunciante perfilSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_anuncio);

        tvNomeAnunciante = (TextView) findViewById(R.id.tvNomeAnunciante);
        etTitulo = (EditText) findViewById(R.id.etTitulo);
        etDescricao = (EditText) findViewById(R.id.etDescricao);
        etValidoAte = (EditText) findViewById(R.id.etValidoAte);
        etValidoAte.setInputType(InputType.TYPE_NULL);
        etValidoAte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });

        Intent intent = getIntent();
        long idPerfil = intent.getLongExtra("idPerfil", 0);
        perfilSelecionado = database.selecionaPerfil(idPerfil);

        tvNomeAnunciante.setText(perfilSelecionado.nome);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        preparaDialogData();
    }

    private void preparaDialogData() {
        Calendar calendar = Calendar.getInstance();
        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar dataInformada = Calendar.getInstance();
                dataInformada.set(year, month, dayOfMonth);
                etValidoAte.setText(dateFormat.format(dataInformada.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void adicionaFoto(View view) {

    }

    public void publicaAnuncio(View view) {
        Anuncio anuncio = new Anuncio();
        anuncio.titulo = etTitulo.getText().toString();
        anuncio.descricao = etDescricao.getText().toString();
        anuncio.validoAte = extraiData(etValidoAte.getText().toString());
        anuncio.guidAnunciante = perfilSelecionado.guid;
        anuncio.idCategoria = perfilSelecionado.idCategoria;
        database.salvaAnuncio(anuncio);
        publicaAnuncioRemotamente(anuncio);
    }

    private void publicaAnuncioRemotamente(Anuncio anuncio) {
        final long idAnuncio = anuncio._id;
        ApiRestAdapter api = ApiRestAdapter.getInstance();
        api.publicaAnuncio(anuncio, new Callback<Anuncio>() {
            @Override
            public void onResponse(Call<Anuncio> call, Response<Anuncio> response) {
                database.marcaAnuncioComoPublicado(idAnuncio);
                Toast.makeText(NovoAnuncioActivity.this, "Anúncio publicado!", Toast.LENGTH_LONG).show();
                fechaActivity();
            }

            @Override
            public void onFailure(Call<Anuncio> call, Throwable t) {
                Toast.makeText(NovoAnuncioActivity.this, "Erro ao publicar", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fechaActivity() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3500);
                    NovoAnuncioActivity.this.finish();
                } catch (Exception e) {

                }
            }
        };
        thread.start();
    }

    private Date extraiData(String data) {
        try {
            return dateFormat.parse(etValidoAte.getText().toString());
        } catch (ParseException e) {
            Date hoje = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(hoje);
            c.add(Calendar.DATE, 7); // dar uma validade de 7 dias
            return c.getTime();
        }
    }
}
