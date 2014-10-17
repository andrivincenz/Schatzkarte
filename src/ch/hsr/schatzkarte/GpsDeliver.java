package ch.hsr.schatzkarte;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GpsDeliver implements LocationListener {
	private GpsLocationListener mListener;
	private Context mContext;
	private LocationManager mLocationManager;
	
	public GpsDeliver(Context context) {
		mContext = context;
		
	}

	public void startDelivery(GpsLocationListener gpsListener) {
		if (gpsListener == null) {
			throw new NullPointerException();
		}
		mListener = gpsListener;
		mLocationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
		
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}
	
	public void stopDelivery() {
		if (mListener != null) {
			mLocationManager.removeUpdates(this);
			mListener = null;
		}
	}
	
	public boolean isRunning() {
		return mListener != null;
	}
	
	@Override
	public void onLocationChanged(Location location) {	
		if (location != null) {
			mListener.onLocation(location);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public void onProviderEnabled(String provider) {
		mListener.onLocationSensorEnabled();
	}

	@Override
	public void onProviderDisabled(String provider) {
		mListener.onLocationSensorDisabled();
	}
}
