package br.com.gdgaracaju.sunshine.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


public class MainActivity extends Activity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case (R.id.action_settings):
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case (R.id.action_show_map):
                showPreferredLocationOnMap();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPreferredLocationOnMap() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String location = sharedPref.getString(this.getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        final Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Use the address as needed
                showMap(address);
            } else {
                // Display appropriate message when Geocoder services are not available
                Toast.makeText(this, getString(R.string.error_geocode_zip), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            // handle exception
            Log.e(LOG_TAG, getString(R.string.error_geolocation),e);
            e.printStackTrace();
        }
    }

    private void showMap(Address address) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        double lat = address.getLatitude();
        double lon = address.getLongitude();
        StringBuilder geoBuilder = new StringBuilder("geo:");
        String geo = geoBuilder.append(Double.toString(lat)).append(",").append(Double.toString(lon)).toString();

        intent.setData(Uri.parse(geo));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
