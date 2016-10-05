package com.example.geolocator;

import javax.servlet.ServletContextEvent;
import org.apache.commons.collections.map.LRUMap;
import static com.example.geolocator.Constants.*;

import java.util.Collections;
import java.util.Map;

/**
 * ServletContextListener used to create Geo address cache. An Apache Commons LRUMap
 * is used as the cache. It is wrapped in a Collections synchronized map to make it
 * thread safe.
 */
public class GeoLocatorContextListener implements javax.servlet.ServletContextListener {
	/**
	 * Called after ServletContext is initialized. The cache is created here.
	 */
    public void contextInitialized(ServletContextEvent sce) {
    	// Create a new Apache LRUMap to be used as the cache.
    	LRUMap geoCacheNotThreadSafe = new LRUMap(GEO_CACHE_MAX_SIZE);
    	
    	// Wrap the LRUMap which is not thread safe in a synchronized map.
    	@SuppressWarnings({ "unchecked", "rawtypes" })
		Map geoCache = Collections.synchronizedMap(geoCacheNotThreadSafe);
    	
    	// Save cache map a ServletContext attribute which can be accessed by the Servlets.
    	sce.getServletContext().setAttribute(GEO_CACHE_KEY, geoCache);
    } 

    public void contextDestroyed(ServletContextEvent sce) {
    	// Unused.
    } 
}