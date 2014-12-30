package br.com.gdgaracaju.sunshine.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import java.text.SimpleDateFormat;

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A generic_placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

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

            TextView dayText = (TextView)rootView.findViewById(R.id.day_detail);
            TextView dateText = (TextView)rootView.findViewById(R.id.date_detail);
            TextView maxTempText = (TextView)rootView.findViewById(R.id.max_temp_detail);
            TextView minTempText = (TextView)rootView.findViewById(R.id.min_temp_detail);
            TextView weatherText = (TextView)rootView.findViewById(R.id.weather_description_detail);


            try {
                WeatherDetail[] weatherDetails = WeatherDataParser.getWeatherDetailFromJson(forecastJson, 7);
                WeatherDetail weather = weatherDetails[dayNumber];

                SimpleDateFormat format = new SimpleDateFormat("MMMM d");
                dateText.setText(format.format(weather.date).toString());

                format = new SimpleDateFormat("EEEE");
                dayText.setText(format.format(weather.date).toString());

                maxTempText.setText(Long.toString(Math.round(weather.maxTemperature))+"º");
                minTempText.setText(Long.toString(Math.round(weather.minTemperature))+"º");
                weatherText.setText(weather.weatherCondition);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return rootView;
        }
    }
}
