package com.tcl.netchannellist.bean;

import android.database.Cursor;

import com.tcl.sevencommon.channel.ChannelItem;

public class ChannelItemBean {
	private String subId;
	private String channelName;
	private String channelNumber;
	private String channelType;
	private String channelTypeText;
	private String channelPackageName;
	private String channelVisible;
	private String channelInstalled;
	private String posterUrl;
	private boolean isFavorite;

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public String getSubId() {
		return subId;
	}

	public void setSubId(String subId) {
		this.subId = subId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelNumber() {
		return channelNumber;
	}

	public void setChannelNumber(String channelNumber) {
		this.channelNumber = channelNumber;
	}

	public String getChannelTypeText() {
		return channelTypeText;
	}

	public void setChannelTypeText(String channelTypeText) {
		this.channelTypeText = channelTypeText;
	}

	public String getChannelPackageName() {
		return channelPackageName;
	}

	public void setChannelPackageName(String channelPackageName) {
		this.channelPackageName = channelPackageName;
	}

	public String getChannelVisible() {
		return channelVisible;
	}

	public void setChannelVisible(String channelVisible) {
		this.channelVisible = channelVisible;
	}

	public String getPosterUrl() {
		return posterUrl;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getChannelInstalled() {
		return channelInstalled;
	}

	public void setChannelInstalled(String channelInstalled) {
		this.channelInstalled = channelInstalled;
	}

	public static ChannelItemBean convertChannelItemFromCursor(Cursor cursor) {
		ChannelItemBean bean = new ChannelItemBean();
		bean.setSubId(cursor.getString(cursor
				.getColumnIndex(ChannelItem.SUB_ID)));
		bean.setPosterUrl(cursor.getString(cursor
				.getColumnIndex(ChannelItem.POSTER_URL)));
		bean.setChannelInstalled(cursor.getString(cursor
				.getColumnIndex(ChannelItem.CHANNEL_INSTALLED)));
		bean.setChannelName(cursor.getString(cursor
				.getColumnIndex(ChannelItem.CHANNEL_NAME)));
		bean.setChannelNumber(cursor.getString(cursor
				.getColumnIndex(ChannelItem.CHANNEL_NUMBER)));
		bean.setChannelPackageName(cursor.getString(cursor
				.getColumnIndex(ChannelItem.CHANNEL_PACKAGE_NAME)));
		bean.setChannelType(cursor.getString(cursor
				.getColumnIndex(ChannelItem.CHANNEL_TYPE)));
		bean.setChannelTypeText(cursor.getString(cursor
				.getColumnIndex(ChannelItem.CHANNEL_TYPE_TEXT)));
		bean.setChannelVisible(cursor.getString(cursor
				.getColumnIndex(ChannelItem.CHANNEL_VISIBLE)));
		bean.setFavorite(cursor.getInt(cursor
				.getColumnIndex(ChannelItem.COLUMN_FAVORITE)) == 1);
		return bean;
	}

}
