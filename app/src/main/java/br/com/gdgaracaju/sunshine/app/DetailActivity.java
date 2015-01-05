package br.com.gdgaracaju.sunshine.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import java.text.SimpleDateFormat;

import br.com.gdgaracaju.sunshine.app.util.TemperatureUnit;
import br.com.gdgaracaju.sunshine.app.util.WeatherDataParser;
import br.com.gdgaracaju.sunshine.app.util.WeatherDetail;


public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
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
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A generic_placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        WeatherDetail weather;
        TextView dayText;
        TextView dateText;
        TextView maxTempText;
        TextView minTempText;
        TextView weatherText;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            //collecting data
            Intent intent = getActivity().getIntent();
            //forecast TODO: remove
            String forecast = intent.getStringExtra(Intent.EXTRA_TEXT);
            String forecastJson = intent.getStringExtra("forecastJson");
            int dayNumber = intent.getIntExtra("dayNumber", 0);

            dayText = (TextView)rootView.findViewById(R.id.day_detail);
            dateText = (TextView)rootView.findViewById(R.id.date_detail);
            maxTempText = (TextView)rootView.findViewById(R.id.max_temp_detail);
            minTempText = (TextView)rootView.findViewById(R.id.min_temp_detail);
            weatherText = (TextView)rootView.findViewById(R.id.weather_description_detail);

            try {
                WeatherDetail[] weatherDetails = WeatherDataParser.getWeatherDetailFromJson(forecastJson, 7);
                setWeatherDetails(weatherDetails[dayNumber]);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            setWeatherDetails(weather);
        }

        private void setWeatherDetails(WeatherDetail weatherDetails) {
            this.weather = weatherDetails;
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String prefUnit = sharedPref.getString(getActivity().getString(R.string.pref_unit_key),
                    getString(R.string.pref_unit_default));
            String[] units = getActivity().getResources().getStringArray(R.array.unit_settings_values);

            if (prefUnit.equals(getActivity().getResources().getStringArray(R.array.unit_settings_values)[0]))
                weather.changeTemperatureUnit(TemperatureUnit.FARENHEIT);
            else
                weather.changeTemperatureUnit(TemperatureUnit.CELSIUS);

            SimpleDateFormat format = new SimpleDateFormat("MMMM d");
            dateText.setText(format.format(weather.date));

            format = new SimpleDateFormat("EEEE");
            dayText.setText(format.format(weather.date));

            maxTempText.setText(Long.toString(Math.round(weather.maxTemperature))+"º");
            minTempText.setText(Long.toString(Math.round(weather.minTemperature))+"º");
            weatherText.setText(weather.weatherCondition);
        }
    }
}
