package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HealthArticlesDeatails extends AppCompatActivity {
    TextView tv1;
    ImageView img;
    Button linkButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_articles_deatails);

        tv1 = findViewById(R.id.textViewHADtitle);
        img = findViewById(R.id.imageHADView);
        linkButton = findViewById(R.id.buttonHADLink);

        Intent intent = getIntent();
        tv1.setText(intent.getStringExtra("text1"));

        if (intent.hasExtra("imageResourceId")) {
            int resId = intent.getIntExtra("imageResourceId", 0);
            img.setImageResource(resId);
        }

        // Set the online resource link if provided
        if (intent.hasExtra("articleUrl")) {
            final String url = intent.getStringExtra("articleUrl");
            linkButton.setVisibility(View.VISIBLE);
            linkButton.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            });
        } else {
            linkButton.setVisibility(View.GONE);
        }
    }
}