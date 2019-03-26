package com.widyz.autocompletemaps.Controllers.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.widyz.autocompletemaps.R;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    this.configureToolbar();
    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(view -> {
      Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
      startActivity(intent);
    });
  }

  private void configureToolbar(){
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

}
