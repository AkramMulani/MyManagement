package com.ak.mymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        Objects.requireNonNull(getSupportActionBar()).hide();

        ImageView imageView = findViewById(R.id.imageViewFullScreen);

        // Get the image URI from the intent
        Intent intent = getIntent();
        if (intent != null) {
            Uri imageUri = intent.getParcelableExtra("imageUri");

            // Load the image into the ImageView using Glide
            Glide.with(this)
                    .load(imageUri)
                    .into(imageView);
        }
    }
}
