package ch.hsr.schatzkarte;

import android.location.Location;

public interface GpsLocationListener {
	void onLocation(Location location);
	void onLocationSensorEnabled();
	void onLocationSensorDisabled();
}
