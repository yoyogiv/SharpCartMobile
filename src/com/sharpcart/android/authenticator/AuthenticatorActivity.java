package com.sharpcart.android.authenticator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sharpcart.android.MainActivity;
import com.sharpcart.android.R;
import com.sharpcart.android.model.UserProfile;
import com.sharpcart.android.net.NetworkUtilities;
import com.sharpcart.android.provider.SharpCartContentProvider;
import com.sharpcart.android.utilities.SharpCartUtilities;
import com.sharpcart.android.wizardpager.SharpCartLoginActivity;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    private static final String TAG = AuthenticatorActivity.class
	    .getCanonicalName();
    private static final int NEW_ACCOUNT = 0;
    public static final String PARAM_ACCOUNT_TYPE = "com.sharpcart.android";
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    public static final String PARAM_USER = "user";
    public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";
    private AccountManager mAccountManager;
    private Thread mAuthThread;
    private String mAuthToken;
    private String mAuthTokenType;
    private Boolean mConfirmCredentials = false;
    private final Handler mHandler = new Handler();

    private TextView mMessage;
    private String mPassword;
    private EditText mPasswordEdit;
    private String mUsername;
    private EditText mUsernameEdit;
    private Button mSignInButton;
    private Button mRegisterButton;

    /** Was the original caller asking for an entirely new account? */
    protected boolean mRequestNewAccount = false;
    private String mUser;

    // Sync interval constants
    public static final long MILLISECONDS_PER_SECOND = 1000L;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;

    @Override
    public void onCreate(final Bundle icicle) {
    	super.onCreate(icicle);

		mAccountManager = AccountManager.get(this);
		checkMaximumNumberOfAccounts();
	
		final Intent intent = getIntent();
	
		mUser = intent.getStringExtra(PARAM_USER);
		mAuthTokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
		mRequestNewAccount = mUsername == null;
		mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRMCREDENTIALS,false);
		
		setContentView(R.layout.login);
	
		findViews();
		initFields();
    }

    private void initFields() {
		mUsernameEdit.setText(mUser);
		//mMessage.setText(getMessage());
		
		//Sign to an existing account
		mSignInButton.setOnClickListener(new OnClickListener() {
	
		    @Override
			public void onClick(final View view) {
		    	handleLogin(view);
		    }
	
		});
		
		//Register for a new one
		mRegisterButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				handleRegister(v);	
			}
		});
    }

    private void handleLogin(final View view) {
		if (mRequestNewAccount) {
		    mUsername = mUsernameEdit.getText().toString();
		}
	
		mPassword = mPasswordEdit.getText().toString();
		
		if(TextUtils.isEmpty(mUsername))
			mUsernameEdit.setError("Enter Email");
		
		if (TextUtils.isEmpty(mPassword))
			mPasswordEdit.setError("Enter Password");
		
		//if both fields have text than we can go ahead and login
		if (!TextUtils.isEmpty(mUsername) && !TextUtils.isEmpty(mPassword)) {
			showProgress();
			mAuthThread = NetworkUtilities.attemptAuth(mUsername, mPassword,mHandler, this);
		}
	
    }
    
    /*
     * Start the register activity to register a new user
     */
    private void handleRegister(final View view)
    {
	    // There are no accounts! We need to create one.
	    Log.d(TAG, "Registering a new account");
	    
	    final Intent intent = new Intent(this, SharpCartLoginActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    startActivityForResult(intent, NEW_ACCOUNT);
    }
    
    @SuppressWarnings("deprecation")
	private void showProgress() {
    	showDialog(0);
    }

    @SuppressWarnings("deprecation")
	private void hideProgress() {
    	dismissDialog(0);
    }

    public void onAuthenticationResult(final Boolean result) {
		hideProgress();
	
		if (result) {
		    if (!mConfirmCredentials) {
		    	finishLogin();
		    } else {
			// TODO see if we need to confirm credentials
		    }
		} else {
		    Log.e(TAG, "onAuthenticationResult: failed to authenticate");
		    mMessage.setText("User and/or password are incorrect");
		}
    }

    @Override
    protected Dialog onCreateDialog(final int id) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Login in");
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
		    @Override
			public void onCancel(final DialogInterface dialog) {
			if (mAuthThread != null) {
			    mAuthThread.interrupt();
			    finish();
			}
		    }
		});
		return dialog;
    }

    private void finishLogin() {
		final Account account = new Account(mUsername, PARAM_ACCOUNT_TYPE);
	
		if (mRequestNewAccount) {
		    mAccountManager.addAccountExplicitly(account, mPassword, null);
	
			/*Copy offline database if it doesn't already exist */
			final String destDir = "/data/data/" + getPackageName() + "/databases/";
			
			final String destPath = destDir + "SharpCart";
			final File f = new File(destPath);
			f.exists();
			f.length();
			
			if (!f.exists() || (f.length()<100))
			{
				Log.d(TAG, "Copying database to local file system");
				
				//---make sure directory exists---
				final File directory = new File(destDir);
				directory.mkdirs();
				
				//---copy the db from the assets folder into
				// the databases folder---
				try 
				{
					CopyDB(getBaseContext().getAssets().open("SharpCart"),
					new FileOutputStream(destPath));
				} catch (final FileNotFoundException e) {
					e.printStackTrace();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			
			//Setup shopping items active field
			final ContentValues cv = new ContentValues();
			
			//Reset all items active to 1
			cv.put(SharpCartContentProvider.COLUMN_ACTIVE, "1");
	   		
			getContentResolver().update(
					SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS, 
					cv, 
					null, 
					null);
			
			/*
			 * Turn on periodic syncing. I need to add randomness to the sync interval to make sure 
			 * that not all users sync at the same time, which will overload the server.
			 */
			
			final Bundle extras = new Bundle();
			final long random = (long) (Math.random()*1000L);
			
			ContentResolver.addPeriodicSync(account,SharpCartContentProvider.AUTHORITY, extras, (8*SYNC_INTERVAL)+random);
			
			//Will run the syncadapter everytime we get a network tinkle
			//ContentResolver.setSyncAutomatically(account,SharpCartContentProvider.AUTHORITY, true);
			
			//Create user profile last updated preference
			UserProfile.getInstance().setLastUpdated(new Date(0));
			
			final SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			
			sharedPreferences.edit()
				.putLong("user_profile_last_updated", 0)
				.commit();
			
			//initiate a sync
			SharpCartUtilities.getInstance().syncFromServer(account);
	
		} else {
		    mAccountManager.setPassword(account, mPassword);
		}
	
		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, PARAM_ACCOUNT_TYPE);
	
		if (mAuthTokenType != null
			&& mAuthTokenType.equals(PARAM_AUTHTOKEN_TYPE)) {
		    intent.putExtra(AccountManager.KEY_AUTHTOKEN, mAuthToken);
		}
	
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		
		finish();
    }

    private void findViews() {
		mMessage = (TextView) findViewById(R.id.message);
		mUsernameEdit = (EditText) findViewById(R.id.fid_edit);
		mPasswordEdit = (EditText) findViewById(R.id.password_edit);
		mSignInButton = (Button) findViewById(R.id.ok_button);
		mRegisterButton = (Button) findViewById(R.id.registerButton);
    }

    private void checkMaximumNumberOfAccounts() {
		final Account[] accounts = mAccountManager.getAccountsByType(PARAM_ACCOUNT_TYPE);
	
		if (accounts.length > 1) {
		    Toast.makeText(this, "More than one account", Toast.LENGTH_SHORT).show();
		    finish();
		}

    }
    
	private void CopyDB(final InputStream inputStream, final OutputStream outputStream)throws IOException {
		//---copy 1K bytes at a time---
		final byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) > 0) 
		{
			outputStream.write(buffer, 0, length);
		}
		
		inputStream.close();
		outputStream.close();
	}
	
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
	
    	super.onActivityResult(requestCode, resultCode, data);
    		
		if (mAccountManager.getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE).length > 0) 
		{		
		    final Intent i = new Intent(this, MainActivity.class);
		    startActivity(i);
		    finish();
		} else {
		    finish();
		}
    }
}
