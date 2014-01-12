package com.sharpcart.android.fragment;

import com.sharpcart.android.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EmailSharpListDialogFragment extends DialogFragment implements OnEditorActionListener{

	private EditText mSharpListName;
	private EditText mEmail;
	
    public interface EmailSharpListDialogFragmentListener {
        void onFinishEditDialog(String sharpListName,String Email);
    }
    
	public EmailSharpListDialogFragment() {
		
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_sharp_list_dialog, container);
        mSharpListName = (EditText) view.findViewById(R.id.sharpListNameEditText);
        mEmail = (EditText) view.findViewById(R.id.sharpListEmailEditText);
        
        getDialog().setTitle("Email Sharp List");
        //getDialog().setTitle(R.id.emailShapListButton);
        
        mEmail.setOnEditorActionListener(this);
        
        return view;
    }

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_DONE == actionId) {
			
			/*
			//validate input
			if(mSharpListName.getText().length()==0)
				mSharpListName.setError("Please enter Sharp List name");
			*/
			
            // Return input text to activity
			EmailSharpListDialogFragmentListener activity = (EmailSharpListDialogFragmentListener) getActivity();
            
			activity.onFinishEditDialog(mSharpListName.getText().toString(),mEmail.getText().toString());
            
            this.dismiss();
            return true;
        }
		
		return false;
	}

}
