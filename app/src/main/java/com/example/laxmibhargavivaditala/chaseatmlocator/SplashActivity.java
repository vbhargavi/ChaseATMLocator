package com.example.laxmibhargavivaditala.chaseatmlocator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * SplashActivity is the splash screen for the app. We wait for few seconds on this screen before going to the ATMLocatorActivity.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Wait for 2 seconds to go to next screen. Potentially here we can load data required to start the app.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goToLocator();
            }
        }, 2000);
    }

    /**
     * Intent to go to the Locator Activity.
     */
    private void goToLocator() {
        Intent intent = new Intent(SplashActivity.this, ATMLocatorActivity.class);
        startActivity(intent);
        finish();//We call finish() to remove from stack since the user should never be able to get back to SplashActivity.
    }
}
