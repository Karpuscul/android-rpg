package com.example.realliferpg;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

@SuppressLint("ParserError")
public class MainActivity extends MapActivity {
	MapView mapView;
	
	public class ArrowOverlay extends MyLocationOverlay {
		GeoPoint aimPoint;
		float orientation = 0;
		
		public ArrowOverlay(android.content.Context context, MapView mapView)
		{
			super(context, mapView);
		}
		
		public void setAim( GeoPoint point )
		{
			aimPoint = point;
		}
		
	    @Override
	    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
	    	boolean ret = super.draw(canvas, mapView, shadow, when);
	    	
	        if (!shadow) {                                                       
	            if(getMyLocation() == null)
	            	return ret;
	            
	            Point point = new Point();
	            Point point2 = new Point();
	            mapView.getProjection().toPixels(getMyLocation(), point);
	            mapView.getProjection().toPixels(aimPoint, point2);

                float orientation = ( float )( Math.atan2(point2.y - point.y, point2.x - point.x) * 180 / Math.PI );
	            Log.e( "test", String.format( "Orientation: %d %d", getMyLocation().getLatitudeE6(), getMyLocation().getLongitudeE6()) );
                
	            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_blue_rounded_right);
	            Matrix transform = new Matrix();
	            
	            int x = point.x;
	            int y = point.y - bmp.getHeight() / 2;
	            transform.setRotate(orientation, 0, bmp.getHeight() / 2);
	            transform.postTranslate( x, y);

	            canvas.drawBitmap(bmp, transform, null);
	        }

	        return true;
	    }
	}
	
	ArrowOverlay myLocationOverlay;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mapView = ( ( MapView )findViewById(R.id.mapview1) );
        mapView.setBuiltInZoomControls(true);
        mapView.getController().setZoom(18);
        
        myLocationOverlay = new ArrowOverlay(this, mapView);
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
