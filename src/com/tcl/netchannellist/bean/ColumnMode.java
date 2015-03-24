package com.tcl.netchannellist.bean;

import android.database.Cursor;

/**
 * @author fand
 * 
 */
public class ColumnMode {
	private boolean isPreview = false;
	private String dbID;
	private String title;
	private String cpid;
	private String type;
	private String publishdate;
	private String cp;
	private String posturl1;
	private String posturl2;
	private String posturl3;
	private String packageName;
	private String activityName;
	private int channelId;
	private String channelName;
	private int layoutid;
	private int width;
	private int height;
	private int toleftid;
	private int totopid;
	private int gap;
	private int category;
	private String packetid;
	private String categoryid;
	private String paramsJson;
	private String actionName;
	private boolean isFavorite;

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getParamsJson() {
		return paramsJson;
	}

	public void setParamsJson(String paramsJson) {
		this.paramsJson = paramsJson;
	}

	public boolean isPreview() {
		return isPreview;
	}

	public void setPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}

	public String getPacketid() {
		return packetid;
	}

	public void setPacketid(String packetid) {
		this.packetid = packetid;
	}

	public String getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(String categoryid) {
		this.categoryid = categoryid;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public int getGap() {
		return gap;
	}

	public void setGap(int gap) {
		this.gap = gap;
	}

	public int getLayoutid() {
		return layoutid;
	}

	public void setLayoutid(int layoutid) {
		this.layoutid = layoutid;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getToleftid() {
		return toleftid;
	}

	public void setToleftid(int toleftid) {
		this.toleftid = toleftid;
	}

	public int getTotopid() {
		return totopid;
	}

	public void setTotopid(int totopid) {
		this.totopid = totopid;
	}

	public String getDbID() {
		return dbID;
	}

	public void setDbID(String dbID) {
		this.dbID = dbID;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getPosturl1() {
		return posturl1;
	}

	public void setPosturl1(String posturl1) {
		this.posturl1 = posturl1;
	}

	public String getPosturl2() {
		return posturl2;
	}

	public void setPosturl2(String posturl2) {
		this.posturl2 = posturl2;
	}

	public String getPosturl3() {
		return posturl3;
	}

	public void setPosturl3(String posturl3) {
		this.posturl3 = posturl3;
	}

	public String getPublishdate() {
		return publishdate;
	}

	public void setPublishdate(String publishdate) {
		this.publishdate = publishdate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCpid() {
		return cpid;
	}

	public void setCpid(String cpid) {
		this.cpid = cpid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ColumnMode other = (ColumnMode) obj;
		if (!equals(title, other.title))
			return false;
		if (!equals(cpid, other.cpid))
			return false;
		if (!equals(type, other.type))
			return false;
		if (!equals(publishdate, other.publishdate))
			return false;
		if (!equals(cp, other.cp))
			return false;
		if (!equals(posturl1, other.posturl1))
			return false;
		if (!equals(posturl2, other.posturl2))
			return false;
		if (!equals(posturl3, other.posturl3))
			return false;
		if (!equals(packageName, other.packageName))
			return false;
		if (!equals(activityName, other.activityName))
			return false;
		if (channelId != other.channelId)
			return false;
		if (!equals(channelName, other.channelName))
			return false;
		if (layoutid != other.layoutid)
			return false;
		if (width != other.width)
			return false;
		if (height != other.height)
			return false;
		if (toleftid != other.toleftid)
			return false;
		if (totopid != other.totopid)
			return false;
		if (gap != other.gap)
			return false;
		if (category != other.category) {
			return false;
		}
		if (!equals(packetid, other.packetid)) {
			return false;
		}
		if (!equals(categoryid, other.categoryid)) {
			return false;
		}
		if (!equals(paramsJson, other.paramsJson)) {
			return false;
		}
		if (!equals(actionName, other.actionName)) {
			return false;
		}
		return true;
	}

	private boolean equals(String a, String b) {
		if (a == null && b == null) {
			return true;
		} else if (a != null && a.equals(b)) {
			return true;
		}
		return false;
	}

	public static ColumnMode convertModeFromCursor(Cursor cursor) {
		ColumnMode mode = new ColumnMode();
		mode.setDbID(cursor.getString(cursor.getColumnIndex(TableColumn._ID)));
		mode.setTitle(cursor.getString(cursor
				.getColumnIndex(TableColumn.COLUMN_NAME_TITLE)));
		mode.setCpid(cursor.getString(cursor
				.getColumnIndex(TableColumn.COLUMN_NAME_CPID)));
		mode.setCp(cursor.getString(cursor
				.getColumnIndex(TableColumn.COLUMN_NAME_CP)));
		mode.setType(cursor.getString(cursor
				.getColumnIndex(TableColumn.COLUMN_NAME_TYPE)));
		mode.setPublishdate(cursor.getString(cursor
				.getColumnIndex(TableColumn.COLUMN_NAME_PUBLISHDATE)));
		mode.setPosturl1(cursor.getString(cursor
				.getColumnIndex(TableColumn.COLUMN_NAME_POSTURL1)));
		mode.setPosturl2(cursor.getString(cursor
				.getColumnIndex(TableColumn.COLUMN_NAME_POSTURL2)));
		mode.setPosturl3(cursor.getString(cursor
				.getColumnIndex(TableColumn.COLUMN_NAME_POSTURL3)));
		mode.setPackageName(cursor.getString(cursor
				.getColumnIndex(TableColumn.COLUMN_PACKAGENAME)));
		mode.setActivityName(cursor.getString(cursor
				.getColumnIndex(TableColumn.COLUMN_ACTIVITYNAME)));
		mode.setChannelId(cursor.getInt(cursor
				.getColumnIndex(TableColumn.COLUMN_CHANNELID)));
		mode.setChannelName(cursor.getString(cursor
				.getColumnIndex(TableColumn.COLUMN_CHANNELNAME)));
		mode.setLayoutid(cursor.getInt(cursor
				.getColumnIndex(TableColumn.COLUMN_LAYOUTID)));
		mode.setWidth(cursor.getInt(cursor
				.getColumnIndex(TableColumn.COLUMN_WIDTH)));
		mode.setHeight(cursor.getInt(cursor
				.getColumnIndex(TableColumn.COLUMN_HEIGHT)));
		mode.setToleftid(cursor.getInt(cursor
				.getColumnIndex(TableColumn.COLUMN_TOLEFTID)));
		mode.setTotopid(cursor.getInt(cursor
				.getColumnIndex(TableColumn.COLUMN_TOTOPID)));
		mode.setGap(cursor.getInt(cursor.getColumnIndex(TableColumn.COLUMN_GAP)));
		mode.setCategory(cursor.getInt(cursor
				.getColumnIndex(TableColumn.COLUMN_CATEGORY)));
		mode.setPacketid(cursor.getString(cursor
				.getColumnIndex(TableColumn.COLUMN_PACKETID)));
		mode.setCategoryid(cursor.getString(cursor
				.getColumnIndex(TableColumn.COLUMN_CATEGORYID)));
		mode.setFavorite(cursor.getInt(cursor
				.getColumnIndex(TableColumn.COLUMN_FAVORITE)) == 1);
		return mode;
	}
}
