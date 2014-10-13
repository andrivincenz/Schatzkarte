package ch.hsr.schatzkarte;

import java.io.File;

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
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.TilesOverlay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		MapView map = (MapView) findViewById(R.id.mapview );
		map.setTileSource(TileSourceFactory.MAPQUESTOSM);
		 
		map.setMultiTouchControls(true);
		map.setBuiltInZoomControls(true);
		 
		IMapController controller = map.getController();
		controller.setZoom(18);
		 
		// Die TileSource beschreibt die Eigenschaften der Kacheln die wir anzeigen
		XYTileSource treasureMapTileSource = new XYTileSource("mbtiles", ResourceProxy.string.offline_mode, 1, 20, 256, ".png", "http://example.org/");
		 
		File file = new File(Environment.getExternalStorageDirectory() /* entspricht /sdcard/ */, "hsr.mbtiles");
		 
		/* Das verwenden von mbtiles ist leider ein wenig aufw�ndig, wir m�ssen
		 * unsere XYTileSource in verschiedene Klassen 'verpacken' um sie dann
		 * als TilesOverlay �ber der Grundkarte anzuzeigen.
		 */
		MapTileModuleProviderBase treasureMapModuleProvider = new MapTileFileArchiveProvider(new SimpleRegisterReceiver(this), 
				treasureMapTileSource, new IArchiveFile[] { MBTilesFileArchive.getDatabaseFileArchive(file) });
		 
		MapTileProviderBase treasureMapProvider = new MapTileProviderArray(treasureMapTileSource, null,
				new MapTileModuleProviderBase[] { treasureMapModuleProvider });
		 
		TilesOverlay treasureMapTilesOverlay = new TilesOverlay(treasureMapProvider, getBaseContext());
		treasureMapTilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		 
		// Jetzt k�nnen wir den Overlay zu unserer Karte hinzuf�gen:
		map.getOverlays().add(treasureMapTilesOverlay);	
	}
}
