package com.example.realliferpg;

import java.io.ByteArrayOutputStream;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

public class AimUpdater {
	ArrowOverlay overlay;
	TimerTask updateTask;
	Timer timer;
	
	public AimUpdater( ArrowOverlay overlay )
	{
		this.overlay = overlay;
		timer = new Timer();
	}
	
	private JSONObject retrieveAim( String url )
	{
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute( new HttpGet( url ) );
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        response.getEntity().writeTo(out);
	        out.close();
	        
	        return new JSONObject( out.toString( "utf-8" ) );
		}
		catch(Exception ex) 
		{
			return new JSONObject();
		}	
	}
	
	public void start()
	{
		updateTask = new TimerTask() {
			public void run() {
				try {
					JSONObject obj = retrieveAim( "http://real-life-rpg.appspot.com/getpoint?user=eugenebolotin" );
					GeoPoint point = new GeoPoint( ( int )( obj.getDouble( "latitude" ) * 1E6 ), ( int )( obj.getDouble( "longitude" ) * 1E6 ) );
					overlay.setAim( point );
					
					SortedMap< Integer, String > disttext = new TreeMap< Integer, String >();
			        disttext.put( Integer.valueOf( 300 ), "It's warm!" );
			        disttext.put( Integer.valueOf( 200 ), "Hey! You're almost there!" );
			        disttext.put( Integer.valueOf( 100 ), obj.getString( "message" ) );
			        overlay.setAimTextInfo( disttext );
			        
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	        }};

		timer.schedule( updateTask, 0, 60000 );
	}

}
