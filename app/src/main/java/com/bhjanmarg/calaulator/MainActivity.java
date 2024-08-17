package com.bhjanmarg.calaulator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

public class MainActivity extends Activity {

    SharedPreferences prefs;
    private static final int DELAY_MILLIS = 10000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        int launchCount = prefs.getInt("launch_count", 0);
        launchCount++;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("launch_count", launchCount);
        editor.apply();

        if (launchCount >= 1 && !prefs.getBoolean("has_rated", false) ) {
            long lastDismissedTime = prefs.getLong("last_dismissed_time", 0);
            if (System.currentTimeMillis() - lastDismissedTime >= 2 * 24 * 60 * 60 * 1000) {
                new Handler().postDelayed(this::showRateUsDialog , DELAY_MILLIS);
            }
        }
    }

    private void showRateUsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate Us");
        builder.setMessage("If you enjoy using this app, please take a moment to rate it.");

        builder.setPositiveButton("Rate Now", (dialog, which) -> {
            // Redirect to Play Store
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + getPackageName())));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }

            // Mark as rated
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("has_rated", true);
            editor.apply();
        });

        builder.setNegativeButton("Later", (dialog, which) -> {
            // Handle later action
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("last_dismissed_time", System.currentTimeMillis());
            editor.apply();
        });

        builder.setNeutralButton("Never", (dialog, which) -> {
            // Handle never action
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("has_rated", true);  // Stop showing the popup
            editor.apply();
        });

        builder.show();
    }
}
