package com.edgelesschat.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.edgelesschat.Entity.User;
import com.edgelesschat.animation.Animation;
import com.edgelesschat.global.ResearchCommon;
import com.edgelesschat.map.BMapApiApp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

public class AnimationTable {
	
	public static final String TABLE_NAME = "AnimationTable";//数据表的名称
	public static final String COLUMN_ANIMATION_ID = "gifid";
	public static final String COLUMN_ANIMATION_LOGINID = "loginid";
	public static final String COLUMN_ANIMATION_NAME = "name";//群昵称
	public static final String COLUMN_ANIMATION_ENNAME = "enname";
	public static final String COLUMN_ANIMATION_COUNT = "count";
	public static final String COLUMN_ANIMATION_URLPATH = "urlpath";
	public static final String COLUMN_ANIMATION_FOLDERPATH = "folder";
	public static final String COLUMN_ANIMATION_COVER = "cover";
	
	private SQLiteDatabase mDBStore;
	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String PRIMARY_KEY_TYPE = "primary key(";
	
	public AnimationTable(SQLiteDatabase sqlLiteDatabase)
	{
		mDBStore = sqlLiteDatabase;
	}
	
	public static String getCreateTableSQLString() 
	{
		if (null == mSQLCreateWeiboInfoTable) 
		{
			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_ANIMATION_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ANIMATION_LOGINID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ANIMATION_NAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ANIMATION_ENNAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ANIMATION_COUNT, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_ANIMATION_URLPATH,COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ANIMATION_FOLDERPATH,COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ANIMATION_COVER, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_ANIMATION_LOGINID + "," + COLUMN_ANIMATION_ID  + ")";
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
	
	public void insert(Animation animationInfo) 
	{
		
		String strLoginId = ResearchCommon.getUserId(BMapApiApp.getInstance());
		remove(animationInfo.id, strLoginId);
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_ANIMATION_ID, animationInfo.id);
		allPromotionInfoValues.put(COLUMN_ANIMATION_LOGINID, strLoginId);
		allPromotionInfoValues.put(COLUMN_ANIMATION_NAME, animationInfo.gif_name);
		allPromotionInfoValues.put(COLUMN_ANIMATION_ENNAME, animationInfo.gif_header);
		allPromotionInfoValues.put(COLUMN_ANIMATION_COUNT, animationInfo.gif_count);
		allPromotionInfoValues.put(COLUMN_ANIMATION_FOLDERPATH, animationInfo.gif_folder);
		allPromotionInfoValues.put(COLUMN_ANIMATION_URLPATH, animationInfo.gif_path);
		allPromotionInfoValues.put(COLUMN_ANIMATION_COVER, animationInfo.gif_cover);
		try 
		{
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} 
		catch (SQLiteConstraintException e) 
		{
			e.printStackTrace();
		}
							
	}
	
	public boolean remove(String strGifId, String strUid) 
	{
		try
		{
			String strDelete = COLUMN_ANIMATION_ID + " = '" + strGifId + "'" + " AND " + COLUMN_ANIMATION_LOGINID + "='" + strUid + "'";
			mDBStore.delete(TABLE_NAME, strDelete, null);
			return true;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return false;
	}
	
	public Animation queryByPackgeName(String strPackageName)
	{
		Cursor cursor = null;
		String strLoginId = ResearchCommon.getUserId(BMapApiApp.getInstance());
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
					COLUMN_ANIMATION_ENNAME + "='" + strPackageName + "'", null);
			
			if(cursor != null)
			{
				if (!cursor.moveToFirst()) 
				{
					return null;
				}
				int indexUid = cursor.getColumnIndex(COLUMN_ANIMATION_ID);
				int indexUrl = cursor.getColumnIndex(COLUMN_ANIMATION_URLPATH);
				int indexName = cursor.getColumnIndex(COLUMN_ANIMATION_NAME);
				int indexEnName = cursor.getColumnIndex(COLUMN_ANIMATION_ENNAME);
				int indexCount = cursor.getColumnIndex(COLUMN_ANIMATION_COUNT);
				int indexFolder = cursor.getColumnIndex(COLUMN_ANIMATION_FOLDERPATH);
				int indexCover = cursor.getColumnIndex(COLUMN_ANIMATION_COVER);
				do
				{
					Animation animation = new Animation();
					animation.setId(cursor.getString(indexUid));
					animation.setGif_path(cursor.getString(indexUrl));
					animation.setGif_name(cursor.getString(indexName));
					animation.setGif_header(cursor.getString(indexEnName));
					animation.setGif_folder(cursor.getString(indexFolder));
					animation.setGif_count(cursor.getInt(indexCount));
					animation.setGif_cover(cursor.getString(indexCover));
					return animation;
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
	
	public Animation query(String strGifId)
	{
		Cursor cursor = null;
		String strLoginId = ResearchCommon.getUserId(BMapApiApp.getInstance());
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
					COLUMN_ANIMATION_LOGINID + "='" + strLoginId + "'" + " AND " + COLUMN_ANIMATION_ID + "='" + strGifId + "'", null);
			
			if(cursor != null)
			{
				if (!cursor.moveToFirst()) 
				{
					return null;
				}
				int indexUid = cursor.getColumnIndex(COLUMN_ANIMATION_ID);
				int indexUrl = cursor.getColumnIndex(COLUMN_ANIMATION_URLPATH);
				int indexName = cursor.getColumnIndex(COLUMN_ANIMATION_NAME);
				int indexEnName = cursor.getColumnIndex(COLUMN_ANIMATION_ENNAME);
				int indexCount = cursor.getColumnIndex(COLUMN_ANIMATION_COUNT);
				int indexFolder = cursor.getColumnIndex(COLUMN_ANIMATION_FOLDERPATH);
				int indexCover = cursor.getColumnIndex(COLUMN_ANIMATION_COVER);
				do
				{
					Animation animation = new Animation();
					animation.setId(cursor.getString(indexUid));
					animation.setGif_path(cursor.getString(indexUrl));
					animation.setGif_name(cursor.getString(indexName));
					animation.setGif_header(cursor.getString(indexEnName));
					animation.setGif_folder(cursor.getString(indexFolder));
					animation.setGif_count(cursor.getInt(indexCount));
					animation.setGif_cover(cursor.getString(indexCover));
					return animation;
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
	
	public List<Animation> query()
	{
		Cursor cursor = null;
		String strLoginId = ResearchCommon.getUserId(BMapApiApp.getInstance());
		List<Animation> lstAnimation = new ArrayList<Animation>();
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
					COLUMN_ANIMATION_LOGINID + "='" + strLoginId + "'", null);
			
			if(cursor != null)
			{
				if (!cursor.moveToFirst()) 
				{
					return lstAnimation;
				}
				int indexUid = cursor.getColumnIndex(COLUMN_ANIMATION_ID);
				int indexUrl = cursor.getColumnIndex(COLUMN_ANIMATION_URLPATH);
				int indexName = cursor.getColumnIndex(COLUMN_ANIMATION_NAME);
				int indexEnName = cursor.getColumnIndex(COLUMN_ANIMATION_ENNAME);
				int indexCount = cursor.getColumnIndex(COLUMN_ANIMATION_COUNT);
				int indexFolder = cursor.getColumnIndex(COLUMN_ANIMATION_FOLDERPATH);
				int indexCover = cursor.getColumnIndex(COLUMN_ANIMATION_COVER);
				do
				{
					Animation animation = new Animation();
					animation.setId(cursor.getString(indexUid));
					animation.setGif_path(cursor.getString(indexUrl));
					animation.setGif_name(cursor.getString(indexName));
					animation.setGif_header(cursor.getString(indexEnName));
					animation.setGif_folder(cursor.getString(indexFolder));
					animation.setGif_count(cursor.getInt(indexCount));
					animation.setGif_cover(cursor.getString(indexCover));
					lstAnimation.add(animation);
				}while (cursor.moveToNext());
				return lstAnimation;
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
