package com.edgelesschat.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.edgelesschat.Entity.GifFavorite;
import com.edgelesschat.Entity.User;

public class GifFavoriteTable {

	public static final String TABLE_NAME = "GifFavoriteTable";//数据表的名称
	public static final String COLUMN_ID = "id";//GIF列表id
	public static final String GIF_JSON = "gifjson";//动态图JSON数据
	
	private SQLiteDatabase mDBStore;
	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String PRIMARY_KEY_TYPE = "primary key(";
	
	public GifFavoriteTable(SQLiteDatabase sqlLiteDatabase)
	{
		mDBStore = sqlLiteDatabase;
	}
	
	public static String getCreateTableSQLString() 
	{
		if (null == mSQLCreateWeiboInfoTable) 
		{
			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(GIF_JSON, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_ID + "," + GIF_JSON  + ")";
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
	
	public void insert(String id, String gifPath) 
	{
		removeRoom(id);
	
			ContentValues allPromotionInfoValues = new ContentValues();
			allPromotionInfoValues.put(COLUMN_ID, id);
			allPromotionInfoValues.put(GIF_JSON, gifPath);
			try 
			{
				mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
			} 
			catch (SQLiteConstraintException e) 
			{
				e.printStackTrace();
			}
							
	}
	
	public void insert(ArrayList<GifFavorite> list) 
	{
	
		for (GifFavorite gifFavorite : list) 
		{
			ContentValues allPromotionInfoValues = new ContentValues();
			allPromotionInfoValues.put(COLUMN_ID, gifFavorite.getId());
			allPromotionInfoValues.put(GIF_JSON, gifFavorite.getGifpath());
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
	
	public boolean removeRoom(String id) 
	{
		try
		{
			mDBStore.delete(TABLE_NAME, COLUMN_ID + " = '" + id + "'", null);
			return true;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return false;
	}
	
	public List<GifFavorite> query()
	{
		Cursor cursor = null;
		List<GifFavorite> gifFavoriteslist = new ArrayList<GifFavorite>();
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME , null);
			
			if(cursor != null)
			{
				if (!cursor.moveToFirst()) 
				{
					return null;
				}
				int indexUid = cursor.getColumnIndex(COLUMN_ID);
				int indexUrl = cursor.getColumnIndex(GIF_JSON);
				do
				{
					GifFavorite gifFavorite = new GifFavorite();
					gifFavorite.setId(cursor.getString(indexUid));
					gifFavorite.setGifpath(cursor.getString(indexUrl));
					gifFavoriteslist.add(gifFavorite);
				}while (cursor.moveToNext());
				return gifFavoriteslist;
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
