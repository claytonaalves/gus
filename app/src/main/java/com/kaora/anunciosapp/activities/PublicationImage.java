package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kaora.anunciosapp.BuildConfig;
import com.kaora.anunciosapp.R;

public class PublicationImage extends AppCompatActivity {

    SimpleDraweeView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication_image);

        image = (SimpleDraweeView) findViewById(R.id.imageView);

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("image_url");

        // Prepares proxied image URI
        String proxiedImageUri = BuildConfig.IMG_PROXY + "/600x,q90/" + imageUrl;

        image.setImageURI(proxiedImageUri);
    }
}
