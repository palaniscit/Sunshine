package com.example.palaniyappan.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.palaniyappan.sunshine.app.data.WeatherContract;
import com.example.palaniyappan.sunshine.app.sync.SunshineSyncAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int FORECAST_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    private static String TAG = ForecastFragment.class.getSimpleName();

    public ForecastAdapter weatherListAdapter;

    int mPosition;

    public ForecastFragment() {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        weatherListAdapter.swapCursor(data);
        if(ListView.INVALID_POSITION != mPosition) {
            ((ListView) getActivity()
                    .findViewById(R.id.listView_forecast))
                    .setSelection(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        weatherListAdapter.swapCursor(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null){
            mPosition = savedInstanceState.getInt("currentPosition");
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        weatherListAdapter = new ForecastAdapter(getActivity(), null, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.listView_forecast);
        listView.setAdapter(weatherListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cur = (Cursor) adapterView.getItemAtPosition(i);
                if (cur != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    ((Callback) getActivity()).onItemSelected(
                            WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting,
                                    cur.getLong(COL_WEATHER_DATE)));
                    /*Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting,
                                    cur.getLong(COL_WEATHER_DATE))
                    );
                    startActivity(intent);*/
                }
                mPosition = i;
            }
        });

        return rootView;
    }

    /*@Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*//noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }*/

        if(id == R.id.action_view_map) {
            if(null != weatherListAdapter){
                Cursor cursor = weatherListAdapter.getCursor();
                if(null != cursor) {
                    if(cursor.moveToFirst()) {
                        String posLat = cursor.getString(COL_COORD_LAT);
                        String posLong = cursor.getString(COL_COORD_LONG);
                        Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(geoLocation);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if(ListView.INVALID_POSITION != mPosition) {
            savedInstanceState.putInt("currentPosition", mPosition);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    public void updateWeather() {
        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String userPreferredLocation = preferences.getString(getString(R.string.pref_loc_key),
                getString(R.string.pref_loc_default_value));

        Intent alarmIntent = new Intent(getActivity(), SunshineService.AlarmReceiver.class);
        alarmIntent.putExtra(SunshineService.LOCATION_QUERY_EXTRA, userPreferredLocation);

        PendingIntent pi = PendingIntent.getBroadcast(getActivity(),
                0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);*/

        SunshineSyncAdapter.syncImmediately(getActivity());
    }

    public void onLocationChanged() {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    public void setUseTodayLayoutInAdapter(boolean useTodayLayout) {
        if(weatherListAdapter != null) {
            weatherListAdapter.setUseTodayLayout(useTodayLayout);
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }
}