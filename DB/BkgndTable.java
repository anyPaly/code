package com.edgelesschat.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.edgelesschat.Entity.ChatBkgnd;
import com.edgelesschat.Entity.Room;
import com.edgelesschat.global.ResearchCommon;
import com.edgelesschat.map.BMapApiApp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

//id, uid, typechat, url
public class BkgndTable {
	public static final String TABLE_NAME = "BkgndTable";//数据表的名称
//	public static final String COLUMN_BKGND_ID = "bkgndid";
	public static final String COLUMN_BKGND_UID = "uid";
	public static final String COLUMN_BKGND_TYPECHAT = "typechat";
	public static final String COLUMN_BKGND_URL = "url";
	public static final String COLUMN_BKGND_LOGINID = "loginid";
	private SQLiteDatabase mDBStore;
	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String PRIMARY_KEY_TYPE = "primary key(";
	
	public BkgndTable(SQLiteDatabase sqlLiteDatabase)
	{
		mDBStore = sqlLiteDatabase;
	}
	
	public static String getCreateTableSQLString() 
	{
		if (null == mSQLCreateWeiboInfoTable) 
		{
			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
//			columnNameAndType.put(COLUMN_BKGND_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_BKGND_UID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_BKGND_TYPECHAT, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_BKGND_URL, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_BKGND_LOGINID, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_BKGND_UID + "," + COLUMN_BKGND_LOGINID + ")";
			mSQLCreateWeiboInfoTable = SqlHelper.formCreateTableSqlString(TABLE_NAME, columnNameAndType, primary_key);
		}
		return mSQLCreateWeiboInfoTable;
	}
	
	public static String getDeleteTableSQLString() 
	{
		if (null == mSQLDeleteWeiboInfoTable) {
			mSQLDeleteWeiboInfoTable = SqlHelper.formDeleteTableSqlString(TABLE_NAME);
		}  
		return mSQLDeleteWeiboInfoTable;
	}
	
	public void insert(List<ChatBkgnd> bkgnds) 
	{
		List<ChatBkgnd> bkgndList = new ArrayList<ChatBkgnd>();
		bkgndList.addAll(bkgnds);
		for (ChatBkgnd bkgnd : bkgndList) {
			ContentValues allPromotionInfoValues = new ContentValues();
//			allPromotionInfoValues.put(COLUMN_BKGND_ID, bkgnd.);
			allPromotionInfoValues.put(COLUMN_BKGND_UID, bkgnd.getUid());
			allPromotionInfoValues.put(COLUMN_BKGND_TYPECHAT, bkgnd.getTypeChat());
			allPromotionInfoValues.put(COLUMN_BKGND_URL, bkgnd.getUrl());
			allPromotionInfoValues.put(COLUMN_BKGND_LOGINID, ResearchCommon.getUserId(BMapApiApp.getInstance()));
			delete(bkgnd.getUid());
			try 
			{
				mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
			} catch (SQLiteConstraintException e) 
			{
				e.printStackTrace();
			}		
		}						
	}
	
	public void insert(ChatBkgnd bkgnd) {

		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_BKGND_UID, bkgnd.getUid());
		allPromotionInfoValues.put(COLUMN_BKGND_TYPECHAT, bkgnd.getTypeChat());
		allPromotionInfoValues.put(COLUMN_BKGND_URL, bkgnd.getUrl());
		allPromotionInfoValues.put(COLUMN_BKGND_LOGINID, ResearchCommon.getUserId(BMapApiApp.getInstance()));

		delete(bkgnd.getUid());
		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}		
	}
	
	public boolean update(ChatBkgnd bkgnd) 
	{
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_BKGND_UID, bkgnd.getUid());
		allPromotionInfoValues.put(COLUMN_BKGND_TYPECHAT, bkgnd.getTypeChat());
		allPromotionInfoValues.put(COLUMN_BKGND_URL, bkgnd.getUrl());
		allPromotionInfoValues.put(COLUMN_BKGND_LOGINID, ResearchCommon.getUserId(BMapApiApp.getInstance()));
		try 
		{
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_BKGND_UID + " = '" + bkgnd.getUid()
					+ "' AND " + COLUMN_BKGND_LOGINID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}			
		return false;
	}
	
	public boolean delete(String strUid) 
	{
		try {
			mDBStore.delete(TABLE_NAME, COLUMN_BKGND_UID + " = '" + strUid + "' AND " + 
					COLUMN_BKGND_LOGINID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public ChatBkgnd query(String strUid)
	{
		Cursor cursor = null;
		String strLoginId = ResearchCommon.getUserId(BMapApiApp.getInstance());
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
					COLUMN_BKGND_LOGINID + "='" + strLoginId + 
					"' AND " + COLUMN_BKGND_UID + "='" + strUid + "'" , null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}				

				int indexUid = cursor.getColumnIndex(COLUMN_BKGND_UID);
				int indexUrl = cursor.getColumnIndex(COLUMN_BKGND_URL);
				int indexTypeChat = cursor.getColumnIndex(COLUMN_BKGND_TYPECHAT);

				ChatBkgnd bkgnd = new ChatBkgnd();
				bkgnd.setUid(cursor.getString(indexUid));
				bkgnd.setUrl(cursor.getString(indexUrl));
				bkgnd.setTypeChat(cursor.getInt(indexTypeChat));
				bkgnd.setTypeChat(cursor.getInt(indexTypeChat));
				bkgnd.setLoginId(strLoginId);
				return bkgnd;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
	
}
