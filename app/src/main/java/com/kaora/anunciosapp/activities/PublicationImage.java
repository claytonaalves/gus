package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kaora.anunciosapp.BuildConfig;
import com.kaora.anunciosapp.R;

public class PublicationImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication_image);

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("image_url");

        // Prepares proxied image URI
        String proxiedImageUri = BuildConfig.IMG_PROXY + "/600x,q90/" + imageUrl;

        SimpleDraweeView image = (SimpleDraweeView) findViewById(R.id.imageView);
        image.getHierarchy().setProgressBarImage(new ProgressBarDrawable());
        image.setImageURI(proxiedImageUri);
    }
}
