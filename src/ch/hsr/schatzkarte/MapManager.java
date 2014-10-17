package ch.hsr.schatzkarte;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Map;

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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Environment;
import android.text.format.DateFormat;
import android.text.format.Time;

public class MapManager {
	private MapView mMap;
	private IMapController mController;
	private XYTileSource mTreasureMapTileSource;
	private File mFile;
	private Context mContext;
	private MarkerOverlay mMarkerOverlay;
	
	public MapManager(Context context, MapView map) {
		mMap = map;
		mContext = context;
		config();
	}
	
	private void config() {
		mMap.setTileSource(TileSourceFactory.MAPQUESTOSM);
		mMap.setMultiTouchControls(true);
		mMap.setBuiltInZoomControls(true);
		
		mController = mMap.getController();
		mController.setZoom(18);
		
		mTreasureMapTileSource = new XYTileSource("mbtiles", ResourceProxy.string.offline_mode, 1, 20, 256, ".png", "http://appquest.hsr.ch/hsr.mbtiles");
		
		try {
			setOfflineMap("hsr.mbtiles");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(mContext);
		Drawable drawableMarker = mContext.getResources().getDrawable(R.drawable.marker);
        int markerWidth = drawableMarker.getIntrinsicWidth();
        int markerHeight = drawableMarker.getIntrinsicHeight();
        drawableMarker.setBounds(0, markerHeight, markerWidth, 0);
        
        mMarkerOverlay = new MarkerOverlay(drawableMarker, resourceProxy);
        mMap.getOverlays().add(mMarkerOverlay);
	}
	
	public void setOfflineMap(String mapname) throws FileNotFoundException {
		mFile = new File(Environment.getExternalStorageDirectory(), "/" + mapname);
		
		if (!mFile.exists()) {
			throw new FileNotFoundException();
		}
		
		MapTileModuleProviderBase treasureMapModuleProvider = new MapTileFileArchiveProvider(new SimpleRegisterReceiver(mContext), 
				mTreasureMapTileSource, new IArchiveFile[] { MBTilesFileArchive.getDatabaseFileArchive(mFile) });
		 
		MapTileProviderBase treasureMapProvider = new MapTileProviderArray(mTreasureMapTileSource, null,
				new MapTileModuleProviderBase[] { treasureMapModuleProvider });
		 
		TilesOverlay treasureMapTilesOverlay = new TilesOverlay(treasureMapProvider, mContext);
		treasureMapTilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		 
		mMap.getOverlays().add(treasureMapTilesOverlay);	
	}
	
	public void setZoom(int zoom) {
		mController.setZoom(zoom);
	}
	
	public void setCenter(Location location) {
		setCenter(new GeoPoint(location));
	}
	
	public void setCenter(double longitude, double latitude) {
		setCenter(new GeoPoint(latitude, longitude));
	}
	
	public void setCenter(GeoPoint point) {
		mController.setCenter(point);
	}
	
	public void addMarker(Location location) {	
		GeoPoint point = new GeoPoint(location);
		addMarker(point);
	}
	public void addMarker(GeoPoint point) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date date = new Date();
		
        mMarkerOverlay.addItem(point, "marker" + dateFormat.format(date), "snippet");
	}
	
	public String getMarkerJson() {
		return mMarkerOverlay.getJSON();
	}
}
