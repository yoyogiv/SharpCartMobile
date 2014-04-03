package com.sharpcart.android.wizardpager;

import android.content.Context;

import com.sharpcart.android.wizardpager.wizard.model.AbstractWizardModel;
import com.sharpcart.android.wizardpager.wizard.model.CustomerInfoPage;
import com.sharpcart.android.wizardpager.wizard.model.MultipleFixedChoicePage;
import com.sharpcart.android.wizardpager.wizard.model.PageList;
import com.sharpcart.android.wizardpager.wizard.model.SingleFixedChoicePage;

public class SharpCartWizardModel extends AbstractWizardModel {

	public SharpCartWizardModel(final Context context) {
		super(context);
	}

	@Override
	protected PageList onNewRootPageList() {
        return new PageList(
        		
        		//user Email and Password
                new CustomerInfoPage(this, "Your info")
                .setRequired(true),
                
                //zip code
                /*
                new SingleFixedChoicePage(this, "ZIP Code")
                .setChoices("78613", "78664", "78681", "78717", "78727","78729")
                .setRequired(true),
                */
                
                //family size
                new SingleFixedChoicePage(this, "Family Size")
                .setChoices("Single", "Couple", "Four or less", "Five or more")
                .setRequired(true),
                
                //stores
                new MultipleFixedChoicePage(this, "Select up to 4 Stores").setMaxChoices(4)
                .setChoices("Costco", "HEB", "Sprouts", "Sams Club","Walmart")
                .setRequired(true)
                            
        );
	}

}
