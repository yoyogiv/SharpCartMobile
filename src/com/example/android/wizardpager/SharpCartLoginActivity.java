/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.wizardpager;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.android.wizardpager.wizard.ui.ReviewFragment;
import com.example.android.wizardpager.wizard.ui.StepPagerStrip;
import com.sharpcart.android.R;
import com.sharpcart.android.model.UserProfile;
import com.sharpcart.android.provider.SharpCartContentProvider;
import com.sharpcart.android.utilities.SharpCartUtilities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SharpCartLoginActivity extends FragmentActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private boolean mEditingAfterReview;

    private AbstractWizardModel mWizardModel = new SharpCartWizardModel(this);

    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;
    private List<ReviewItem> mCurrentReviewItems;
    
    private Context mContext;
    
    public static final String PARAM_ACCOUNT_TYPE = "com.sharpcart.android";
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    public static final String PARAM_USER = "user";
    public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";
    
    private AccountManager mAccountManager;
    private String mAuthToken;
    private String mAuthTokenType;
    private Boolean mConfirmCredentials = false;
    
    /** Was the original caller asking for an entirely new account? */
    protected boolean mRequestNewAccount = true;
    
    // Sync interval constants
    public static final long MILLISECONDS_PER_SECOND = 1000L;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_wizard);
        
        mContext = this.getBaseContext();
        mAccountManager = AccountManager.get(this);
        
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        mWizardModel.registerListener(this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(mPagerAdapter.getCount() - 1, position);
                if (mPager.getCurrentItem() != position) {
                    mPager.setCurrentItem(position);
                }
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
                	
                	/*
                	 * Login/Create new account
                	 * 0. Copy db 
                	 * 1. Save relevant information to UserProfile object
                	 * 2. Update SharedPreferences
                	 * 3. Create Authenticator account
                	 * 4. Update server
                	 */
                	
         			/*Copy offline database if it doesn't already exist */
         			final String destDir = "/data/data/" + getPackageName() + "/databases/";
         			
         			final String destPath = destDir + "SharpCart";
         			final File f = new File(destPath);
         			if (!f.exists()) 
         			{
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
         			
                    ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
                    for (Page page : mWizardModel.getCurrentPageSequence()) {
                        page.getReviewItems(reviewItems);
                    }
                    Collections.sort(reviewItems, new Comparator<ReviewItem>() {
                        @Override
                        public int compare(ReviewItem a, ReviewItem b) {
                            return a.getWeight() > b.getWeight() ? +1 : a.getWeight() < b.getWeight() ? -1 : 0;
                        }
                    });
                    
                    mCurrentReviewItems = reviewItems;
                    
                	//Save relevant inforation to UserProfile object
                	UserProfile.getInstance().setUserName(((ReviewItem)mCurrentReviewItems.get(0)).getDisplayValue());
                	UserProfile.getInstance().setZip(((ReviewItem)mCurrentReviewItems.get(2)).getDisplayValue());
                	UserProfile.getInstance().setFamilySize(((ReviewItem)mCurrentReviewItems.get(3)).getDisplayValue());
                	String storesString = ((ReviewItem)mCurrentReviewItems.get(4)).getDisplayValue();
                	UserProfile.getInstance().setStores(UserProfile.getInstance().storesStringFromStoreName(storesString));
                	
                	//update shared preferences
                	//update settings
                	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
                	sharedPref.edit().putString("pref_zip", UserProfile.getInstance().getZip()).commit();
                	sharedPref.edit().putString("pref_family_size", String.valueOf(UserProfile.getInstance().getFamilySize())).commit();
                	
                	Set<String> stores = new TreeSet<String>();
                	String stores_string_from_db = UserProfile.getInstance().getStores();
                	String[] stores_array = stores_string_from_db.split("-");
                	
                	for (String store : stores_array)
                	{
                		stores.add(store);
                	}
                	
                	//update stores settings
                	sharedPref.edit().putStringSet("pref_stores", stores).commit();
                	
                	//create authenticator account
                	createAccount(((ReviewItem)mCurrentReviewItems.get(0)).getDisplayValue(),((ReviewItem)mCurrentReviewItems.get(1)).getDisplayValue());
                	
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        onPageTreeChanged();
        updateBottomBar();
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 = review step
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size()) {
            mNextButton.setText(R.string.finish);
            mNextButton.setBackgroundResource(R.drawable.finish_background);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        } else {
            mNextButton.setText(mEditingAfterReview
                    ? R.string.review
                    : R.string.next);
            mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
            mNextButton.setTextAppearance(this, v.resourceId);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    private void createAccount(String mUsername, String mPassword) {
 		final Account account = new Account(mUsername, PARAM_ACCOUNT_TYPE);
 	
 		if (mRequestNewAccount) {
 		    mAccountManager.addAccountExplicitly(account, mPassword, null);
 		
 			/*
 			 * Turn on periodic syncing. I need to add randomness to the sync interval to make sure 
 			 * that not all users sync at the same time, which will overload the server.
 			 */
 			
 			final Bundle extras = new Bundle();
 			final long random = (long) (Math.random()*1000L);
 			
 			ContentResolver.addPeriodicSync(account,SharpCartContentProvider.AUTHORITY, extras, (8*SYNC_INTERVAL)+random);
 			
 			//Will run the syncadapter everytime we get a network tinkle
 			//ContentResolver.setSyncAutomatically(account,SharpCartContentProvider.AUTHORITY, true);
 			
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
 	
 		//setAccountAuthenticatorResult(intent.getExtras());
 		
 		setResult(RESULT_OK, intent);
 		
 		//Set alarm manager to initiate the sync adapter every day around 10:00 pm
 		/*
 		Context context = getApplication().getApplicationContext();
 		
 		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
 		*/
 		
 		finish();
     }
    
	private void CopyDB(InputStream inputStream, OutputStream outputStream)throws IOException {
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
	
    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }

            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            if (mCurrentPageSequence == null) {
                return 0;
            }
            return Math.min(mCutOffPage + 1, mCurrentPageSequence.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }
}
