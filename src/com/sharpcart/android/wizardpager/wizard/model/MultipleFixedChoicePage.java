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

package com.sharpcart.android.wizardpager.wizard.model;

import com.sharpcart.android.wizardpager.wizard.ui.MultipleChoiceFragment;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * A page offering the user a number of non-mutually exclusive choices.
 */
public class MultipleFixedChoicePage extends SingleFixedChoicePage {
	private int maxChoices = 0;
	
    public MultipleFixedChoicePage(final ModelCallbacks callbacks, final String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return MultipleChoiceFragment.create(getKey());
    }

    @Override
    public void getReviewItems(final ArrayList<ReviewItem> dest) {
        final StringBuilder sb = new StringBuilder();

        final ArrayList<String> selections = mData.getStringArrayList(Page.SIMPLE_DATA_KEY);
        if (selections != null && selections.size() > 0) {
            for (final String selection : selections) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(selection);
            }
        }

        dest.add(new ReviewItem(getTitle(), sb.toString(), getKey()));
    }

    @Override
    public boolean isCompleted() {
        final ArrayList<String> selections = mData.getStringArrayList(Page.SIMPLE_DATA_KEY);
        if (maxChoices==0)
        	maxChoices = selections.size();
        
        return selections != null && selections.size() > 0 && selections.size() <= maxChoices;
    }
    
    public MultipleFixedChoicePage setMaxChoices(final int maxChoices)
    {
    	this.maxChoices = maxChoices;
    	return this;
    }
}
