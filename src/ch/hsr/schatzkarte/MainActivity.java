package ch.hsr.schatzkarte;

import java.io.File;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.MBTilesFileArchive;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.TilesOverlay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {

	private LocationManager locationManager;
	private double laenge;
	private double breite;
	private Button btn;
	private Tracker tracker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		btn = (Button)findViewById(R.id.button1);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(getApplicationContext(), "Kein PGS Empfäger. Bitte aktivieren", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		MapView map = (MapView) findViewById(R.id.mapview );
		map.setTileSource(TileSourceFactory.MAPQUESTOSM);
		 
		map.setMultiTouchControls(true);
		map.setBuiltInZoomControls(true);
		 
		IMapController controller = map.getController();
		controller.setZoom(18);
		 
		// Die TileSource beschreibt die Eigenschaften der Kacheln die wir anzeigen
		XYTileSource treasureMapTileSource = new XYTileSource("mbtiles", ResourceProxy.string.offline_mode, 1, 20, 256, ".png", "http://appquest.hsr.ch/hsr.mbtiles");
		
		File file = new File(Environment.getExternalStorageDirectory(), "/hsr.mbtiles");
		
		if (file.exists()) {
			/* Das verwenden von mbtiles ist leider ein wenig aufwändig, wir müssen
			 * unsere XYTileSource in verschiedene Klassen 'verpacken' um sie dann
			 * als TilesOverlay über der Grundkarte anzuzeigen.
			 */
			MapTileModuleProviderBase treasureMapModuleProvider = new MapTileFileArchiveProvider(new SimpleRegisterReceiver(this), 
					treasureMapTileSource, new IArchiveFile[] { MBTilesFileArchive.getDatabaseFileArchive(file) });
			 
			MapTileProviderBase treasureMapProvider = new MapTileProviderArray(treasureMapTileSource, null,
					new MapTileModuleProviderBase[] { treasureMapModuleProvider });
			 
			TilesOverlay treasureMapTilesOverlay = new TilesOverlay(treasureMapProvider, getBaseContext());
			treasureMapTilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
			 
			// Jetzt können wir den Overlay zu unserer Karte hinzufügen:
			map.getOverlays().add(treasureMapTilesOverlay);	
			
			GeoPoint point = new GeoPoint(47.223124, 8.817465);
			controller.setCenter(point);
			
			ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
			Drawable marker=getResources().getDrawable(android.R.drawable.star_big_on);
	        int markerWidth = marker.getIntrinsicWidth();
	        int markerHeight = marker.getIntrinsicHeight();
	        marker.setBounds(0, markerHeight, markerWidth, 0);
	        
			tracker = new Tracker(marker, resourceProxy);
	        map.getOverlays().add(tracker);
	        
	        
		}

		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GeoPoint myPoint1 = new GeoPoint(breite, laenge);
		        tracker.addItem(myPoint1, "myPoint1", "myPoint1");
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		locationManager.removeUpdates(this);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		laenge = location.getLongitude();
		breite = location.getLatitude();
		// listener und benarchtigen wenn gps gefunden
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
}
