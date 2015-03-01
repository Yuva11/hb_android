package com.HungryBells.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.activity.R;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Date;

public class ServiceListener {
	public static final String RESPONSEDATA = "RESPONSEDATA";
	GlobalAppState appState;

	public ServiceListener(GlobalAppState appState) {

        this.appState = appState;
	}

	public void sendRequest(final String requestData, final Bundle extra,
			final ServiceListenerType what, final Handler messageHandler,
			final String method, final StringEntity entity) {
        /*NetworkInfo info = getNetworkInfo();
        try {
            if(info.getExtraInfo().contains("mobile")){
                if(!networkType().equals("HSPA")){
                    customToast(appState);
                }
            }
        }catch(Exception e){
             Log.e("Error",e.toString(),e);
        }*/

		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				BufferedReader in = null;
				OutputStream os = null;
				String url = "";
				Message msg = Message.obtain();
				msg.obj = what;
				try {
					url = appState.getUrl() + requestData;
					URI website = new URI(url);
                    Log.e("Url",url);
                    Log.e("Url",entity+"");
					HttpResponse response = requestType(website, method, entity);
                    /*HttpResponse response = requestType(website, method, entity);
                    String responseData = EntityUtils.toString(response.getEntity());*/
					in = new BufferedReader(new InputStreamReader(response
							.getEntity().getContent()));
					StringBuffer sb = new StringBuffer("");
					String l = "";
					String nl = System.getProperty("line.separator");
					while ((l = in.readLine()) != null) {
						sb.append(l + nl);
					}
					in.close();
					String responseData = sb.toString();
					Bundle b = new Bundle();
					if (extra != null)
						b.putAll(extra);
					if (responseData.trim().length() != 0) {
						b.putString(RESPONSEDATA, responseData);
					} else {
						msg.obj = ServiceListenerType.ERROR_MSG;
						b.putString(RESPONSEDATA, "Empty Data");
					}
					msg.setData(b);
				} catch (SocketTimeoutException e) {
					Log.e("SendRequest", "SocketTimeoutException", e);
					msg.obj = ServiceListenerType.ERROR_MSG;
					Bundle b = new Bundle();
					if (extra != null)
						b.putAll(extra);
					b.putString(RESPONSEDATA,
							"Cannot establish connection to server. Please try again later.");
					msg.setData(b);
				} catch (SocketException e) {
					Log.e("SendRequest", "SocketException", e);
					msg.obj = ServiceListenerType.ERROR_MSG;
					Bundle b = new Bundle();
					if (extra != null)
						b.putAll(extra);
					b.putString(RESPONSEDATA,
							"Cannot establish connection to server. Please try again later.");
					msg.setData(b);
				} catch (IOException e) {
					Log.e("SendRequest", "IOException", e);
					msg.obj = ServiceListenerType.ERROR_MSG;
					Bundle b = new Bundle();
					if (extra != null)
						b.putAll(extra);
					b.putString(RESPONSEDATA, ""
							+ "Internal Error.Please Try Again");
					msg.setData(b);
				} catch (Exception e) {
					Log.e("SendRequest", "Exception", e);
					msg.obj = ServiceListenerType.ERROR_MSG;
					Bundle b = new Bundle();
					if (extra != null)
						b.putAll(extra);
					b.putString(RESPONSEDATA, "Internal Error");
					msg.setData(b);
				} finally {
					try {
						if (os != null) {
							os.close();
						}
						if (in != null) {
							in.close();
						}
					} catch (Exception e) {
						Log.e("HTTP", "Error", e);
					}
					messageHandler.sendMessage(msg);
				}

			}
		}.start();

	}

    public NetworkInfo getNetworkInfo(){
        ConnectivityManager cm = (ConnectivityManager) appState.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    private  void customToast(GlobalAppState con) {
        SharedPreferences pref = appState.getApplicationContext().getSharedPreferences("HB", Context.MODE_PRIVATE);
        long lastOne = pref.getLong("HBToast",0l);
        if(new Date().getTime() - lastOne > (5*60*1000)) {
                SharedPreferences.Editor editor = pref.edit();
            editor.putLong("HBToast",new Date().getTime());
            editor.commit();
            Toast.makeText(con.getApplicationContext(), con.getApplicationContext().getString(R.string.errorinnetwork), Toast.LENGTH_LONG).show();
        }
    }

    private String networkType() {
        TelephonyManager telephone = (TelephonyManager)
                appState.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = telephone.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT: return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA: return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE: return "EDGE";//Slow
            case TelephonyManager.NETWORK_TYPE_EHRPD: return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_EVDO_0: return "EVDO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A: return "EVDO rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B: return "EVDO rev. B";
            case TelephonyManager.NETWORK_TYPE_GPRS: return "GPRS";//Slow
            case TelephonyManager.NETWORK_TYPE_HSPAP: return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSDPA: return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPA: return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA: return "HSPA";
            case TelephonyManager.NETWORK_TYPE_IDEN: return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE: return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS: return "UMTS";//Slow
            case TelephonyManager.NETWORK_TYPE_UNKNOWN: return "Unknown";
        }
        throw new RuntimeException("New type of network");
    }

	private HttpResponse requestType(URI website, String method,
			StringEntity entity) {
		HttpResponse response = null;
		try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 50000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			int timeoutSocket = 50000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
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