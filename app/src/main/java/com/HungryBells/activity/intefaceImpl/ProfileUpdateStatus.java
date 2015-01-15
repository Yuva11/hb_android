package com.HungryBells.activity.intefaceImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.DTO.User;
import com.HungryBells.activity.MainActivity;
import com.HungryBells.activity.R;
import com.HungryBells.activity.intefaces.ProfileUpdate;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.CustomProgressDialog;
import com.HungryBells.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ProfileUpdateStatus implements ProfileUpdate {
	Activity context;
	ServiceListener httpConnection;
	CustomProgressDialog progressBar;
	Handler SyncHandler;

	@Override
	public void profileUpdate(Activity context, ServiceListener connection,
			CustomProgressDialog progressBar, Handler SyncHandler) {
		this.context = context;
		this.httpConnection = connection;
		this.progressBar = progressBar;
		this.SyncHandler = SyncHandler;
		updateProfile();

	}

	@SuppressLint("SimpleDateFormat")
	private void updateProfile() {
		SharedPreferences preferencesReader = context.getSharedPreferences(
				"HB", Context.MODE_PRIVATE);
		String profileSerialized = preferencesReader.getString("Profile", "");
		Customers profile = Customers.create(profileSerialized);
		TextView bDate = (TextView) context.findViewById(R.id.textViewprodob);
		try {
			String fName = ((EditText) context
					.findViewById(R.id.textViewprofirstname)).getText()
					.toString().trim();
			User user = new User();
			if (fName != null && fName.length() > 0) {
				profile.setFirstName(fName);
				user.setName(fName);
			} else {
				Util.customToast(context, context.getString(R.string.nameempty));
				return;
			}
			String mail = ((EditText) context
					.findViewById(R.id.textViewpromailid)).getText().toString()
					.trim();
			profile.setEmail(mail);
			user.setUserName(mail);
			String mobile = ((EditText) context
					.findViewById(R.id.textViewpromobile)).getText().toString()
					.trim();
			if (mobile != null && mobile.length() > 0) {
				if (Util.isValidMobile(mobile)) {
					profile.setMobileNumber(mobile);
					user.setContactNumber(Long.parseLong(mobile));
				} else {
					Util.customToast(context, context.getString(R.string.mobilevalid));
					return;
				}
			} else {
				Util.customToast(context, context.getString(R.string.mobileempty));
				return;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date birthDate = new Date();

			birthDate = sdf.parse(bDate.getText().toString());

			if (getDiffYears(birthDate, new Date())<16) {
				Util.customToast(context, context.getString(R.string.birthdate));
				return;
			} else {
				DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
				String birthdayDate = df.format(birthDate);
				profile.setDob(birthdayDate);
			}
			profile.setUser(user);
			profile.setAuthenticationId(getRegistrationId(context));
			String url = "customer/updatecustomer/" + profile.getId();
			StringEntity custEntity = null;
			GsonBuilder gsonBuilder = new GsonBuilder();
			Gson gson = gsonBuilder.create();
			String customerData = gson.toJson(profile);
			custEntity = new StringEntity(customerData, HTTP.UTF_8);
			progressBar.setCancelable(false);
			progressBar.show();
			httpConnection.sendRequest(url, null,
					ServiceListenerType.CUSTOMER_EDIT, SyncHandler, "PUT",
					custEntity);
		} catch (Exception e) {
			Log.e("Error", "Excep", e);
		}

	}
    public  int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(b.YEAR) - a.get(a.YEAR);
        if (a.get(a.MONTH) > b.get(b.MONTH) ||
                (a.get(a.MONTH) == b.get(b.MONTH) && a.get(a.DATE) > b.get(b.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
	private SharedPreferences getGCMPreferences(Context context) {
		return context.getSharedPreferences(MainActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString("registration_id", "");
		if (registrationId.isEmpty()) {
			Log.i("GCMRegistrationActivity", "Registration not found.");
			return "";
		}
		return registrationId;
	}

	private boolean calculateAge(Date birthDate) {
		Calendar birthDay = Calendar.getInstance();
		birthDay.setTimeInMillis(birthDate.getTime());
		long currentTime = System.currentTimeMillis();
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(currentTime);
		return birthDay.after(now);
	}
}
