package com.widyz.autocompletemaps.Views;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.widyz.autocompletemaps.R;
import java.util.ArrayList;

public class RouteRecyclerViewHolder extends RecyclerView.ViewHolder {
  private View view;
  private TextView arrivee;
  private TextView depart;
  private TextView time;
  //private ImageView travelIcon1;

  public RouteRecyclerViewHolder(@NonNull View itemView) {
    super(itemView);
    view = itemView;
    arrivee = view.findViewById(R.id.item_road_list_arrive);
    depart = view.findViewById(R.id.item_road_list_depart);
    time = view.findViewById(R.id.item_road_list_time_travel);
    //travelIcon1 = view.findViewById(R.id.item_road_list_icon1);
  }

  public void updateWithRoads(DirectionsRoute routeItem){
    this.time.setText(routeItem.legs[0].duration.humanReadable);
    this.depart.setText(routeItem.legs[0].startAddress);
    this.arrivee.setText(routeItem.legs[0].endAddress);
    //this.travelIcon1.setImageResource(defineTravelModeIcon(routeItem.legs[0].steps));
    //this.image.setImageResource(courseItem.getImageId());
  }

 /* private int defineTravelModeIcon(DirectionsStep[] steps){
    int idIcon = R.drawable.ic_directions_walk_purple_700_24dp;
    for(DirectionsStep step : steps){
      switch(step.travelMode.toString()) {
        case "WALKING":
          idIcon = R.drawable.ic_directions_walk_purple_700_24dp;
          break;
        case "BUS":
          idIcon = R.drawable.ic_directions_walk_purple_700_24dp;
          break;
        case "WALKING":
          idIcon = R.drawable.ic_directions_walk_purple_700_24dp;
          break;
        case "WALKING":
          idIcon = R.drawable.ic_directions_walk_purple_700_24dp;
          break;
      }
    }
    return idIcon;
  }*/

}
