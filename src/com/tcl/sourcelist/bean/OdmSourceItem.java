package com.tcl.sourcelist.bean;
import com.tcl.netchannellist.R;
import com.tcl.util.DebugUtil;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OdmSourceItem extends LinearLayout {


	public ImageView imageSource = null;
	public TextView sourceName = null;
    public TextView sourceedit = null;
    public EditText sourceInputEdit = null;
	
	private int resId = 0;
	private boolean isCurrentSrc = false;
	
	public boolean isCurrentSrc() {
		return isCurrentSrc;
	}

	public void setCurrentSrc(boolean isCurrentSrc) {
		this.isCurrentSrc = isCurrentSrc;
	}

	public OdmSourceItem(Context context) {
		this(context, null);
	}

	public OdmSourceItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public OdmSourceItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void initSourceItem(int sourceShiyi, ImageView imageSource2,
			TextView itemSource,TextView sourcezhushi, EditText inputZhushi)
		{
		resId = sourceShiyi;
		imageSource = imageSource2;
		sourceName = itemSource;
		sourceedit = sourcezhushi;
		sourceInputEdit = inputZhushi;
	}
	
	public void showInputEdit(String alias) {
		sourceedit.setVisibility(View.GONE);
		sourceInputEdit.setVisibility(View.VISIBLE);
		sourceInputEdit.requestFocus();
		sourceInputEdit.setText(alias);
	}
	
	public void showEditInfo(String alias) {
		sourceInputEdit.setVisibility(View.GONE);
		sourceedit.setVisibility(View.VISIBLE);
		sourceedit.setText(alias);
	}



	public void setCurrentSource(boolean current) {
		DebugUtil.d("lyf", "set setCurrentSource="+current);
		isCurrentSrc = current;
		if (current) {
              sourceSelected();
		} 
	}
	public void sourceNotFocused() {
		if (imageSource == null) {
			return;
		}
		switch (resId) {
		case TVSrcConst.SOURCMONI:
			imageSource.setImageResource(R.drawable.odm_atv);
			break;
		case TVSrcConst.SOURCE_DIGITAL:
			imageSource.setImageResource(R.drawable.odm_dtv);
			break;
		case TVSrcConst.SOURCE_AV1:
			imageSource.setImageResource(R.drawable.odm_av);
			break;
		case TVSrcConst.SOURCE_AV2:
			imageSource.setImageResource(R.drawable.odm_av);
			break;
		case TVSrcConst.SOURCE_AV3:
			imageSource.setImageResource(R.drawable.odm_av);
			break;
		case TVSrcConst.SOURCE_HDMI1:
			imageSource.setImageResource(R.drawable.odm_hdmi);
			break;
		case TVSrcConst.SOURCE_HDMI2:
			imageSource.setImageResource(R.drawable.odm_hdmi);
			break;
		case TVSrcConst.SOURCE_VGA:
			imageSource.setImageResource(R.drawable.odm_pc);
			break;
		default:
			break;
		}
		if(sourceName!=null){
			sourceName.setTextColor(Color.WHITE);
		}
			if(sourceedit!=null){
			sourceedit.setTextColor(Color.WHITE);
		}
	}
	
	public void sourceSelected() {
		if (imageSource == null) {
			return;
		}
		switch (resId) {
		case TVSrcConst.SOURCMONI:
			imageSource.setImageResource(R.drawable.odm_atv_selected);
			break;
		case TVSrcConst.SOURCE_DIGITAL:
			imageSource.setImageResource(R.drawable.odm_dtv_selected);
			break;
		case TVSrcConst.SOURCE_AV1:
			imageSource.setImageResource(R.drawable.odm_av_selected);
			break;
		case TVSrcConst.SOURCE_AV2:
			imageSource.setImageResource(R.drawable.odm_av_selected);
			break;
		case TVSrcConst.SOURCE_AV3:
			imageSource.setImageResource(R.drawable.odm_av_selected);
			break;
		case TVSrcConst.SOURCE_HDMI1:
			imageSource.setImageResource(R.drawable.odm_hdmi_selected);
			break;
		case TVSrcConst.SOURCE_HDMI2:
			imageSource.setImageResource(R.drawable.odm_hdmi_selected);
			break;
		case TVSrcConst.SOURCE_VGA:
			imageSource.setImageResource(R.drawable.odm_pc_selected);
			break;
		default:
			break;
		}
		if(sourceName!=null){
			sourceName.setTextColor(0xffB3D300);			
		}
		if(sourceedit!=null){
			sourceedit.setTextColor(0xffB3D300);		
		}
		
	}


}
