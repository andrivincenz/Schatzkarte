package ch.hsr.schatzkarte;

import java.util.ArrayList;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class MarkerOverlay extends ItemizedOverlay<Marker> {
 
	private ArrayList<Marker> overlayItemList = new ArrayList<Marker>();

	public MarkerOverlay(Drawable pDefaultMarker, ResourceProxy pResourceProxy) {
		super(pDefaultMarker, pResourceProxy);
	}
	 
	public void addItem(GeoPoint p, String title, String snippet){
		Marker newItem = new Marker(title, snippet, p);
		overlayItemList.add(newItem);
		populate(); 
	 }
	
	@Override
	public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3) {
		return false;
	}
	
	@Override
	protected Marker createItem(int arg0) {
		return overlayItemList.get(arg0);
	}
	
	@Override
	public int size() {
		return overlayItemList.size();
	}
	
	public String getJSON() {
		String json = "[";
		boolean isFirst = true;
		for (Marker marker : overlayItemList) {
			if (isFirst) {
				json += "{\"lat\": " + marker.getPoint().getLatitudeE6() + ", \"lon\": " + marker.getPoint().getLongitudeE6() + "}";
				isFirst = false;
			} else {
				json += ", {\"lat\": " + marker.getPoint().getLatitudeE6() + ", \"lon\": " + marker.getPoint().getLongitudeE6() + "}";
			}
		}
		json += "]";
		
		return json;
	}
}