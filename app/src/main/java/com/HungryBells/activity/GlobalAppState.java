package com.HungryBells.activity;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.HungryBells.DTO.ContentDealDTO;
import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.Deals;
import com.HungryBells.DTO.MerchantBranchDto;
import com.HungryBells.DTO.UserLocation;
import com.HungryBells.DTO.UserLocationDTO;
import com.HungryBells.activity.subactivity.UILWrapper;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * This is the class contains all the details of app and used in the entire app
 */
public class GlobalAppState extends Application {
    /*Used to get current location*/
	MyLocation location = new MyLocation();

    /*User profile instance*/
	private Customers profile;
    @Getter @Setter
     int selectedItem = 0;

    /*SharedPreferences instance*/
     SharedPreferences sharedPreferencesLocation;


    /* Code Related to google analytics v4 */
    // ------ satrt of google anaytilcs code ------//
    private static final String PROPERTY_ID = "UA-55723283-2";

    public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER,
        GLOBAL_TRACKER,
        ECOMMERCE_TRACKER,
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(
                    R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    // ---- End of Google analytics code -- //


    public String getActionBarLocation() {
        return actionBarLocation;
    }

    public void setActionBarLocation(String actionBarLocation) {
        this.actionBarLocation = actionBarLocation;
    }

    public List<UserLocationDTO> getUserLocations() {
        return userLocations;
    }

    public void setUserLocations(List<UserLocationDTO> userLocations) {
        this.userLocations = userLocations;
    }

    public UserLocation getDealChangeLocation() {
        return dealChangeLocation;
    }

    public void setDealChangeLocation(UserLocation dealChangeLocation) {
        this.dealChangeLocation = dealChangeLocation;
    }

    public boolean isSearchByLocation() {
        return searchByLocation;
    }

    public void setSearchByLocation(boolean searchByLocation) {
        this.searchByLocation = searchByLocation;
    }

    public List<Deals> getAllDeals() {
        return allDeals;
    }

    public void setAllDeals(List<Deals> allDeals) {
        this.allDeals = allDeals;
    }

    public List<ContentDealDTO> getAdvertisements() {
        return advertisements;
    }

    public void setAdvertisements(List<ContentDealDTO> advertisements) {
        this.advertisements = advertisements;
    }

    public List<Deals> getAllPromos() {
        return allPromos;
    }

    public void setAllPromos(List<Deals> allPromos) {
        this.allPromos = allPromos;
    }

    public List<MerchantBranchDto> getMerchatBranch() {
        return merchatBranch;
    }

    public void setMerchatBranch(List<MerchantBranchDto> merchatBranch) {
        this.merchatBranch = merchatBranch;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String actionBarLocation;

	List<UserLocationDTO> userLocations;

	UserLocation dealChangeLocation;

	boolean searchByLocation = false;

	List<Deals> allDeals;

	List<ContentDealDTO> advertisements;

	List<Deals> allPromos;

	List<MerchantBranchDto> merchatBranch;

	String url;

    public Customers getProfile() {
        return profile;
    }

    public void setProfile(Customers profile) {
        this.profile = profile;
    }

    public GlobalAppState() {
        super();
	}

	public double getLatitude() {
		return location.getLatitude();
	}

	public double getLongitude() {
		return location.getLongitude();
	}

	public String getCity() {
		return location.getCity();
	}

	public void updateLocation(Location location, Context context) {
		this.location.updateLocation(location, context);
	}

	private class MyLocation {

		public double getLatitude() {
			return getLatitudeFromUser();
		}

		public double getLongitude() {
            return getLongitudeFromUser();
            	}

		public String getCity() {
			return getCityFromUser();
		}

		private double latitude;
		private double longitude;
		private static final int TWO_MINUTES = 1000 * 60 * 2;
		private String city;
		private Location currentBestLocation;
		private boolean imageLoaderConfig = false;

		private boolean nullLocationObtained = true;

		private MyLocation() {

            Log.i("MyLcoation", "Creating Mylocation object");
		}
        /*return the user current city*/
        private  String getCityFromUser(){
            if (imageLoaderConfig == false) {
                UILWrapper.initImageLoader(getApplicationContext());
                imageLoaderConfig = true;
            }
          sharedPreferencesLocation = getSharedPreferences("HB", MODE_PRIVATE);
          String currentCity =  sharedPreferencesLocation.getString("City", "");
          if(city!=null &&city.length()>0){
              return city;
          }else if (currentCity.length()>0){
                 return currentCity;
          }
         return "Hungry Bells";
       }

        /*return the user current Longitude */
        private double getLongitudeFromUser(){
                sharedPreferencesLocation = getSharedPreferences("HB", MODE_PRIVATE);
                String currentLongitude =  sharedPreferencesLocation.getString("longitude", "");
                if(longitude!=0.0){
                    Log.e("Lattitude",longitude+"");
                    Log.e("Inside","Inside Null");
                    return longitude;
                }else if (currentLongitude.length()>0){
                    return Double.parseDouble(currentLongitude);
                }
                return 0.0;
        }

        /*return the user current Latitude*/
        private double getLatitudeFromUser(){
            sharedPreferencesLocation = getSharedPreferences("HB", MODE_PRIVATE);
            String currentLattitude =  sharedPreferencesLocation.getString("lattitude", "");
            if(latitude!=0.0){
                Log.e("Lattitude",latitude+"");
                Log.e("Inside","Inside Null");
                return latitude;
            }else if (currentLattitude.length()>0){
                return Double.parseDouble(currentLattitude);
            }
            return 0.0;
        }

        /*This method is used to update the current location*/
		public void updateLocation(Location location, Context context) {
			if (imageLoaderConfig == false) {
				UILWrapper.initImageLoader(getApplicationContext());
				imageLoaderConfig = true;


			}
			Log.i("MyLocation object address", this.toString());
			if (isBetterLocation(location, currentBestLocation) == true) {
				currentBestLocation = location;
				latitude = currentBestLocation.getLatitude();
				longitude = currentBestLocation.getLongitude();
				Log.i("MyLocation", "Getting city from lat/long");

				new GetAddressTask(context).execute(location);
				if (nullLocationObtained == true) {
					new GetLocationFromHBServer(context).execute(location);
				}
			} else {
				Log.i("MyLocation", "Ignoring the received location ....");
			}

		}

		/**
		 * Determines whether one Location reading is better than the current
		 * Location fix
		 * 
		 * @param location
		 *            The new Location that you want to evaluate
		 * @param currentBestLocation
		 *            The current Location fix, to which you want to compare the
		 *            new one
		 */
		protected boolean isBetterLocation(Location location,
				Location currentBestLocation) {
			if (currentBestLocation == null) {
				// A new location is always better than no location
				return true;
			}
			// Check whether the new location fix is newer or older
			long timeDelta = location.getTime() - currentBestLocation.getTime();
			boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
			boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
			boolean isNewer = timeDelta > 0;

			// If it's been more than two minutes since the current location,
			// use the new location
			// because the user has likely moved
			if (isSignificantlyNewer) {
				return true;
				// If the new location is more than two minutes older, it must
				// be worse
			} else if (isSignificantlyOlder) {
				return false;
			}

			// Check whether the new location fix is more or less accurate
			int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
					.getAccuracy());
			boolean isLessAccurate = accuracyDelta > 0;
			boolean isMoreAccurate = accuracyDelta < 0;
			boolean isSignificantlyLessAccurate = accuracyDelta > 200;

			// Check if the old and new location are from the same provider
			boolean isFromSameProvider = isSameProvider(location.getProvider(),
					currentBestLocation.getProvider());

			// Determine location quality using a combination of timeliness and
			// accuracy
			if (isMoreAccurate) {
				return true;
			} else if (isNewer && !isLessAccurate) {
				return true;
			} else if (isNewer && !isSignificantlyLessAccurate
					&& isFromSameProvider) {
				return true;
			}
			return false;
		}

		/** Checks whether two providers are the same */
		private boolean isSameProvider(String provider1, String provider2) {
			if (provider1 == null) {
				return provider2 == null;
			}
			return provider1.equals(provider2);
		}

		/**
		 * A subclass of AsyncTask that calls getFromLocation() in the
		 * background. The class definition has these generic types: Location -
		 * A Location object containing the current location. Void - indicates
		 * that progress units are not used String - An address passed to
		 * onPostExecute()
		 */
		private class GetAddressTask extends AsyncTask<Location, Void, String> {
			Context mContext;

			public GetAddressTask(Context context) {
				super();
				mContext = context;
			}

			@Override
			protected void onPostExecute(String address) {

				Log.i("GetAddressTask", "OnPostExecute :" + address);
				if (address == null) {
					nullLocationObtained = true;
				} else {
					nullLocationObtained = false;
				}

			}

			/**
			 * Get a Geocoder instance, get the latitude and longitude look up
			 * the address, and return it
			 * 
			 * @params params One or more Location objects
			 * @return A string containing the address of the current location,
			 *         or an empty string if no address can be found, or an
			 *         error message
			 */
			@Override
			protected String doInBackground(Location... params) {
				Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
				// Get the current location from the input parameter list
				Location loc = params[0];
				// Create a list to contain the result address

				List<Address> addresses = null;
				try {
					/*
					 * Return 1 address.
					 */
					addresses = geocoder.getFromLocation(loc.getLatitude(),
							loc.getLongitude(), 9);
				} catch (IOException e1) {
					Log.e("LocationSampleActivity",
							"IO Exception in getFromLocation()");

					return null;
				} catch (Exception e2) {
					// Error message to post in the log
					String errorString = "Illegal arguments "
							+ Double.toString(loc.getLatitude()) + " , "
							+ Double.toString(loc.getLongitude())
							+ " passed to address service";
					Log.e("LocationSampleActivity", errorString);
					e2.printStackTrace();
					return null;
				}
				// If the reverse geocode returned an address
				Log.i("Total address size", " " + addresses.size());
				if (addresses != null && addresses.size() >= 4) {
					// Get the first address
					Address address = null;
					for (int i = 3; i >= 0; i--) {
						address = addresses.get(i);
						if (address != null && address.getSubLocality() != null) {
							Log.i("MyLocation", "Locality got in iteration ["
									+ i + "]");
							break;
						}
					}

					/*
					 * Format the first line of address (if available), city,
					 * and country name.
					 */
					// Locality is usually a city
					if (address != null && address.getSubLocality() != null) {

						city = address.getSubLocality();

					} else {
						Log.e("Error", "sublocality is null");
					}

					// Log.i("Address lin", " " + address.getAddressLine(3));

					Log.i("GetAddressTask", "City :" + address.getLocality()
							+ " Sublocality :" + address.getSubLocality()
							+ " Sub Admin area " + address.getAdminArea()
							+ "Provider" + currentBestLocation.getProvider());

					Log.i("Mylocation city name", "" + city);
                    sharedPreferencesLocation = getSharedPreferences("HB", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferencesLocation.edit();
                    editor.putString("City", city);
                    editor.putString("longitude", loc.getLongitude()+"");
                    editor.putString("lattitude", loc.getLatitude()+"");

                    editor.commit();

					return city;
				} else {
					return null;
				}
			}

		}

		private class GetLocationFromHBServer extends
				AsyncTask<Location, Void, String> {
			@SuppressWarnings("unused")
			Context mContext;

			public GetLocationFromHBServer(Context context) {
				super();
				mContext = context;
			}

			@Override
			protected void onPostExecute(String location) {
				Log.i("GetLocationFromHBServer", "OnPostExecute :" + location);

				if (location != null) {
					Log.e("GetLocationFromHBServer",
							"HB server setting lcoaion" + location);
					if (city == null)
						city = location;
                    sharedPreferencesLocation = getSharedPreferences("HB", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferencesLocation.edit();
                    editor.putString("City", city);
                    editor.commit();
				} else {
					Log.e("GetLocationFromHBServer",
							"HB server returned null location");

				}
			}

			@SuppressWarnings("unused")
			@Override
			protected String doInBackground(Location... params) {

				try {
					Location loc = params[0];
					GsonBuilder gsonBuilder = new GsonBuilder();
					Gson gson = gsonBuilder.create();
					com.HungryBells.DTO.Location mAppLocation = new com.HungryBells.DTO.Location();
					mAppLocation.setLat(loc.getLatitude() + "");
					mAppLocation.setLng(loc.getLongitude() + "");
                    sharedPreferencesLocation = getSharedPreferences("HB", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferencesLocation.edit();
                    editor.putString("lattitude", loc.getLatitude()+"");
                    editor.putString("longitude", loc.getLongitude()+"");
                    editor.commit();
					String mAppJsonData = gson.toJson(mAppLocation);
					Log.i("Json data", mAppJsonData);
					StringEntity request = new StringEntity(mAppJsonData, HTTP.UTF_8);

					HttpResponse response = getLocationFromServer(new URI(
							getString(R.string.serverurl_test)
									+ "location/currentlocation"), "POST",
							request);
					Log.i("HB server returned location", response.toString());
					if (response != null) {
						return EntityUtils.toString(response.getEntity());
					} else {
						Log.e("GetLocationFromHBServer",
								"Http response is null");
						return null;
					}

				} catch (Exception e2) {
					Log.e("Error", e2.toString(), e2);
					return null;
				}

			}

		}

		private HttpResponse getLocationFromServer(URI website, String method,
				StringEntity entity) {
			HttpResponse response = null;
			try {
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 30000;
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						timeoutConnection);
				int timeoutSocket = 35000;
				HttpConnectionParams
						.setSoTimeout(httpParameters, timeoutSocket);
				HttpClient client = new DefaultHttpClient(httpParameters);
				if (method.equals("POST")) {
					HttpPost request = new HttpPost();
					request.setURI(website);
					request.addHeader("Content-Type", "application/json");
					request.setEntity(entity);
					return client.execute(request);
				} else if (method.equals("PUT")) {
					HttpPut request = new HttpPut();
					request.setURI(website);
					request.addHeader("Content-Type", "application/json");
					request.setEntity(entity);
					return client.execute(request);
				} else {
					HttpGet request = new HttpGet();
					request.setURI(website);
					return client.execute(request);
				}
			} catch (Exception e) {
				Log.e("Error", e.toString(), e);
			}
			return response;
		}

	}

}
