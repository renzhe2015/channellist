package com.tcl.netchannellist.bean;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author fand
 * 
 */
public class TableColumn implements BaseColumns {

	public static final String AUTHORITY = "com.tcl.boxui.contentprovider.ColumnContentProvider";

	public static final String TABLE_NAME = "tbl_column";

	private static final String SCHEME = "content://";

	private static final String PATH_COLUMN = "/tablecolumn";
	private static final String PATH_COLUMN_ID = "/tablecolumn/";
	public static final int COLUMN_PATH_POSITION = 1;
	public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY
			+ PATH_COLUMN);
	public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY
			+ PATH_COLUMN_ID);
	public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME
			+ AUTHORITY + PATH_COLUMN_ID + "/#");

	public static final String DEFAULT_SORT_ORDER = "_id asc";
	public static final String COLUMN_NAME_TITLE = "title";
	public static final String COLUMN_NAME_CPID = "cpid";
	public static final String COLUMN_NAME_CP = "cpname";
	public static final String COLUMN_NAME_TYPE = "type";
	public static final String COLUMN_NAME_PUBLISHDATE = "publishdate";
	public static final String COLUMN_NAME_POSTURL1 = "posturl1";
	public static final String COLUMN_NAME_POSTURL2 = "posturl2";
	public static final String COLUMN_NAME_POSTURL3 = "posturl3";
	public static final String COLUMN_PACKAGENAME = "packagename";
	public static final String COLUMN_ACTIVITYNAME = "activityname";
	public static final String COLUMN_CHANNELID = "channelid";// 频道ID
	public static final String COLUMN_CHANNELNAME = "channelname";// 频道名称
	public static final String COLUMN_LAYOUTID = "layoutid";// layout的id
	public static final String COLUMN_WIDTH = "width";// 宽
	public static final String COLUMN_HEIGHT = "height";// 高
	public static final String COLUMN_TOLEFTID = "toleftid";// 左ID
	public static final String COLUMN_TOTOPID = "totopid";// 上ID
	public static final String COLUMN_GAP = "gap";// 间隔
	public static final String COLUMN_CATEGORY = "category";// 栏目类别
	public static final String COLUMN_PACKETID = "packetid";// 芒果分类
	public static final String COLUMN_CATEGORYID = "categoryid";// 芒果分类ID
	public static final String COLUMN_PARAMS_JSON = "params_json";// 芒果分类ID
	public static final String COLUMN_ACTIONNAME = "actionname";// 芒果分类ID
	public static final String COLUMN_FAVORITE = "favorite";// 是否收藏
}