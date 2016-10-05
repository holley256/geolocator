# geolocator
Java Servlet RESTful web service that takes latitude and longitude coordinates as URL parameters and return a JSON response with the nearest street address.

## Installation
1. Download latest Maven application.
2. Download or pull the master brach of this project.

## Running the Servlet
##### The Geo Address Locator Servlet
1. Start a command prompt or terminal.
2. Change directorys to the gelocator folder in the downloaded directory.
3. Run `mvn jetty:run`
4. Launch a browser with the link [http://localhost:8080/getAddress?latitude=33.969601&longitude=-84.100033](http://localhost:8080/getAddress?latitude=33.969601&longitude=-84.100033).
5. You should get the response:

```
{
   "time-stamp": "Tue Oct 04 21:01:06 EDT 2016",
   "address": "2651 Satellite Blvd, Duluth, GA 30096, USA",
   "latitude": "33.969601",
   "version": "0.1",
   "longitude": "-84.100033",
   "status": "OK"
}
```
##### The Geo Address Cache Dump Servlet
1. Assuming Jetty is still running.
2. From the browser lauch the link [http://localhost:8080/dumpCache](http://localhost:8080/dumpCache).
3. You should get the response:

```
{"cached-addresses": [{
   "time-stamp": "Tue Oct 04 21:01:06 EDT 2016",
   "address": "2651 Satellite Blvd, Duluth, GA 30096, USA",
   "latitude": "33.969601",
   "version": "0.1",
   "longitude": "-84.100033",
   "status": "OK"
}]}
```

## Error Codes
##### HTTP Codes
- 400 Bad Request is returned if the `latitude` or `longitude` URL params are malformed.
- 400 Bad Request is returned if the Google maps server return a JSON response with a `status` that is not `OK`.
- 500 Internal Server Error is returned if the JSON parser/builder has an exception.

##### JSON Codes
The Returned JSON object has a `status` field the is set to `OK` if the results is valid. If a request
is sent with `latitude` or `longitude` set to values that are out of bounds, the `status` field
is set to `out-of-range`.

## Development Notes
##### Maven
This project is obviously a Maven project which solve the issue of dependencies for distribution.

##### Jetty
This project contains an embedded Jetty server which alleviates the need for an external server or container.

##### Google Maps
This project uses the Google maps API HTTP server to retrieve street addresses based on geo coordinates.
I use a URLConnection to connect to the Google server.

##### Cache
The address cache is implemented using the Apache Commons LRUMap class. This class implements the Java
Map interface and allow an instantiation to limit the number of key/value entries. The least recently used
entry is discarded when a new key/value is inserted beyond the set limit. This class is not thread safe, so
I wrap it in a Collections.synchronizedMap to provide thread safety. The thread safe caching map is then
stored in a ServletContext attribute so that it will survive HTTP calls and is shared among the servlets.

##### JSON
I use the `org.json` library for parsing and creating JSON strings.

