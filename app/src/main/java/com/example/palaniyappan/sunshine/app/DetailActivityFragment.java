package com.example.palaniyappan.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.palaniyappan.sunshine.app.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_ACTIVITY_LOADER_ID = 1;

    private TextView dateView;
    private TextView maxTempView;
    private TextView minTempView;
    private TextView humidityView;
    private TextView windView;
    private TextView pressureView;
    private ImageView iconView;
    private TextView descriptionView;

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
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_PRESSURE = 6;
    static final int COL_WEATHER_WIND_SPEED = 7;
    static final int COL_WEATHER_DEGREES = 8;
    static final int COL_WEATHER_CONDITION_ID = 9;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();

        if(intent == null) {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()) {
            return;
        }

        boolean isMetric = Utility.isMetric(getActivity());

        // Load corresponding data from cursor into the view components
        maxTempView.setText(Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MAX_TEMP), isMetric));
        minTempView.setText(Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MIN_TEMP), isMetric));
        humidityView.setText(getString(R.string.format_humidity,
                data.getDouble(COL_WEATHER_HUMIDITY)));
        windView.setText(Utility.getFormattedWind(getActivity(),
                data.getFloat(COL_WEATHER_WIND_SPEED),
                data.getFloat(COL_WEATHER_DEGREES)));
        pressureView.setText(getString(R.string.format_pressure,
                data.getDouble(COL_WEATHER_PRESSURE)));
        descriptionView.setText(data.getString(COL_WEATHER_DESC));
        dateView.setText(Utility.getFriendlyDayString(getActivity(),
                data.getLong(COL_WEATHER_DATE)));

        int weatherIconId = Utility.getArtResourceForWeatherCondition
                (data.getInt(COL_WEATHER_CONDITION_ID));
        iconView.setImageResource(weatherIconId);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(
                DETAIL_ACTIVITY_LOADER_ID,
                savedInstanceState,
                this
        );
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        // Get the different components in the View
        dateView = (TextView)rootView.findViewById(R.id.detail_date_textview);
        maxTempView = (TextView)rootView.findViewById(R.id.detail_high_temp_textview);
        minTempView = (TextView)rootView.findViewById(R.id.detail_low_temp_textview);
        humidityView = (TextView)rootView.findViewById(R.id.detail_humidity_textview);
        windView = (TextView)rootView.findViewById(R.id.detail_wind_textview);
        pressureView = (TextView)rootView.findViewById(R.id.detail_pressure_textview);
        iconView = (ImageView)rootView.findViewById(R.id.detail_icon_textview);
        descriptionView = (TextView)rootView.
                findViewById(R.id.detail_description_textview);
        return rootView;
    }
}