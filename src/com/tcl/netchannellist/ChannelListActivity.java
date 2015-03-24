package  com.tcl.netchannellist;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.tv.ChannelInfo;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tcl.netchannellist.ChannelListAdapter.onEditableListener;
import com.tcl.netchannellist.TvManagerHelper.ChannelInformation;
import com.tcl.netchannellist.TvManagerHelper.ChannelSchedule;
import com.tcl.netchannellist.TvManagerHelper.TvProgram;
import com.tcl.netchannellist.bean.TableColumn;
import com.tcl.sevencommon.channel.ChannelContract;
import com.tcl.sevencommon.channel.ChannelItem;
import com.tcl.util.DebugUtil;
import com.tcl.util.TvClientTypeList;

@SuppressLint("HandlerLeak")
public class ChannelListActivity extends Activity implements
OnItemClickListener,OnFocusChangeListener,onEditableListener{   
	private static final String TAG = "TosibaChannelListFragment";
	private TextView rightArrow, leftArrow,channelListtype,mFoucsView;

	private LinearLayout programInfoLayout1,programInfoLayout2;
	private TextView programNameTextView, programTimeTextView,
	programNameTextView1, programTimeTextView1,
	programNameTextView2, programTimeTextView2;

	private LinearLayout listviewFocusImageLayout;

	private LinearLayout popMeunContainer;

	private Button mBtnFavorite, mBtnEdit;

	private TextView channelTips;

	private ListView channelListView;

	private ChannelListAdapter listAdapter;

	private int itemHeight;

	private final int DIRECTION_UP = 1;

	private final int DIRECTION_DOWN = 2;

	private final int FOCUS_STATE_GET_FOCUS = 1;

	private final int FOCUS_STATE_LOSE_FOCUS = 2;

	private final int HANDLER_MSG_DELEY_PUT_FOCUS = 1;
	private final int HANDLER_MSG_SHOW_EPG_INFO =100;

	private int focusImagePosition;


	private final int GET_CH_LIST_OK = 102;
	private final int START_GET_CH_LIST = 103;

	public List<ChannelInformation> channelInfoList, favoriteChannelList,networkChannelList;


	private int curType =-1;

	private boolean isDTV = false;

	private static final long DISMISS_TIMEOUT = 10000;

	private enum CHANNEL_MODE {
		NORMAL,
		ATV_EDIT,
		ATV_EDIT_NAME,
		DTV_EDIT,
		NETWORK_EDIT
	}
	private CHANNEL_MODE channelMode = CHANNEL_MODE.NORMAL;

	private TvManagerHelper mTvManager;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

	private int mCurrentPosition=0;
	private String clientType;
	private boolean isFromTv=false;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		isFromTv=getIntent().getBooleanExtra("fromTV", false);
		mTvManager = TvManagerHelper.getInstance(this);  	
		clientType=mTvManager.mTvManager.getClientType();
		setContentView(R.layout.fast_change_channel_activity_layout2);
		isDTV =TvManagerHelper.isDtvSource(mTvManager.getInputSource());      
		channelListView = (ListView)findViewById(R.id.fast_change_channel_listview);
		listviewFocusImageLayout = (LinearLayout)findViewById(R.id.listview_focus_image_linearlayout);
		leftArrow = (TextView)findViewById(R.id.ch_list_left_arrow);
		rightArrow = (TextView)findViewById(R.id.ch_list_right_arrow);
		programNameTextView = (TextView)findViewById(R.id.program_name);
		programTimeTextView = (TextView)findViewById(R.id.program_time);
		channelListtype = (TextView)findViewById(R.id.ch_type);
		popMeunContainer = (LinearLayout)findViewById(R.id.menu_container);
		mFoucsView = (TextView)findViewById(R.id.listview_focus_image);
		channelTips = (TextView)findViewById(R.id.channellist_tips);
		if(isDTV){
			channelTips.setText(R.string.dtv_channellist_tips);
		}else{
			channelTips.setText(R.string.atv_channellist_tips);
		}
		mHandler.sendEmptyMessage(START_GET_CH_LIST);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_MSG_SHOW_EPG_INFO :
				showPopWindow();
				break;
			case HANDLER_MSG_DELEY_PUT_FOCUS:
				onfocusChange(getCurrentFocusView(), FOCUS_STATE_GET_FOCUS);
				break;
			case START_GET_CH_LIST:
				channelInfoList = new ArrayList<ChannelInformation>();
				networkChannelList = new ArrayList<ChannelInformation>();
				networkChannelList = mTvManager.getNetworkChannelList(ChannelListActivity.this);
				favoriteChannelList = new ArrayList<ChannelInformation>();
				if (TvManagerHelper.isAtvSource(mTvManager.getInputSource())) {
					channelInfoList=mTvManager.getAtvChannelList();				
					updateFavoriteChannelList(true);					
				} else if(TvManagerHelper.isDtvSource(mTvManager.getInputSource())) {
					channelInfoList=mTvManager.getDtvChannelList();
					updateFavoriteChannelList(false);			
				}
				mHandler.sendEmptyMessage(GET_CH_LIST_OK);
				break;
			case GET_CH_LIST_OK:
				if(favoriteChannelList.size()>0&&!isFromTv){
					if (TvManagerHelper.isAtvSource(mTvManager.getInputSource())) {
						listAdapter = new ChannelListAdapter(ChannelListActivity.this,
								channelListView.getHeight(), ChannelListAdapter.LIST_TYPE_ATV);
					} else{
						listAdapter = new ChannelListAdapter(ChannelListActivity.this,
								channelListView.getHeight(), ChannelListAdapter.LIST_TYPE_DTV);
					}
					listAdapter.setChannelData(favoriteChannelList);
					channelListtype.setText(R.string.channellist_favorite);
					curType=0;
				}else if(channelInfoList.size()>0){
					if (TvManagerHelper.isAtvSource(mTvManager.getInputSource())) {
						listAdapter = new ChannelListAdapter(ChannelListActivity.this,
								channelListView.getHeight(), ChannelListAdapter.LIST_TYPE_ATV);
					} else{
						listAdapter = new ChannelListAdapter(ChannelListActivity.this,
								channelListView.getHeight(), ChannelListAdapter.LIST_TYPE_DTV);
					}
					listAdapter.setChannelData(channelInfoList);
					channelListtype.setText(R.string.channellist_all);
					curType=1;
				}else{
					listAdapter = new ChannelListAdapter(ChannelListActivity.this,
							channelListView.getHeight(), ChannelListAdapter.LIST_TYPE_NETWORK);
					listAdapter.setChannelData(networkChannelList);
					channelListtype.setText(R.string.channellist_network_channel);
					curType=2;
				}

				listAdapter.setOnEditableListener(ChannelListActivity.this);
				channelListView.setAdapter(listAdapter);
				channelListView.setOnScrollListener(new OnScrollListener() {
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState) {
						DebugUtil.v(TAG, "onScrollStateChanged==" + scrollState);
						if (scrollState == SCROLL_STATE_IDLE) {
							correctListPosition();
						}
					}
					@Override
					public void onScroll(AbsListView view, int firstVisibleItem,
							int visibleItemCount, int totalItemCount) {
					}
				});

				itemHeight = listAdapter.getItemHeight();
				int location[] = { 2, 2 };
				channelListView.getLocationOnScreen(location);
				listviewFocusImageLayout.setY(location[1]
						- (listviewFocusImageLayout.getHeight() - itemHeight) / 2);
				int curChPosition = 0;
				curChPosition = getCurChPosition();
				if (curChPosition != -1) {
					setFocusItemByPosition(curChPosition);
				}
				else {
					setFocusItemByPosition(0);
				}
				mHandler.sendEmptyMessageDelayed(HANDLER_MSG_DELEY_PUT_FOCUS, 500);
				// DebugUtil.v(TAG, "X ========= " + fastChangeChannelListLayout.getX());
				// DebugUtil.v(TAG, "width is : " + fastChangeChannelListLayout.getWidth());
				if (listAdapter.getCount() <= listAdapter.getItemCountInOneScreen()) {
					leftArrow.setVisibility(View.INVISIBLE);
					rightArrow.setVisibility(View.INVISIBLE);
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	private static String getTimeString(Date start, Date end) {
		return String.format("%s-%s", 
				DATE_FORMAT.format(start),
				DATE_FORMAT.format(end));
	}

	private void setCurrentProgramText() {

		if(!isDTV){
			popMeunContainer.setVisibility(View.INVISIBLE);
			return;
		}
		popMeunContainer.removeAllViews();
		DebugUtil.v(TAG, " ____Channel List setCurrentProgramText ");

		//ChannelSchedule epgLists = mTvManager.getCurrentChannelSchedule();

		ChannelInformation specProgram = (ChannelInformation) listAdapter.getItem(getFocusItemIndex());

		if(specProgram==null||specProgram.sourceType==11||specProgram.sourceType==12)
			return;

		ChannelInfo ci =mTvManager.getChanneIInfoByIndex(specProgram.channelIndex);

		ChannelSchedule epgLists = mTvManager.getChannelSchduleByChannelInfo( ci, 0);

		if (epgLists == null){
			DebugUtil.v(TAG, "___setCurrentProgramText__  epgLists is null!");
			popMeunContainer.removeAllViews();
			popMeunContainer.setVisibility(View.VISIBLE);
			return;
		}

		mCurrentPosition = epgLists.findLatestProgramPosition(mTvManager.currentTvTimeMillis());
		TvProgram Curprogram = null;
		TvProgram NextProgram = null;
		TvProgram NNextProgram2 = null;
		Curprogram = epgLists.optProgramAt(mCurrentPosition);
		NextProgram = epgLists.optProgramAt(mCurrentPosition + 1);
		NNextProgram2 = epgLists.optProgramAt(mCurrentPosition + 2);

		if((Curprogram!=null)&&(null != this))
		{
			DebugUtil.v(TAG, "------Curprogram  00000000000   is NOT not  null!");
			popMeunContainer.removeAllViews();
			popMeunContainer.addView(getEPGInfoView());
			popMeunContainer.setVisibility(View.VISIBLE);
			programNameTextView.setText(Curprogram.title);
			programTimeTextView.setText(getTimeString(Curprogram.startTime, Curprogram.endTime));
		}      

		if(NextProgram!=null&&(null != this)){
			DebugUtil.v(TAG, "NextProgram 111111  is  not null!");
			if (NextProgram.startTime!= null && NextProgram.endTime!= null && NextProgram.title != null) {
				programNameTextView1.setText(NextProgram.title);
				programTimeTextView1.setText(getTimeString(NextProgram.startTime, NextProgram.endTime));
			}else{
				programInfoLayout1.setVisibility(View.GONE);
				return;
			}
		}

		if(NNextProgram2!=null&&(null != this)){
			if (NNextProgram2.startTime!= null && NNextProgram2.endTime!= null && NNextProgram2.title != null) {
				programNameTextView2.setText(NNextProgram2.title);
				programTimeTextView2.setText(getTimeString(NNextProgram2.startTime, NNextProgram2.endTime));
			}else{
				programInfoLayout2.setVisibility(View.GONE);
				return;
			}
		}

		//  if(epgLists.size() < 3){
		//	 mHandler.removeMessages(HANDLER_MSG_SHOW_EPG_INFO);
		//     mHandler.sendEmptyMessageDelayed(HANDLER_MSG_SHOW_EPG_INFO, 3000);
		//}
	}

	private int getCurChPosition() {

		ChannelInformation curProgramInfo = mTvManager.getCurrentChannelInformation();
		if (curProgramInfo == null) {
			return -1;
		}
		int curChPosition;
		if (curType==1||curType==2) {
			for (curChPosition = 0; curChPosition < channelInfoList.size(); curChPosition++) {
				if (channelInfoList.get(curChPosition).channelNumber == curProgramInfo.channelNumber) {
					break;
				}
			}
			if (curChPosition >= channelInfoList.size()) {
				return -1;
			} else {
				return curChPosition;
			}
		} else {
			for (curChPosition = 0; curChPosition < favoriteChannelList.size(); curChPosition++) {
				if (favoriteChannelList.get(curChPosition).channelNumber == curProgramInfo.channelNumber) {
					break;
				}
			}

			if (curChPosition >= favoriteChannelList.size()) {
				return -1;
			} else {
				return curChPosition;
			}
		}

	}
	public void onfocusChange(View view, int focusState) {
		DebugUtil.v(TAG, "######### onfocusChange  START ");
		if (view == null) {
			return;
		}
		if (focusState == FOCUS_STATE_GET_FOCUS) {
			setFocusItemByPosition(getFocusItemIndex());
			((TextView) view.findViewById(R.id.channellist_item_text_number))
			.setTextColor(Color.WHITE);
			((TextView) view.findViewById(R.id.channellist_item_text_name))
			.setTextColor(Color.WHITE);
			((TextView) view.findViewById(R.id.channellist_item_text_name)).setSelected(true);
			mHandler.removeMessages(HANDLER_MSG_SHOW_EPG_INFO);
			mHandler.sendEmptyMessageDelayed(HANDLER_MSG_SHOW_EPG_INFO, 0);
			((TextView) view.findViewById(R.id.channellist_item_text_name))
			.setEllipsize(TruncateAt.MARQUEE);
		}
		else if (focusState == FOCUS_STATE_LOSE_FOCUS) {
			((TextView) view.findViewById(R.id.channellist_item_text_number))
			.setTextColor(0xFFAAAAAA);
			((TextView) view.findViewById(R.id.channellist_item_text_name))
			.setTextColor(0xFFAAAAAA);
			((TextView) view.findViewById(R.id.channellist_item_text_name)).setSelected(false);
			popMeunContainer.setVisibility(View.INVISIBLE);
			((TextView) view.findViewById(R.id.channellist_item_text_name))
			.setEllipsize(TruncateAt.END);
		}
		DebugUtil.v(TAG, "######### onfocusChange END END");

	}

	private void setFocusItemByPosition(int position) {
		mFoucsView.setVisibility(View.VISIBLE);
		if (position > listAdapter.getCount() - 1) {
			position = listAdapter.getCount() - 1;
		}
		if (position < 0) {
			position = 0;
		}

		DebugUtil.v(TAG, "#########setFocusItemByPosition position== " + position);
		if (listAdapter.getCount() <= listAdapter.getItemCountInOneScreen()) {
			listviewFocusImageLayout.setY(listviewFocusImageLayout.getY()
					+ (position - focusImagePosition) * itemHeight);
			focusImagePosition = position;
		}
		else {
			if (position >= 0 && position <= listAdapter.getItemCountInOneScreen() / 2) {
				channelListView.setSelection(0);
				listviewFocusImageLayout.setY(listviewFocusImageLayout.getY()
						+ (position - focusImagePosition) * itemHeight);
				focusImagePosition = position;
			}
			else if (position <= listAdapter.getCount() - 1
					&& position >= listAdapter.getCount() - 1
					- listAdapter.getItemCountInOneScreen() / 2) {
				channelListView.setSelection(listAdapter.getCount() - 1);
				listviewFocusImageLayout
				.setY(listviewFocusImageLayout.getY()
						+ (position
								- (listAdapter.getCount() - listAdapter
										.getItemCountInOneScreen()) - focusImagePosition)
										* itemHeight);
				focusImagePosition = position
						- (listAdapter.getCount() - listAdapter.getItemCountInOneScreen());
			}
			else {
				channelListView.setSelection(position - listAdapter.getItemCountInOneScreen() / 2);
				int location[] = { 2, 2 };
				channelListView.getLocationOnScreen(location);
				listviewFocusImageLayout.setY(location[1]
						- (listviewFocusImageLayout.getHeight() - itemHeight) / 2);
				float y = listviewFocusImageLayout.getY() + ((listAdapter.getItemCountInOneScreen() / 2 ) * itemHeight);
				listviewFocusImageLayout.setY(y);

				focusImagePosition = listAdapter.getItemCountInOneScreen() / 2;
			}
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			int keyCode=event.getKeyCode();
			String deviceName = InputDevice.getDevice(event.getDeviceId()).getName();
			mHandler.removeCallbacks(mRunDismiss);
			mHandler.postDelayed(mRunDismiss, DISMISS_TIMEOUT);
			if(keyCode == KeyEvent.KEYCODE_TV_INPUT&&"Smart_TV_Keypad".equals(deviceName)) {
				keyCode = KeyEvent.KEYCODE_DPAD_CENTER;
			}
			//	if (listAdapter == null || channelInfoList.size() == 0) { 
			//		switch (keyCode) {
			//		case KeyEvent.KEYCODE_BACK:
			//			finish();
			//			return true;
			//		case KeyEvent.KEYCODE_DPAD_CENTER:
			//
			//		case KeyEvent.KEYCODE_ENTER:
			//			return true;
			//		default: 
			//			return super.onKeyDown(keyCode, event);
			//		}
			//	}    
			DebugUtil.v(TAG, "________OnKeyDown  Onkey Down ");
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP:
				if(channelMode == CHANNEL_MODE.ATV_EDIT_NAME){ 
					channelMode = CHANNEL_MODE.NORMAL;                    
					DebugUtil.v(TAG, "ivan______key up up_____sss333333333331" );
					listAdapter.exitEditChannelName();
					//remove by huangfh@tcl.com 09-10 mHandler.removeMessages(HANDLER_MSG_SHOW_EPG_INFO);
					//remove by huangfh@tcl.com 09-10 mHandler.sendEmptyMessageDelayed(HANDLER_MSG_SHOW_EPG_INFO, 500);
				}else if(channelMode == CHANNEL_MODE.DTV_EDIT||channelMode == CHANNEL_MODE.ATV_EDIT||channelMode == CHANNEL_MODE.NETWORK_EDIT){
					channelMode = CHANNEL_MODE.NORMAL;
				}
				if (getFocusItemIndex() == 0) {
					setFocusItemByPosition(listAdapter.getCount() - 1);
					mHandler.removeMessages(HANDLER_MSG_DELEY_PUT_FOCUS);
					mHandler.sendEmptyMessageDelayed(HANDLER_MSG_DELEY_PUT_FOCUS, 500);
					return true;
				}
				onfocusChange(getCurrentFocusView(), FOCUS_STATE_LOSE_FOCUS);
				if (listAdapter.getCount() <= listAdapter.getItemCountInOneScreen()) {
					moveFocusImage(DIRECTION_UP);
				}  else {
					if (channelListView.getFirstVisiblePosition() == 0
							&& channelListView.getChildAt(0).getY() == 0) {
						moveFocusImage(DIRECTION_UP);
					}
					else if (focusImagePosition > listAdapter.getItemCountInOneScreen() / 2) {
						moveFocusImage(DIRECTION_UP);
					}
					else {
						listScroll(DIRECTION_DOWN, 1, 0);

					}
				}
				return true;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if(channelMode == CHANNEL_MODE.ATV_EDIT_NAME){ 
					channelMode = CHANNEL_MODE.NORMAL;                       
					DebugUtil.v(TAG, "ivan______key down down _____sss222222222221" );
					listAdapter.exitEditChannelName();
					//remove by huangfh@tcl.com 09-10  mHandler.removeMessages(HANDLER_MSG_SHOW_EPG_INFO);
					//remove by huangfh@tcl.com 09-10 mHandler.sendEmptyMessageDelayed(HANDLER_MSG_SHOW_EPG_INFO, 500);
				}else if(channelMode == CHANNEL_MODE.DTV_EDIT||channelMode == CHANNEL_MODE.ATV_EDIT||channelMode == CHANNEL_MODE.NETWORK_EDIT){
					channelMode = CHANNEL_MODE.NORMAL;
				}
				onfocusChange(getCurrentFocusView(), FOCUS_STATE_LOSE_FOCUS);

				if (getFocusItemIndex() == listAdapter.getCount() - 1) {

					DebugUtil.v(TAG, "ivan______key down down 33333" );
					setFocusItemByPosition(0);
					mHandler.removeMessages(HANDLER_MSG_DELEY_PUT_FOCUS);
					mHandler.sendEmptyMessageDelayed(HANDLER_MSG_DELEY_PUT_FOCUS, 500);
					return true;
				}
				if (listAdapter.getCount() <= listAdapter.getItemCountInOneScreen()) {

					DebugUtil.i(TAG,"@@111111111");
					moveFocusImage(DIRECTION_DOWN);
				}  else {

					DebugUtil.i(TAG,"@@22222");
					if (getFirstVisibleItemIndex() == listAdapter.getCount()
							- listAdapter.getItemCountInOneScreen()) {
						moveFocusImage(DIRECTION_DOWN);
					}
					else if (focusImagePosition < listAdapter.getItemCountInOneScreen() / 2) {
						moveFocusImage(DIRECTION_DOWN);
					}
					else {
						listScroll(DIRECTION_UP, 1, 0);
					}
				}
				return true;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if(channelMode == CHANNEL_MODE.NORMAL){ 
					curType--;
					changeChannelType();
					showPopWindow();
					return true;
				}  
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if(channelMode == CHANNEL_MODE.NORMAL){ 
					curType++;
					changeChannelType();
					showPopWindow();
					return true;
				}else if(channelMode==CHANNEL_MODE.ATV_EDIT||channelMode==CHANNEL_MODE.DTV_EDIT)
				{
					mHandler.removeMessages(HANDLER_MSG_SHOW_EPG_INFO);
					if(channelMode==CHANNEL_MODE.DTV_EDIT)
						mBtnFavorite.requestFocus();
				}

				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
				DebugUtil.v(TAG, "now location is " + channelListView.getFirstVisiblePosition()
						+ "  " + channelListView.getChildAt(0).getY());
				DebugUtil.v(TAG, "now focus position is : " + getFocusItemIndex());

				if(channelMode == CHANNEL_MODE.NORMAL){
					if (curType==1) {
						int index = getFocusItemIndex();
						if(index != getCurChPosition()){
							switchToChannel(index);
						}
						return true;
					} else if(curType==0||curType==2){
						int index = getFocusItemIndex();
						ChannelInformation programInfo = (ChannelInformation) listAdapter.getItem(index);
						ChannelInformation curProgramInfo =mTvManager.getCurrentChannelInformation();
						if (programInfo != null) {
							int channelNum = -1;
							channelNum = programInfo.channelNumber;
							if (programInfo.sourceType == 11) {
								startCategory(programInfo.packetid, programInfo.categoryid);
							}else if (programInfo.sourceType == 12) {
								channelNum = programInfo.channelNumber;
								startChannel(channelNum);
							}else mTvManager.setCurrentChannel(channelNum);
						}                        
						DebugUtil.v(TAG, "33333333 KEYCODE_ENTER  focus position is3333333 : " + getFocusItemIndex());

						mHandler.removeMessages(HANDLER_MSG_SHOW_EPG_INFO);

						this.finish();
						return true;                        
					}
				}  

				break;
			case KeyEvent.KEYCODE_BACK:
				if(channelMode != CHANNEL_MODE.NORMAL){ //Exit edit mode
					channelMode = CHANNEL_MODE.NORMAL; 

					DebugUtil.v(TAG, "ivan_____KEYCODE_BACK ______sss1111111" );
					listAdapter.exitEditChannelName();   //exit rename==
					//********add by huangfh@tcl.com 09-12
					showPopWindow();
					//******end
					mHandler.removeMessages(HANDLER_MSG_SHOW_EPG_INFO);
					// mHandler.sendEmptyMessageDelayed(HANDLER_MSG_SHOW_EPG_INFO, 500);
					return true; 
				}                  
				mHandler.removeMessages(HANDLER_MSG_SHOW_EPG_INFO);
				this.finish();
				break;
			case KeyEvent.KEYCODE_F2:
			case KeyEvent.KEYCODE_MENU:
				if(channelMode != CHANNEL_MODE.NORMAL){ //Exit edit mode
					channelMode = CHANNEL_MODE.NORMAL;
					listAdapter.setEditPos(-1);  //exit rename
				}else {
					if (curType == 2 || curType == 0) {
						channelMode = CHANNEL_MODE.NETWORK_EDIT;
					} else if(TvManagerHelper.isAtvSource(mTvManager.getInputSource())){  //Enter eidt mode
						channelMode = CHANNEL_MODE.ATV_EDIT; 
					} else{
						channelMode = CHANNEL_MODE.DTV_EDIT; 
					}                        
				}
				showPopWindow();
				break;
			default:
				break;              
			}
		}
		return super.dispatchKeyEvent(event);

	}

	private void showPopWindow(){

		if(channelMode == CHANNEL_MODE.NORMAL){

			DebugUtil.d(TAG, "   showPopWindow:	 CHANNEL_MODE.NORMAL ");
			setCurrentProgramText();         
			if(TvManagerHelper.isDtvSource(mTvManager.getInputSource())){
				channelTips.setText(R.string.dtv_channellist_tips);
			}else{
				channelTips.setText(R.string.atv_channellist_tips);
			}
		}else if(channelMode == CHANNEL_MODE.ATV_EDIT){
			DebugUtil.d(TAG, "     showPopWindow   :   CHANNEL_MODE.ATV_EDIT");
			popMeunContainer.removeAllViews();
			popMeunContainer.addView(getATVEditView());
			popMeunContainer.setVisibility(View.VISIBLE);
			channelTips.setText(R.string.atv_channellist_tips1);
		}else if(channelMode == CHANNEL_MODE.DTV_EDIT){
			DebugUtil.d(TAG, "    showPopWindow    :CHANNEL_MODE.DTV_EDIT");
			popMeunContainer.removeAllViews();
			popMeunContainer.addView(getDTVEditView());
			popMeunContainer.setVisibility(View.VISIBLE);
			channelTips.setText(R.string.dtv_channellist_tips1);
		} else if(channelMode == CHANNEL_MODE.ATV_EDIT_NAME){

			DebugUtil.d(TAG, "    showPopWindow    :CHANNEL_MODE.ATV_EDIT_NAME");
			popMeunContainer.removeAllViews();
			channelTips.setText(R.string.atv_channellist_tips1);
		}else if (channelMode == CHANNEL_MODE.NETWORK_EDIT) {
			DebugUtil.d(TAG, "    showPopWindow    :CHANNEL_MODE.NETWORK_EDIT");
			popMeunContainer.removeAllViews();
			popMeunContainer.addView(getNetWorkChannelEditView());
			popMeunContainer.setVisibility(View.VISIBLE);
			channelTips.setText(R.string.dtv_channellist_tips1);
		}
	}


	private void changeChannelType(){
		DebugUtil.e(TAG, "___________changeChannelType");
		if(TvManagerHelper.isAtvSource(mTvManager.getInputSource())){
			updateFavoriteChannelList(true);			
		}else{
			updateFavoriteChannelList(false);			
		}
		if(favoriteChannelList.size()==0) {
			if(clientType.equals(TvClientTypeList.RowaClient)){
				if(channelInfoList.size()==0){
					curType = 2;	
				}else{
					curType = curType < 1 ? 2 : curType;
					curType = curType > 2 ? 1 : curType;
				}
			}else{				
				curType = 1;
			}
		}else if(channelInfoList.size()==0){
			if(clientType.equals(TvClientTypeList.RowaClient)){
				if(favoriteChannelList.size()==0){
					curType = 2;
				}else{
					if(curType<0) curType=2;
					else if(curType<2) curType=2;
					else curType=0;
				}
			}else{
				curType = 1;
			}
		}else{
			if(clientType.equals(TvClientTypeList.RowaClient)){
				curType = curType < 0 ? 2 : curType;
				curType = curType > 2 ? 0 : curType;				
			}else{
				curType = curType < 0 ? 1 : curType;
				curType = curType > 1 ? 0 : curType;	
			}
		}
		if(curType==1){
			DebugUtil.v(TAG, " ivan_____isAllChannel  changeChannelType  " );
			channelListtype.setText(R.string.channellist_all);
			if (TvManagerHelper.isAtvSource(mTvManager.getInputSource())) {
				listAdapter.setListType(ChannelListAdapter.LIST_TYPE_ATV);
			}else if (TvManagerHelper.isDtvSource(mTvManager.getInputSource())) {
				listAdapter.setListType(ChannelListAdapter.LIST_TYPE_DTV);
			}

			listAdapter.setChannelData(channelInfoList);
			// mHandler.sendEmptyMessage(GET_CH_LIST_OK);   
		}else if(curType==2){		
			channelListtype.setText(R.string.channellist_network_channel);
			listAdapter.setListType(ChannelListAdapter.LIST_TYPE_NETWORK);
			listAdapter.setChannelData(networkChannelList);
		}else if(curType==0){			
			if(favoriteChannelList.size()>0){
				channelListtype.setText(R.string.channellist_favorite);
				if (TvManagerHelper.isAtvSource(mTvManager.getInputSource())) {
					listAdapter.setListType(ChannelListAdapter.LIST_TYPE_ATV);
				}else if (TvManagerHelper.isDtvSource(mTvManager.getInputSource())) {
					listAdapter.setListType(ChannelListAdapter.LIST_TYPE_DTV);
				}
				listAdapter.setChannelData(favoriteChannelList);
			}
		}      	
		int curChPosition = curType ==1?getCurChPosition():0;
		setFocusItemByPosition(curChPosition);
	}


	private void updateFavoriteChannelList(boolean flag){
		if(flag)
		{
			channelInfoList=mTvManager.getAtvChannelList();
			favoriteChannelList.clear();
			for (ChannelInformation info : channelInfoList) {
				if(info != null && info.isFavorite){
					favoriteChannelList.add(info);
				}
			}
		}
		else
		{
			channelInfoList=mTvManager.getDtvChannelList();
			favoriteChannelList.clear();
			for(int i=0;i<channelInfoList.size();i++)
				if(channelInfoList.get(i).isFavorite==true)
					favoriteChannelList.add(channelInfoList.get(i));

		}

		for (ChannelInformation info : networkChannelList) {
			if (info != null && info.isFavorite) {
				favoriteChannelList.add(info);
			}
		}

	}

	private void switchToChannel(int position) {
		DebugUtil.v(TAG, "switchToChannel  position : " + position);
		if (channelInfoList != null) {
			ChannelInformation programInfo = (ChannelInformation) listAdapter.getItem(position);// channelInfoList.get(position);
			if (programInfo != null) {
				int channelNum = -1;
				channelNum = programInfo.channelNumber;
				mTvManager.setCurrentChannel(channelNum);			      
				//		        getFragmentManager().beginTransaction().remove(this).commit();
				// Activity a = this;
				// if(a instanceof Tv_strategy)
				// ((Tv_strategy) a).checkSignal();

			}
		}
	}
	private void startCategory(String packetid, String categoryid) {
		Intent intent = new Intent();
		intent.setAction("com.starcor.hunan.mgtv");
		intent.putExtra("cmd_ex", "show_category");
		intent.putExtra("packet_id", packetid);
		intent.putExtra("category_id", categoryid);
		ComponentName cn = new ComponentName("com.starcor.hunan",
				"com.starcor.hunan.SplashActivity");
		intent.setComponent(cn);
		startActivity(intent);
		//		finish();
	}
	private void startChannel(int channelNum) {
		Intent intent = new Intent("com.tcl.channel.StartChannelService");
		intent.putExtra("channelNum", channelNum);
		startService(intent);
		//		finish();
	}
	private void listScroll(int direction, int offset, int duration) {
		DebugUtil.v(TAG, "-------------------------------listScroll!!!!! before coordinate is ("
				+ channelListView.getFirstVisiblePosition() + ","
				+ (int) channelListView.getChildAt(0).getY() + ")");
		if (direction == DIRECTION_DOWN) {
			int surplusScollDistance = (int) (channelListView.getFirstVisiblePosition()
					* itemHeight + (-channelListView.getChildAt(0).getY()));
			if (surplusScollDistance > offset * itemHeight) {
				channelListView.smoothScrollBy(-itemHeight * offset, duration);
			}
			else {
				channelListView.smoothScrollBy(-surplusScollDistance, duration);
			}
			DebugUtil.v(TAG, "count is " + channelListView.getAdapter().getCount());
		} else if (direction == DIRECTION_UP) {
			int firstVisiblePositionMaxValue = listAdapter.getCount()
					- listAdapter.getItemCountInOneScreen();
			if (firstVisiblePositionMaxValue < 0) {
				firstVisiblePositionMaxValue = 0;
			}
			int surplusScollDistance = (int) (itemHeight
					* (firstVisiblePositionMaxValue - channelListView.getFirstVisiblePosition() - 1) + (itemHeight + channelListView
							.getChildAt(0).getY()));
			if (surplusScollDistance > offset * itemHeight) {
				channelListView.smoothScrollBy(itemHeight * offset, duration);
			}
			else {
				channelListView.smoothScrollBy(surplusScollDistance, duration);
			}
		}
	}

	private void correctListPosition() {
		if(null == channelListView || null == channelListView.getChildAt(0)) return;
		DebugUtil.v(TAG,
				"begin to correctListPosition! before position is ("
						+ channelListView.getFirstVisiblePosition() + ","
						+ (int) channelListView.getChildAt(0).getY() + ")");
		int i = (int) channelListView.getChildAt(0).getY();
		if (i >= -itemHeight / 2) {
			channelListView.smoothScrollBy(i, 500);
		} else {
			channelListView.smoothScrollBy(itemHeight + i, 500);
		}
		if (i == 0 || i == -itemHeight) {
			DebugUtil.i(TAG,"AAAAAAAAAAA");
			onfocusChange(getCurrentFocusView(), FOCUS_STATE_GET_FOCUS);
		}
	}

	private int getFirstVisibleItemIndex() {        

		if(channelListView.getChildCount() <= 0){
			return 0;
		}
		if (channelListView.getChildAt(0).getY() == -itemHeight) {

			DebugUtil.i(TAG,"getFirstVisibleItemIndex channelListView.getFirstVisiblePosition() =="+channelListView.getFirstVisiblePosition());
			return channelListView.getFirstVisiblePosition() + 1;
		} else
			return channelListView.getFirstVisiblePosition();
	}


	private int getFocusItemIndex() {

		DebugUtil.i(TAG,"___getFirstVisibleItemIndex getFirstVisibleItemIndex() =="+getFirstVisibleItemIndex());

		DebugUtil.i(TAG,"______focusImagePosition =="+focusImagePosition);
		return getFirstVisibleItemIndex() + focusImagePosition;
	}


	public View getCurrentFocusView() {
		if (channelListView.getChildAt(0) == null) {
			return null;
		}   
		if (channelListView.getChildAt(0).getY() == -itemHeight) {
			DebugUtil.i(TAG,"getCurrentFocusView channelListView.getChildAt(0).getY()=="+channelListView.getChildAt(0).getY());
			DebugUtil.i(TAG,"getCurrentFocusView -itemHeight=="+(-itemHeight));
			DebugUtil.i(TAG,"focusImagePosition==="+focusImagePosition);
			return channelListView.getChildAt(focusImagePosition + 1);
		} else {
			return channelListView.getChildAt(focusImagePosition);
		}
	}


	private void moveFocusImage(int direction) {
		if (direction == DIRECTION_DOWN) {
			if (focusImagePosition < listAdapter.getCount() - 1
					&& focusImagePosition < listAdapter.getItemCountInOneScreen() - 1) {
				listviewFocusImageLayout.setY(listviewFocusImageLayout.getY() + itemHeight);
				focusImagePosition++;
			}
			onfocusChange(getCurrentFocusView(), FOCUS_STATE_GET_FOCUS);
		} else if (direction == DIRECTION_UP) {
			if (focusImagePosition > 0) {
				listviewFocusImageLayout.setY(listviewFocusImageLayout.getY() - itemHeight);
				focusImagePosition--;
			}
			onfocusChange(getCurrentFocusView(), FOCUS_STATE_GET_FOCUS);
		}
	}
	private View getEPGInfoView(){
		View view = LayoutInflater.from(this).inflate(R.layout.channel_epg_info, null);
		programNameTextView = (TextView)view.findViewById(R.id.program_name);
		programTimeTextView = (TextView)view.findViewById(R.id.program_time);
		programNameTextView1 = (TextView)view.findViewById(R.id.program_name1);
		programTimeTextView1 = (TextView)view.findViewById(R.id.program_time1);
		programNameTextView2 = (TextView)view.findViewById(R.id.program_name2);
		programTimeTextView2 = (TextView)view.findViewById(R.id.program_time2);
		programInfoLayout1 = (LinearLayout)view.findViewById(R.id.program_info_layout1);
		programInfoLayout2 = (LinearLayout)view.findViewById(R.id.program_info_layout2);
		return view;  
	}


	private View getATVEditView(){
		View view = LayoutInflater.from(this).inflate(R.layout.channel_edit, null);
		if(!clientType.equals(TvClientTypeList.RowaClient_82))
			view.setBackgroundResource(R.drawable.favorite_edit_bottom);
		mBtnEdit = (Button)view.findViewById(R.id.editName);
		mBtnFavorite = (Button)view.findViewById(R.id.addFavorite);

		DebugUtil.v(TAG, "________getATVEditView   : ");

		final int pos = getFocusItemIndex();

		final boolean isFavorite = listAdapter.isFavorite(pos);
		if(isFavorite){
			mBtnFavorite.setText(R.string.channellist_delfavorite);
		}else{
			mBtnFavorite.setText(R.string.channellist_addfavorite);
		}
		mBtnFavorite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				DebugUtil.v(TAG, "getATVEditView  mBtnFavorite : ");


				boolean  Favorite=listAdapter.isFavorite(pos);

				ChannelInformation programInfo = (ChannelInformation) listAdapter.getItem(pos);

				mTvManager.setChannelFav(programInfo.channelIndex,!Favorite);


				if(listAdapter.isFavorite(pos)){
					mBtnFavorite.setText(R.string.channellist_delfavorite);
				}else{
					mBtnFavorite.setText(R.string.channellist_addfavorite);
				}              
				DebugUtil.v(TAG, "getATVEditView	kkkkkkkkkkkkkkk : ");             
				listAdapter.notifyDataSetChanged();

			}
		});

		mBtnEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				DebugUtil.v(TAG, "switchToChannel  positio33333333333333333333 : ");
				int pos = getFocusItemIndex();
				listAdapter.setEditPos(pos);
				channelMode = CHANNEL_MODE.ATV_EDIT_NAME;
				showPopWindow();               
				//Toast.makeText(ChannelListActivity.this, ""+programInfo.number+"."+programInfo.programName, Toast.LENGTH_SHORT).show();
			}
		});

		DebugUtil.v(TAG, "switchToChannel  positio777777777777777777772 : ");
		initBtnFocusListener();
		return view;
	}



	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus &&(v == mBtnEdit || v == mBtnFavorite)){
			mFoucsView.setVisibility(View.INVISIBLE);
		} else{
			mFoucsView.setVisibility(View.VISIBLE);
		}
	}

	private View getDTVEditView(){
		View view = LayoutInflater.from(this).inflate(R.layout.channel_edit, null);
		if(!clientType.equals(TvClientTypeList.RowaClient_82))
			view.setBackgroundResource(R.drawable.edit_bottom);
		mBtnEdit = (Button)view.findViewById(R.id.editName);
		mBtnEdit.setVisibility(View.GONE);
		mBtnFavorite = (Button)view.findViewById(R.id.addFavorite);

		final int pos = getFocusItemIndex();
		final boolean isFavorite = listAdapter.isFavorite(pos);
		if(isFavorite){

			DebugUtil.v(TAG, " &&&_________00000");
			mBtnFavorite.setText(R.string.channellist_delfavorite);
		}else{

			DebugUtil.v(TAG, " &&&_________111111");
			mBtnFavorite.setText(R.string.channellist_addfavorite);
		}
		mBtnFavorite.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){


				DebugUtil.v(TAG, "   getDTVEditView     mBtnFavorite   pos======: "+pos);
				boolean  Favorite=listAdapter.isFavorite(pos);
				ChannelInformation programInfo = (ChannelInformation) listAdapter.getItem(pos);

				// programInfo.isFavorite= !programInfo.isFavorite
				// Favorite=!Favorite;

				DebugUtil.v(TAG, "	 getDTVEditView  programInfo.channelIndex======: "+programInfo.channelIndex);

				mTvManager.setChannelFav(programInfo.channelIndex, !Favorite);


				DebugUtil.v(TAG, "   getDTVEditView     mBtnFavorite   pos  end !!!!!======");

				if(listAdapter.isFavorite(pos)){
					mBtnFavorite.setText(R.string.channellist_delfavorite);
				}else{
					mBtnFavorite.setText(R.string.channellist_addfavorite);
				}
				listAdapter.notifyDataSetChanged();
			}
		});
		initBtnFocusListener();
		return view;
	}
	private View getNetWorkChannelEditView() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.channel_edit, null);
		if(!clientType.equals(TvClientTypeList.RowaClient_82))
			view.setBackgroundResource(R.drawable.edit_bottom);
		mBtnEdit = (Button) view.findViewById(R.id.editName);
		mBtnEdit.setVisibility(View.GONE);
		mBtnFavorite = (Button) view.findViewById(R.id.addFavorite);

		final int pos = getFocusItemIndex();
		final boolean isFavorite = listAdapter.isFavorite(pos);
		if (isFavorite) {
			mBtnFavorite.setText(R.string.channellist_delfavorite);
		} else {
			mBtnFavorite.setText(R.string.channellist_addfavorite);
		}
		mBtnFavorite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChannelInformation programInfo = (ChannelInformation) listAdapter
						.getItem(pos);
				if (programInfo.sourceType == 11) {
					// 更新数据库数据
					DebugUtil.d("cedar", "更新数据库数据11...isFavorite = "
							+ !programInfo.isFavorite);
					int favoriteCV = !programInfo.isFavorite ? 1 : 0;
					ContentValues cv = new ContentValues();
					cv.put(TableColumn.COLUMN_FAVORITE, favoriteCV);
					getContentResolver().update(TableColumn.CONTENT_URI, cv,
							TableColumn._ID + "=?",
							new String[] { programInfo.DbId });
					programInfo.isFavorite = !programInfo.isFavorite;
				} else if (programInfo.sourceType == 12) {
					// 更新数据库数据
					DebugUtil.d("cedar", "更新数据库数据12...isFavorite = "
							+ !programInfo.isFavorite);
					int favoriteCV = !programInfo.isFavorite ? 1 : 0;
					ContentValues cv = new ContentValues();
					cv.put(ChannelItem.COLUMN_FAVORITE, favoriteCV);
					cv.put(ChannelItem.CHANNEL_NUMBER,
							programInfo.channelNumber);
					getContentResolver().update(
							Uri.parse(ChannelContract.AUTHORITY_URI
									+ "/channel"), cv, null, null);
					programInfo.isFavorite = !programInfo.isFavorite;
				} else {
					mTvManager.setChannelFav(programInfo.channelIndex,
							!programInfo.isFavorite);
				}
				if (programInfo.isFavorite) {
					mBtnFavorite.setText(R.string.channellist_delfavorite);
				} else {
					mBtnFavorite.setText(R.string.channellist_addfavorite);
				}
				listAdapter.notifyDataSetChanged();
			}
		});
		initBtnFocusListener();
		return view;
	}
	public void onResume() {
		super.onResume();
		mHandler.postDelayed(mRunDismiss, DISMISS_TIMEOUT);
	}
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mRunDismiss);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	private final Runnable mRunDismiss = new Runnable(){

		@Override
		public void run() {
			mHandler.removeCallbacks(this);
			finish();
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if ((true)&&(null != this)) {
			Toast.makeText(this, "Channel switched: " + position,
					Toast.LENGTH_SHORT).show();
		}

		DebugUtil.v(TAG, "______onItemClick	 : ");

		ChannelInformation info = (ChannelInformation)listAdapter.getItem(position);
		mTvManager.setCurrentChannel(info.channelNumber);
	}

	public void onEditStart() {
		mFoucsView.setVisibility(View.INVISIBLE);
		mHandler.removeCallbacks(mRunDismiss);
	}

	@Override
	public void onEditEnd() {
		mFoucsView.setVisibility(View.VISIBLE);
		mHandler.postDelayed(mRunDismiss, DISMISS_TIMEOUT);
	}

	private void initBtnFocusListener(){
		if(mBtnEdit != null){
			mBtnEdit.setOnFocusChangeListener(this);
		}
		if(mBtnFavorite != null){
			mBtnFavorite.setOnFocusChangeListener(this);
		}
	}
}
