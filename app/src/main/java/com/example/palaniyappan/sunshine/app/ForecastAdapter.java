package com.example.palaniyappan.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private boolean mUseTodayLayout;

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    /*
                Remember that these views are reused as needed.
             */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = this.getItemViewType(cursor.getPosition());
        int layoutId = -1;

        // Get layout based on the view type
        if(viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        } else if(viewType == VIEW_TYPE_FUTURE_DAY) {
            layoutId = R.layout.list_item_layout;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int viewType = getItemViewType(cursor.getPosition());

        int weatherConditionId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int weatherIconId;
        if(viewType == 0) {
            weatherIconId = Utility.getArtResourceForWeatherCondition
                    (weatherConditionId);
        } else {
            weatherIconId = Utility.getIconResourceForWeatherCondition
                    (weatherConditionId);
        }
        viewHolder.iconView.setImageResource(weatherIconId);

        // Get Unit of Measurement selected by user
        boolean isMetric = Utility.isMetric(mContext);
        // Get data stored in cursor
        String maxTemp = Utility.formatTemperature(context, cursor.getDouble
                (ForecastFragment.COL_WEATHER_MAX_TEMP), isMetric);
        String minTemp = Utility.formatTemperature(context, cursor.getDouble
                (ForecastFragment.COL_WEATHER_MIN_TEMP), isMetric);
        String dateText = Utility.getFriendlyDayString(mContext,
                cursor.getLong(ForecastFragment.COL_WEATHER_DATE));

        // Set appropriate values to the corresponding text views
        viewHolder.descriptionView.setText(cursor.getString(ForecastFragment.COL_WEATHER_DESC));
        viewHolder.maxTempView.setText(maxTemp);
        viewHolder.minTempView.setText(minTemp);
        viewHolder.dateView.setText(dateText);
    }

    // Using the view holder can reduce the number of times the findViewById method is called
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView maxTempView;
        public final TextView minTempView;

        public ViewHolder(View view) {
            iconView = (ImageView)view.findViewById(R.id.list_item_icon);
            dateView = (TextView)view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView)view.findViewById(R.id.list_item_forecast_textview);
            maxTempView = (TextView)view.findViewById(R.id.list_item_high_textview);
            minTempView = (TextView)view.findViewById(R.id.list_item_low_textview);
        }
    }
}