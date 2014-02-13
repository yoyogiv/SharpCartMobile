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

import com.sharpcart.android.wizardpager.wizard.ui.SingleChoiceFragment;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A page representing a branching point in the wizard. Depending on which choice is selected, the
 * next set of steps in the wizard may change.
 */
public class BranchPage extends SingleFixedChoicePage {
    private final List<Branch> mBranches = new ArrayList<Branch>();

    public BranchPage(final ModelCallbacks callbacks, final String title) {
        super(callbacks, title);
    }

    @Override
    public Page findByKey(final String key) {
        if (getKey().equals(key)) {
            return this;
        }

        for (final Branch branch : mBranches) {
            final Page found = branch.childPageList.findByKey(key);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    @Override
    public void flattenCurrentPageSequence(final ArrayList<Page> destination) {
        super.flattenCurrentPageSequence(destination);
        for (final Branch branch : mBranches) {
            if (branch.choice.equals(mData.getString(Page.SIMPLE_DATA_KEY))) {
                branch.childPageList.flattenCurrentPageSequence(destination);
                break;
            }
        }
    }

    public BranchPage addBranch(final String choice, final Page... childPages) {
        final PageList childPageList = new PageList(childPages);
        for (final Page page : childPageList) {
            page.setParentKey(choice);
        }
        mBranches.add(new Branch(choice, childPageList));
        return this;
    }
    
    public BranchPage addBranch(final String choice) {
        mBranches.add(new Branch(choice, new PageList()));
        return this;
    }

    @Override
    public Fragment createFragment() {
        return SingleChoiceFragment.create(getKey());
    }

    @Override
	public String getOptionAt(final int position) {
        return mBranches.get(position).choice;
    }

    @Override
	public int getOptionCount() {
        return mBranches.size();
    }

    @Override
    public void getReviewItems(final ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY), getKey()));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }

    @Override
    public void notifyDataChanged() {
        mCallbacks.onPageTreeChanged();
        super.notifyDataChanged();
    }

    @Override
	public BranchPage setValue(final String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }

    private static class Branch {
        public String choice;
        public PageList childPageList;

        private Branch(final String choice, final PageList childPageList) {
            this.choice = choice;
            this.childPageList = childPageList;
        }
    }
}
