package com.example.palaniyappan.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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

        if(id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            //TextView textView = (TextView)findViewById(R.id.weatherData);
            CharSequence textToShare = "Test date string" + " " + getString(R.string.hashtag_sunshine);
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.menu_share)));
        }

        return super.onOptionsItemSelected(item);
    }
}
