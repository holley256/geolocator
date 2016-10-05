package com.example.geolocator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;

import static com.example.geolocator.Constants.*;

/**
 * Servlet that returns closest street address, given a latitude and longitude as parameters
 * on the URL string. The results are return as JSON. Uses Google maps HTTP server to obtain
 * results. Results returned from Google maps are cached.
 */
@SuppressWarnings("serial")
@WebServlet(
	name = "GeoLocatorServlet",
	urlPatterns = {"/getAddress"}
)
public class GeoLocatorServlet extends HttpServlet
{
	/**
	 * Used to build a JSON object, if necessary, to be returned.
	 * 
	 * @param jsonObject current JSON object
	 * @return the current JSON object or a new JSON object if current is null
	 */
	private JSONObject getReturnJsonObject(JSONObject jsonObject) {
		JSONObject returnJsonObject = jsonObject;
		if (returnJsonObject == null) {
			returnJsonObject = new JSONObject();
			returnJsonObject.put("version", VERSION);
	        returnJsonObject.put("time-stamp", new Date().toString());
		}
		return returnJsonObject;
	}
	
	@Override 
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	    throws IOException
	{
		// Get output stream to return results.
		ServletOutputStream out = response.getOutputStream();
		
		// JSON object and string to hold results to be returned.
		JSONObject returnJson = null;
        String resultString;
        
		// Get URL params for latitude and longitude.
		String latitude = request.getParameter(LATITUDE);
		String longitude = request.getParameter(LONGITUDE);
		
		// Check validity of params
		try {
			double aDouble = Double.parseDouble(latitude);
			if (aDouble < MIN_LATITUDE || aDouble > MAX_LATITUDE) {
				// Out of range requests has a response JSON with a 'status' of 'out-of-range'.
				returnJson = getReturnJsonObject(returnJson).put("status", "out-of-range");
			} else {
				aDouble = Double.parseDouble(longitude);
				if (aDouble < MIN_LONGITUDE || aDouble > MAX_LONGITUDE) {
					// Out of range requests has a response JSON with a 'status' of 'out-of-range'.
					returnJson = getReturnJsonObject(returnJson).put("status", "out-of-range");
				}
			}
		} catch (NullPointerException e0) {
			// Null param requests are reported as 400 code
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        	return;
		} catch (NumberFormatException e1) {
			// Invalid number formate requests are reported as 400 code
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        	return;
		}
		
		// Did we have out of range params?
		if (returnJson != null) {
	        // Convert result JSON object onto string.
	        resultString = returnJson.toString(3); // formatted
			// Stream result string to requester.
	        out.print(resultString);
	        return;
		}
		
		// Calc a key to used in cached results map.
		String cacheKey = latitude + longitude;
		
		// Get cached result map from ServletContext.
		@SuppressWarnings("unchecked")
		Map<String, JSONObject> geoCache = (Map<String, JSONObject>) request.getServletContext().getAttribute(GEO_CACHE_KEY);
		
        // Check to see of results are in the cache.
		if (geoCache.containsKey(cacheKey)) {
			// Found in cache return found results.
			JSONObject cachedJson = geoCache.get(cacheKey); // Retrieve results from cache.
			resultString = cachedJson.toString(3);
		} else {
			// Results not found in cache, request address from Google maps HTTP server.
			
			// Build a URL string to call Google server.
			StringBuilder sb = new StringBuilder();
			sb	.append(GOOGLE_MAP_URL)
				.append("?latlng=")
				.append(latitude)
				.append(",")
				.append(longitude)
				.append("&key=")
				.append(GOOGLE_API_KEY);
			String googleMapUrlString = sb.toString();
			
			// Send request to Google maps
	        URL googleMapURL = new URL(googleMapUrlString);
	        
	        // Open connection and stream results from Google maps and build into a string.
	        URLConnection gmc = googleMapURL.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(gmc.getInputStream()));
	        String inputLine;
	        sb = new StringBuilder();
	        while ((inputLine = in.readLine()) != null) {
	        	sb.append(inputLine);
	        }
	        in.close();
	        String googleJsonResultString = sb.toString();
	        
	        // Parse results from Google maps and build JSON string to return.
	        try {
	        	// Build JSON object to return as results
	        	returnJson = getReturnJsonObject(returnJson);
		        
		        // Create new JSON object that contains parsed Google map results.
		        JSONObject googleJsonResultObject = new JSONObject(googleJsonResultString);
		        
		        // Check status of Google map results.
		        String status = googleJsonResultObject.getString("status");
		        if (!status.equals("OK")) {
		        	// Report bad results from Google maps as 400 code.
		        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		        	return;
		        } else {
		        	// Build result JSON object from Google maps JSON object.
		        	
		        	// Google maps JSON contains a 'results' array with the addresses.
			        JSONArray resultArray = googleJsonResultObject.getJSONArray("results");
			        
			        // Most details address in in first entry in array.
			        JSONObject entry0Object = resultArray.getJSONObject(0);
			        String address = entry0Object.getString("formatted_address");

			        // Build result JSON object
			        returnJson.put("address", address);
			        returnJson.put(LATITUDE, latitude);
			        returnJson.put(LONGITUDE, longitude);
			        returnJson.put("status", "OK");
			        
			        // Cache result JSON object.
			        geoCache.put(cacheKey, returnJson);
		        }
		        // Convert result JSON object onto string.
		        resultString = returnJson.toString(3); // formatted
	        } catch (JSONException e) {
	        	// Report 500 code on JSON parser error.
	        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        	return;
	        }
		}
		
		// Stream result string to requester.
        out.print(resultString);
	}
}
