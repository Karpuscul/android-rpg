package com.example.realliferpg;

import java.util.SortedMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.location.Location;
import android.util.FloatMath;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class ArrowOverlay extends MyLocationOverlay {
	private final MainActivity mainActivity;
	private AlertDialog ad;
	private SortedMap< Integer, String > disttext = null;
	private int id = -1;
	private GeoPoint aimPoint = null;
	
	public ArrowOverlay(MainActivity mainActivity, android.content.Context context, MapView mapView)
	{
		super(context, mapView);
		this.mainActivity = mainActivity;
		
		ad = new AlertDialog.Builder(context).create();
    	ad.setCancelable(false);
    	ad.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
    	});
    	
	}
	
	public void setAim( GeoPoint point, SortedMap< Integer, String > disttext, int id )
	{
		this.aimPoint = point;
		this.disttext = disttext;
		this.id = id;
	}
	
	public int getAimId()
	{
		return id;
	}
	
	private void checkPointFound()
	{
        GeoPoint loc = getMyLocation();
        float[] d = new float[ 3 ];
        Location.distanceBetween(loc.getLatitudeE6()/1E6, loc.getLongitudeE6()/1E6, 
        		aimPoint.getLatitudeE6()/1E6, aimPoint.getLongitudeE6()/1E6, d);

        double distance = FloatMath.sqrt( d[ 0 ] * d[ 0 ] + d[ 1 ] * d[ 1 ] + d[ 2 ] * d[ 2 ] );
        if( distance < disttext.lastKey() && !ad.isShowing() )
        { 	
        	Log.e("distance", disttext.get( disttext.lastKey() ) );
        	ad.setMessage( disttext.get( disttext.lastKey() ) );  
        	ad.show();
        	
        	disttext.remove(disttext.lastKey());
        }
	}
	
	public boolean isAimFound()
	{
    	if( aimPoint == null || disttext == null )
    		return false;
    	
		return disttext.isEmpty();
	}
	
	public void clearAim()
	{
		aimPoint = null;
		disttext = null;
	}
	
    @Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
    	boolean ret = super.draw(canvas, mapView, shadow, when);
    	
    	if( aimPoint == null || disttext == null )
    		return ret;
    	
    	if( isAimFound() )
    		return ret;
    	
        if (!shadow) {                                                       
            if( getMyLocation() == null )
            	return ret;

            checkPointFound();
            
            Point point = new Point();
            Point point2 = new Point();
            mapView.getProjection().toPixels(getMyLocation(), point);
            mapView.getProjection().toPixels(aimPoint, point2);

            if( point == point2 )
            	return ret;
            float orientation = ( float )( Math.atan2(point2.y - point.y, point2.x - point.x) * 180 / Math.PI );
            
            Bitmap bmp = BitmapFactory.decodeResource(this.mainActivity.getResources(), R.drawable.arrow_blue_rounded_right);
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