package com.example.geolocator;

/**
 * Class used to hold global constants.
 */
public class Constants {
	private Constants() {} // Never instantiated.
	
	// Program version number.
	static final public String VERSION = "0.1";

	// URL of Google map HTTP server.
	static final public String GOOGLE_MAP_URL = "https://maps.googleapis.com/maps/api/geocode/json";
	
	// Google API key to authorize access to Google maps API server.
	static final public String GOOGLE_API_KEY = "AIzaSyAPXLIga488qCcElOF_HeB8rKufRtEtIkw";
	
	// Key used to store address cache in ServletContext attribute.
	static final public String GEO_CACHE_KEY = "geoCache";
	
	// Max size of address cache.
	static final public int GEO_CACHE_MAX_SIZE = 10;
	
	// Max and min values for Google maps latitude and longitude.
	static final public double MAX_LATITUDE = 85.0;
	static final public double MIN_LATITUDE = -85.0;
	static final public double MAX_LONGITUDE = 180.0;
	static final public double MIN_LONGITUDE = -180.0;
	
	// Commonly uses strings.
	static final public String LATITUDE = "latitude";
	static final public String LONGITUDE = "longitude";

}
