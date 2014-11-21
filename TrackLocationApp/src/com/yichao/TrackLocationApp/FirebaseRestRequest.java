package com.yichao.TrackLocationApp;

import android.widget.TextView;
import com.firebase.client.*;
import com.yichao.TrackLocationApp.Model.Coordinate;
import com.yichao.TrackLocationApp.Model.Track;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yichaoyu on 11/20/14.
 */
public class FirebaseRestRequest {

    private final Firebase rootRef;

    public FirebaseRestRequest() {
        rootRef = new Firebase("https://trackapp.firebaseio.com/");
    }

    /**
     * REST API samples:
     * curl -X POST -d '{"user_id": "testuser1"}' https://trackapp.firebaseio.com/routes.json
     * curl -X POST -d '{"capture_ts": 1416528528, "lat": 33.847875, "lng": -58.480225}'
     *      https://trackapp.firebaseio.com/routes/-JbEfZwBCaWKIC5wdjYq/coordinates.json
     * curl -X PATCH -d '{"-JbEfZwBCaWKIC5wdjYq": {"friendlyName": "new track",
     *      "start_coordinate":{"lat": 33.847875, "lng": -58.480225, "capture_ts": 1416528528}}}'
     *      https://trackapp.firebaseio.com/tracks/testuser1.json
     *
     * @param userId
     * @param latLng
     * @return
     */
    public String startNewTrack(final String userId, final String[] latLng) {
        // create new route
        Map<String, String> route = new HashMap<String, String>();
        route.put("user_id", userId);
        final Firebase routesRef = rootRef.child("routes");
        String newRouteId = routesRef.push().getKey();
        routesRef.child(newRouteId).setValue(route);

        // insert the start coordinate
        Long timestamp = System.currentTimeMillis();
        Coordinate cooridate = new Coordinate(Double.valueOf(latLng[0]), Double.valueOf(latLng[1]), timestamp);
        createCoordiate(newRouteId, cooridate);

        // create a new track of this route for the user
        Track track = new Track();
        track.setStart(cooridate);
        final Firebase userTrackRef = rootRef.child("tracks/" + userId + '/' +newRouteId);
        userTrackRef.setValue(track);

        return newRouteId;
    }

    /**
     * REST API samples:
     * curl -X POST -d '{"capture_ts": 1416528528, "lat": 33.847875, "lng": -58.480225}'
     *      https://trackapp.firebaseio.com/routes/-JbEfZwBCaWKIC5wdjYq/coordinates.json
     * curl -X PATCH -d '{"end_coordinate":{"lat": 38.959456, "lng": -77.360374}, "end_ts": 1416528847}'
     *      https://trackapp.firebaseio.com/tracks/testuser1/-JbEfZwBCaWKIC5wdjYq.json
     *
     * @param userId
     * @param routeId
     * @param latLng
     */
    public void endCurrentTrack(final String userId, final String routeId, final String[] latLng) {
        Long timestamp = System.currentTimeMillis();
        Coordinate cooridate = new Coordinate(Double.valueOf(latLng[0]), Double.valueOf(latLng[1]), timestamp);
        // insert the end coordinate
        createCoordiate(routeId, cooridate);
        // insert the end coordinate
        final Firebase userTrackRef = rootRef.child("tracks/" + userId + '/' + routeId + "/end_coordinate");
        userTrackRef.setValue(cooridate);
    }

    /**
     * REST API samples:
     * curl -X POST -d '{"capture_ts": 1416528528, "lat": 33.847875, "lng": -58.480225}'
     *      https://trackapp.firebaseio.com/routes/-JbEfZwBCaWKIC5wdjYq/coordinates.json
     *
     * @param routeId
     * @param latLng
     */
    public void recordCoordinate(final String routeId, final String[] latLng) {
        Long timestamp = System.currentTimeMillis();
        Coordinate cooridate = new Coordinate(Double.valueOf(latLng[0]), Double.valueOf(latLng[1]), timestamp);
        createCoordiate(routeId, cooridate);
    }

    public void getRouteSummary(final TextView summary, final String routeId) {
        //Firebase ref = new Firebase("https://tardis.firebaseio.com/");
        // Get a reference to our posts
        Query routeRef = rootRef.child("routes/" + routeId).orderByKey();
        routeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                HashMap<String, Object> map = (HashMap) snapshot.getValue();
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, Object> e : map.entrySet()) {
                    sb.append(e.getKey())
                            .append(':')
                            .append(e.getValue() == null ? "" : e.getValue().toString());
                }
                summary.setText(sb.toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void createCoordiate(final String routeId, final Coordinate cooridate) {
        final Firebase coordinatesRef = rootRef.child("routes/" + routeId + "/coordinates");
        coordinatesRef.push().setValue(cooridate);
    }
}
