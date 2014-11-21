package com.yichao.TrackLocationApp;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by yichaoyu on 11/19/14.
 */
public class TrackLocationActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = "TrackApp";

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 10000;
    private static final long CAR_MODE_INTERVAL = 1000; // 1s
    private static final long WALK_MODE_INTERVAL = 5000; // 5s

    private long updateInterval = CAR_MODE_INTERVAL;

    private TextView mLat;
    private TextView mLng;
    private TextView mConnectionState;
    private TextView mRouteSummary;

    private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;

    private FirebaseRestRequest firebaseRequest;
    private String currentRouteId;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Get handles to the UI view objects
        mLat = (TextView) findViewById(R.id.lat);
        mLng = (TextView) findViewById(R.id.lng);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mRouteSummary = (TextView) findViewById(R.id.summary);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(updateInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(1000);

        mLocationClient = new LocationClient(this, this, this);

        Firebase.setAndroidContext(this);
        firebaseRequest = new FirebaseRestRequest();
        currentRouteId = null;
    }

    public void startTrack(View v) {
        if (currentRouteId != null) return;
        String[] latLng = getLocation();
        if (latLng[0] != null && latLng[1] != null) {
            Log.d(TAG, "Latitude is " + latLng[0]);
            Log.d(TAG, "Longtitude is " + latLng[1]);
            currentRouteId = firebaseRequest.startNewTrack("testuser1", latLng);
            firebaseRequest.getRouteSummary(mRouteSummary, currentRouteId);
        }
    }

    public void stopTrack(View v) {
        if (currentRouteId == null) return;
        String[] latLng = getLocation();
        if (latLng[0] != null && latLng[1] != null) {
            Log.d(TAG, "Latitude is " + latLng[0]);
            Log.d(TAG, "Longtitude is " + latLng[1]);
            firebaseRequest.endCurrentTrack("testuser1", currentRouteId, latLng);
            firebaseRequest.getRouteSummary(mRouteSummary, currentRouteId);
        }
        currentRouteId = null;
    }

    /**
     * this method is used to mimic getLocationUpdate method in
     * indoor testing.
     *
     */
    public void getLocation(View v) {
        if (currentRouteId == null) return;
        String[] latLng = getLocation();
        if (latLng[0] != null && latLng[1] != null) {
            Log.d(TAG, "Latitude is " + latLng[0]);
            Log.d(TAG, "Longtitude is " + latLng[1]);
            firebaseRequest.recordCoordinate(currentRouteId, latLng);
            firebaseRequest.getRouteSummary(mRouteSummary, currentRouteId);
        }
    }

    public void switchCarMode(View v) {
        updateInterval = CAR_MODE_INTERVAL;
        mLocationRequest.setInterval(updateInterval);
    }

    public void switchWalkMode(View v) {
        updateInterval = WALK_MODE_INTERVAL;
        mLocationRequest.setInterval(updateInterval);
    }

    private static String[] getLatLng(Context context, Location currentLocation) {
        String[] latLng = new String[2];
        // If the location is valid
        if (currentLocation != null) {
            // Return the latitude and longitude as strings
            latLng[0] = context.getString(
                    R.string.latitude_longitude,
                    currentLocation.getLatitude());
            latLng[1] = context.getString(
                    R.string.latitude_longitude,
                    currentLocation.getLongitude());
        }
        return latLng;
    }

    @Override
    public void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    public void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:
                        // Log the result
                        Log.d(TAG, "Connection failure is resolved.");

                        // Display the result
                        mConnectionState.setText(R.string.connected);
                        break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        Log.d(TAG, "Connection failure is not resolved.");

                        // Display the result
                        mConnectionState.setText(R.string.disconnected);
                        break;
                }
            default:
                // Log the request code
                Log.d(TAG, "Unknown request code " + requestCode);
                break;
        }
    }

    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // Continue
            Log.d(TAG, "Google Play service is now available.");
            return true;
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getFragmentManager(), TAG);
            }
            return false;
        }
    }

    private String[] getLocation() {
        // If Google Play Services is available
        if (servicesConnected()) {
            Log.d(TAG, "Location info is got.");
            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();

            // Display the current location in the UI
            String[] latLng = getLatLng(this, currentLocation);
            mLat.setText(latLng[0]);
            mLng.setText(latLng[1]);
            return latLng;
        }
        return new String[2];
    }

    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + ',' +
                Double.toString(location.getLongitude());
        // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        firebaseRequest.recordCoordinate(currentRouteId, getLatLng(this, location));
        firebaseRequest.getRouteSummary(mRouteSummary, currentRouteId);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Google Play Service is connected");
        mConnectionState.setText(R.string.connected);
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "Google Play Service is disconnected");
        mConnectionState.setText(R.string.disconnected);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            connectionResult.getErrorCode();
        }
    }

    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
