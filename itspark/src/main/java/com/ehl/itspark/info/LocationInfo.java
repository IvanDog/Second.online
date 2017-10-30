package com.ehl.itspark.info;

import com.ehl.itspark.common.CommonRequestHeader;

public class LocationInfo {

	private CommonRequestHeader header;
	private double longitude;
	private double latitude;
	public CommonRequestHeader getheader() {
		return header;
	}
	public void setHeader(CommonRequestHeader header) {
		this.header = header;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
}
