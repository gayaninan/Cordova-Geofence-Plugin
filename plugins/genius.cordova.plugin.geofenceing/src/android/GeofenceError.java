package genius.cordova.plugin.geofencing;

      import android.content.Context;
        import android.content.res.Resources;

        import com.google.android.gms.location.GeofenceStatusCodes;

public class GeofenceError{

    private GeofenceError() {}

    public static String getErrorString(Context context, int errorCode) {
        Resources mResources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence is not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown geofencing error";
        }
    }
}