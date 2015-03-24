package com.tcl.netchannellist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.TvManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tcl.sourcelist.bean.OdmSourceItem;
import com.tcl.sourcelist.bean.TVSrcConst;
import com.tcl.sourcelist.bean.TvResource;
import com.tcl.util.DebugUtil;
import com.tcl.util.TvClientTypeList;


@SuppressLint("HandlerLeak")
public class SourceSelectorActivity extends Activity {

	private static final String TAG = "SourceSelectorFragment";
	private ListView  mListView = null;
	private ArrayList<OdmSourceItem> allSrcView = new ArrayList<OdmSourceItem>();
	private int focusIndex = 0;
	private LinearLayout smart_inputsource;
	private TextView sourcetip;
	private LinearLayout listview_source;
	DataAdapter insrcListAdapter;

	private static final long DISMISS_TIMEOUT = 10000;
	private final static int search_end = -100;

	private boolean first_src=false;

	private boolean pc_state=false;

	private boolean hdmi1_state=false;

	private boolean hdmi2_state=false;
	private boolean hdmi3_state=false;
	private final String mPath = "/storage/udisk/";
	private boolean usb_state=false;
	private boolean av1_state=false;
	private boolean av2_state=false;
	//add by fugui.luo 
	private int checknum = -1;
	private int mCurrentnum = 0;
	private int mCurrentInputnum = -1;
	private static final int MAX_NUM = 4;
	private static final long DELAY_MIN = 1000;
	private static final long DELAY_MAX = 3000;
	public static final int ENTER_DESIGN_MODE = 1;

	public static final int ENTER_FACTORY_MODE = 2;

	private String clientType;
	private static class Source {
		public final String name;
		public final int id;

		public Source(int id, String name) {
			this.name = name;
			this.id = id;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHandler.postDelayed(mRunDismiss, DISMISS_TIMEOUT);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHandler.removeCallbacks(mRunDismiss);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private TvManagerHelper tm;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		tm = TvManagerHelper.getInstance(this);    
		clientType=tm.mTvManager.getClientType();
		if(clientType.equals(TvClientTypeList.ToshibaClient)||clientType.equals(TvClientTypeList.HaierClient)||clientType.equals(TvClientTypeList.RowaClient)){
			setContentView(R.layout.odm_insrc_settings_layout);
			smart_inputsource = (LinearLayout)findViewById(R.id.zhisou_button);
			smart_inputsource.setOnClickListener(smart_input);       
		}
		else setContentView(R.layout.odm_insrc_settings_layout2);
		listview_source =(LinearLayout)findViewById(R.id.ll_main_menu);
		sourcetip = (TextView)findViewById(R.id.souce_tip);
		mListView = (ListView)findViewById(R.id.insrc_mListView);
		initGridView();
		sourcetip.setVisibility(View.GONE); 
		if(clientType.equals(TvClientTypeList.ToshibaClient)||clientType.equals(TvClientTypeList.HaierClient)||clientType.equals(TvClientTypeList.RowaClient)){
			smart_inputsource.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {					    
						sourcetip.setVisibility(View.GONE);
					} 
				}
			});
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		initCurrentSourceStatus();
	}

	public void initCurrentSourceStatus() {
		int currentSourceId = getCurInpSrcId();
		int viewIndex = insrcListAdapter.getItemIdBySource(currentSourceId);
		insrcListAdapter.getView(viewIndex).setCurrentSource(true);		
		mListView.setSelection(viewIndex);
		mListView.requestFocus();
	}
	public int getCurInpSrcId() {
		int inputSource = tm.getInputSource();

		DebugUtil.v(TAG, "timeshift value:"+SystemProperties.get("persist.sys.homekey.storage"));
		DebugUtil.v(TAG, "current input source:" + inputSource);

		if (TvManagerHelper.isAtvSource(inputSource)) {

			return TVSrcConst.SOURCMONI;
		} else if (TvManagerHelper.isDtvSource(inputSource)) {

			return TVSrcConst.SOURCE_DIGITAL;
		} else if (TvManagerHelper.isAV1Source(inputSource)) {

			return TVSrcConst.SOURCE_AV1;
		} else if (TvManagerHelper.isAV2Source(inputSource)) {

			return TVSrcConst.SOURCE_AV2;
		} else if (TvManagerHelper.isAV3Source(inputSource)) {

			return TVSrcConst.SOURCE_AV3;
		} else if (inputSource == TvManager.SOURCE_HDMI2) {

			return TVSrcConst.SOURCE_HDMI2;
		} else if (inputSource == TvManager.SOURCE_HDMI1) {

			return TVSrcConst.SOURCE_HDMI1;
		} else if (TvManagerHelper.isPCSource(inputSource)) {

			return TVSrcConst.SOURCE_VGA;       
		} else if (inputSource==TvManager.SOURCE_PLAYBACK){
			return TVSrcConst.SOURCE_USB;
		}else {
			tm.setInputSource(inputSource);
			return TVSrcConst.SOURCMONI;
		}
	}

	View.OnClickListener smart_input= new View.OnClickListener() {	
		@Override
		public void onClick(View arg0) {	
			if (!isAutoSearch) {
				new Thread() {
					public void run() {
						for (int i = 0; i < insrcListAdapter.getCount(); i++) {
							Source source = insrcListAdapter.getItem(i);
							int sourceId = source.id;
							boolean hasSignal;
							if(sourceId==TVSrcConst.SOURCE_USB){

								File file = new File(mPath);
								File[] files = file.listFiles(); 
								if(files == null) hasSignal = false;
								if(files.length <=0) 
									hasSignal = false;    
								else 
									hasSignal = true;
							}
							else hasSignal = tm.mTvManager.getSourceEnableState(sourceId);
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Message msg = uiHandler.obtainMessage();
							Bundle data = new Bundle();
							data.putInt("position", i);
							data.putBoolean("signal", hasSignal);
							msg.setData(data);
							msg.sendToTarget();
						}						
						sourceHandler.sendEmptyMessage(search_end);                        															
					};
				}.start();
				isAutoSearch = true;
				mListView.requestFocus();				               				
			}
		}						
	};
	private boolean isAutoSearch = false;

	@SuppressLint("HandlerLeak")
	private Handler uiHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Bundle data = msg.getData();
			int searchPosition = data.getInt("position");
			boolean hasSignal = data.getBoolean("signal");
			mListView.setSelection(searchPosition+1);
			setItemSourceState(searchPosition, hasSignal);
			if (searchPosition == insrcListAdapter.getCount() - 1) {
				isAutoSearch = false;
			}
		};
	};
	private Handler sourceHandler = new Handler() {

		public void handleMessage(android.os.Message msg){
			if (msg.what == ENTER_DESIGN_MODE)
			{
				Intent it = new Intent();
				ComponentName component = new ComponentName("com.rtk.TSBFactoryMode", "com.rtk.TSBFactoryMode.TSBDesignMode");
				it.setComponent(component);
				startActivity(it);
			}
			else if (msg.what == ENTER_FACTORY_MODE)
			{
				Intent it1 = new Intent();
				ComponentName component1 = new ComponentName("com.rtk.TSBFactoryMode", "com.rtk.TSBFactoryMode.TSBFactoryMode");
				it1.setComponent(component1);
				startActivity(it1);
			}
			else if (msg.what == search_end)
			{ 
				listview_source.requestFocus();
				if(av1_state==true&&first_src==false)
				{ 
					first_src=true;
					mListView.setSelection(2);
				}
				if(clientType.equals(TvClientTypeList.RowaClient)||clientType.equals(TvClientTypeList.ToshibaClient)){
					if(av2_state==true&&first_src==false)
					{ 
						first_src=true;
						mListView.setSelection(3);
					}
					if(pc_state==true&&first_src==false)
					{ 
						first_src=true;
						mListView.setSelection(4);
					}
					if(hdmi1_state==true&&first_src==false)
					{ 
						first_src=true;
						mListView.setSelection(5);
					}
					if(hdmi2_state==true&&first_src==false)
					{ 
						first_src=true;
						mListView.setSelection(6);
					}
					if(usb_state==true&&first_src==false){
						first_src=true;
						mListView.setSelection(7);
					}
					if(first_src==false)
					{
						int currentSourceId = getCurInpSrcId();
						switch(currentSourceId){
						case 3:
							mListView.setSelection(0);
							break;
						case 1:
							mListView.setSelection(1);
							break;
						case 5:
							mListView.setSelection(2);
							break;
						case 6:
							mListView.setSelection(3);
							break;
						case 14:
							mListView.setSelection(4);
							break;
						case 16:
							mListView.setSelection(5);
							break;
						case 17:
							mListView.setSelection(6);
							break;
						case 24:
							mListView.setSelection(7);
							break;
						}
					}
				}else{
					if(av2_state==true&&first_src==false)
					{ 
						first_src=true;
						mListView.setSelection(3);
					}
					if(pc_state==true&&first_src==false)
					{ 
						first_src=true;
						mListView.setSelection(5);
					}
					if(hdmi1_state==true&&first_src==false)
					{ 
						first_src=true;
						mListView.setSelection(6);
					}
					if(hdmi2_state==true&&first_src==false)
					{ 
						first_src=true;
						mListView.setSelection(7);
					}
					if(first_src==false)
					{
						int currentSourceId = getCurInpSrcId();
						switch(currentSourceId){
						case 3:
							mListView.setSelection(0);
							break;
						case 1:
							mListView.setSelection(1);
							break;
						case 5:
							mListView.setSelection(2);
							break;
						case 6:
							mListView.setSelection(3);
							break;
						case 7:
							mListView.setSelection(4);
							break;
						case 14:
							mListView.setSelection(5);
							break;
						case 16:
							mListView.setSelection(6);
							break;
						case 17:
							mListView.setSelection(7);
							break;
						}
					}
				}
				first_src=false;
			}
		}

	};

	protected void setItemSourceState(int searchPosition, boolean hasSignal) {
		DebugUtil.i(TAG, "--ivan ivan--MAIN UI----handler start  -- ");
		switch(insrcListAdapter.getItem(searchPosition).id) {
		case TVSrcConst.SOURCMONI:
			((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xff787878);
			((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.gray_atv);					   
			((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xff787878);///////
			break;
		case TVSrcConst.SOURCE_DIGITAL:
			((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xff787878);
			((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.gray_dtv);					   
			((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xff787878);///////
			break;
		case TVSrcConst.SOURCE_AV1:
			if(hasSignal)
			{
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xffFFFFFF);
				((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.odm_av);	
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xffFFFFFF);///////
				av1_state=true;
			}
			else
			{
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xff787878);
				((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.gray_av);	
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xff787878);///////
			}
			break;
		case TVSrcConst.SOURCE_AV2:
			if(hasSignal)
			{
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xffFFFFFF);
				((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.odm_av);	
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xffFFFFFF);///////
				av2_state=true;
			}
			else
			{
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xff787878);
				((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.gray_av);	
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xff787878);///////
			}
			break;
		case TVSrcConst.SOURCE_HDMI1:
			if(hasSignal)
			{ 
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xffFFFFFF);
				((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.odm_hdmi);						
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xffFFFFFF);///////
				hdmi1_state=true;
			}
			else
			{ 
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xff787878);
				((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.gray_hdmi);						
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xff787878);///////
			}
			break;
		case TVSrcConst.SOURCE_HDMI2:
			if(hasSignal)
			{
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xffFFFFFF);
				((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.odm_hdmi);						
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xffFFFFFF);///////
				hdmi2_state=true;
			}
			else
			{
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xff787878);
				((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.gray_hdmi);	
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xff787878);///////
			}
			break;
		case TVSrcConst.SOURCE_VGA:
			if(hasSignal)
			{						   
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xffFFFFFF);
				((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.odm_pc);
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xffFFFFFF);///////
				pc_state=true;
			} 
			else
			{						   
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xff787878);
				((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.gray_pc);
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xff787878);///////

			}
			break;      	
		case TVSrcConst.SOURCE_USB:
			if(hasSignal)
			{						   
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xffFFFFFF);
				((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.odm_usb);
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xffFFFFFF);///////
				usb_state=true;
			} 
			else
			{						   
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.sourceitem)).setTextColor(0xff787878);
				((ImageView)mListView.getChildAt(searchPosition).findViewById(R.id.imagesource)).setImageResource(R.drawable.gray_usb);
				((TextView)mListView.getChildAt(searchPosition).findViewById(R.id.source_edit)).setTextColor(0xff787878);///////

			}
			break;      	
		}		             	     
		DebugUtil.i(TAG, "--ivan ivan---MAIN UI -handler  END  -- ");
	}

	public String getSecondTopActivity(Context mContext) {
		try {
			ActivityManager activityManager = (ActivityManager) mContext
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(2);

			if (tasksInfo.size() > 0) {
				String packgename = tasksInfo.get(1).topActivity.getPackageName();
				return packgename;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mContext.getPackageName();
	}

	public void SetInputType(int sourcetype)
	{
		switch(sourcetype)
		{
		case  TVSrcConst.SOURCE_HDMI1:
			tm.setInputSource(TvManager.SOURCE_HDMI1);
			break;
		case  TVSrcConst.SOURCE_HDMI2:
			tm.setInputSource(TvManager.SOURCE_HDMI2);
			break;
		case TVSrcConst.SOURCE_AV1:
			tm.setInputSource(TvManager.SOURCE_AV1);
			break;
		case TVSrcConst.SOURCE_AV2:
			tm.setInputSource(TvManager.SOURCE_AV2);
			break;
		case TVSrcConst.SOURCE_AV3:
			tm.setInputSource(TvManager.SOURCE_AV3);
			break;
		case TVSrcConst.SOURCE_VGA:
			tm.setInputSource(TvManager.SOURCE_VGA1);
			break;
		default:
			break;
		}
	}

	private void initGridView() {

		if (mListView != null) {
			insrcListAdapter = new DataAdapter(this);
			mListView.setAdapter(insrcListAdapter);
			mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					DebugUtil.d(TAG, "==onItemClick position=" + position);
					onItemChange(position);
				}
			});			
		}

	}

	private void onItemChange(int position) {
		DebugUtil.i(TAG, "onItemChange:"+position);
		if(insrcListAdapter.getItem(position).id!= tm.getInputSource()&&insrcListAdapter.getItem(position).id!=TVSrcConst.SOURCE_USB) {
			sendBroadcast(new Intent("com.tcl.changesource"));
		}
		switch (insrcListAdapter.getItem(position).id) {
		case TVSrcConst.SOURCMONI:// 1
			tm.setInputSource(TvManager.SOURCE_ATV1);
			break;
		case TVSrcConst.SOURCE_DIGITAL:// 3
			tm.setInputSource(TvManager.SOURCE_DTV1);
			break;
		case TVSrcConst.SOURCE_AV1:// 5
			tm.setInputSource(TvManager.SOURCE_AV1);
			break;	      
		case TVSrcConst.SOURCE_AV2:// 6
			tm.setInputSource(TvManager.SOURCE_AV2);
			break;	      
		case TVSrcConst.SOURCE_AV3:// 7
			tm.setInputSource(TvManager.SOURCE_AV3);
			break;
		case TVSrcConst.SOURCE_HDMI3:// 18
			tm.setInputSource(TvManager.SOURCE_HDMI2);
			break;
		case TVSrcConst.SOURCE_HDMI2:// 17
			tm.setInputSource(TvManager.SOURCE_HDMI2);
			break;
		case TVSrcConst.SOURCE_HDMI1:// 16
			tm.setInputSource(TvManager.SOURCE_HDMI1);
			break;
		case TVSrcConst.SOURCE_VGA:// 14
			tm.setInputSource(TvManager.SOURCE_VGA1);
			break;
		case TVSrcConst.SOURCE_USB:
			ComponentName componetName = new ComponentName("com.rtk.mediabrowser","com.rtk.mediabrowser.TSB_MediaBrowser");  
			Intent intent = new Intent();  
			intent.setComponent(componetName);  
			startActivity(intent); 
			finish();
			return;
		default:
			break;
		}
		ComponentName componetName = new ComponentName("com.tsb.tv","com.tsb.tv.Tv_strategy");  
		Intent intent = new Intent();  
		intent.setComponent(componetName);  
		startActivity(intent); 
		finish();

	}

	public class DataAdapter extends BaseAdapter {
		View view = null;
		ImageView imageSource = null;
		TextView itemSource = null;
		TextView itemedit = null;
		EditText itemInputEdit = null;

		List<Source> data = new ArrayList<Source>();
		SharedPreferences sharedPreferences;
		public DataAdapter(Context context) {
			LayoutInflater mInflater = LayoutInflater.from(context);
			initSourceListData(SourceSelectorActivity.this, data);
			for (int i = 0; i < data.size(); i++) {
				view = mInflater.inflate(R.layout.odm_insrc_sub_item2, null);
				imageSource = (ImageView) view.findViewById(R.id.imagesource);
				itemSource = (TextView) view.findViewById(R.id.sourceitem);
				itemedit = (TextView) view.findViewById(R.id.source_edit);
				itemInputEdit = (EditText) view.findViewById(R.id.source_edit_input);

				OdmSourceItem sourceitem = (OdmSourceItem) view.findViewById(R.id.sourceitem_linear);
				sharedPreferences =getSharedPreferences("user_info", Context.MODE_PRIVATE);
				switch (data.get(i).id) {
				case TVSrcConst.SOURCMONI:
					itemSource.setText(getString(R.string.ATV));
					imageSource.setImageResource(R.drawable.odm_atv_selector);					
					//						    itemedit.setText(sharedPreferences.getString(data.get(i).name, null));
					sourceitem.initSourceItem(TVSrcConst.SOURCMONI, imageSource, itemSource,itemedit,itemInputEdit);
					break;
				case TVSrcConst.SOURCE_DIGITAL:
					itemSource.setText(getString(R.string.DTV));
					imageSource.setImageResource(R.drawable.odm_dtv);
					//						    itemedit.setText(sharedPreferences.getString(data.get(i).name, null));
					sourceitem.initSourceItem(TVSrcConst.SOURCE_DIGITAL, imageSource,itemSource,itemedit,itemInputEdit);
					break;
				case TVSrcConst.SOURCE_AV1:
					itemSource.setText(R.string.AV1);
					imageSource.setImageResource(R.drawable.odm_av);
					//						    itemedit.setText(sharedPreferences.getString(data.get(i).name, null));
					sourceitem.initSourceItem(TVSrcConst.SOURCE_AV1, imageSource, itemSource,itemedit,itemInputEdit);
					break;
				case TVSrcConst.SOURCE_AV2:
					itemSource.setText(R.string.AV2);
					imageSource.setImageResource(R.drawable.odm_av);
					//						    itemedit.setText(sharedPreferences.getString(data.get(i).name, null));
					sourceitem.initSourceItem(TVSrcConst.SOURCE_AV2, imageSource, itemSource,itemedit,itemInputEdit);
					break;
				case TVSrcConst.SOURCE_AV3:
					itemSource.setText(R.string.AV3);
					imageSource.setImageResource(R.drawable.odm_av);
					//						    itemedit.setText(sharedPreferences.getString(data.get(i).name, null));
					sourceitem.initSourceItem(TVSrcConst.SOURCE_AV3, imageSource, itemSource,itemedit,itemInputEdit);
					break;
				case TVSrcConst.SOURCE_HDMI1:
					itemSource.setText(R.string.HDMI1);
					imageSource.setImageResource(R.drawable.odm_hdmi);
					//						    itemedit.setText(sharedPreferences.getString(data.get(i).name, null));
					sourceitem.initSourceItem(TVSrcConst.SOURCE_HDMI1, imageSource, itemSource,itemedit,itemInputEdit);
					break;
				case TVSrcConst.SOURCE_HDMI2:
					itemSource.setText(R.string.HDMI2);
					imageSource.setImageResource(R.drawable.odm_hdmi);
					//							itemedit.setText(sharedPreferences.getString(data.get(i).name, null));
					sourceitem.initSourceItem(TVSrcConst.SOURCE_HDMI2, imageSource, itemSource,itemedit,itemInputEdit);
					break;  
				case TVSrcConst.SOURCE_HDMI3:
					itemSource.setText(R.string.HDMI3);
					imageSource.setImageResource(R.drawable.odm_hdmi);
					//	                          	              itemedit.setText(sharedPreferences.getString(data.get(i).name, null));
					sourceitem.initSourceItem(TVSrcConst.SOURCE_HDMI3, imageSource, itemSource,itemedit,itemInputEdit);
					break;
				case TVSrcConst.SOURCE_VGA:
					itemSource.setText(R.string.PC);
					imageSource.setImageResource(R.drawable.odm_pc);
					//							itemedit.setText(sharedPreferences.getString(data.get(i).name, null));
					sourceitem.initSourceItem(TVSrcConst.SOURCE_VGA, imageSource, itemSource,itemedit,itemInputEdit);
					break;
				case TVSrcConst.SOURCE_USB:
					itemSource.setText(R.string.USB);
					imageSource.setImageResource(R.drawable.odm_usb);
					//							itemedit.setText(sharedPreferences.getString(data.get(i).name, null));
					sourceitem.initSourceItem(TVSrcConst.SOURCE_USB, imageSource, itemSource,itemedit,itemInputEdit);
					break;
				default:
					break;
				}
				if(data.get(i).id==TVSrcConst.SOURCE_USB)
					sourceitem.showEditInfo(null);
				else sourceitem.showEditInfo(sharedPreferences.getString(data.get(i).name, null));
				allSrcView.add(sourceitem);
			}
		}

		public void add(Source source) {
			data.add(source);
		}

		public void clear() {
			data.clear();
		}

		public int getCount() {
			return data.size();
		}

		public Source getItem(int position) {
			return data.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public int getItemIdBySource(int inputSourceId) {
			int position = 0;
			for (int i = 0; i < insrcListAdapter.getCount(); i++) {
				if (inputSourceId == insrcListAdapter.getItem(i).id) {
					position = i;
				}
			}
			return position;
		}

		public OdmSourceItem getView(int position, View convertView, ViewGroup parent) {
			return allSrcView.get(position);
		}
		public OdmSourceItem getView(int position) {
			return allSrcView.get(position);
		}

		public void setSelectSourceView(int position) {
			for (int i = 0; i < getCount(); i++) {
				allSrcView.get(i).sourceNotFocused();
			}
			allSrcView.get(position).setCurrentSource(true);
		}

		//显示当前信源的别名编辑窗口
		public void showInputEdit(int position) {
			String alias = sharedPreferences.getString(data.get(position).name, null);
			allSrcView.get(position).showInputEdit(alias);
		}
		//显示当前信源的别名编辑窗口
		public void showEditInfo(int position) {
			String alias = sharedPreferences.getString(data.get(position).name, null);
			allSrcView.get(position).showEditInfo(alias);
			mListView.requestFocus();
			mListView.setSelection(position);
		}
	}

	private void initSourceListData(Context context, List<Source> data) {			
		List<Integer> list = tm.getInputSourceList();			
		for (int i = 0; i < list.size(); i++) {
			int id = list.get(i);
			if(id==7) continue;
			String name = TvResource.getInputSourceLabel(context, id);
			data.add(new Source(id, name));
		}
		if(clientType.equals(TvClientTypeList.ToshibaClient)){
			data.add(new Source(24,"usb"));
		}
	}

	SharedPreferences sharedPreferences1;
	//标记是否在编辑信源别名
	boolean isEditStatus =false;
	//标记编辑信源别名时选中的位置
	int currentPosition = -1;
	EditText input;

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			uiHandler.removeCallbacks(mRunDismiss);
			uiHandler.postDelayed(mRunDismiss, DISMISS_TIMEOUT);
			//********end
			focusIndex = mListView.getSelectedItemPosition();
			if(focusIndex < 0) focusIndex = 0;
			int keyCode=event.getKeyCode();
			String deviceName = InputDevice.getDevice(event.getDeviceId()).getName();
			if(keyCode == KeyEvent.KEYCODE_TV_INPUT && "Smart_TV_Keypad".equals(deviceName)) {
				keyCode = KeyEvent.KEYCODE_DPAD_CENTER;
			}
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP:
				if(clientType.equals(TvClientTypeList.RowaClient)||clientType.equals(TvClientTypeList.ToshibaClient)){					
					if(smart_inputsource.hasFocus()){
						listview_source.requestFocus();
						mListView.setSelection(mListView.getCount()-1);
						return true;
					}
				}else if (focusIndex==0) {						
					mListView.setSelection(mListView.getCount()-1);
					return true;
				}
				if (isEditStatus && currentPosition != -1) {
					String alias = input.getText().toString();
					String sourceName = insrcListAdapter.getItem(currentPosition).name;
					sharedPreferences1.edit().putString(sourceName, alias).commit();
					insrcListAdapter.showEditInfo(currentPosition);
					isEditStatus =false;
					currentPosition = -1;
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if(clientType.equals(TvClientTypeList.RowaClient)||clientType.equals(TvClientTypeList.ToshibaClient))
				{     
					if(focusIndex==(insrcListAdapter.getCount()-1) &&(!(smart_inputsource.hasFocus()))){
						smart_inputsource.requestFocus();
						return true;
					}
				}else if(focusIndex==(insrcListAdapter.getCount()-1)){
					mListView.setSelection(0);
					return true;
				}
				if (isEditStatus && currentPosition != -1) {
					String alias = input.getText().toString();
					String sourceName = insrcListAdapter.getItem(currentPosition).name;
					sharedPreferences1.edit().putString(sourceName, alias).commit();
					insrcListAdapter.showEditInfo(currentPosition);
					isEditStatus =false;
					currentPosition = -1;
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				if(clientType.equals(TvClientTypeList.RowaClient)||clientType.equals(TvClientTypeList.ToshibaClient)){
					if (!isAutoSearch&&smart_inputsource.hasFocus()) {
						new Thread() {
							public void run() {
								for (int i = 0; i < insrcListAdapter.getCount(); i++) {
									Source source = insrcListAdapter.getItem(i);
									int sourceId = source.id;
									boolean hasSignal=false;
									if(sourceId==TVSrcConst.SOURCE_USB){
										File file = new File(mPath);
										File[] files = file.listFiles(); 
										if(files == null) hasSignal = false;
										if(files.length <=0) 
											hasSignal = false;    
										else 
											hasSignal = true;
									}
									else hasSignal = tm.mTvManager.getSourceEnableState(sourceId);
									try {
										Thread.sleep(200);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Message msg = uiHandler.obtainMessage();
									Bundle data = new Bundle();
									data.putInt("position", i);
									data.putBoolean("signal", hasSignal);
									msg.setData(data);
									msg.sendToTarget();
								}						
								sourceHandler.sendEmptyMessage(search_end);                        															
							};
						}.start();
						isAutoSearch = true;
						mListView.requestFocus();
						return true;
					}else {
						onItemChange(focusIndex);
						return true;
					}  
				}
				break;	
			case KeyEvent.KEYCODE_BACK:			      
				finish();  
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT: checknum = 0;break;
			case KeyEvent.KEYCODE_DPAD_LEFT: checknum = 1;break;

			}
			if( tm.mTvManager.getClientType().contains("RT2984"))
			{
				setenterfactoryaction(checknum);
			}  
		}
		return super.dispatchKeyEvent(event);
	}

	private void setenterfactoryaction(int checknum)

	{
		if (mCurrentnum <= 0) {
			mCurrentInputnum = checknum;
		} else {
			mCurrentInputnum *= 10;
			mCurrentInputnum += checknum;
		}
		mCurrentnum++;
		if (mCurrentnum >= MAX_NUM) {
			sourceHandler.postDelayed(mRunConfirmInput, DELAY_MIN);
		} else {
			sourceHandler.postDelayed(mRunConfirmInput, DELAY_MIN);
		}
	}

	private Runnable mRunConfirmInput = new Runnable() {

		@Override
		public void run() {
			if (mCurrentnum == 4) {
				Message msg =sourceHandler.obtainMessage();
				if (mCurrentInputnum == 1010) {
					msg.what = ENTER_FACTORY_MODE;
					sourceHandler.sendMessage(msg);
				}
				mCurrentnum = 0;
			}
		}
	};

	//********add by huangfh@tcl.com14-09-10
	private final Runnable mRunDismiss = new Runnable(){

		@Override
		public void run() {
			uiHandler.removeCallbacks(this);
			finish();
		}
	};

	//*********end
}
