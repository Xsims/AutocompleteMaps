package com.widyz.autocompletemaps.Controllers.Activities;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TransitMode;
import com.google.maps.model.TravelMode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.widget.Button;
import android.widget.Toast;

import com.widyz.autocompletemaps.R;
import java.util.ArrayList;
import org.joda.time.DateTime;

import java.io.IOException;
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


public class AddAlarmActivity extends AppCompatActivity {
  //Directions parameters
  private static final String LANGUAGE_RESULT_FRANCAIS = "fr";
  private static final boolean ENABLE_ALTERNATIVE_ROUTES = true;
  //Request code for Autocomplete Activity
  int AUTOCOMPLETE_REQUEST_CODE_DEPART = 1;
  int AUTOCOMPLETE_REQUEST_CODE_ARRIVEE = 2;
  //Layout Element
  @BindView(R.id.main_activity_response_view) TextView responseView;
  @BindView(R.id.main_activity_button_depart) Button departButton;
  @BindView(R.id.main_activity_button_arrivee) Button arriveButton;

  private String nomDepart, nomArrive = "";




  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_alarm);
    ButterKnife.bind(this);
    String apiKey = getString(R.string.google_maps_key);

    if (apiKey.equals("")) {
      Toast.makeText(this, getString(R.string.error_api_key), Toast.LENGTH_LONG).show();
      return;
    }
    // Setup Places Client
    if (!Places.isInitialized()) {
      Places.initialize(getApplicationContext(), apiKey);
    }
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
        Place placeDepart = Autocomplete.getPlaceFromIntent(intent);
        departButton.setTextColor(Color.BLACK);
        departButton.setText(placeDepart.getName());
        nomDepart = placeDepart.getName();
        if(nomArrive.equals("")){
        } else {
          getItinerary(nomDepart, nomArrive);
        }

      } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
        Status status = Autocomplete.getStatusFromIntent(intent);
        responseView.setText(status.getStatusMessage());
      } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
        // The user canceled the operation.
      }
    }
    if (requestCode == AUTOCOMPLETE_REQUEST_CODE_ARRIVEE) {
      if (resultCode == AutocompleteActivity.RESULT_OK) {
        Place placeArrivee = Autocomplete.getPlaceFromIntent(intent);
        arriveButton.setTextColor(Color.BLACK);
        arriveButton.setText(placeArrivee.getName());
        nomArrive = placeArrivee.getName();
        if(nomDepart.equals("")){

        } else {
          getItinerary(nomDepart, nomArrive);
        }

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

  /*Enum<TransitMode> transitModeEnum = new Enum<TransitMode>("truc", 5) {
    @Override public String toString() {
      return super.toString();
    }
  };*/

  //Create itinerary with the addresses entered by the user
  private void getItinerary(String depart, String arrive){
    DateTime now = new DateTime();
    try {
      DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
          .mode(TravelMode.TRANSIT)
          .transitMode(TransitMode.RAIL, TransitMode.BUS)
          .alternatives(ENABLE_ALTERNATIVE_ROUTES)
          .language(LANGUAGE_RESULT_FRANCAIS)
          .origin(depart)
          .destination(arrive)
          .departureTime(now)
          .await();
      showDirectionsResult(result);
    } catch (ApiException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void showDirectionsResult(DirectionsResult result){
    responseView.setText(result.routes[0].legs[0].distance.humanReadable );
  }

  private GeoApiContext getGeoContext() {
    GeoApiContext geoApiContext = new GeoApiContext();
    return geoApiContext.setQueryRateLimit(3)
        .setApiKey(getString(R.string.google_maps_key))
        .setConnectTimeout(1, TimeUnit.MINUTES)
        .setReadTimeout(1, TimeUnit.MINUTES)
        .setWriteTimeout(1, TimeUnit.MINUTES);
  }


  private boolean isTransitmodeAllChecked(){
    return ((CheckBox) findViewById(R.id.toggle_button_all)).isChecked();
  }
  private boolean isTransitmodeBusChecked(){
    return ((CheckBox) findViewById(R.id.toggle_button_bus)).isChecked();
  }
  private boolean isTransitmodeSubwayChecked(){
    return ((CheckBox) findViewById(R.id.toggle_button_metro)).isChecked();
  }
  private boolean isTransitmodeTrainChecked(){
    return ((CheckBox) findViewById(R.id.toggle_button_train)).isChecked();
  }
  private boolean isTransitmodeTramChecked(){
    return ((CheckBox) findViewById(R.id.toggle_button_tram)).isChecked();
  }
}
