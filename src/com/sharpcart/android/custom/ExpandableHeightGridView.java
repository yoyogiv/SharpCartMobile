package com.sharpcart.android.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.GridView;

public class ExpandableHeightGridView extends GridView
{

    boolean expanded = false;

    public ExpandableHeightGridView(final Context context)
    {
        super(context);
    }

    public ExpandableHeightGridView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ExpandableHeightGridView(final Context context, final AttributeSet attrs,
            final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public boolean isExpanded()
    {
        return expanded;
    }

    @Override
    public void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
    {
        // HACK! TAKE THAT ANDROID!
        if (isExpanded())
        {
        	
            // Calculate entire height by providing a very large height hint.
            // View.MEASURED_SIZE_MASK represents the largest height possible.
            final int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            
            /*
            final ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
            */
        }
        else
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setExpanded(final boolean expanded)
    {
        this.expanded = expanded;
    }

	/* (non-Javadoc)
	 * @see android.widget.AbsListView#onTouchEvent(android.view.MotionEvent)
	 * prevent grid view from scrolling
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		if(ev.getAction() == ev.ACTION_MOVE) 
		{ 
			return true; 
		} 
		
		return super.onTouchEvent(ev);
	}
    
    
}