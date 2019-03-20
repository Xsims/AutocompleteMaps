package com.widyz.autocompletemaps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.widget.Button;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import afu.org.checkerframework.checker.nullness.qual.Nullable;

import android.graphics.Color;

import android.widget.TextView;

import com.google.android.gms.common.api.Status;

import com.google.android.libraries.places.api.Places;

import android.content.Intent;


import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity
    implements
    OnMyLocationButtonClickListener,
    OnMyLocationClickListener,
    OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

  /**
   * Request code for location permission request.
   *
   * @see #onRequestPermissionsResult(int, String[], int[])
   */
  private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

  /**
   * Flag indicating whether a requested permission has been denied after returning in
   * {@link #onRequestPermissionsResult(int, String[], int[])}.
   */
  private boolean mPermissionDenied = false;
  int AUTOCOMPLETE_REQUEST_CODE_DEPART = 1;
  int AUTOCOMPLETE_REQUEST_CODE_ARRIVEE = 2;
  private TextView responseView;
  private TextView textView;
  private Button departButton;
  private Button arriveButton;
  private Place placeDepart;
  private String nomDepart;
  private String nomArrive;
  private Place placeArrivee;
  private GoogleMap mMap;
  private ArrayList<LatLng> tabLatlng = new ArrayList<>();



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);



    String apiKey = getString(R.string.places_api_key);

    if (apiKey.equals("")) {
      Toast.makeText(this, getString(R.string.error_api_key), Toast.LENGTH_LONG).show();
      return;
    }
    // Setup Places Client
    if (!Places.isInitialized()) {
      Places.initialize(getApplicationContext(), apiKey);
    }
    responseView = findViewById(R.id.main_activity_response_view);
    textView = findViewById(R.id.main_activity_response_itinerary);
    departButton = findViewById(R.id.main_activity_button_depart);
    arriveButton = findViewById(R.id.main_activity_button_arrivee);

    SupportMapFragment mapFragment =
        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    assert mapFragment != null;
    mapFragment.getMapAsync(this);

    // Set listeners for Autocomplete activity
    findViewById(R.id.main_activity_button_depart)
        .setOnClickListener(view -> startAutocompleteActivity(AUTOCOMPLETE_REQUEST_CODE_DEPART));

    findViewById(R.id.main_activity_button_arrivee)
        .setOnClickListener(view -> startAutocompleteActivity(AUTOCOMPLETE_REQUEST_CODE_ARRIVEE));
    findViewById(R.id.main_activity_button_itinerary)
        .setOnClickListener(view -> getItinerary(nomDepart, nomArrive));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
    if (requestCode == AUTOCOMPLETE_REQUEST_CODE_DEPART) {
      if (resultCode == AutocompleteActivity.RESULT_OK) {
        placeDepart = Autocomplete.getPlaceFromIntent(intent);
        departButton.setTextColor(Color.BLACK);
        departButton.setText(placeDepart.getName());
        nomDepart = placeDepart.getName();

      } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
        Status status = Autocomplete.getStatusFromIntent(intent);
        responseView.setText(status.getStatusMessage());
      } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
        // The user canceled the operation.
      }
    }
    if (requestCode == AUTOCOMPLETE_REQUEST_CODE_ARRIVEE) {
      if (resultCode == AutocompleteActivity.RESULT_OK) {
        placeArrivee = Autocomplete.getPlaceFromIntent(intent);
        arriveButton.setTextColor(Color.BLACK);
        arriveButton.setText(placeArrivee.getName());
        nomArrive = placeArrivee.getName();


      } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
        Status status = Autocomplete.getStatusFromIntent(intent);
        responseView.setText(status.getStatusMessage());
      } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
        // The user canceled the operation.
      }
    }

    // Required because this class extends AppCompatActivity which extends FragmentActivity
    // which implements this method to pass onActivityResult calls to child fragments
    // (eg AutocompleteFragment).
    super.onActivityResult(requestCode, resultCode, intent);
  }

  private void startAutocompleteActivity(int autocomplete_request_code) {
    // Set the fields to specify which types of place data to
    // return after the user has made a selection.
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

    Intent autocompleteIntent =
        new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(this);
    startActivityForResult(autocompleteIntent, autocomplete_request_code);
  }

  //Create itinerary with the addresses entered by the user
  private void getItinerary(String depart, String arrive){
    DateTime now = new DateTime();
    try {
      DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
          .mode(TravelMode.TRANSIT)
          .origin(depart)
          .destination(arrive)
          .departureTime(now)
          .await();

      addPolyline( result, mMap);
    } catch (ApiException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onMapReady(GoogleMap map) {
    mMap = map;

    mMap.setOnMyLocationButtonClickListener(this);
    mMap.setOnMyLocationClickListener(this);
    enableMyLocation();

    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
      @Override
      public void onMapClick(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng));
        tabLatlng.add(new LatLng(latLng.latitude, latLng.longitude));
      }
    });
  }

  /**
   * Enables the My Location layer if the fine location permission has been granted.
   */
  private void enableMyLocation() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      // Permission to access the location is missing.
    } else if (mMap != null) {
      // Access to the location has been granted to the app.
      mMap.setMyLocationEnabled(true);
      // tabLatlng.add(new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude()));

    }
  }

  @Override
  public boolean onMyLocationButtonClick() {
    Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
    // Return false so that we don't consume the event and the default behavior still occurs
    // (the camera animates to the user's current position).
    return false;
  }

  @Override
  public void onMyLocationClick(@NonNull Location location) {
    Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
      return;
    }
    enableMyLocation();

  }

  @Override
  protected void onResumeFragments() {
    super.onResumeFragments();
    if (mPermissionDenied) {
      // Permission was not granted, display error dialog.
      showMissingPermissionError();
      mPermissionDenied = false;
    }
  }

  /**
   * Displays a dialog with error message explaining that the location permission is missing.
   */
  private void showMissingPermissionError() {

  }

  private GeoApiContext getGeoContext() {
    GeoApiContext geoApiContext = new GeoApiContext();
    return geoApiContext.setQueryRateLimit(3)
        .setApiKey(getString(R.string.places_api_key))
        .setConnectTimeout(1, TimeUnit.MINUTES)
        .setReadTimeout(1, TimeUnit.MINUTES)
        .setWriteTimeout(1, TimeUnit.MINUTES);
  }

  private void addPolyline(DirectionsResult results, GoogleMap mMap) {
    List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
    mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
  }




}
