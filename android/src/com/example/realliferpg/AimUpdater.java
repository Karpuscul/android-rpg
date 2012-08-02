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
	String username;
	
	public AimUpdater( ArrowOverlay overlay, String username )
	{
		this.overlay = overlay;
		this.username = username;
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
	
	private boolean sendAimCompleted()
	{
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute( new HttpGet( "http://real-life-rpg.appspot.com/completepoint?user=" + username ) );
			return response.getStatusLine().getStatusCode() == 200;
		}
		catch(Exception ex) 
		{
			return false;
		}
	}
	
	public void start()
	{
		updateTask = new TimerTask() {
			public void run() {
				try {
					if( overlay.isAimFound() )
					{
						if( sendAimCompleted() )
							overlay.clearAim();
						else
							return;
					}
					
					JSONObject obj = retrieveAim( "http://real-life-rpg.appspot.com/getpoint?user=" + username );
					if( obj.getInt( "id" ) == overlay.getAimId() )
						return;
					
					GeoPoint point = new GeoPoint( ( int )( obj.getDouble( "latitude" ) * 1E6 ), ( int )( obj.getDouble( "longitude" ) * 1E6 ) );
					
					SortedMap< Integer, String > disttext = new TreeMap< Integer, String >();
			        disttext.put( Integer.valueOf( 300 ), "It's warm!" );
			        disttext.put( Integer.valueOf( 200 ), "Hey! You're almost there!" );
			        disttext.put( Integer.valueOf( 100 ), obj.getString( "message" ) );

			        overlay.setAim( point, disttext, obj.getInt( "id" ) );
			        
				} catch (JSONException e) {
					return;
				}
				
	        }};

		timer.schedule( updateTask, 0, 60000 );
	}

}
