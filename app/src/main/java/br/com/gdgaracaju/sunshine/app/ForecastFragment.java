package br.com.gdgaracaju.sunshine.app;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import br.com.gdgaracaju.sunshine.app.util.TemperatureUnit;
import br.com.gdgaracaju.sunshine.app.util.WeatherDataParser;

/**
 * A generic_placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    public ArrayAdapter<String> mForecastAdapter;

    String forecastJsonStr = null;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Report that this fragment would like to participate in populating the options menu by receiving a call to onCreateOptionsMenu(Menu, MenuInflater) and related methods.
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        this.mForecastAdapter = new ArrayAdapter<String>(
                //context
                getActivity(),
                //id of list item layout
                R.layout.list_item_forecast,
                //id of textview to populate
                R.id.list_item_forecast_textview,
                //data -- filled on updateWeather()
                new ArrayList<String>());


        ListView listView = (ListView) rootView.findViewById(
                R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = mForecastAdapter.getItem(position);
                //Toast for debug
                //Toast.makeText(container.getContext(),
                //        forecast,
                //        Toast.LENGTH_SHORT).show();
                // Executed in an Activity, so 'this' is the Context
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, forecast)
                    .putExtra("forecastJson", forecastJsonStr)
                    .putExtra("dayNumber",position);
                getActivity().startActivity(detailIntent);
            }
        });

        return rootView;
    }

    @Override
    //Initialize the contents of the Activity's standard options menu. You should place your menu items in to menu.
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            //Log.v(LOG_TAG, "Menu id " + Integer.toString(id));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        //getting live data
        updateWeather();
    }

    private void updateWeather(){
        //getting live data
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = sharedPref.getString(this.getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        Log.v(LOG_TAG, location);
        new FetchWeatherTask().execute(location);
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private URL buildURL(String postalcode) throws MalformedURLException {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            //http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.openweathermap.org")
                    .appendPath("data")
                    .appendPath("2.5")
                    .appendPath("forecast")
                    .appendPath("daily")
                    .appendQueryParameter("q", postalcode)
                    .appendQueryParameter("mode", "json")
                    .appendQueryParameter("units", "metric")
                    .appendQueryParameter("cnt", "7");
            String builtUri = builder.build().toString();
            Log.v(LOG_TAG, "Built URI " + builtUri);
            return new URL(builtUri);
        }

        protected String[] doInBackground(String... params) {
            if (params.length == 0)
                return null;

            String postalcode = params[0];

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            forecastJsonStr = null;
            String[] forecast = null;


            try {

                URL url = buildURL(postalcode);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            if (forecastJsonStr != null) {
                //Log.v(LOG_TAG, forecastJsonStr);
                try {

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String prefUnit = sharedPref.getString(getActivity().getString(R.string.pref_unit_key),
                            getString(R.string.pref_unit_default));
                    String[] units = getActivity().getResources().getStringArray(R.array.unit_settings_values);

                    if (prefUnit.equals(units[0]))
                        forecast = WeatherDataParser.getWeatherDataFromJson(forecastJsonStr, 7, TemperatureUnit.FARENHEIT);
                    else
                        forecast = WeatherDataParser.getWeatherDataFromJson(forecastJsonStr, 7, TemperatureUnit.CELSIUS);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Malformed JSON", e);
                }
            }

            return forecast;
        }

        @Override
        protected void onPostExecute(String[] result){
            if (result != null){
                //Log.v(LOG_TAG, result[0]);
                //Repopulates adapter
                mForecastAdapter.clear();
                mForecastAdapter.addAll(new ArrayList<String>(Arrays.asList(result)));
            }

        }
    }

}
