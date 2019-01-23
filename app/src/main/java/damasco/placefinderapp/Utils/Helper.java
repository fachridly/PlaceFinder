package damasco.placefinderapp.Utils;

import com.google.android.gms.maps.model.LatLng;

public class Helper {
    private static final String DIRECTION_API = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    public static final String API_KEY = "AIzaSyAAB2oAECsBem_qnPag3H1XHkDEHnlAbM4";
    public static final int MY_SOCKET_TIMEOUT_MS = 5000;

    public static String getUrl(LatLng start, LatLng end){
        String originLat = String.valueOf(start.latitude);
        String originLon = String.valueOf(start.longitude);
        String destinationLat = String.valueOf(end.latitude);
        String destinationLon = String.valueOf(end.longitude);
        return Helper.DIRECTION_API + originLat+","+originLon+"&destination="+destinationLat+","+destinationLon+"&key="+API_KEY;
    }
}
