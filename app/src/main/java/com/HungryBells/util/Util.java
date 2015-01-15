package com.HungryBells.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.HungryBells.DTO.AndroidDeviceProperties;
import com.HungryBells.DTO.ContentDealDTO;
import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.DealSummaryDto;
import com.HungryBells.DTO.Deals;
import com.HungryBells.DTO.PaymentDTO;
import com.HungryBells.DTO.PaymentResponse;
import com.HungryBells.DTO.UserLocationDTO;
import com.HungryBells.activity.R;
import com.HungryBells.activity.UserActivity;
import com.HungryBells.dialog.SettingsDialog;

@SuppressLint("SimpleDateFormat")
public class Util {

	public static boolean checkNetworkAndLocation(Activity context) {
		if (isNetworkConnectionAvailable(context)) {
				return true;
		} else {
            ((UserActivity) context).getmUndoBarController().showUndoBar(true,
                    "No Network", null);
		}

		return false;
	}
    public static boolean checkNetwork(Activity context) {
        if (isNetworkConnectionAvailable(context)) {
            return true;
        } else {
            SettingsDialog settingsDialog = new SettingsDialog(context);
            settingsDialog.showSettingsAlert();

        }

        return false;
    }
	private static boolean isNetworkConnectionAvailable(Context con) {
		ConnectivityManager cm = (ConnectivityManager) con
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null)
			return false;
		State network = info.getState();
		return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
	}

    public static boolean isNetworkConnectionAvailablity(Context con) {
        ConnectivityManager cm = (ConnectivityManager) con
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null)
            return false;
        State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

	public AndroidDeviceProperties getDeviceProperties(Activity con) {
		AndroidDeviceProperties androidDevice = new AndroidDeviceProperties();
		androidDevice.setBoard(Build.BOARD);
		androidDevice.setBootLoader(Build.BOOTLOADER);
		androidDevice.setBrand(Build.BRAND);
		androidDevice.setCpu(Build.CPU_ABI);
		androidDevice.setCpu2(Build.CPU_ABI2);
		androidDevice.setDevice(Build.DEVICE);
		androidDevice.setDisplay(Build.DISPLAY);
		androidDevice.setFingerprint(Build.FINGERPRINT);
		androidDevice.setHardware(Build.HARDWARE);
		androidDevice.setHost(Build.HOST);
		androidDevice.setAndroidId(Build.ID);
		androidDevice.setManuFacturer(Build.MANUFACTURER);
		androidDevice.setDeviceName(Build.MODEL);
		androidDevice.setProduct(Build.PRODUCT);
		androidDevice.setRadio(Build.getRadioVersion());
		androidDevice.setSerial(Build.SERIAL);
		androidDevice.setTags(Build.TAGS);
		androidDevice.setType(Build.TYPE);
		androidDevice.setSerialNumber(Secure.getString(
				con.getContentResolver(), Secure.ANDROID_ID));
		androidDevice.setAndroidVersion(android.os.Build.VERSION.RELEASE);
		androidDevice.setSdkVersion(android.os.Build.VERSION.SDK_INT);
		androidDevice.setMemory(getMemoryInfo(con));
		androidDevice.setScreenResolution(getScreenResultion(con));
		androidDevice.setImeiNo(IMEIno(con));
		androidDevice.setCpuSpeed(getMaxCPUFreqMHz() + " M");
		androidDevice = apkDetails(con, androidDevice);
		return androidDevice;
	}

	private String IMEIno(Activity con) {
		TelephonyManager mngr = (TelephonyManager) con
				.getSystemService(Context.TELEPHONY_SERVICE);
		return mngr.getDeviceId();
	}

	private String getScreenResultion(Activity con) {
		DisplayMetrics metrics = new DisplayMetrics();
		con.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels + "X" + metrics.widthPixels;
	}

	private AndroidDeviceProperties apkDetails(Activity con,
			AndroidDeviceProperties androidDevice) {
		PackageInfo packageInfonfo;
		try {
            packageInfonfo = con.getPackageManager().getPackageInfo(
					con.getPackageName(), 0);
			androidDevice.setVersionName(packageInfonfo.versionName);
			androidDevice.setVersionCode(packageInfonfo.versionCode);
			Long firstInstallTime = packageInfonfo.firstInstallTime;
			Long lastUpdateTime = packageInfonfo.lastUpdateTime;
			Date date = new Date(firstInstallTime);
			Date date2 = new Date(lastUpdateTime);
			SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
			androidDevice.setFirstInstallTime(df2.format(date));
			androidDevice.setLastUpdateTime(df2.format(date2));
		} catch (NameNotFoundException e) {
            Log.e("Name Eror","Name Error",e);
        }
		return androidDevice;
	}

	private int getMaxCPUFreqMHz() {

		int maxFreq = -1;
		try {
			RandomAccessFile reader = new RandomAccessFile(
					"/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq",
					"r");

			boolean done = false;
			while (!done) {
				String line = reader.readLine();
				if (null == line) {
					done = true;
					break;
				}
				maxFreq = Integer.parseInt(line) / 1000;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return maxFreq;
	}

	private String getMemoryInfo(Context con) {
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) con
				.getSystemService(Activity.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		long availableMegs = mi.availMem / 1048576L;
		return availableMegs + "M";
	}
	public static boolean isValidMobile(String number) {
		number = number.replace(" ", "");
		number = number.replace("+", "");
		number = number.replace("-", "");// Remove spaces, sometimes people
		return number.matches("[0-9]{8,13}");
	}

	public static boolean isValidEmailAddress(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}
    public static void customToastShort(Activity con, String toastMessage) {
        LayoutInflater inflater = con.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) con.findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.textToast);
        text.setText(toastMessage);
        Toast toast = new Toast(con.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
	public static void customToast(Activity con, String toastMessage) {
		LayoutInflater inflater = con.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout,
				(ViewGroup) con.findViewById(R.id.toast_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.textToast);
		text.setText(toastMessage);

		Toast toast = new Toast(con.getApplicationContext());
		if (toastMessage.startsWith("Like ")) {
			toast.setDuration(Toast.LENGTH_SHORT);
		} else
			toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}

	public static void customToastMessage(Activity con, String toastMessage) {
		LayoutInflater inflater = con.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout,
				(ViewGroup) con.findViewById(R.id.toast_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.textToast);
		text.setText(toastMessage);
		text.setTextSize(13f);
		Toast toast = new Toast(con.getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public static String getDistance(double distanceFrom) {
		String distance ;
        NumberFormat formatter;
		if (distanceFrom * 1000 < 1000) {
            formatter = new DecimalFormat("#0");
			distance = formatter.format(distanceFrom * 1000) + " m";
		} else {
            formatter = new DecimalFormat("#0.0");
			distance = formatter.format(distanceFrom) + " km";
		}
		return distance;
	}

	public class Sink {
		public List<UserLocationDTO> userLocation;
		public List<Deals> deals;
		public List<Deals> promos;
		public List<ContentDealDTO> advertisements;
		public List<DealSummaryDto> orderSummary;
		public List<TermsConditions> Terms;
		public PaymentDTO paymentDTO;
		public PaymentResponse paymentResponse;
		public Customers profile;

	}
}
