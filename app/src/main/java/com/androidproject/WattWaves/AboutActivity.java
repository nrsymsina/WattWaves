package com.androidproject.WattWaves;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidproject.WattWaves.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding.toolbar.titleTv.setText("About");

        binding.toolbar.infoIconIv.setVisibility(View.GONE);

        binding.toolbar.shareIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent with ACTION_SEND action
                Intent shareIntent = new Intent(Intent.ACTION_SEND);

                // Set the type of content you want to share (text/plain for plain text)
                shareIntent.setType("text/plain");

                // Add the content you want to share
                String shareText = "Sharing this text with you!";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

                // Create a chooser intent to display a list of apps for sharing
                Intent chooserIntent = Intent.createChooser(shareIntent, "Share via");

                // Check if there are apps that can handle the sharing action
                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    // Start the activity with the chooser intent
                    startActivity(chooserIntent);
                } else {
                    // Handle the case where no apps can handle the sharing action
                    Toast.makeText(getApplicationContext(), "No apps available for sharing", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.toolbar.backIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.linkToGitHubTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // GitHub repository URL
                String githubUrl = "https://github.com/hamza372";

                // Create intent to open the GitHub URL
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
                    startActivity(browserIntent);

            }
        });

    }
}