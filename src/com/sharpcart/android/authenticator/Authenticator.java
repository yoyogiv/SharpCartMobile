package com.sharpcart.android.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sharpcart.android.api.LoginServiceImpl;
import com.sharpcart.android.exception.SharpCartException;
import com.sharpcart.android.provider.DatabaseHelper;
import com.sharpcart.android.provider.SharpCartContentProvider;

public class Authenticator extends AbstractAccountAuthenticator {
	public String[] authoritiesToSync = { SharpCartContentProvider.AUTHORITY };
	private final Context mContext;

	public Authenticator(final Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public Bundle addAccount(final AccountAuthenticatorResponse response,
			final String accountType, final String authTokenType,
			final String[] requiredFeatures, final Bundle options)
			throws NetworkErrorException {

		final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
		intent.putExtra(AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE,
				authTokenType);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);

		return bundle;

	}

	@Override
	public Bundle confirmCredentials(final AccountAuthenticatorResponse response,
			final Account account, final Bundle options) throws NetworkErrorException {
		return null;
	}

	@Override
	public Bundle editProperties(final AccountAuthenticatorResponse response,
			final String accountType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle getAuthToken(final AccountAuthenticatorResponse response,
			final Account account, final String authTokenType, final Bundle options)
			throws NetworkErrorException {

		if (!authTokenType.equals(AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE)) {

			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ERROR_MESSAGE,
					"invalid authTokenType");

			return result;
		}

		final AccountManager am = AccountManager.get(mContext);
		final String password = am.getPassword(account);

		if (password != null) {
			boolean verified = false;

			String loginResponse = null;
			try {
				loginResponse = LoginServiceImpl.sendCredentials(account.name,password);
				verified = LoginServiceImpl.hasLoggedIn(loginResponse);
			} catch (final SharpCartException e) {
				verified = false;
			}

			if (verified) {
				final Bundle result = new Bundle();
				result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
				result.putString(AccountManager.KEY_ACCOUNT_TYPE,AuthenticatorActivity.PARAM_ACCOUNT_TYPE);

				return result;
			}
		}
		// Password is missing or incorrect
		final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
		intent.putExtra(AuthenticatorActivity.PARAM_USER, account.name);
		intent.putExtra(AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE,authTokenType);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public String getAuthTokenLabel(final String authTokenType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle hasFeatures(final AccountAuthenticatorResponse response,
			final Account account, final String[] features) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle updateCredentials(final AccountAuthenticatorResponse response,
			final Account account, final String authTokenType, final Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAccountRemovalAllowed(
			final AccountAuthenticatorResponse response, final Account account)
			throws NetworkErrorException {
		final Bundle result = super.getAccountRemovalAllowed(response, account);

		if (result != null
				&& result.containsKey(AccountManager.KEY_BOOLEAN_RESULT)
				&& !result.containsKey(AccountManager.KEY_INTENT)) {
			final boolean allowed = result
					.getBoolean(AccountManager.KEY_BOOLEAN_RESULT);

			if (allowed) {
				for (int i = 0; i < authoritiesToSync.length; i++) {
					ContentResolver.cancelSync(account, authoritiesToSync[i]);
				}

				mContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);
			}
		}

		return result;
	}
}
