package com.edgelesschat.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.edgelesschat.Entity.ChatBackgnd;
import com.edgelesschat.global.ResearchCommon;
import com.edgelesschat.map.BMapApiApp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

public class ChatBackgndTable {

	public static final String TABLE_NAME = "ChatBackgndTable"; //数据表的名称
	public static final String COLUMN_BKGND_ID = "bkgndid";
	public static final String COLUMN_BKGND_LOGINID = "loginid";
	public static final String COLUMN_BKGND_NAME = "name"; //图片昵称
	public static final String COLUMN_BKGND_URL = "url";
	public static final String COLUMN_BKGND_URL_SMALL = "url_small";
	public static final String COLUMN_BKGND_FOLDERPATH = "folder";
	public static final String COLUMN_BKGND_CREATETIME = "createtime";
	public static final String COLUMN_BGFND_FORID = "fid";
	
	private SQLiteDatabase mDBStore;
	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String PRIMARY_KEY_TYPE = "primary key(";
	
	public ChatBackgndTable(SQLiteDatabase sqlLiteDatabase)
	{
		mDBStore = sqlLiteDatabase;
	}
	
	public static String getCreateTableSQLString() 
	{
		if (null == mSQLCreateWeiboInfoTable) 
		{
			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_BKGND_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_BKGND_LOGINID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_BKGND_NAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_BKGND_URL, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_BKGND_URL_SMALL, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_BKGND_FOLDERPATH,COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_BKGND_CREATETIME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_BGFND_FORID, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_BKGND_LOGINID + "," + COLUMN_BKGND_ID  + ")";
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
	
	public void insert(ChatBackgnd chatBakgndInfo) 
	{
		String strLoginId = ResearchCommon.getUserId(BMapApiApp.getInstance());
		remove(chatBakgndInfo.bkgndid, strLoginId);
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_BKGND_ID, chatBakgndInfo.bkgndid);
		allPromotionInfoValues.put(COLUMN_BKGND_LOGINID, strLoginId);
		allPromotionInfoValues.put(COLUMN_BKGND_NAME, chatBakgndInfo.bkgnd_name);
		allPromotionInfoValues.put(COLUMN_BKGND_URL, chatBakgndInfo.bkgnd_url);
		allPromotionInfoValues.put(COLUMN_BKGND_URL_SMALL, chatBakgndInfo.bkgnd_url_small);
		allPromotionInfoValues.put(COLUMN_BKGND_FOLDERPATH, chatBakgndInfo.folder);
		allPromotionInfoValues.put(COLUMN_BKGND_CREATETIME, chatBakgndInfo.createtime);
		try 
		{
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} 
		catch (SQLiteConstraintException e) 
		{
			e.printStackTrace();
		}
							
	}
	
	public void insert(ChatBackgnd chatBakgndInfo, String forUid) 
	{
		String strLoginId = ResearchCommon.getUserId(BMapApiApp.getInstance());
		remove(chatBakgndInfo.bkgndid, strLoginId);
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_BKGND_ID, chatBakgndInfo.bkgndid);
		allPromotionInfoValues.put(COLUMN_BKGND_LOGINID, strLoginId);
		allPromotionInfoValues.put(COLUMN_BKGND_NAME, chatBakgndInfo.bkgnd_name);
		allPromotionInfoValues.put(COLUMN_BKGND_URL, chatBakgndInfo.bkgnd_url);
		allPromotionInfoValues.put(COLUMN_BKGND_URL_SMALL, chatBakgndInfo.bkgnd_url_small);
		allPromotionInfoValues.put(COLUMN_BKGND_FOLDERPATH, chatBakgndInfo.folder);
		allPromotionInfoValues.put(COLUMN_BKGND_CREATETIME, chatBakgndInfo.createtime);
		allPromotionInfoValues.put(COLUMN_BGFND_FORID, forUid);
		try 
		{
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} 
		catch (SQLiteConstraintException e) 
		{
			e.printStackTrace();
		}
							
	}
	
	public boolean remove(String strBakgndId, String strUid) 
	{
		try
		{
			String strDelete = COLUMN_BKGND_ID + " = '" + strBakgndId + "'" + " AND " + COLUMN_BKGND_LOGINID + "='" + strUid + "'";
			mDBStore.delete(TABLE_NAME, strDelete, null);
			return true;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return false;
	}
	
//	public Animation queryByPackgeName(String strPackageName)
//	{
//		Cursor cursor = null;
//		String strLoginId = ResearchCommon.getUserId(BMapApiApp.getInstance());
//		try {
//			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
//					COLUMN_ANIMATION_ENNAME + "='" + strPackageName + "'", null);
//			
//			if(cursor != null)
//			{
//				if (!cursor.moveToFirst()) 
//				{
//					return null;
//				}
//				int indexUid = cursor.getColumnIndex(COLUMN_ANIMATION_ID);
//				int indexUrl = cursor.getColumnIndex(COLUMN_ANIMATION_URLPATH);
//				int indexName = cursor.getColumnIndex(COLUMN_ANIMATION_NAME);
//				int indexEnName = cursor.getColumnIndex(COLUMN_ANIMATION_ENNAME);
//				int indexCount = cursor.getColumnIndex(COLUMN_ANIMATION_COUNT);
//				int indexFolder = cursor.getColumnIndex(COLUMN_ANIMATION_FOLDERPATH);
//				int indexCover = cursor.getColumnIndex(COLUMN_ANIMATION_COVER);
//				do
//				{
//					Animation animation = new Animation();
//					animation.setId(cursor.getString(indexUid));
//					animation.setGif_path(cursor.getString(indexUrl));
//					animation.setGif_name(cursor.getString(indexName));
//					animation.setGif_header(cursor.getString(indexEnName));
//					animation.setGif_folder(cursor.getString(indexFolder));
//					animation.setGif_count(cursor.getInt(indexCount));
//					animation.setGif_cover(cursor.getString(indexCover));
//					return animation;
//				}while (cursor.moveToNext());
//				
//			}
//		} 
//		catch (Exception e) 
//		{
//			e.printStackTrace();
//		} 
//		finally
//		{
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return null;
//	}
	
	public ChatBackgnd query(String strbakgndId)
	{
		Cursor cursor = null;
		String strLoginId = ResearchCommon.getUserId(BMapApiApp.getInstance());
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
					COLUMN_BKGND_LOGINID + "='" + strLoginId + "'" + " AND " + COLUMN_BKGND_ID + "='" + strbakgndId + "'", null);
			if(cursor != null)
			{
				if (!cursor.moveToFirst()) 
				{
					return null;
				}
				int indexUid = cursor.getColumnIndex(COLUMN_BKGND_ID);
				int indexName = cursor.getColumnIndex(COLUMN_BKGND_NAME);
				int indexUrl = cursor.getColumnIndex(COLUMN_BKGND_URL);
				int indexUrlSmall = cursor.getColumnIndex(COLUMN_BKGND_URL_SMALL);
				int indexFolder = cursor.getColumnIndex(COLUMN_BKGND_FOLDERPATH);
				int indexCreatetime = cursor.getColumnIndex(COLUMN_BKGND_CREATETIME);
				do
				{
					ChatBackgnd chatbakgnd = new ChatBackgnd();
					chatbakgnd.setBkgndid(cursor.getString(indexUid));
					chatbakgnd.setBkgnd_name(cursor.getString(indexName));
					chatbakgnd.setBkgnd_url(cursor.getString(indexUrl));
					chatbakgnd.setBkgnd_url_small(cursor.getString(indexUrlSmall));
					chatbakgnd.setFolder(cursor.getString(indexFolder));
					chatbakgnd.setCreatetime(cursor.getInt(indexCreatetime));
					return chatbakgnd;
				}while (cursor.moveToNext());
				
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
	
	public ChatBackgnd queryFid(String strFid)
	{
		Cursor cursor = null;
		String strLoginId = ResearchCommon.getUserId(BMapApiApp.getInstance());
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
					COLUMN_BKGND_LOGINID + "='" + strLoginId + "'" + " AND " + COLUMN_BGFND_FORID + "='" + strFid + "'", null);
			if(cursor != null)
			{
				if (!cursor.moveToFirst()) 
				{
					return null;
				}
				int indexUid = cursor.getColumnIndex(COLUMN_BKGND_ID);
				int indexName = cursor.getColumnIndex(COLUMN_BKGND_NAME);
				int indexUrl = cursor.getColumnIndex(COLUMN_BKGND_URL);
				int indexUrlSmall = cursor.getColumnIndex(COLUMN_BKGND_URL_SMALL);
				int indexFolder = cursor.getColumnIndex(COLUMN_BKGND_FOLDERPATH);
				int indexCreatetime = cursor.getColumnIndex(COLUMN_BKGND_CREATETIME);
				int indexFid = cursor.getColumnIndex(COLUMN_BGFND_FORID);
				do
				{
					ChatBackgnd chatbakgnd = new ChatBackgnd();
					chatbakgnd.setBkgndid(cursor.getString(indexUid));
					chatbakgnd.setBkgnd_name(cursor.getString(indexName));
					chatbakgnd.setBkgnd_url(cursor.getString(indexUrl));
					chatbakgnd.setBkgnd_url_small(cursor.getString(indexUrlSmall));
					chatbakgnd.setFolder(cursor.getString(indexFolder));
					chatbakgnd.setCreatetime(cursor.getInt(indexCreatetime));
					chatbakgnd.setCreatetime(cursor.getInt(indexFid));
					return chatbakgnd;
				}while (cursor.moveToNext());
				
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
	
	public List<ChatBackgnd> query()
	{
		Cursor cursor = null;
		String strLoginId = ResearchCommon.getUserId(BMapApiApp.getInstance());
		List<ChatBackgnd> lstChatBakgnd = new ArrayList<ChatBackgnd>();
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
					COLUMN_BKGND_LOGINID + "='" + strLoginId + "'", null);
			
			if(cursor != null)
			{
				if (!cursor.moveToFirst()) 
				{
					return lstChatBakgnd;
				}
				int indexUid = cursor.getColumnIndex(COLUMN_BKGND_ID);
				int indexName = cursor.getColumnIndex(COLUMN_BKGND_NAME);
				int indexUrl = cursor.getColumnIndex(COLUMN_BKGND_URL);
				int indexUrlSmall = cursor.getColumnIndex(COLUMN_BKGND_URL_SMALL);
				int indexFolder = cursor.getColumnIndex(COLUMN_BKGND_FOLDERPATH);
				int indexCreatetime = cursor.getColumnIndex(COLUMN_BKGND_CREATETIME);
				do
				{
					ChatBackgnd chatbakgnd = new ChatBackgnd();
					chatbakgnd.setBkgndid(cursor.getString(indexUid));
					chatbakgnd.setBkgnd_name(cursor.getString(indexName));
					chatbakgnd.setBkgnd_url(cursor.getString(indexUrl));
					chatbakgnd.setBkgnd_url_small(cursor.getString(indexUrlSmall));
					chatbakgnd.setFolder(cursor.getString(indexFolder));
					chatbakgnd.setCreatetime(cursor.getInt(indexCreatetime));
					lstChatBakgnd.add(chatbakgnd);
				}while (cursor.moveToNext());
				return lstChatBakgnd;
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
