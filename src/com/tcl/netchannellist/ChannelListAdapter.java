package com.tcl.netchannellist;
import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.tcl.netchannellist.TvManagerHelper.ChannelInformation;
import com.tcl.util.DebugUtil;

public class ChannelListAdapter extends BaseAdapter {
    private static final String TAG = "ChannelListAdapter";
	private int listType;
	private LayoutInflater mInflater;
	//每个item的高度
	private int itemHeight = -1;
	//列表的总高度
	private int listHeight;
	//列表一屏能容纳的item个数
	private int itemCountInOneScreen;
	// 列表类型常量
	static final public int LIST_TYPE_DTV = 1;
	static final public int LIST_TYPE_ATV = 2;
	static final public int LIST_TYPE_NETWORK = 3;

	final int ANTENNA_TYPE_DVBC = 0;
	final int ANTENNA_TYPE_DTMB = 1;
	
	private int mEditPos = -1;
	
	private List<ChannelInformation> channelData; 
	
	private onEditableListener listener;
	
	private Context context;
	
	private String name = null;	
	
	public ChannelListAdapter(Context context, int listHeight, int listType) {
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.listHeight = listHeight;
		this.itemHeight = measureItemHeight();
		itemCountInOneScreen = listHeight / itemHeight;
		DebugUtil.v(TAG,"listHeight=="+listHeight + "  itemHeight== " + itemHeight + "  itemCountInOneScreen== "
				+ itemCountInOneScreen);
		this.listType = listType;
		
		//mTvManager = TvManagerHelper.getInstance(getActivity());
	}

	public void setListType(int listType) {
		this.listType = listType;
	}

	public void setChannelData(List<ChannelInformation> data) {
		channelData = data;
		notifyDataSetChanged();
	}
	
	public void setEditPos(int pos){
	    mEditPos = pos;
	    notifyDataSetChanged();
	}
	
	public void setOnEditableListener(onEditableListener listener){
	    this.listener =  listener;
	}
    
	@Override
	public int getCount() {
		return getChannelCount();
	}

	@Override
	public Object getItem(int position) {
		if(channelData != null && position >=0 && position < channelData.size()){
		    return channelData.get(position);
		}
	    return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void exitEditChannelName(){
	    mEditPos = -1;
        DebugUtil.e("apple", "11111");
        notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.fast_channel_change_item_layout2, null);
		}
		DebugUtil.i(TAG,"____________5555position====="+position);
		if (listType == LIST_TYPE_DTV){
			
			((TextView) convertView.findViewById(R.id.channellist_item_icon))
			.setBackgroundResource(getChannelIconIdByPosition(position));
			if(channelData.get(position).sourceType==11||channelData.get(position).sourceType==12){
				((TextView) convertView.findViewById(R.id.channellist_item_encry))
				.setBackgroundResource(0);
				((TextView) convertView.findViewById(R.id.channellist_item_text_number))
				.setText(" ");		
			}else{
				((TextView) convertView.findViewById(R.id.channellist_item_encry))
				.setBackgroundResource(getChannelEncryIconIdByPosition(position));
				((TextView) convertView.findViewById(R.id.channellist_item_text_number))
				.setText(getChannelNumber(position) + "");				
			}
			((TextView) convertView.findViewById(R.id.channellist_item_text_name))
			.setText(getChannelNameByPosition(position));
		}
		else if (listType == LIST_TYPE_ATV){
			if(channelData.get(position).sourceType==11||channelData.get(position).sourceType==12){
				((TextView) convertView.findViewById(R.id.channellist_item_icon))
				.setBackgroundResource(R.drawable.dvbc_channel_icon_dark);		
			}else 
				((TextView) convertView.findViewById(R.id.channellist_item_icon))
				.setBackgroundResource(R.drawable.atv_channel_icon_dark);
			if(channelData.get(position).sourceType==11||channelData.get(position).sourceType==12){
				((TextView) convertView.findViewById(R.id.channellist_item_text_number))
				.setText(" ");		
			}else{
				((TextView) convertView.findViewById(R.id.channellist_item_text_number))
				.setText(getChannelNumber(position) + "");				
			}
			TextView  chName = ((TextView) convertView.findViewById(R.id.channellist_item_text_name));
			final EditText chNameEdit = ((EditText) convertView.findViewById(R.id.channellist_item_text_name_edit));
			chName.setText(getATVChannelNameByPosition(position));
			chNameEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus){
                        mEditPos = -1;
                        DebugUtil.e("apple", "11111");
                        notifyDataSetChanged();
                        if(listener != null){
                            listener.onEditEnd(); 
                        }
                    }else{
                        DebugUtil.e("apple", "22222");
                        chNameEdit.requestFocus();
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);  
                        imm.showSoftInput(chNameEdit,InputMethodManager.SHOW_FORCED);
                        if(listener != null){
                            listener.onEditStart(); 
                        }
                    }
                }
            });
			
			if(mEditPos == position){
			    DebugUtil.i(TAG," ^^^  00000");
			    chName.setVisibility(View.INVISIBLE);
			    chNameEdit.setVisibility(View.VISIBLE);
			    chNameEdit.setText(getATVChannelNameByPosition(position));
			    chNameEdit.requestFocus();
			    chNameEdit.setSelection(chNameEdit.getText().length());
			} else{
			
			    DebugUtil.i(TAG," ^^^  111111");
			    chName.setVisibility(View.VISIBLE);
			    chNameEdit.setVisibility(View.INVISIBLE);
			}
			chNameEdit.addTextChangedListener(new TextWatcher() {
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    
                }
                
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    
                }
                
                @Override
                public void afterTextChanged(Editable s) {
                    String newName = s.toString();
                    DebugUtil.e("apple", "newName-->"+newName);
                    if(newName.equals(name)){
                        return;
                    }else if(TextUtils.isEmpty(newName)){
                    	name= "";
                    }
                    name = newName;
                    
                    DebugUtil.e("apple", "000000000 position===="+position);
                    
                    ChannelInformation info = (ChannelInformation) getItem(position);
                    //if(info==null)
                    //DebugUtil.e("apple", " 55555555----------position-->"+position);
                   // if(info.channelName==null)
				//		DebugUtil.e("apple", " 666666666666----------position-->"+position);
                    
                    DebugUtil.e("apple", "000000000 old name ===="+info.channelName);
                    
                    DebugUtil.e("apple", "000000000 info.channelIndex ===="+info.channelIndex);
                    
                    info.channelName = s.toString();
                   // TTvChannelManager.getInstance(null).setProgramName(info.serviceType, info.index, s.toString());               
				    TvManagerHelper.getInstance(context).setChannelName(info.channelIndex,s.toString());
                    //DebugUtil.e("apple", "11111  new name ====="+info.channelName);
                }
            });
			
		} else if (listType == LIST_TYPE_NETWORK) {
			((TextView) convertView.findViewById(R.id.channellist_item_encry))
			.setBackgroundResource(0);
			((TextView) convertView.findViewById(R.id.channellist_item_icon))
					.setBackgroundResource(getChannelIconIdByPosition(position));			
			((TextView) convertView
					.findViewById(R.id.channellist_item_text_number))
					.setText(" ");
			((TextView) convertView
					.findViewById(R.id.channellist_item_text_name))
					.setText(getChannelNameByPosition(position));
		}
		
		DebugUtil.e("apple", "the Favorite position is=="+position+":"+isFavorite(position));
		
		DebugUtil.e("apple", "@@@000000000 info.channelIndex ===="+ ((ChannelInformation) getItem(position)).channelIndex);
		int visibility =isFavorite(position) ? View.VISIBLE : View.INVISIBLE;
		((TextView) convertView.findViewById(R.id.channellist_item_favorite)).setVisibility(visibility);
          
		return convertView;
	}

	/**
	 * 计算每个item的高度
	 * @return 每个item的高度，单位是像素
	 */
	public int measureItemHeight() {
		View itemView = mInflater.inflate(
				R.layout.fast_channel_change_item_layout2, null);
		itemView.measure(0, 0);
		return itemView.getMeasuredHeight();
	}

	/**
	 * 返回当前频道总数量
	 * @todo 需实现函数
	 * @return 当前频道总数量
	 */
	public int getChannelCount(){
	    
	    if(channelData != null){
	        return channelData.size();
	    }
	    return 0;
	}
	
	/**
	 * 根据在列表中的位置返回当前频道类型图标的id（dvbc、atv、radio、dtmb）
	 * @todo 需实现函数
	 * @param position 列表中的位置，从0开始
	 * @return 当前频道类型图标的id
	 */
	public int getChannelIconIdByPosition(int position){
		if (listType == LIST_TYPE_ATV){
			return R.drawable.atv_channel_icon_dark;
		}
		else {
			ChannelInformation programInfo = channelData.get(position);
			int chServiceType = programInfo.sourceType;
			if (chServiceType == 2){
				//DebugUtil.v("FH", "antennaType : " + programInfo.antennaType);
				//if (programInfo.antennaType == ANTENNA_TYPE_DTMB){
					return R.drawable.dtmb_channel_icon_dark;
				//}
				//else {
				//	return R.drawable.dvbc_channel_icon_dark;
				//}
			}
			else if (chServiceType == 3){
				return R.drawable.radio_channel_icon_dark;
			}
			else {
				return R.drawable.dvbc_channel_icon_dark;
			}
		}
	}
	/**
	 * add by huangfh@tcl.com 09-15
	 * 根据在列表中的位置返回当前频道加密类型图标的id
	 * @todo 需实现函数
	 * @param position 列表中的位置，从0开始
	 * @return 当前频道类型图标的id
	 */
	public int getChannelEncryIconIdByPosition(int position){
		ChannelInformation programInfo = channelData.get(position);
		
		if (TvManagerHelper.getInstance(context).mTvManager.GetChannelScrambled(programInfo.channelIndex)){
			return R.drawable.ic_info_lock;
		}
		return 0;
	}
	/**
	 * 需实现函数，根据列表位置返回频道号
	 * @param position 列表中的位置，从0开始
	 * @return 要显示在列表position位置的频道号
	 */
	public int getChannelNumber(int position){
		/*return channelNumber手动搜索列表序号不能按顺序依次排列*/
		return channelData.get(position).channelIndex+1;
	}

	public ChannelInformation getProgramInfo(int position){
		return channelData.get(position);
	}
	
	public boolean isFavorite(int pos){
	    if(listType==LIST_TYPE_ATV||listType==LIST_TYPE_DTV){
	    	if(channelData.get(pos).sourceType==11||channelData.get(pos).sourceType==12){
	    		return channelData.get(pos).isFavorite;
	    	}
	    	else return  TvManagerHelper.getInstance(context).getChannelFav(channelData.get(pos).channelIndex);
	    }	    
	    else return channelData.get(pos).isFavorite;
	}
	
	/**
	 * 根据在列表中的位置返回频道名
	 * @todo 需实现函数
	 * @param position 要获得频道名的频道信息在列表中的位置。
	 * @return  获得的频道名
	 */
	public String getChannelNameByPosition(int position){
		String channelName = channelData.get(position).channelName;
		if (channelName == null || channelName.equals("")){
			channelName = "未知频道";
		}
		return channelName;
		//		return "测试频道" + position;
	}
	
	public String getATVChannelNameByPosition(int position){
	    String channelName = channelData.get(position).channelName;
	    if (TextUtils.isEmpty(channelName) || channelName.equals("null")){
	        channelName = context.getString(R.string.a_channel) + (channelData.get(position).channelIndex + 1);
	    }
	    return channelName;
	    //      return "测试频道" + position;
	}
	/**
	 * 根据在列表中的位置获取当前频道播放的节目名
	 * @todo 需实现函数
	 * @param position 
	 * @return
	 */
	public String getChannelProgramNameByPosition(int position){
		return "测试节目" + position;
	}

	/**
	 * 
	 * @return 返回每一个item的高度
	 */
	public int getItemHeight() {
		return itemHeight;
	}
	/**
	 * 
	 * @return 返回整个列表的高度
	 */
	public int getListHeight() {
		return listHeight;
	}
	/**
	 * 
	 * @return 返回在当前列表的高度下，一屏最多能显示的item个数
	 */
	public int getItemCountInOneScreen() {
		return itemCountInOneScreen;
	}
	
	public interface onEditableListener{
	    public void onEditStart();
	    public void onEditEnd();
	    
	}
	
}
