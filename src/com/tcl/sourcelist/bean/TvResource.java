package com.tcl.sourcelist.bean;

import java.util.Locale;
import android.app.TvManager;
import android.content.Context;
import com.tcl.netchannellist.R;

public class TvResource {
	
	public static final String getLanguageName(Context context, String languageCode) {
		return new Locale(languageCode).getDisplayName();
	}	  	  	
	public static final String getInputSourceLabel(Context context, int sourceType) {
		int stringId = 0;
		switch (sourceType) {
		case TvManager.SOURCE_ATV1:
			stringId = R.string.ATV;
			break;
		case TvManager.SOURCE_DTV1:
			stringId = R.string.DTV;
			break;
		case TvManager.SOURCE_IDTV1:
			stringId = R.string.IDTV;
			break;
		case TvManager.SOURCE_AV1:
			stringId = R.string.AV1;
			break;
		case TvManager.SOURCE_AV2:
			stringId = R.string.AV2;
			break;
		case TvManager.SOURCE_AV3:
			stringId = R.string.AV3;
			break;
		case TvManager.SOURCE_SV1:
			stringId = R.string.SV1;
			break;
		case TvManager.SOURCE_SV2:
			stringId = R.string.SV2;
			break;
		case TvManager.SOURCE_YPP1:
			stringId = R.string.YPP1;
			break;
		case TvManager.SOURCE_VGA1:
			stringId = R.string.PC;
			break;
		case TvManager.SOURCE_HDMI1:
			stringId = R.string.HDMI1;
			break;
		case TvManager.SOURCE_HDMI2:
			stringId = R.string.HDMI2;
			break;
		case TvManager.SOURCE_HDMI3:
			stringId = R.string.HDMI3;
			break;
		case TvManager.SOURCE_BROWSER:
			stringId = R.string.BROWSER;
			break;
		case TvManager.SOURCE_PLAYBACK:
		default:
			return "";
		}
		return context.getString(stringId);
	}
}
