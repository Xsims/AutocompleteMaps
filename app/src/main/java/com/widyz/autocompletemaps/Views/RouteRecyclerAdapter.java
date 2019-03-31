package com.widyz.autocompletemaps.Views;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.maps.model.DirectionsRoute;
import com.widyz.autocompletemaps.R;
import java.util.ArrayList;
import java.util.List;

public class RouteRecyclerAdapter extends RecyclerView.Adapter<RouteRecyclerViewHolder> {
  private List<DirectionsRoute> routeList;

  public RouteRecyclerAdapter(List<DirectionsRoute> routeList) {
    this.routeList = routeList;
  }

  @NonNull @Override
  public RouteRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.road_item, viewGroup, false);

    return new RouteRecyclerViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull RouteRecyclerViewHolder routeRecyclerViewHolder, int position) {
      routeRecyclerViewHolder.updateWithRoads(this.routeList.get(position));
  }

  public void swap(List<DirectionsRoute> routes)
  {
    routeList.clear();
    routeList.addAll(routes);
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return routeList.size();
  }

  public DirectionsRoute getRouteItem(int position) { return this.routeList.get(position); }
}
