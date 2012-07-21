package com.example.realliferpg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class ArrowOverlay extends MyLocationOverlay {
	private final MainActivity mainActivity;
	GeoPoint aimPoint;
	float orientation = 0;
	
	public ArrowOverlay(MainActivity mainActivity, android.content.Context context, MapView mapView)
	{
		super(context, mapView);
		this.mainActivity = mainActivity;
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
            Log.d( "test", String.format( "Orientation: %d %d", getMyLocation().getLatitudeE6(), getMyLocation().getLongitudeE6()) );
            
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