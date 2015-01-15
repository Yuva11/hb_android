package com.HungryBells.activity;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;


public class AccountManagerActivity extends AccountAuthenticatorActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_acmanager);
			ArrayList<String> accountsInfo = new ArrayList<String>();
			AccountManager manager = AccountManager.get(this);
			Account[] accounts = manager.getAccounts();
			for (Account account : accounts) {
				String name = account.name;
				String type = account.type;
				int describeContents = account.describeContents();
				int hashCode = account.hashCode();
				accountsInfo.add("name = " + name + "\ntype = " + type
						+ "\ndescribeContents = " + describeContents
						+ "\nhashCode = " + hashCode);
			}
			String[] result = new String[accountsInfo.size()];
			accountsInfo.toArray(result);
			GlobalAppState appState = (GlobalAppState) getApplication();
			String username = appState.getProfile().getEmail();
			String password = "hungrybell";
			String accountType = "hungrybell";

			final Bundle extraData = new Bundle();
			extraData.putString("profileData", appState.getProfile()
					.serialize());
			final Account account = new Account(username, accountType);

			manager.addAccountExplicitly(account, password, extraData);

			Bundle extraDataId = new Bundle();
			manager.addAccountExplicitly(account, password, extraDataId);

			final Intent intent = new Intent();
			intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
			intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
			intent.putExtra(AccountManager.KEY_AUTHTOKEN, accountType);
			this.setAccountAuthenticatorResult(intent.getExtras());
			this.setResult(RESULT_OK, intent);
			this.finish();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}