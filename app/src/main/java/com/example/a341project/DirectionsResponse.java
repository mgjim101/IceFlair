package com.example.a341project;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DirectionsResponse {

    @SerializedName("routes")
    public List<Route> routes;

    public static class Route {
        @SerializedName("overview_polyline")
        public Polyline overviewPolyline;
    }

    public static class Polyline {
        @SerializedName("points")
        public String points;
    }

    public String getPolyline() {
        if (routes != null && !routes.isEmpty()) {
            return routes.get(0).overviewPolyline.points;
        }
        return null;
    }
}
