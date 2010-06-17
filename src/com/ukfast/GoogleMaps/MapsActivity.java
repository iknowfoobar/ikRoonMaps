package com.ukfast.GoogleMaps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class MapsActivity extends MapActivity {
	private MapView mapView;
	private MapController mapController;
	private static final int SATELLITE_ID = 1;
	private static final int STREET_ID = 2;
	private static final int FIND_ME_ID = 3;
	private boolean isSatelliteView = false;
	private boolean isStreetView = false;
	private LocationManager locationManager;
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        
        mapController = mapView.getController();
    }
    
    @Override
    protected boolean isRouteDisplayed() {
    	return false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, SATELLITE_ID, 0, R.string.menu_satellite);
        menu.add(0, STREET_ID, 0, R.string.menu_street);
        menu.add(0, FIND_ME_ID, 0, R.string.find_me);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case SATELLITE_ID:
            	if(isSatelliteView == true) {
            		isSatelliteView = false;
            	} else {
            		isSatelliteView = true;
            	}
                
            	mapView.setSatellite(isSatelliteView);
                return true;
                
            case STREET_ID:
            	if(isStreetView == true) {
            		isStreetView = false;
            	} else {
            		isStreetView = true;
            	}
                mapView.setStreetView(isStreetView);
                return true;
            case FIND_ME_ID:
            	findMe();
            	return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

	private boolean findMe() {
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, false);
		Location location = null;
		String errorMes = "";
		
		if(bestProvider != null) {
			location = locationManager.getLastKnownLocation(bestProvider);
			errorMes = "You must turn on Network Location or GPS";
		}
		
		if(bestProvider != null && location != null) {
			Double lat = location.getLatitude() * 1E6;
			Double lng = location.getLongitude() * 1E6;
			GeoPoint point = new GeoPoint(lat.intValue(), lng.intValue());
			mapController.animateTo(point);
		} else {
			if(location.getProvider() == "network") {
				errorMes = ": " + getResources().getString(R.string.location_error_network);
			}
				
			if(location.getProvider() == "gps") {
				errorMes = ": " + getResources().getString(R.string.location_error_gps);
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Unable to find your location" + errorMes)
			.setCancelable(false)
			.setNeutralButton("OK", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();					
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		return true;
	}
}