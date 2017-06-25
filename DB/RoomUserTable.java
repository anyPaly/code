package com.edgelesschat.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.edgelesschat.Entity.User;
import com.edgelesschat.global.ResearchCommon;
import com.edgelesschat.map.BMapApiApp;

public class RoomUserTable 
{
	public static final String TABLE_NAME = "RoomUserTable";//数据表的名称
	public static final String COLUMN_USER_ID = "uid";//群昵称
	public static final String COLUMN_ROOM_ID = "roomid";
	public static final String COLUMN_USERNAME = "name";
	public static final String COLUMN_HEADURL = "headurl";
	
	private SQLiteDatabase mDBStore;
	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String PRIMARY_KEY_TYPE = "primary key(";
	
	public RoomUserTable(SQLiteDatabase sqlLiteDatabase)
	{
		mDBStore = sqlLiteDatabase;
	}
	
	public static String getCreateTableSQLString() 
	{
		if (null == mSQLCreateWeiboInfoTable) 
		{
			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_USER_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ROOM_ID, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_USERNAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_HEADURL, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_USER_ID + "," + COLUMN_ROOM_ID  + ")";
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
	
	public void insert(String roomId, List<User> lstUser) 
	{
		removeRoom(roomId);
		for (User user : lstUser) 
		{
			ContentValues allPromotionInfoValues = new ContentValues();
			allPromotionInfoValues.put(COLUMN_USER_ID, user.getUserId());
			allPromotionInfoValues.put(COLUMN_ROOM_ID, user.getGroupId());
			allPromotionInfoValues.put(COLUMN_USERNAME, user.getName());
			allPromotionInfoValues.put(COLUMN_HEADURL, user.getHeadSmall());
			try 
			{
				mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
			} 
			catch (SQLiteConstraintException e) 
			{
				e.printStackTrace();
			}
		}						
	}
	
	public boolean removeRoom(String roomId) 
	{
		try
		{
			mDBStore.delete(TABLE_NAME, COLUMN_ROOM_ID + " = '" + roomId + "'", null);
			return true;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return false;
	}
	
	public List<User> query(String roomId)
	{
		Cursor cursor = null;
		List<User> lstUser = new ArrayList<User>();
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
					COLUMN_ROOM_ID + "='" + roomId + "'", null);
			
			if(cursor != null)
			{
				if (!cursor.moveToFirst()) 
				{
					return null;
				}
				int indexUid = cursor.getColumnIndex(COLUMN_USER_ID);
				int indexUrl = cursor.getColumnIndex(COLUMN_HEADURL);
				int indexName = cursor.getColumnIndex(COLUMN_USERNAME);
				do
				{
					User user = new User();
					user.setUserId(cursor.getString(indexUid));
					user.setHeadSmall(cursor.getString(indexUrl));
					user.setName(cursor.getString(indexName));
					user.setGroupId(roomId);
					lstUser.add(user);
				}while (cursor.moveToNext());
				return lstUser;
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		finally
		{
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
}
