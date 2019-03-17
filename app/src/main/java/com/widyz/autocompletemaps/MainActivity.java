package com.widyz.autocompletemaps;


import afu.org.checkerframework.checker.nullness.qual.Nullable;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  int AUTOCOMPLETE_REQUEST_CODE_DEPART = 1;
  int AUTOCOMPLETE_REQUEST_CODE_ARRIVEE = 2;
  private TextView responseView;
  private Button departButton;
  private Button arriveButton;

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
    departButton = findViewById(R.id.main_activity_button_depart);
    arriveButton = findViewById(R.id.main_activity_button_arrivee);
    // Set listeners for Autocomplete activity
    findViewById(R.id.main_activity_button_depart)
        .setOnClickListener(view -> startAutocompleteActivity(AUTOCOMPLETE_REQUEST_CODE_DEPART));

    findViewById(R.id.main_activity_button_arrivee)
        .setOnClickListener(view -> startAutocompleteActivity(AUTOCOMPLETE_REQUEST_CODE_ARRIVEE));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
    if (requestCode == AUTOCOMPLETE_REQUEST_CODE_DEPART) {
      if (resultCode == AutocompleteActivity.RESULT_OK) {
        Place place = Autocomplete.getPlaceFromIntent(intent);
        responseView.setText(
            StringUtil.stringifyAutocompleteWidget(place, true));
        departButton.setTextColor(Color.BLACK);
        departButton.setText(place.getName());

      } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
        Status status = Autocomplete.getStatusFromIntent(intent);
        responseView.setText(status.getStatusMessage());
      } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
        // The user canceled the operation.
      }
    }
    if (requestCode == AUTOCOMPLETE_REQUEST_CODE_ARRIVEE) {
      if (resultCode == AutocompleteActivity.RESULT_OK) {
        Place place = Autocomplete.getPlaceFromIntent(intent);
        responseView.setText(
            StringUtil.stringifyAutocompleteWidget(place, true));
        arriveButton.setTextColor(Color.BLACK);
        arriveButton.setText(place.getName());


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
        new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this);
    startActivityForResult(autocompleteIntent, autocomplete_request_code);
  }

}
