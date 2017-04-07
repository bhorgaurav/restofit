package edu.csulb.android.restofit.helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class LocationHelper {

    private static LocationManager locationManager;

    public static void init(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static boolean statusCheck() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void listen(Context context, LocationListener locationListener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "GPS permission wasn't granted", Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
    }

    public static Location getLastKnownLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "GPS permission wasn't granted", Toast.LENGTH_SHORT).show();
            return null;
        }
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public static void getAddress(final Context context, final Callback callback) {
        listen(context, new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                try {
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    callback.success(addresses.get(0));

                    // Remove listener after we get the location update
                    locationManager.removeUpdates(this);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error getting location.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        });
    }
}


