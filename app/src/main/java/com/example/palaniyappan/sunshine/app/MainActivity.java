package com.example.palaniyappan.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.palaniyappan.sunshine.app.data.WeatherContract;
import com.example.palaniyappan.sunshine.app.sync.SunshineSyncAdapter;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback{
    private final String TAG = MainActivity.class.getSimpleName();
    String mLocation;
    boolean mTwoPane;
    private final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocation = Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.weather_detail_container) != null) {
            // Set the mTwoPane variable to true since the weather detail container
            // will be added only in the two pane layout
            mTwoPane = true;
            // In mTwoPane, add the detail fragment to the main activity
            // Null check added so that the system doesn't keep adding fragments on screen rotation
            if(savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.weather_detail_container,
                                new DetailActivityFragment(),
                                DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        ff.setUseTodayLayoutInAdapter(!mTwoPane);

        SunshineSyncAdapter.initializeSyncAdapter(this);
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
                    findFragmentById(R.id.fragment_forecast);
            if(null != ff) {
                ff.onLocationChanged();
            }
            DetailActivityFragment df = (DetailActivityFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df ) {
                df.onLocationChanged(currentLocationSetting);
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

    @Override
    public void onItemSelected(Uri dateUri) {
        if(mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable("detailsUri", dateUri);
            DetailActivityFragment df = new DetailActivityFragment();
            df.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container,
                            df,
                            DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(dateUri);
            startActivity(intent);
        }
    }
}
