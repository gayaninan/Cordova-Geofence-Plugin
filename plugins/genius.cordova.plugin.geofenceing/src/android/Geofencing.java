package genius.cordova.plugin.geofencing;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaInterface;
import android.provider.Settings;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class Geofencing extends CordovaPlugin implements
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status>{
    private static CordovaWebView webView = null;
	private float lat,lang;
    LocationListener locationListener;
    LocationManager locationManager;

    protected static final String TAG = "geofence-cordova-plugin-add-remove-geofences";

    public static final String PACKAGE_NAME = "com.ram.Geofence";
    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =24 * 60 * 60 * 1000;

    protected GoogleApiClient mGoogleApiClient;
    protected ArrayList<Geofence> mGeofenceList;
    private boolean mGeofencesAdded;
    private PendingIntent mGeofencePendingIntent;
    private SharedPreferences mSharedPreferences;
	public Context context;

    public Geofencing() {
		
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        
		Geofence.webView=webView;
        Log.v(TAG,"Init CoolPlugin");
		context=cordova.getActivity();
		mGeofenceList = new ArrayList<Geofence>();
        mGeofencePendingIntent = null;
		
        mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE);
        mGeofencesAdded = mSharedPreferences.getBoolean(GEOFENCES_ADDED_KEY, false);
        buildGoogleApiClient();
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("vibrate")) {
            this.vibrate(args.getLong(0));

            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), "vibrate",Toast.LENGTH_SHORT );
                    toast.show();
                }
            });
        }
        else if (action.equals("vibrateWithPattern")) {
            JSONArray pattern = args.getJSONArray(0);
            int repeat = args.getInt(1);
            //add a 0 at the beginning of pattern to align with w3c
            long[] patternArray = new long[pattern.length()+1];
            patternArray[0] = 0;
            for (int i = 0; i < pattern.length(); i++) {
                patternArray[i+1] = pattern.getLong(i);
            }
            this.vibrateWithPattern(patternArray, repeat);
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), "vibrate",Toast.LENGTH_SHORT );
                    toast.show();
                }
            });
        }
        else if (action.equals("cancelVibration")) {
            this.cancelVibration();
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), "cancel vibrate",Toast.LENGTH_SHORT );
                    toast.show();
                }
            });

        }
        else {
            return false;
        }

        // Only alert and confirm are async.
        callbackContext.success();

        return true;
    }

    //--------------------------------------------------------------------------
    // LOCAL METHODS
    //--------------------------------------------------------------------------

    /**
     * Vibrates the device for a given amount of time.
     *
     * @param time      Time to vibrate in ms.
     */
    public void vibrate(long time) {
        // Start the vibration, 0 defaults to half a second.
        if (time == 0) {
            time = 500;
        }
        Vibrator vibrator = (Vibrator) this.cordova.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }

    /**
     * Vibrates the device with a given pattern.
     *
     * @param pattern     Pattern with which to vibrate the device.
     *                    Pass in an array of longs that
     *                    are the durations for which to
     *                    turn on or off the vibrator in
     *                    milliseconds. The first value
     *                    indicates the number of milliseconds
     *                    to wait before turning the vibrator
     *                    on. The next value indicates the
     *                    number of milliseconds for which
     *                    to keep the vibrator on before
     *                    turning it off. Subsequent values
     *                    alternate between durations in
     *                    milliseconds to turn the vibrator
     *                    off or to turn the vibrator on.
     *
     * @param repeat      Optional index into the pattern array at which
     *                    to start repeating, or -1 for no repetition (default).
     */
    public void vibrateWithPattern(long[] pattern, int repeat) {
        Vibrator vibrator = (Vibrator) this.cordova.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, repeat);
        alertUI();
    }

    /**
     * Immediately cancels any currently running vibration.
     */
    public void cancelVibration() {
        Vibrator vibrator = (Vibrator) this.cordova.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();
    }

    public static void alertUI(){
            String js = "setTimeout('alert();',0)";
        if (webView == null) {
            Log.d(TAG, "Webview is null");
        } else {
            webView.sendJavascript("alert('from native side');");
        }
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void locationRequestEnable(long updateSeconds,int updateDistance){
        locationManager=(LocationManager)this.cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, "Location Changed");
                lat= (float) location.getLatitude();
                lang= (float) location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,updateSeconds,updateDistance,locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,updateSeconds,updateDistance,locationListener);
    }

    public void locationRequestDisable(){
        locationManager.removeUpdates(locationListener);
        locationManager=null;
        locationListener=null;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.cordova.getActivity())
                .addConnectionCallbacks(this.cordova.getActivity())
                .addOnConnectionFailedListener(this.cordova.getActivity())
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    public void addGeofencesButtonHandler(View view) {
        addOrUpdateGeofence("frontgate", 6.7953759f,79.9008272f, 200.0f,GEOFENCE_EXPIRATION_IN_MILLISECONDS,3);
        locationRequestEnable(0,10);
    }

    public void removeGeofencesButtonHandler(View view) {
        ArrayList rmlist=new ArrayList<String>();
        rmlist.add("frontgate");
        removeGeofences(rmlist);
        locationRequestDisable();
    }


    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = true;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.commit();

            /*Toast.makeText(
                    this,
                    getString(mGeofencesAdded ? "Geofences added" :
                            "Geofences removed"),
                    Toast.LENGTH_SHORT
            ).show();*/
        } else {
            String errorMessage = GeofenceError.getErrorString(this.cordova.getActivity(),
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this.cordova.getActivity(), GeofenceTransitionMonitorService.class);
        return PendingIntent.getService(this.cordova.getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void createList(String key,float latitude,float longitude,float radius,long expireDuration,int event){
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(
                        latitude,
                        longitude,
                        radius
                )
                .setExpirationDuration(expireDuration)
                .setTransitionTypes(event)
                .build());
    }



    public void addOrUpdateGeofence(String key,float latitude,float longitude,float radius,long expireDuration,int event){

        createList(key,latitude,longitude,radius,expireDuration,event);

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this.cordova.getActivity(), "Geofence Api not Connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this.cordova.getActivity()); // Result processed in onResult().
        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }
    }

    public void removeGeofences(List<String> id){
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this.cordova.getActivity(), "Geofence Api not Connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    id
            ).setResultCallback(this.cordova.getActivity()); // Result processed in onResult().
        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }
    }


    public boolean serviceOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.cordova.getActivity());
        if (isAvailable == ConnectionResult.SUCCESS) {

            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Toast.makeText(this.cordova.getActivity(),"User Recoverble Error in Geofence Plugin",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.cordova.getActivity(), "Can't Connect to Google Play Services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}