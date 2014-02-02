package com.example.android.wizardpager;

import android.content.Context;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.BranchPage;
import com.example.android.wizardpager.wizard.model.CustomerInfoPage;
import com.example.android.wizardpager.wizard.model.MultipleFixedChoicePage;
import com.example.android.wizardpager.wizard.model.PageList;
import com.example.android.wizardpager.wizard.model.SingleFixedChoicePage;

public class SharpCartWizardModel extends AbstractWizardModel {

	public SharpCartWizardModel(Context context) {
		super(context);
	}

	@Override
	protected PageList onNewRootPageList() {
        return new PageList(
        		
        		//user Email and Password
                new CustomerInfoPage(this, "Your info")
                .setRequired(true),
                
                //zip code
                new SingleFixedChoicePage(this, "ZIP Code")
                .setChoices("78613", "78664", "78681", "78717", "78727","78729")
                .setRequired(true),
                
                //family size
                new SingleFixedChoicePage(this, "Family Size")
                .setChoices("Single", "Couple", "Four or less", "Five or more")
                .setRequired(true),
                
                //stores
                new MultipleFixedChoicePage(this, "Stores")
                .setChoices("Costco", "HEB", "Sprouts", "Sams Club",
                        "Walmart").setRequired(true)
                        
        );
	}

}
