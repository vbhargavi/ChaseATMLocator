package com.example.laxmibhargavivaditala.chaseatmlocator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.laxmibhargavivaditala.chaseatmlocator.Adapter.ATMLocationAdapter;
import com.example.laxmibhargavivaditala.chaseatmlocator.Modal.ChaseATMResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;

/**
 * ATMLocatorActivity is responsbile for getting users current location and displaying list of atms near by the users location.
 */
public class ATMLocatorActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LoaderManager.LoaderCallbacks<ChaseATMResponse> {
    private static final int REQUEST_SETTINGS = 111;
    private static final int REQUEST_LOCATION = 222;

    private ProgressBar mProgressBar;
    private TextView mEmptyTxtView;
    private RecyclerView mRecyclerView;

    /**
     * Provides the entry point to Google Play services.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Save the current location of the user.
     */
    private Location mCurrentLocation;
    private ChaseATMResponse mChaseATMResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        initToolbar();
        loadResources();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Build the GoogleApiClient
     */
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     * Initialize the toolbar and set it as action bar. Also sets the title.
     */
    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
        }
    }

    /**
     * Load all the resources in this activity view.
     */
    private void loadResources() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mEmptyTxtView = (TextView) findViewById(R.id.empty_text_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ATMLocatorActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    /**
     * This method makes a request to the API to get ATM mLocations data near the current location.
     */
    private void loadData() {
        if (mCurrentLocation == null) {
            showError(getString(R.string.location_not_available));
            return;
        }

        if (mChaseATMResponse == null) {
            //start the loader.
            getSupportLoaderManager().restartLoader(0, null, this);
        } else {
            setRecyclerView();
        }
    }

    @Override
    public Loader<ChaseATMResponse> onCreateLoader(int id, Bundle args) {
        return new ATMLocationsLoader(ATMLocatorActivity.this, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
    }

    @Override
    public void onLoadFinished(Loader<ChaseATMResponse> loader, ChaseATMResponse data) {
        getSupportLoaderManager().destroyLoader(0);
        mChaseATMResponse = data;
        //Data loading finished.
        if (data != null && data.locations != null && data.locations.size() > 0) {
            setRecyclerView();
        } else {
            //No results found
            showError(getString(R.string.no_atms));
        }
    }

    /**
     * Create adapter for recyclerview and set it.
     */
    private void setRecyclerView() {
        mProgressBar.setVisibility(View.GONE);
        ATMLocationAdapter atmLocationAdapter = new ATMLocationAdapter(ATMLocatorActivity.this, mChaseATMResponse.locations, new ATMLocationAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(ChaseATMResponse.Location location) {
                Intent intent = new Intent(ATMLocatorActivity.this, ATMDetailActivity.class);
                intent.putExtra(ATMDetailActivity.EXTRA_ATM, location);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(atmLocationAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<ChaseATMResponse> loader) {

    }

    private void showError(String errorMsg) {
        mEmptyTxtView.setText(errorMsg);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mEmptyTxtView.setVisibility(View.VISIBLE);
    }

    /**
     * This method first checks to see if location permission has been granted. If not we'll show request permission dialogue.
     * If location permission is available and location settings is not turned on, it'll show dialog to switch on location in settings.
     */
    @RequiresPermission(
            anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}
    )
    @SuppressWarnings("MissingPermission")
    private void getLocation() {
        mProgressBar.setVisibility(View.VISIBLE);
        mEmptyTxtView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

        if (hasLocationPermission()) {
            final LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    final Status status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            //Resolution Not required. Get the location.
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    mCurrentLocation = location;
                                    loadData();
                                }
                            });
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                status.startResolutionForResult(ATMLocatorActivity.this, REQUEST_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way
                            // to fix the settings so we won't show the dialog.
                            showError(getString(R.string.location_settings_error));
                            break;
                    }
                }
            });
        } else {
            showLocationPermissionDialog(REQUEST_LOCATION);
        }
    }

    /**
     * Called when user responds to permission dialog.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @SuppressWarnings("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Location permission has been granted. We should get location now.
                getLocation();
            } else {
                showError(getString(R.string.no_permission));
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SETTINGS) {
            if (resultCode == RESULT_OK) {
                //Settings has been turned on. Now we can get the location.
                getLocation();
            } else {
                //Settings has not be turned on. Display error.
                showError(getString(R.string.location_setting_off_error));
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Google play services is connected now. Get the current location.
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Google play services is suspended.
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showError(getString(R.string.location_not_available));
    }

    /**
     * Checks to see if Location permission is granted by the user.
     *
     * @return true if granted permission.
     */
    private boolean hasLocationPermission() {
        return !(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Show the dialog to ask user to give permission to access location.
     */
    private void showLocationPermissionDialog(int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
    }

    /**
     * This AsyncTaskLoader class is used to make API call to ATM Location Api in a separate thread.
     */
    public static class ATMLocationsLoader extends AsyncTaskLoader<ChaseATMResponse> {
        private double mLatitude;
        private double mLongitude;

        public ATMLocationsLoader(Context context, double latitude, double longitude) {
            super(context);
            mLatitude = latitude;
            mLongitude = longitude;
        }

        @Override
        public ChaseATMResponse loadInBackground() {
            try {
                return ServiceManager.searchATMs(mLatitude, mLongitude);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }
}
