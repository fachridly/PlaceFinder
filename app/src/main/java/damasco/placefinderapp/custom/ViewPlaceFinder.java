package damasco.placefinderapp.custom;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.habibmustofa.gmapstools.DirectionDrawer;
import android.habibmustofa.gmapstools.util.DirectionUtils;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import damasco.placefinderapp.MapsActivity;
import damasco.placefinderapp.R;
import damasco.placefinderapp.Utils.GsonRequest;
import damasco.placefinderapp.Utils.Helper;
import damasco.placefinderapp.Utils.VolleySingleton;
import damasco.placefinderapp.algoritma.Haversine;
import damasco.placefinderapp.dao.DaoFasilitas;
import damasco.placefinderapp.dao.DaoSubKategori;
import damasco.placefinderapp.dao.DirectionObject;
import damasco.placefinderapp.dao.LegsObject;
import damasco.placefinderapp.dao.PolylineObject;
import damasco.placefinderapp.dao.RouteObject;
import damasco.placefinderapp.dao.StepsObject;
import damasco.placefinderapp.entity.Fasilitas;
import damasco.placefinderapp.entity.SubKategori;

/**
 * Created by Habib Mustofa on 12/11/2017.
 */

public class ViewPlaceFinder implements DirectionDrawer.DirectionDrawerListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = ViewPlaceFinder.class.getSimpleName();
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 100;
    private Dialog dialog;
    private Context context;
    private GoogleMap map;
    private LatLng myLocation;
    private LatLng destination;
    private String selectedFasilitas;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double latitudeValue = 0.0;
    private double longitudeValue = 0.0;


    public ViewPlaceFinder(Context context, GoogleMap map, LatLng myLocation) {
        this.context = context;
        this.map = map;
        this.myLocation = myLocation;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        setup();
    }

    private void setup() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        AutoCompleteTextView text = new AutoCompleteTextView(context);
        setupSuggestion(context, text);
        text.setHint("Ketik kata kunci (ATM, Pasar, Masjid, dll)");
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(30, 20, 30, 0);
        layout.addView(text, p);
        dialog = Popup.create(context).setView(layout).create();
        Window window = dialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
    }

    private void setupSuggestion(Context context, AutoCompleteTextView textView) {
        try {
            DaoSubKategori dao = new DaoSubKategori(context);
            DaoFasilitas daoFasilitas = new DaoFasilitas(context);
            List<SubKategori> f = dao.findAllSubKategori();
            String[] data = new String[f.size()];
            for (int i = 0; i < data.length; i++) {
                data[i] = f.get(i).getName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, data);
            textView.setSingleLine(true);
            textView.setAdapter(adapter);
            textView.setOnItemClickListener((parent, view, position, id) -> {
                String selected = parent.getItemAtPosition(position).toString();
                if (myLocation != null) calc(daoFasilitas, selected);
                dialog.dismiss();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calc(DaoFasilitas daoFasilitas, String selected) {
        List<Fasilitas> fasilitas = daoFasilitas.findAllFasilitas(selected);
        if (fasilitas.size() <= 0) {
            Popup.show(context, "Peringatan", "Tidak di temukan hasil untuk kata kunci \"" + selected
                    + "\"! Data mungkin belum ada pada sistem.");
            dialog.dismiss();
        } else {
            selectedFasilitas = selected;
            List<Result> results = new ArrayList<>();
            for (Fasilitas f : fasilitas) {
                results.add(new Result(f.getId(), Haversine.distance(myLocation, f.getLatitude(), f.getLongitude())));
            }
            Collections.sort(results, (o1, o2) -> Double.compare(o1.distance, o2.distance));
            // TODO: Possible throw error. Change 2 dynamically.
            for (int i = 2; i < results.size(); i++) {
                results.remove(i);
            }
            String[] ids = new String[results.size()];
            for (int i = 0; i < results.size(); i++) {
                ids[i] = results.get(i).id;
            }
            fasilitas = daoFasilitas.findAllFasilitasById(ids);
            Log.w(TAG, "calc: Nearby Fasilitas count: " + fasilitas.size());
            map.clear();
            for (Fasilitas f : fasilitas) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(f.getNama())
                        .position(new LatLng(f.getLatitude(), f.getLongitude()));
                Marker marker = map.addMarker(markerOptions);
                marker.setTag(0);
            }
            Result resultMin = Collections.min(results, (o1, o2) -> Double.compare(o1.distance, o2.distance));
            for (Fasilitas f : fasilitas) {
                if (f.getId().equalsIgnoreCase(resultMin.id)) {
                    destination = new LatLng(f.getLatitude(), f.getLongitude());
                    //noinspection UnnecessaryContinue
                    continue;
                }
            }
            if (destination != null) {
                String directionAPIPath = Helper.getUrl(myLocation, destination);
                getDirectionFromDirectionApiServer(directionAPIPath);
                //DirectionDrawer drawer = new DirectionDrawer(context, map, myLocation, destination);
                //drawer.setDirectionDrawerListener(this);
                //drawer.setPolylineColor(Color.BLACK);
                //drawer.draw();
            }
        }
    }

    @Override
    public void onReady(Polyline polyline, DirectionUtils directionUtils) {
        Log.d(TAG, "onReady: test");
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f));
        String message = "Lokasi " + selectedFasilitas + " terdekat dari lokasi anda.\n" +
                "Jarak: " + directionUtils.getDistanceText() + "\n" +
                "Waktu: " + directionUtils.getDurationText();

        new Handler().postDelayed(() -> {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 16f));
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            pushNotification(selectedFasilitas + " Jarak: " + directionUtils.getDistanceText()
                    + " Waktu: " + directionUtils.getDurationText());
        }, 1500);
    }

    private void pushNotification(String message) {
        //noinspection deprecation
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(message).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.notify(001, mBuilder.build());
        }
    }

    @Override
    public void onFailed(String s) {
        Toast.makeText(context, "Gagal membuat garis jalur! " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "Connection method has been called");
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            assignLocationValues(mLastLocation);
                        } else {
                            //ActivityCompat.requestPermissions(, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private class Result {
        private String id;
        private Double distance;

        Result(String id, Double distance) {
            this.id = id;
            this.distance = distance;
        }
    }

    public void start() {
        this.dialog.show();
    }

    private void getDirectionFromDirectionApiServer(String url) {
        GsonRequest<DirectionObject> serverRequest = new GsonRequest<DirectionObject>(
                Request.Method.GET,
                url,
                DirectionObject.class,
                createRequestSuccessListener(),
                createRequestErrorListener());
        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(serverRequest);
    }

    private Response.Listener<DirectionObject> createRequestSuccessListener() {
        return new Response.Listener<DirectionObject>() {
            @Override
            public void onResponse(DirectionObject response) {
                try {
                    Log.d("JSON Response", response.toString());
                    if (response.getStatus().equals("OK")) {
                        List<LatLng> mDirections = getDirectionPolylines(response.getRoutes());
                        drawRouteOnMap(map, mDirections);
                    } else {
                        Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };
    }

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions) {
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.addAll(positions);
        Polyline polyline = map.addPolyline(options);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(positions.get(1).latitude, positions.get(1).longitude))
                .zoom(17)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private List<LatLng> getDirectionPolylines(List<RouteObject> routes) {
        List<LatLng> directionList = new ArrayList<LatLng>();
        for (RouteObject route : routes) {
            List<LegsObject> legs = route.getLegs();
            for (LegsObject leg : legs) {
                List<StepsObject> steps = leg.getSteps();
                for (StepsObject step : steps) {
                    PolylineObject polyline = step.getPolyline();
                    String points = polyline.getPoints();
                    List<LatLng> singlePolyline = decodePoly(points);
                    for (LatLng direction : singlePolyline) {
                        directionList.add(direction);
                    }
                }
            }
        }
        return directionList;
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void assignLocationValues(Location currentLocation) {
        if (currentLocation != null) {
            latitudeValue = currentLocation.getLatitude();
            longitudeValue = currentLocation.getLongitude();
            Log.d(TAG, "Latitude: " + latitudeValue + " Longitude: " + longitudeValue);
            markStartingLocationOnMap(map, new LatLng(latitudeValue, longitudeValue));
            addCameraToMap(new LatLng(latitudeValue, longitudeValue));
        }
    }

    private void addCameraToMap(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(16)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    private void markStartingLocationOnMap(GoogleMap mapObject, LatLng location) {
        mapObject.addMarker(new MarkerOptions().position(location).title("Current location"));
        mapObject.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}
