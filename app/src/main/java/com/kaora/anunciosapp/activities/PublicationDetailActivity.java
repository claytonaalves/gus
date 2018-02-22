package com.kaora.anunciosapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kaora.anunciosapp.BuildConfig;
import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.Publication;
import com.kaora.anunciosapp.rest.ApiRestAdapter;
import com.kaora.anunciosapp.utils.DateUtils;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ViewListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicationDetailActivity extends AppCompatActivity {

    private Publication publication;
    private TextView tvPublicationTitle;
    private TextView tvPublicationDescription;
    private TextView tvPublicationDate;
    private TextView tvPublicationDueDate;
    private TextView tvAdvertiserName;
    private TextView tvStreetName;
    private TextView tvAddressNumber;
    private TextView tvNeighbourhood;
    private Button btCallAdvertiser;

    private List<SimpleDraweeView> images = new ArrayList<>();

    CarouselView carouselView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication_detail);

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setViewListener(viewListener);
        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(PublicationDetailActivity.this, PublicationImage.class);
                intent.putExtra("image_url", ApiRestAdapter.PUBLICATIONS_IMAGE_PATH + publication.images.get(position));
                startActivity(intent);
            }
        });

        tvPublicationTitle = (TextView) findViewById(R.id.tvTitulo);
        tvPublicationDescription = (TextView) findViewById(R.id.tvDescricao);
        tvPublicationDate = (TextView) findViewById(R.id.tvDataPublicacao);
        tvPublicationDueDate = (TextView) findViewById(R.id.tvDataValidade);

        tvAdvertiserName = (TextView) findViewById(R.id.tvNomeAnunciante);
        tvStreetName = (TextView) findViewById(R.id.tvStreetName);
        tvAddressNumber = (TextView) findViewById(R.id.tvAddressNumber);
        tvNeighbourhood = (TextView) findViewById(R.id.tvNeighbourhood);

        btCallAdvertiser = (Button) findViewById(R.id.btLigarPara);

        Intent intent = getIntent();

        if (intent.hasExtra("publication")) {
            publication = (Publication) intent.getSerializableExtra("publication");
            updateViewItems(publication);
        } else {
            String guidPublicacao = intent.getStringExtra("guid_publicacao");
            obtemPublicacaoDoWebservice(guidPublicacao);
        }
    }

    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {
            return images.get(position);
        }
    };

    private void obtemPublicacaoDoWebservice(String guidPublicacao) {
        ApiRestAdapter webservice = ApiRestAdapter.getInstance();
        webservice.obtemPublicacao(guidPublicacao, new Callback<Publication>() {
            @Override
            public void onResponse(Call<Publication> call, Response<Publication> response) {
                Publication publication = response.body();
                updateViewItems(publication);
            }

            @Override
            public void onFailure(Call<Publication> call, Throwable t) {
                Toast.makeText(PublicationDetailActivity.this, "Falha ao baixar Publicação", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateViewItems(Publication publication) {
        loadPublicationImages(publication);

        carouselView.setPageCount(publication.images.size());

        tvPublicationTitle.setText(publication.title);
        tvPublicationDescription.setText(publication.description);
        tvPublicationDate.setText(DateUtils.textoDataPublicacao(publication.publicationDate));

        tvAdvertiserName.setText(publication.advertiser.tradingName);
        tvStreetName.setText(publication.advertiser.streetName);
        tvAddressNumber.setText(publication.advertiser.addressNumber);
        tvNeighbourhood.setText(publication.advertiser.neighbourhood);

        btCallAdvertiser.setText("Ligar para " + publication.advertiser.tradingName + "\n" + publication.advertiser.phoneNumber);

        Resources res = this.getResources();
        DateFormat df = DateFormat.getDateInstance();
        tvPublicationDueDate.setText(
            String.format(res.getString(R.string.data_validade), df.format(publication.dueDate))
        );
    }

    private void loadPublicationImages(Publication publication) {
        if (!publication.hasImages()) return;
        SimpleDraweeView image;
        for (String imageFilename : publication.images) {
            image = new SimpleDraweeView(this);

            // Prepares proxied image URI
            String imageUri = ApiRestAdapter.PUBLICATIONS_IMAGE_PATH + imageFilename;
            String proxiedImageUri = BuildConfig.IMG_PROXY + "/300x,q90/" + imageUri;

            image.setImageURI(proxiedImageUri);
            images.add(image);
        }
    }

    public void ligarParaAnunciante(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + publication.advertiser.phoneNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

}
