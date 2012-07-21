package com.example.realliferpg;

import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MainActivity extends MapActivity {
	MapView mapView;
	
	ArrowOverlay myLocationOverlay;
	
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
        
        GeoPoint point = new GeoPoint(55734600, 37586160);
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.ic_launcher);
        MapView.LayoutParams lp = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, 
                MapView.LayoutParams.WRAP_CONTENT, point, 0, 0, 
                MapView.LayoutParams.CENTER);

        mapView.addView(iv, lp);
        
        
        myLocationOverlay.setAim(point);
        
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