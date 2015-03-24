package com.tcl.netchannellist;

import com.tcl.util.DebugUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class MyListView extends ListView{

	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyListView(Context context) {
		super(context);
	}
	
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		DebugUtil.v("FH", "++++++++++++++++++++++" + event);
		/*if (event.getAction() == MotionEvent.ACTION_SCROLL){
			ChannelListActivity chListActivity = ChannelListActivity.mChannelListActivity;
			if (chListActivity != null){
				chListActivity.onfocusChange(chListActivity.getCurrentFocusView(), chListActivity.FOCUS_STATE_LOSE_FOCUS);
			}
			chListActivity.mHandler.removeMessages(chListActivity.HANDLER_MSG_DELEY_PUT_FOCUS);
			chListActivity.mHandler.sendEmptyMessageDelayed(chListActivity.HANDLER_MSG_DELEY_PUT_FOCUS, 200l);
		}*/
		return super.onGenericMotionEvent(event);
	}
}
