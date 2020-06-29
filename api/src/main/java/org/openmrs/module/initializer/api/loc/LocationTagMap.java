package org.openmrs.module.initializer.api.loc;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Location;
import org.openmrs.LocationTag;

public class LocationTagMap extends BaseOpenmrsObject {
	
	private Integer locationTagMapId; // not used
	
	private Location location;
	
	private LocationTag locationTag;
	
	private boolean remove = false;
	
	public LocationTagMap() {
	}
	
	public LocationTagMap(Location location, LocationTag tag, boolean remove) {
		this.location = location;
		locationTag = tag;
		this.remove = remove;
	}
	
	@Override
	public Integer getId() {
		return locationTagMapId;
	}
	
	@Override
	public void setId(Integer id) {
		locationTagMapId = id;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public LocationTag getLocationTag() {
		return locationTag;
	}
	
	public void setLocationTag(LocationTag locationTag) {
		this.locationTag = locationTag;
	}
	
	public void setRemove(boolean remove) {
		this.remove = remove;
	}
	
	public LocationTagMap save() {
		if (this.remove) {
			this.location.removeTag(locationTag);
		} else {
			this.location.addTag(locationTag);
		}
		return this;
	}
}
