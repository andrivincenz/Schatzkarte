package ch.hsr.schatzkarte;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements GpsLocationListener {
	private Button btnSetMarker;
	private MapManager mMapManager;
	private GpsDeliver mGpsDeliver;
	private Location mCurrentLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// initialise components
		btnSetMarker = (Button)findViewById(R.id.button1);
		final MapView map = (MapView) findViewById(R.id.mapview);
		mGpsDeliver = new GpsDeliver(getApplicationContext());
		mMapManager = new MapManager(getApplicationContext(), map);

		GeoPoint point = new GeoPoint(47.223124, 8.817465);
		mMapManager.setCenter(point);
		
		initListener();
	}

	private void initListener() {
		btnSetMarker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMapManager.setCenter(mCurrentLocation);
				mMapManager.addMarker(mCurrentLocation);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGpsDeliver.startDelivery(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mGpsDeliver.stopDelivery();
	}

	@Override
	public void onLocation(Location location) {
		btnSetMarker.setEnabled(true);
		mCurrentLocation = location;
	}

	@Override
	public void onLocationSensorEnabled() {
		
	}

	@Override
	public void onLocationSensorDisabled() {
		Toast.makeText(getApplicationContext(), "Can't find a satellit. Please gps turn on!", Toast.LENGTH_LONG).show();
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) { 
		MenuItem menuItem = menu.add("Log");
		menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				log();
				return false;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}
	 
	
	private void log() {
		Intent intent = new Intent("ch.appquest.intent.LOG");
	 
		if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
			Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
			return;
		}
	 
		intent.putExtra("ch.appquest.taskname", "Schatzkarte");
		intent.putExtra("ch.appquest.logmessage", mMapManager.getMarkerJson());
	 
		startActivity(intent);
	}
}
