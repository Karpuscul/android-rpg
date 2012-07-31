package com.example.realliferpg;

import java.util.LinkedList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MainActivity extends MapActivity {
	MapView mapView;
	AimUpdater aimUpdater;
	ArrowOverlay myLocationOverlay;
	
	private String getUsername() {
	    AccountManager manager = AccountManager.get(this); 
	    Account[] accounts = manager.getAccountsByType("com.google"); 
	    List<String> possibleEmails = new LinkedList<String>();

	    for (Account account : accounts) {
	    	// TODO: Check possibleEmail against an email regex or treat
	    	// account.name as an email address only for certain account.type values.
	    	possibleEmails.add(account.name);
	    }

	    if(!possibleEmails.isEmpty() && possibleEmails.get(0) != null){
	        String email = possibleEmails.get(0);
	        String[] parts = email.split("@");
	        if(parts.length > 0 && parts[0] != null)
	            return parts[0];
	        else
	            return null;
	    }else
	        return null;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mapView = ( ( MapView )findViewById(R.id.mapview1) );
        mapView.setBuiltInZoomControls(true);
        mapView.getController().setZoom(18);
        
        myLocationOverlay = new ArrowOverlay(this, this, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableCompass();

        aimUpdater = new AimUpdater( myLocationOverlay, getUsername() );
        aimUpdater.start();
        
        myLocationOverlay.runOnFirstFix(new Runnable() {
        	public void run() {
        		GeoPoint point = myLocationOverlay.getMyLocation();
        		mapView.getController().animateTo(point);
       	 	}
        });
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}