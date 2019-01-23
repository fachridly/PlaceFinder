package damasco.placefinderapp.algoritma;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Habib Mustofa on 12/11/2017.
 */

public class Haversine {

    public static final double R = 6372.8; // In kilometers

    public static double distance(LatLng start, double endLat, double endLng) {
        return distance(start.latitude, start.longitude, endLat, endLng);
    }

    private static double distance(double startLat, double startLng, double endLat, double endLng) {
        double dLat = Math.toRadians(endLat - startLat);
        double dLon = Math.toRadians(endLng - startLng);
        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(startLat) * Math.cos(endLat);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
