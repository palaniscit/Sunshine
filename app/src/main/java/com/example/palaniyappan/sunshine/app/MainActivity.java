package com.example.palaniyappan.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    String mLocation;
    private final String FORECASTFRAGMENT_TAG = "FFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocation = Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "In onStart");
        // SOme changes
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentLocationSetting = Utility.getPreferredLocation(this);
        // Check if location setting has changed
        //if(currentLocationSetting != null && !currentLocationSetting.equalsIgnoreCase(mLocation)) {
            // Get the forecast fragment using the Forecast fragment tag
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().
                    findFragmentByTag(FORECASTFRAGMENT_TAG);
            if(null != ff) {
                ff.onLocationChanged();
            }
            mLocation = currentLocationSetting;
        //}
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "In onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "In onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "In onDestroy");
    }
}
