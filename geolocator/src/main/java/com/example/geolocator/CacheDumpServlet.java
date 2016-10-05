package com.example.geolocator;

import static com.example.geolocator.Constants.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet to dump the contents of the address cache. The results are return as
 * a JSON object containing an array of JSON objects, each containing an address.
 */
@SuppressWarnings("serial")
@WebServlet(
	name = "CacheDumpServlet",
	urlPatterns = {"/dumpCache"}
)
public class CacheDumpServlet extends HttpServlet 
{

	@Override 
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	    throws IOException
	{
		// Get output stream to return results.
		ServletOutputStream out = response.getOutputStream();
		
		// Get cached result map from ServletContext.
		@SuppressWarnings("unchecked")
		Map<String, JSONObject> geoCache = (Map<String, JSONObject>) request.getServletContext().getAttribute(GEO_CACHE_KEY);
		
		// String to hold results to be returned.
        String resultString = "No Results";
        
        // Retrieve cached results and build result string to be returned.
        try {
        	// Check if cache is empty.
			if (geoCache.isEmpty()) {
				// Return empty JSON object.
				JSONObject emptyJson = new JSONObject();
				resultString = emptyJson.toString();
			} else {
				// Collect cached result addresses and build JSON object to return.
				// Get all values in cache map.
				Collection<JSONObject> cachedJsonValues = geoCache.values();
				
				// Create return JSON object and contained array.
				JSONObject resultsJsonObject = new JSONObject();
				JSONArray resultsJsonArray = new JSONArray();
				resultsJsonObject.put("cached-addresses", resultsJsonArray);
				
				// Populate array with JSON objects containing addresses from cache.
				for (JSONObject cachedJsonObject : cachedJsonValues) {
					resultsJsonArray.put(cachedJsonObject);
				}
				
				// Convert results JSON object to string
				resultString = resultsJsonObject.toString(3); // formatted
			}
        } catch (JSONException e) {
        	e.printStackTrace();
        }
        
		// Stream result string to requester.
        out.print(resultString);
	}
}
